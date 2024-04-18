package ru.raticate.spWebApplication.controllers.dto

import ru.raticate.spWebApplication.DateConvertor
import java.time.LocalDate

class Dict(val notes: String? = null, val notes1: String? = null, dot1: Number? = null, dot2: Number? = null, dot3: Number? = null, val flg: Int? = null) {
    private  val dateConvertor = DateConvertor()
    val dot1: LocalDate? = dot1?.let { dateConvertor.numToDate(it)?.toLocalDate() }
    val dot2: LocalDate? = dot2?.let { dateConvertor.numToDate(it)?.toLocalDate() }
    val dot3: LocalDate? = dot3?.let { dateConvertor.numToDate(it)?.toLocalDate() }
}