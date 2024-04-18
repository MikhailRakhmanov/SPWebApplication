package ru.raticate.spWebApplication.controllers.dto;

import ru.raticate.spWebApplication.DateConvertor;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ResultExport {
    public String none = "-";
    public Integer kolvo = 1;
    public Integer barcode;
    public String numDog;
    public String caption;
    public Integer h;
    public Integer w;
    public String notes;
    public String typeIzd;
    public Integer idPacket;
    public String brig;
    public String dot;
    private static final Pattern pattern
            = Pattern.compile("(?:\\d-кам \\d{2} \\()?([^)\\n]+)(?:\\)| GLASS)|(.+)");

    public ResultExport(Packet packet, Export export) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateConvertor convertor = new DateConvertor();
        this.barcode = export.getBarcode();
        this.numDog = export.getNumDog() + " " + export.getNpos() +
                      "." + export.getPos();
        Matcher matcher = pattern.matcher(Objects.requireNonNull(export.getCaption()));
        while (matcher.find()) {
            this.caption = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
        }
        this.h = export.getH();
        this.w = export.getW();
        this.notes = export.getNotes();
        this.typeIzd = export.getTypeIzd();
        if (packet != null) {
            this.idPacket = packet.getIdPacket();
            this.brig = packet.getBrig();
            this.dot = (packet.getDot() != null && packet.getDot() != 0) ? Objects.requireNonNull(convertor.numToDate(packet.getDot())).format(formatter) : null;
        }
        this.kolvo = 1;
    }


}