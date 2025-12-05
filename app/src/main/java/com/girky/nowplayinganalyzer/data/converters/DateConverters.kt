package com.girky.nowplayinganalyzer.data.converters

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * LocalDateTime 用の Room TypeConverter
 */
class DateConverters {
  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  @TypeConverter
  fun fromTimestamp(value: String?): LocalDateTime? {
    return value?.let { LocalDateTime.parse(it, formatter) }
  }

  @TypeConverter
  fun dateToTimestamp(date: LocalDateTime?): String? {
    return date?.format(formatter)
  }
}
