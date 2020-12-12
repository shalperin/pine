package com.samhalperin.android.pine.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Entity
data class Timer(
   @PrimaryKey
   @ColumnInfo(name="datetime") var dateTime: LocalDateTime
) {


}

fun Timer.timeLeft(now: LocalDateTime = LocalDateTime.now()):String {
   val millis = Duration.between(now, dateTime).toMillis()

   return String.format("%02dh %02dm",
      TimeUnit.MILLISECONDS.toHours(millis),
      TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))
   )
}

fun Timer.isInPast():Boolean {
   return dateTime.isBefore( LocalDateTime.now())
}