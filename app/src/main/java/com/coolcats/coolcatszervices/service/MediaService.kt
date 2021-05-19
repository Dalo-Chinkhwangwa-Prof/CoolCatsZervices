package com.coolcats.coolcatszervices.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.coolcats.coolcatszervices.R
import com.coolcats.coolcatszervices.model.Zong

//Normal Service
class MediaService : Service() {

    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var zongs: List<Zong>

    private lateinit var mediaDelegate: MediaDelegate

    interface MediaDelegate {
        fun currentZong(zong: Zong)
        // fun currentZong(zongId: Int)
    }

    fun updateZongs(zongs: List<Zong>, mediaDelegate: MediaDelegate) {
        this.mediaDelegate = mediaDelegate
        this.zongs = zongs
        startPlaying()
    }

    private var index = 0

    private fun startPlaying() {
        zongs[index].let {
            //mediaPlayer.setDataSource (this, it.songPath)
            mediaDelegate.currentZong(it)
            stopSong()
            mediaPlayer = MediaPlayer.create(this, it.songPath)
            mediaPlayer.start()


            val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notifManager.createNotificationChannel(
                    NotificationChannel(
                        "ZONG_ID",
                        packageName,
                        NotificationManager.IMPORTANCE_LOW
                    )
                )
            }
            val notification: Notification = NotificationCompat.Builder(this, "ZONG_ID")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentTitle(it.title)
                .setContentText(it.artistName)
                .setSmallIcon(R.drawable.ic_play)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setShowWhen(true)
                .build()

            startForeground(7777, notification)

            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(7777, notification)
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d("TAG_X", "onTaskRemoved...")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

    fun stopSong() {
        mediaPlayer.stop()
    }

    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer.create(this, R.raw.desert_wav)

//        mediaPlayer.
        Log.d("TAG_X", "onCreate Service")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TAG_X", "onDestroy Service")
        //mediaPlayer.stop()
    }

    fun next() {
        index = if (index == zongs.size - 1)
            0
        else index + 1

        startPlaying()
    }

    fun previous() {
        index = if (index == 0)
            zongs.size - 1
        else index - 1

        startPlaying()
    }

    fun playPauseSong(): Boolean {
        return when (mediaPlayer.isPlaying) {
            true -> {
                mediaPlayer.pause()
                false
            }
            else -> {
                mediaPlayer.start()
                true
            }
        }

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TAG_X", "onStartCommand Service")
        mediaPlayer.start()
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onBind(p0: Intent?): IBinder {
        Log.d("TAG_X", "onBind...")
        return MediaBinder() //Implementing a bound service
    }

    inner class MediaBinder : Binder() {
        fun getServiceClass(): MediaService {
            return this@MediaService //in java this would be MediaService.this
        }
    }
}