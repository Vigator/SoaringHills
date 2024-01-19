package com.example.soaringhills

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log

class Pigeon (screenHeight : Int, screenWidth : Int, fps: Long, resources : Resources, ){
    lateinit var walkList: MutableList<Bitmap>
    //lateinit var jumpList: MutableList<Bitmap>
    private lateinit var flyList: MutableList<Bitmap>
    private var screenY = screenHeight
    private var lastAnimationTime = System.currentTimeMillis()
    private var currentAnimationFrameIndex = 0
    private var isFlying = true
    private var rect : Rect
    private val gravity = screenHeight/25f
    private var velocityY = 0f

    init {

        // import and scale PNGs for animation
        flyList = mutableListOf<Bitmap>()
        flyList.add(0, scaleBitmap(BitmapFactory.decodeResource(resources, R.drawable.pfly5), screenHeight))
        flyList.add(1, scaleBitmap(BitmapFactory.decodeResource(resources, R.drawable.pfly4), screenHeight))
        flyList.add(2, scaleBitmap(BitmapFactory.decodeResource(resources, R.drawable.pfly3), screenHeight))
        flyList.add(3, scaleBitmap(BitmapFactory.decodeResource(resources, R.drawable.pfly2), screenHeight))
        flyList.add(4, scaleBitmap(BitmapFactory.decodeResource(resources, R.drawable.pfly1), screenHeight))

        // set starting position
        var startX = screenWidth/4
        var startY = screenHeight/2
        rect = Rect(
            startX,
            startY,
            startX + flyList[0].width,
            startY + flyList[0].height
        )
    }

    private fun scaleBitmap(originalBitmap : Bitmap, screenHeight: Int) : Bitmap{
        val scaledBitmap : Bitmap
        val aspectRatio = originalBitmap.width.toFloat() / originalBitmap.height
        val targetWidth = (screenHeight * aspectRatio).toInt()/17
        scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, screenHeight/17, true)
        return scaledBitmap
    }

    fun update(gameSpeed: Float, fps: Long,  canvas: Canvas){

        /////////// Animation /////////

        var tempAnimationList : MutableList<Bitmap>
        var tempAnimationIndexSize: Int

        //Checks if animation should change to fly or walk
        //And calculates the number of Bitmaps (frames) in the list (TODO)
        if (isFlying){
            tempAnimationList = flyList
        }
        else{
            tempAnimationList = walkList
        }
        tempAnimationIndexSize = tempAnimationList.size

        // Checks if animation swap should occur (set to once every 150 milliseconds)
        var now = System.currentTimeMillis()


        // moves to next frame after 250ms
        if (now - lastAnimationTime > 50){
            currentAnimationFrameIndex ++
            // sets current animation index to 0 if end is reached
            if (currentAnimationFrameIndex > tempAnimationIndexSize - 1){
                currentAnimationFrameIndex = 0
            }
            now = System.currentTimeMillis()
            lastAnimationTime = now
        }

        /////// Movement /////////
        move(fps, screenY)


        canvas.drawBitmap(
            tempAnimationList[currentAnimationFrameIndex],
            rect.left.toFloat(),
            rect.top.toFloat(),
            null)
    }

    private fun move(fps: Long, screenHeight: Int){

        // Gravity simulation
        velocityY += gravity / fps
        rect.top += velocityY.toInt()
        rect.bottom += velocityY.toInt()

        // Ensure that the top does not go over 0
        if (rect.top < 0) {
            rect.top = 0
            velocityY = 0f
        }

        // Ensure that the bottom does not go beyond screen height
        if(rect.bottom > screenHeight){
            rect.bottom = screenHeight
            rect.top = rect.bottom - flyList[0].height
            velocityY = 0f
        }

        Log.e("Screen height:", "$screenHeight")
        Log.e("bottom rect:", "${rect.bottom}")
        Log.e("top rect:", "${rect.top}")


    }

    fun jump(){
        velocityY = -(screenY/35f)
    }

    // GETTERS
    fun getRect() : Rect{
        return rect
    }
}