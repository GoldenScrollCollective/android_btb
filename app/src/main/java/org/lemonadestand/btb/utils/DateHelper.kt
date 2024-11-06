package com.aisynchronized.helper

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

object DateHelper {
    fun parse(value: String?, format: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"): Date? {
        try {
            val dateFormat = SimpleDateFormat(format)
            return value?.let { dateFormat.parse(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun format(value: Date?, format: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"): String? {
        try {
            val dateFormat = SimpleDateFormat(format)
            return value?.let { dateFormat.format(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun year(date: Date?): Int {
        if (date == null) return 0
        val dateFormat = SimpleDateFormat("yyyy")
        return dateFormat.format(date).toInt()
    }

    fun month(date: Date?): Int {
        if (date == null) return 0
        val dateFormat = SimpleDateFormat("MM")
        return dateFormat.format(date).toInt()
    }

    fun day(date: Date?): Int {
        if (date == null) return 0
        val dateFormat = SimpleDateFormat("dd")
        return dateFormat.format(date).toInt()
    }

    fun prevDay(date: Date?): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DATE, -1)
        return cal.time
    }

    fun nextDay(date: Date?): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DATE, 1)
        return cal.time
    }

    fun isStartOfMonth(date: Date?): Boolean {
        val c = Calendar.getInstance() // this takes current date
        c.time = date
        c[Calendar.DAY_OF_MONTH] = 1
        val start = c.time
        return (year(date) == year(start)
                && month(date) == month(start)
                && day(date) == day(start))
    }

    fun isEndOfMonth(date: Date?): Boolean {
        val c = Calendar.getInstance()
        c.time = date
        c[Calendar.DAY_OF_MONTH] = c.getActualMaximum(Calendar.DAY_OF_MONTH)
        val end = c.time
        return (year(date) == year(end)
                && month(date) == month(end)
                && day(date) == day(end))
    }

    fun isSameDay(date1: Date?, date2: Date?): Boolean {
        if (date1 == null || date2 == null) {
            return false
        }
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val strDate1 = dateFormat.format(date1)
        val strDate2 = dateFormat.format(date2)
        return strDate1 == strDate2
    }
}