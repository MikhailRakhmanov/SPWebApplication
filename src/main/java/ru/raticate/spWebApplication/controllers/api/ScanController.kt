package ru.raticate.spWebApplication.controllers.api;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.raticate.spWebApplication.controllers.dto.table.PlatformDTO;
import ru.raticate.spWebApplication.controllers.dto.table.Product;
import java.text.DecimalFormat;

@RestController
@RequestMapping("/scan")
class ScanController(@Qualifier("sPJdbcTemplate") val spJdbcTemplate: JdbcTemplate, val apiController: ApiController) {


    @GetMapping("/table")
    fun platform(id: Int? = null): PlatformDTO {
        val products: List<Product>
        val platform: Int = id ?: apiController.currentPlatform
        return run {
            products = spJdbcTemplate.query(
                "select * from V0859_1_c1(?) order by mark",
                DataClassRowMapper(Product::class.java),
                platform
            );
            val count = products.count()
            val area = products.stream().mapToDouble(Product::sm).sum()
            PlatformDTO(
                products = products, platformName = platform,
                count = count, area = area
            );
        }
    }
}
