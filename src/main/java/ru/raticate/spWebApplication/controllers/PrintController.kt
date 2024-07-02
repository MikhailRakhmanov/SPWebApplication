package ru.raticate.spWebApplication.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import ru.raticate.spWebApplication.controllers.api.ApiController
import ru.raticate.spWebApplication.controllers.api.ScanController
import ru.raticate.spWebApplication.controllers.dto.table.Product
import ru.raticate.spWebApplication.util.Pair
import java.text.DecimalFormat

@Controller
class PrintController(private val apiController: ApiController, val scanController: ScanController) {
    @GetMapping("/print")
    fun print(model: Model): String {
        val platform = apiController.currentPlatform;
        return printPlatform(apiController.currentPlatform, model);
    }

    @GetMapping("/print/{id}")
    fun printPlatform(@PathVariable id: Int, model: Model): String {
        val platform = scanController.platform(id);
        val product2CountAndArea = HashMap<Product, Pair<Int, Double>>()
        platform.products?.forEach { product ->
            product2CountAndArea[product] = product2CountAndArea[product]?.let { pair ->
                Pair(pair.key + 1, pair.value + product.sm)
            } ?: Pair(1, product.sm)
        }
        val formatter = DecimalFormat("#.###")
        model.addAttribute("platform", platform)
        model.addAttribute("product2CountAndArea", product2CountAndArea)
        model.addAttribute("count", product2CountAndArea.values.stream().mapToInt { el -> el.key }.sum())
        model.addAttribute("area", formatter.format(product2CountAndArea.values.stream().mapToDouble { el -> el.value }.sum()))

        return "print"
    }
}