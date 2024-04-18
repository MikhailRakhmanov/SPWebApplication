package ru.raticate.spWebApplication.controllers.dto;

data class ExportDTO(
    val idZmat: Int?,
    val idPacket: Int?, 
    val bCode: Int?, 
    val pos: String?, 
    val caption: String?, 
    val brig: String?, 
    val h: Int?, 
    val w: Int?, 
    val kolvo: Int?, 
    val wDate: Int?, 
    val typeIzd: Int?, 
    val notes: String?
)