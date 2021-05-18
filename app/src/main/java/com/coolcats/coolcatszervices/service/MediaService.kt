package com.coolcats.coolcatszervices.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.coolcats.coolcatszervices.R
import com.coolcats.coolcatszervices.model.Zong

//Normal Service
class MediaService: Service() {

    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var zongs: List<Zong>

    fun updateZongs(zongs: List<Zong>){
        Log.d("TAG_X", "zongzzzzzz!")
        this.zongs = zongs
        startPlaying()
    }

    private var index = 0

    private fun startPlaying() {
        zongs[index].let {
            //mediaPlayer.setDataSource (this, it.songPath)
            stopSong()
            mediaPlayer = MediaPlayer.create(this, it.songPath)
            mediaPlayer.start()
        }
    }

    fun stopSong(){
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
        mediaPlayer.stop()
    }

    fun next(){
        index = if(index == zongs.size-1)
            0
        else index+1

        startPlaying()
    }

    fun previous(){
        index = if(index == 0)
            zongs.size-1
        else index-1

        startPlaying()
    }

    fun playPauseSong(){
        if(mediaPlayer.isPlaying)
            mediaPlayer.pause()
        else
            mediaPlayer.start()

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

    inner class MediaBinder: Binder() {
        fun getServiceClass(): MediaService {
            return this@MediaService //in java this would be MediaService.this
        }
    }
}