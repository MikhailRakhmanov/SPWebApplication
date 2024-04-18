package ru.raticate.spWebApplication.controllers.dto.table;

data class PlatformDTO(
        val products: List<Product>? = listOf(),
        val platformName: Int? = null,
        val count: Int = 0,
        val area: String = "0.0"
)
