package ru.raticate.spWebApplication.controllers.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.raticate.spWebApplication.controllers.dto.Dict
import ru.raticate.spWebApplication.servises.DictService

@RestController
@RequestMapping("/dict_api")
class DictController(val dictService: DictService) {
    @GetMapping("")
    fun diction(): List<Dict> {
        return dictService.getDict()
    }
}