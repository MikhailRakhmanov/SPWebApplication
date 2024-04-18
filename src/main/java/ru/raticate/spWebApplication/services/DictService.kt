package ru.raticate.spWebApplication.servises

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import ru.raticate.spWebApplication.controllers.dto.Dict
@Service
class DictService(@Qualifier("mainJdbcTemplate") val mainJdbcTemplate: JdbcTemplate) {
    fun getDict(): List<Dict>{
        return mainJdbcTemplate.query("select * from v0792(1,100) order by DOT2",  DataClassRowMapper(Dict::class.java))
    }
}