package ru.raticate.spWebApplication.controllers.dto

data class Export(
    var barcode: Int,
    var numDog: Int,
    var npos: Int?,
    var pos: Int?,
    var caption: String?,
    var h: Int,
    var w: Int,
    var notes: String?,
    var typeIzd: String?
) 