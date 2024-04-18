package ru.raticate.spWebApplication;

import java.time.Duration
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter


class DateConvertor {
    private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    fun dateToDouble(now: LocalDateTime?): Number{
        if (now == null) return 0
        val then = LocalDateTime.of(1900, Month.JANUARY, 1, 0, 0, 0, 0);
        val duration = Duration.between(then, now.plusDays(2));
        return duration.toSeconds().toDouble() / 86400;
    }
    fun numToDate(date: Number?): LocalDateTime? {
        if (date == null) return null
        val duration = Duration.ofSeconds((date.toDouble() * 86400).toLong());
        val then = LocalDateTime.of(1900, Month.JANUARY, 1, 0, 0, 0, 0);
        return then.plus(duration).minusDays(2);
    }
    fun numToStr(date: Number): String? {
        return numToDate(date)?.format(formatter);
    }
}
