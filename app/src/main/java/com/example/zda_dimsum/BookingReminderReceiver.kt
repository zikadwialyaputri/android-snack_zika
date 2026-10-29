
package com.example.zda_dimsum

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class BookingReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val namaLapangan =
            intent.getStringExtra("NAMA_LAPANGAN") ?: "GOR WIRA"

        val jamBooking =
            intent.getStringExtra("JAM_BOOKING") ?: "-"

        val channelId = "booking_reminder_channel"

        // Membuat channel notifikasi untuk Android 8.0 ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                channelId,
                "Pengingat Booking Lapangin",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description =
                    "Notifikasi pengingat jadwal booking lapangan"
            }

            val notificationManager =
                context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

        // Membuat notifikasi booking
        val notification = NotificationCompat.Builder(
            context,
            channelId
        )
            .setSmallIcon(R.drawable.ic_booking)
            .setContentTitle("Pengingat Booking Lapangin 🏸")
            .setContentText(
                "Booking kamu di $namaLapangan pukul $jamBooking WIB. Jangan sampai terlambat!"
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "Booking kamu di $namaLapangan pukul $jamBooking WIB. " +
                                "Jangan sampai terlambat!"
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Menampilkan notifikasi dan mencatat error jika gagal
        try {

            NotificationManagerCompat
                .from(context)
                .notify(1001, notification)

            Log.d(
                "BookingReminder",
                "Notifikasi berhasil dikirim"
            )

        } catch (e: SecurityException) {

            Log.e(
                "BookingReminder",
                "Izin notifikasi belum diberikan",
                e
            )
        }
    }
}
