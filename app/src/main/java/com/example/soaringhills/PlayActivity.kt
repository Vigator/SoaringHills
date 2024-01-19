package com.example.soaringhills

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.view.Window
import android.os.Bundle
import android.view.WindowManager

class PlayActivity : Activity() {

    private lateinit var mSoaringGame : SoaringGame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().setFlags(WindowManager.LayoutParams.
        FLAG_FULLSCREEN,WindowManager.
        LayoutParams.FLAG_FULLSCREEN)

        val display = windowManager.defaultDisplay
        var size = Point()
        display.getSize(size)

        mSoaringGame = SoaringGame(this, this, size.x, size.y)
        setContentView(mSoaringGame)
    }

    override fun onResume() {
        super.onResume()
        //more later
        mSoaringGame.resume()
    }

    override fun onPause() {
        super.onPause()
        //more later
        mSoaringGame.pause()
    }

}