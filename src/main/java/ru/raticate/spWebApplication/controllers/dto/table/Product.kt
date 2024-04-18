package ru.raticate.spWebApplication.controllers.dto.table;

import ru.raticate.spWebApplication.DateConvertor

class Product(
        val caption: String?,
        val mark: String?,
        val numDog: Int?,
        val raz: String?,
        val client: String?,
        val sm: Double,
        dts: Number?,
        dtf: Number?
){
        private val convertor = DateConvertor()
        val dts = dts?.let { convertor.numToStr(it) }
        val dtf = dtf?.let { convertor.numToStr(it) }
        
}
