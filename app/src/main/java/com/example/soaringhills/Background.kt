package com.example.soaringhills

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log

class Background(val screenHeight: Int, val layer: Int, val originalBitmap: Bitmap ) {


    private lateinit var list: MutableList<Bitmap>
    private lateinit var scaledBitmap: Bitmap

    // current position of the first image in the list
    private var positionX = 0f


    init {
        scaleBitmap()
        // Every instance of Background has two identical images that rotate off screen
        list = mutableListOf(scaledBitmap, scaledBitmap)
    }

    //Scale image to screen
    private fun scaleBitmap(){
        val targetHeight = screenHeight
        val aspectRatio = originalBitmap.width.toFloat() / originalBitmap.height
        val targetWidth = (targetHeight * aspectRatio).toInt()
        scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)
    }

    //GETTERS
    fun getImages(): MutableList<Bitmap>{
        return list
    }

    fun update(gameSpeed: Float, fps: Long,  canvas: Canvas){
        //move the first image to the left at gameSpeed depending on fps and background layer
        var speed = getSpeed(gameSpeed)
        positionX -= speed/fps

        // if the first image has gone out of bounds, put it to the right of the second image.
        if (positionX+scaledBitmap.width <= 0){
            val temp = list[0]
            list[0] = list[1]
            list[1] = temp
            positionX = 0f
        }
        canvas.drawBitmap(list[0], positionX, 0f, null)
        canvas.drawBitmap(list[1], positionX+list[0].width, 0f, null)
    }

    //The further down the layers you go, the slower the background moves
    private fun getSpeed(gameSpeed: Float): Float{
        var speed: Float = gameSpeed
        for (iteration in 6 downTo layer){
            if (iteration!=6){ // 6th layer (topmost) should move at game speed
                speed *= 0.55f
            }
        }
        return speed
    }
}