package com.example.soaringhills

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import kotlin.random.Random

class Crow (screenHeight : Int, screenWidth : Int, resources : Resources, ){
    lateinit var walkList: MutableList<Bitmap>
    //lateinit var jumpList: MutableList<Bitmap>
    private lateinit var flyList: MutableList<Bitmap>
    private var lastAnimationTime = System.currentTimeMillis()
    private var currentAnimationFrameIndex = 0
    private var isFlying = true
    private lateinit var rect : Rect

    init {

        // import and scale PNGs for animation
        flyList = mutableListOf<Bitmap>()
        flyList.add(0, scaleBitmap(BitmapFactory.decodeResource(resources, R.drawable.cfly1), screenHeight))
        flyList.add(1, scaleBitmap(BitmapFactory.decodeResource(resources, R.drawable.cfly2), screenHeight))
        flyList.add(2, scaleBitmap(BitmapFactory.decodeResource(resources, R.drawable.cfly3), screenHeight))
        flyList.add(3, scaleBitmap(BitmapFactory.decodeResource(resources, R.drawable.cfly4), screenHeight))
        flyList.add(4, scaleBitmap(BitmapFactory.decodeResource(resources, R.drawable.cfly5), screenHeight))
        flyList.add(5, scaleBitmap(BitmapFactory.decodeResource(resources, R.drawable.cfly6), screenHeight))

        setRect(screenHeight, screenWidth)
    }

    private fun scaleBitmap(originalBitmap : Bitmap, screenHeight: Int) : Bitmap {
        val scaledBitmap : Bitmap
        val aspectRatio = originalBitmap.width.toFloat() / originalBitmap.height
        val targetWidth = (screenHeight * aspectRatio).toInt()/11
        scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, screenHeight/11, true)
        return scaledBitmap
    }

    fun setRect(screenHeight : Int, screenWidth : Int){

        // set starting position (randomized on both axis, at least one screen behind)
        var startX = (screenWidth .. screenWidth * 2).random()
        var startY = (0 .. screenHeight-flyList[0].height).random()

        flyList[0].height
        rect = Rect(
            startX,
            startY,
            startX + flyList[0].width,
            startY + flyList[0].height
        )
    }

    fun update(gameSpeed: Float, fps: Long, screenHeight: Int, screenWidth: Int,  canvas: Canvas){

        //////////// ANIMATION ///////////

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


        // moves to next animation frame after 50ms
        if (now - lastAnimationTime > 50){
            currentAnimationFrameIndex ++
            // sets current animation index to 0 if end is reached
            if (currentAnimationFrameIndex > tempAnimationIndexSize - 1){
                currentAnimationFrameIndex = 0
            }
            lastAnimationTime = now
        }

        ///////////// Movement ///////////////

        rect.left -= ((gameSpeed / fps)).toInt()
        rect.right -= ((gameSpeed / fps)).toInt()

        // Resets position if outside of screen (to the right)
        if (rect.left < 0 - (screenWidth + flyList[0].width)){
            setRect(screenHeight, screenWidth)
        }


        ///////////// Drawing ////////////////

        canvas.drawBitmap(
            tempAnimationList[currentAnimationFrameIndex],
            rect.left.toFloat(),
            rect.top.toFloat(),
            null)
    }

    // GETTERS
    fun getRect() : Rect {
        return rect
    }


}
