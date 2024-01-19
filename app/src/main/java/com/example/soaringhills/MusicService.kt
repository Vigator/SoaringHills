package com.example.soaringhills

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder

class MusicService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private val binder = MusicBinder()

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.landof8bitsstephenbennett)
        mediaPlayer.isLooping = true
    }

    inner class MusicBinder : Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }
    }

    fun playMusic(){
        mediaPlayer.start()
    }
    fun pauseMusic(){
        mediaPlayer.pause()
    }

    override fun onBind(intent: Intent): IBinder {
        return MusicBinder()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}