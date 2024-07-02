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
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Product

                if (caption != other.caption) return false
                if (raz != other.raz) return false

                return true
        }

        override fun hashCode(): Int {
                var result = caption?.hashCode() ?: 0
                result = 31 * result + (raz?.hashCode() ?: 0)
                return result
        }

}
