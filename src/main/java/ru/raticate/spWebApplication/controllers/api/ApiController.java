package ru.raticate.spWebApplication.controllers.api;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import ru.raticate.spWebApplication.DateConvertor;
import ru.raticate.spWebApplication.controllers.dto.*;
import ru.raticate.spWebApplication.controllers.dto.table.PlatformDTO;
import ru.raticate.spWebApplication.controllers.dto.table.Product;
import ru.raticate.spWebApplication.dbConnection.ScanService;
import ru.raticate.spWebApplication.servises.Tpir;
import ru.raticate.spWebApplication.util.Pair;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    /*
        JedisPool pool = new JedisPool(
                "redis-13167.c300.eu-central-1-1.ec2.cloud.redislabs.com",
                13167, "default",
                "My4NBIdzvdH5FEQYmeicatWriheFLzt1");
        Jedis jedis = pool.getResource();
    */
    final JdbcTemplate sPJdbcTemplate;
    final ScanService scanService;
    final JdbcTemplate mainJdbcTemplate;
    private Integer currentPlatform;


    public Integer getCurrentPlatform() {
        return currentPlatform;
    }

    public ApiController(
            ScanService scanService,

            @Qualifier("sPJdbcTemplate")
            JdbcTemplate sPJdbcTemplate,

            @Qualifier("mainJdbcTemplate")
            JdbcTemplate mainJdbcTemplate
    ) {
        this.scanService = scanService;
        this.sPJdbcTemplate = sPJdbcTemplate;
        this.mainJdbcTemplate = mainJdbcTemplate;
    }

    @PostMapping(value = "/sp_data", produces = MediaType.APPLICATION_JSON_VALUE)
    String postData(@RequestBody PairDTO pair) {
        System.out.println(pair.getPlatform() + " - " + pair.getProduct());
        currentPlatform = pair.getPlatform();
        if (pair.getProduct() != null) {
            scanService.sendQuery(new Pair<>(pair.getPlatform(), pair.getProduct()));
            return "succes";
        } else {
            return "failed";
        }
    }

    @GetMapping("/import")
    public List<ImportDTO> importOrders() {
        return mainJdbcTemplate.query(
                """
                        select IDZMAT,
                                      DOTPRN,
                                      PACKET,
                                      NUM,
                                      NOTES,
                                      NOTES1,
                                      FNAME,
                                      SLC,
                                      WDATE,
                                      STRVALUE as idbrig,
                                      DTP,
                                      DOT6,
                                      PARENT,
                                      MANAGER,
                                      VD,
                                      KOLVO
                        from V0172_3_C1(15, 16267820, 0)
                                 left join (select * from TBL_LISTVAR where TDEF_ID = 292)
                                           ON ID = IDBRIG
                        where DOTPRN is null
                        order by idbrig, wdate
                        """,
                new DataClassRowMapper<>(ImportDTO.class));
    }

    @GetMapping("/delivery")
    private List<DeliveryDTO> delivery() {
        return sPJdbcTemplate.query("select * from V0852 order by DOTOUT,TIMEOUT,NUMPIR",
                new DataClassRowMapper<>(DeliveryDTO.class));
    }

    @GetMapping("/tpir")
    public List<Tpir> platformList() {
        return sPJdbcTemplate.query("select * from TPIR order by num", new DataClassRowMapper<>(Tpir.class));
    }


    @GetMapping("/table/{id}")
    public PlatformDTO platform(@PathVariable Integer id) {
        List<Product> products = sPJdbcTemplate.query(
                "select * from V0859_1_c1(?) order by mark",
                new DataClassRowMapper<>(Product.class),
                id

        );

        int count = products.size();
        Double area = products.stream().mapToDouble(Product::getSm).sum();
        return new PlatformDTO(
                products, id, count,
                new DecimalFormat("#.###").format(area)
        );
    }

    @GetMapping("/table/old/{id}")
    public List<String> oldPlatformInfo(@PathVariable Integer id) {
        /*
            String places = jedis.get(id.toString());
            if (places != null) {
                System.out.println(places);
                return List.of(places.split(", "));
            }
         */
        List<String> result;
        Double date = sPJdbcTemplate.queryForObject(
                "select max(dt) from tpirlist where num = ? and dotout is not null ",
                Double.class, id);
        result = sPJdbcTemplate.query(
                """
                        select distinct caption
                        from dogovor
                                 left join sprenprise
                                           on dogovor.client = sprenprise.idsprenprise
                        where iddogovor in (select distinct IDDOG
                                            from zmatlist
                                                     left join LISTIZD
                                                               on ZMATLIST.IDIZD = LISTIZD.ID
                                            where DTPIR >= ?
                                              and truck = ?
                        );
                        """,
                (rs, rowNum) -> rs.getString("caption"),
                date,
                id);
//        System.out.println(new String(
//                jedis.setex(id.toString(), 24 * 3600, String.join(", ", result)).getBytes(),
//                Charset.forName("windows-1251"))
//        );
        return result;
    }

    List<ResultExport> resultExport = new ArrayList<>();


    @PostMapping(value = "/export", produces = MediaType.APPLICATION_JSON_VALUE)
    List<ResultExport> exportData(@RequestBody List<Integer> idZmat) {
        isImporting = true;
        Map<Integer, Packet> idZmat2Packet = new HashMap<>();
        Map<Integer, List<Export>> idZmat2Export = new HashMap<>();
        for (Integer id : idZmat) {
            Packet packet;
            try {
                packet = mainJdbcTemplate.queryForObject(
                        """
                                SELECT FIRST 1 IDPACKET, STRVALUE as BRIG, DOT2 as DOT 
                                        FROM ZMAT 
                                        LEFT JOIN base_baza 
                                        ON IDPACKETHAFF=ZMAT.IDPACKET 
                                        LEFT JOIN TBL_LISTVAR 
                                        ON ID=IDBRIG 
                                        WHERE IDZMAT = ?  AND 
                                        TDEF_ID = 292
                                """,
                        new DataClassRowMapper<>(Packet.class),
                        id
                );
                idZmat2Packet.put(id, packet);
            } catch (Exception ex) {
                System.out.println("Packet for IDZMAT = " + id + " is null.");
            }
        }
        for (Integer id : idZmat) {
            List<Export> export;
            export = mainJdbcTemplate.query("""
                            select barcode,
                                   numDog,
                                   npos,
                                   pos,
                                   caption,
                                   h,
                                   w,
                                   a.notes,
                                   typeizd
                            from listizd a,
                                 packetinizd b,
                                 ZMATLIST c,
                                 dogovor d
                            where b.idizd = a.id
                              and a.id = c.idizd
                              and c.IDCOMP = b.IDGRAPH
                              and c.IDZMAT = ?
                              and b.IDMAT not in (5705806, 5705807)
                              and a.iddog = d.iddogovor
                            order by a.npos, b.pos
                            """,
                    (resultSet, rowNum) -> new Export(
                            resultSet.getInt("barcode"),
                            resultSet.getInt("numDog"),
                            resultSet.getInt("npos"),
                            resultSet.getInt("pos"),
                            resultSet.getString("caption"),
                            resultSet.getInt("h"),
                            resultSet.getInt("w"),
                            resultSet.getString("notes"),
                            resultSet.getString("typeIzd")
                    ), id
            );
            idZmat2Export.put(id, export);
        }
        resultExport = new ArrayList<>();
        DateConvertor dateConvertor = new DateConvertor();
        Integer now = dateConvertor.dateToDouble(LocalDateTime.now()).intValue();
        Integer first = idZmat.getFirst();
        for (Integer id : idZmat) {
            for (Export export : idZmat2Export.get(id)) {
                resultExport.add(new ResultExport(idZmat2Packet.get(id), export));
            }
            mainJdbcTemplate.update("update zmat set dotprn=?, dotprnst=? where idzmat=? or parent=?", now, now, id, id);
            if (id.equals(first)) {
                continue;
            }
            mainJdbcTemplate.update("update zmat set PARENT = ? where IDZMAT = ?", first, id);
            System.out.println(idZmat);
        }
        Thread thread = new Thread(() -> {
            String[] order = {
                    "numDog",
                    "caption",
                    "w",
                    "h",
                    "kolvo",
                    "none",
                    "barcode",
                    "brig",
                    "dot"
            };

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Лист 1");
            int rownum = 0;
            for (ResultExport line : resultExport) {
                Row row = sheet.createRow(rownum++);
                Cell firstCell = row.createCell(0);
                firstCell.setCellValue(rownum);
                for (int i = 1; i <= order.length; i++) {
                    Cell cell = row.createCell(i);
                    try {
                        Object value = ResultExport.class.getDeclaredField(order[i - 1]).get(line);
                        cell.setCellValue(value != null ? String.valueOf(value) : "");
                    } catch (Exception ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            }
            FileOutputStream out = null;
            try {
                System.out.println("создание файла");
                out = new FileOutputStream("sp_front/dist/downloads/import.xlsx");
                workbook.write(out);
                out.close();
                System.out.println("записан и закрыт");
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isImporting = false;
        return resultExport;
    }
    @GetMapping("/isImporting")
    Boolean getIsImporting(){
        return isImporting;
    }

    volatile Boolean isImporting = false;
    @GetMapping("/export")
    List<ResultExport> getExportData() {
        return resultExport;
    }

}