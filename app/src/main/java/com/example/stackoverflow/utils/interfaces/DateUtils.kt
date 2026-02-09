package com.example.stackoverflow.utils.interfaces

import java.util.Date

interface DateUtils {
    fun createDate(timestamp: Long): Date
    fun getCurrentTimeMillis(): Long
}
