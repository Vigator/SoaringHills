package com.example.soaringhills

import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.SimpleAdapter.ViewBinder
import com.example.soaringhills.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    //////////////////// Background Music ///////////////////
    private var musicService: MusicService? = null

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            val binder = service as? MusicService.MusicBinder
            musicService = binder?.getService()
            Log.e(ContentValues.TAG, "Service connected")
            musicService?.playMusic()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e(ContentValues.TAG, "Service disconnected")
        }

    }

    /////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Connects to MusicService
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        //

        binding.btnAbout.setOnClickListener{
            Intent(this, AboutActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.btnPlay.setOnClickListener{
            Intent(this, PlayActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.swMusic.setOnClickListener {
            if (binding.swMusic.isChecked){
                musicService?.playMusic()
            }
            else if (!binding.swMusic.isChecked){
                musicService?.pauseMusic()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (time.toInt() != 0){
            var tempLevel = level.toString()
            var tempTime = time.toString()
            binding.tvLevel.setText("You reached level $level")
            binding.tvTime.setText("Time: $time seconds")
        }
    }

    companion object {
        var level: Int = 0
        var time: Long = 0
    }
}