
package com.example.zda_dimsum

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class BookingReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val namaLapangan = intent.getStringExtra("NAMA_LAPANGAN")
            ?: "Lapangan"

        val jamBooking = intent.getStringExtra("JAM_BOOKING")
            ?: "-"

        val channelId = "booking_reminder"

        val notificationManager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Pengingat Booking",
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.description = "Notifikasi pengingat jadwal booking lapangan"

            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(
            context,
            channelId
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Pengingat Booking Lapangan")
            .setContentText(
                "Booking $namaLapangan dimulai pukul $jamBooking"
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "Jangan lupa! Booking $namaLapangan kamu dimulai pukul $jamBooking."
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }
}
