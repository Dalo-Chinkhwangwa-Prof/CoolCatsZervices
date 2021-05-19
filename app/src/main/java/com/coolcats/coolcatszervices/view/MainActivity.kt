package com.coolcats.coolcatszervices.view

import android.content.ComponentName
import android.content.ContentUris
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.coolcats.coolcatszervices.R
import com.coolcats.coolcatszervices.model.Zong
import com.coolcats.coolcatszervices.service.MediaService
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URI

class MainActivity : AppCompatActivity(), MediaService.MediaDelegate {

    private lateinit var normalServiceIntent: Intent
    private lateinit var boundServiceIntent: Intent

    private var mediaService: MediaService? = null

    ///zongs...assume this is from the server or database
    val zongs = mutableListOf<Zong>(
        Zong("Eye of The Tiger", "Survivor", R.raw.eott, "https://upload.wikimedia.org/wikipedia/en/8/8e/Eye_of_the_Tiger_Survivor.jpg"),
        Zong("Desert Trap", "Murray", R.raw.desert_wav, "https://i.ytimg.com/vi/IhcF8rSB_9w/maxresdefault.jpg"),
        Zong("Over The Horizon", "Samsung", R.raw.over_the_horizon, "https://img.global.news.samsung.com/global/wp-content/uploads/2019/02/Over-the-Horizon-2019_thumb728.jpg")
    )

    val serviceCon = object: ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder) {

            mediaService = (p1 as MediaService.MediaBinder).getServiceClass()
            mediaService?.updateZongs(zongs, this@MainActivity)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            //called when an error occurs....
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //normalServiceIntent = Intent(this, MediaService::class.java)
        boundServiceIntent = Intent(this, MediaService::class.java)

        prev_button.setOnClickListener {
            mediaService?.previous()
        }
        next_button.setOnClickListener {
            mediaService?.next()
        }
        play_pause_button.setOnClickListener {
            mediaService?.playPauseSong()?.let { isPlaying ->
                when(isPlaying){
                    true -> play_pause_button.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause))
                    else -> play_pause_button.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play))
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //startService(normalServiceIntent)

        bindService(boundServiceIntent, serviceCon, BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceCon)
        //stopService(normalServiceIntent)
    }

    override fun currentZong(zong: Zong) {
        zong.apply {
            artist_name_textview.text = artistName
            song_title_text.text = title

            Glide.with(this@MainActivity)
                .applyDefaultRequestOptions(RequestOptions().centerCrop())
                .load(songArt)
                .into(imageView)

        }
    }
}