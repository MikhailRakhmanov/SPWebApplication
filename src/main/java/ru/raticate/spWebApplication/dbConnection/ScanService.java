package ru.raticate.spWebApplication.dbConnection;

import org.springframework.beans.factory.annotation.Qualifier;
import ru.raticate.spWebApplication.util.Pair;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.raticate.spWebApplication.DateConvertor;

import java.time.LocalDateTime;

@Component
public class ScanService {
    DateConvertor convertor = new DateConvertor();
    final JdbcTemplate sPJdbcTemplate, mainJdbcTemplate;

    public ScanService(@Qualifier("sPJdbcTemplate") JdbcTemplate sPJdbcTemplate, @Qualifier("mainJdbcTemplate") JdbcTemplate mainJdbcTemplate) {
        this.sPJdbcTemplate = sPJdbcTemplate;
        this.mainJdbcTemplate = mainJdbcTemplate;
    }

    record Platform(Integer num, Integer sosPir) {}


    public void sendQuery(Pair<Integer, Integer> platformAndProduct) {
        Integer platformId = platformAndProduct.getKey();
        Integer product = platformAndProduct.getValue();
        Platform platform;
        if (platformId == null) {
            throw new RuntimeException("Платформа не задана");
        }
        try {
            platform = sPJdbcTemplate.queryForObject("SELECT * FROM TPIR WHERE NUM = ?", new DataClassRowMapper<>(Platform.class), platformId);
        } catch (EmptyResultDataAccessException ex) {
            sPJdbcTemplate.update("insert into TPIR(NUM,SOSPIR) values (?,1)", platformId);
            sPJdbcTemplate.update("insert into tpirlist(NUM,DT,CLIENT) values (?,?,970)", platformId, convertor.dateToDouble(LocalDateTime.now()));
            platform = sPJdbcTemplate.queryForObject("SELECT * FROM TPIR WHERE NUM = ?", new DataClassRowMapper<>(Platform.class), platformId);
        }
        if (platform != null && platform.sosPir != 1) {
            sPJdbcTemplate.update("update TPIR set sospir = 1 where NUM = ?", platform.num);
            sPJdbcTemplate.update("insert into tpirlist(NUM,DT,CLIENT) values (?,?,970)", platform.num, convertor.dateToDouble(LocalDateTime.now()));
        }
        if (product == null) {
            return;
        }
        if (product == 99999995) {
            sPJdbcTemplate.update("insert into tpirlist(NUM,DT,CLIENT) values (?,?,970)", platformId, convertor.dateToDouble(LocalDateTime.now()));
            sPJdbcTemplate.update("update tpir set sospir=1 where num=?", platformId);
        } else if (product == 99999996) {
            sPJdbcTemplate.update("execute procedure v0382_6(?)", platformId);
        } else {
            Integer barcode = null;
            try {
                barcode = sPJdbcTemplate.queryForObject("select BARCODE from ZMATLIST WHERE BARCODE = ?", Integer.class, product);
                System.out.println("Штрих из таблицы 1: " + barcode);
            } catch (Exception ex) {
                System.out.println("Штрих кода " + barcode + " нет в ZMATLIST.");
            }
            if (barcode == null) {
                try {
                    barcode = sPJdbcTemplate.queryForObject("select ZMATLIST.barcode as barcode " +
                                                            "from listizd, zmatlist  where LISTIZD.id=ZMATLIST.idizd " +
                                                            "and ZMATLIST.maked is null and LISTIZD.np=?", Integer.class, product);
                    System.out.println("Штрих из таблицы 2: " + barcode);
                } catch (Exception ex) {
                    System.out.println("Штрих кода " + barcode + " нет в ZMATLIST.");
                }
                if (barcode == null) {
                    System.out.println("Штрих кода нет в базе! Проверьте верная ли база данных СП подключена.");
                }
            }

            sPJdbcTemplate.update(
                    "update ZMATLIST set TRUCK = ?, DTPIR = ? where BARCODE = ?",
                    platformId,
                    convertor.dateToDouble(LocalDateTime.now()),
                    barcode);
            Double mainBarcode = sPJdbcTemplate.queryForObject("select a.np from listizd a join zmatlist b on b.idizd = a.id where b.barcode = ?",
                    Double.class,
                    barcode);
            if (mainBarcode != null) {
                System.out.println("Штрих из основной: " + mainBarcode.intValue());
                mainJdbcTemplate.update(
                        "update ZMATLIST set TRUCK = ?,DTPIR = ?,maked = 1 where BARCODE = ?",
                        platformId,
                        convertor.dateToDouble(LocalDateTime.now()),
                        mainBarcode.intValue()
                );
            } else {
                System.out.println("mainBarcode == null");
            }
        }
    }
}

