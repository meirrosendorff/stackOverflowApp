package com.example.stackoverflow.utils.implementations

import com.example.stackoverflow.utils.interfaces.DateUtils
import java.util.Date

class DateUtilsImplementation : DateUtils {
    override fun createDate(timestamp: Long): Date {
        return Date(timestamp)
    }

    override fun getCurrentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}
