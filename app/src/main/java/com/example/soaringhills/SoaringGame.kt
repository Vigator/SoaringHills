package com.example.soaringhills

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View

class SoaringGame(private val playActivity: PlayActivity, context : Context, x: Int, y: Int) : SurfaceView(context), Runnable, View.OnTouchListener {

    // Are we debugging?
    private val DEBUGGING = true

    private var soundPool: SoundPool? = null
    private var jump = 0

    // These objects are needed to do the drawing
    private lateinit var mOurHolder: SurfaceHolder
    private lateinit var mCanvas: Canvas
    private lateinit var mPaint: Paint

    // FPS
    private var mFPS: Long = 0

    // The number of milliseconds in a second
    private val MILLIS_IN_SECOND = 1000

    // Current time in ms
    private val mStartTime = System.currentTimeMillis()
    private var mElapsedTime = mStartTime
    private var mLastDifficultyIncrease = mStartTime
    private var mLastCrowAdded = mStartTime

    // Holds the resolution of the screen
    private var mScreenX: Int = 0
    private var mScreenY: Int = 0

    // Text size
    private var mFontSize: Int = 0
    private var mFontMargin: Int = 0

    // Background layers
    private lateinit var layer1: Background
    private lateinit var layer2: Background
    private lateinit var layer3: Background
    private lateinit var layer4: Background
    private lateinit var layer5: Background
    private lateinit var layer6: Background

    // The game objects
    private lateinit var mPigeon: Pigeon
    private lateinit var mCrowList: MutableList<Crow>

    // The current level
    private var mLevel: Int = 0
    private var mAlive: Boolean = true

    // The current speed of the game
    private var mSpeed : Float = 0f

    // Here is the Thread and two control variables
    private var mGameThread: Thread? = null

    // This volatile variable can be accessed
    // from inside and outside the thread
    @Volatile
    private var mPlaying: Boolean = false

    private var mPaused: Boolean = true

    init {
        setOnTouchListener(this)


        ///////////////////// Initializing SoundPool ////////////////////////

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(10) // Maximum simultaneous sounds
                .setAudioAttributes(audioAttributes)
                .build()
        } else {
            soundPool = SoundPool(2, AudioManager.STREAM_MUSIC, 0)
        }
        // Loading sound files
        jump = soundPool?.load(context, R.raw.bleep, 1) ?: 0

        /////////////////////////////////////////////////////////////////////

        mScreenX = x
        mScreenY = y

        mSpeed = mScreenX/3f

        // Font is 5% (1/20th) of screen width
        mFontSize = mScreenX / 40
        // Font margin is 1/75th of screen width
        mFontMargin = mScreenX / 75

        //import and initialize background
        layer1 = Background(mScreenY, 1, BitmapFactory.decodeResource(resources, R.drawable.layer01))
        layer2 = Background(mScreenY, 2, BitmapFactory.decodeResource(resources, R.drawable.layer02))
        layer3 = Background(mScreenY, 3, BitmapFactory.decodeResource(resources, R.drawable.layer03))
        layer4 = Background(mScreenY, 4, BitmapFactory.decodeResource(resources, R.drawable.layer04))
        layer5 = Background(mScreenY, 5, BitmapFactory.decodeResource(resources, R.drawable.layer05))
        layer6 = Background(mScreenY, 6, BitmapFactory.decodeResource(resources, R.drawable.layer06))

        // import and initialize pidgeon
        mPigeon = Pigeon(mScreenY, mScreenX, mFPS, resources)

        // import and initialize Crows
        mCrowList = mutableListOf<Crow>()
        mCrowList.add(0, Crow(mScreenY, mScreenX, resources))

        // ready for drawing
        mOurHolder = holder
        mPaint = Paint()

        mPaused = false

        // Everything is ready so start the game
        startNewGame()
    }

    private fun startNewGame(){
        // Put the bird back to the starting position and remove other objects

        // Reset the score and the player's chances
        mLevel = 1
        mAlive = true
    }

    override fun run() {
        // mPlaying gives us finer control
        // rather than just relying on the calls to run
        while (mPlaying) {
            // What time is it now at the start of the loop?
            val frameStartTime = System.currentTimeMillis()

            // Provided the game isn't paused
            // call the update method
            if (!mPaused) {
                update()

                // we can see if there have
                // been any collisions
                detectCollisions()
            }

            //  draw the scene.
            draw()

            // How long did this frame/loop take?
            val timeThisFrame = System.currentTimeMillis() - frameStartTime

            // Making sure timeThisFrame is at least 1 millisecond to prevent crash
            if (timeThisFrame > 0) {
                // Store the current frame rate in mFPS
                // ready to pass to the update methods of
                // background and other moving objects next frame/loop
                mFPS = MILLIS_IN_SECOND / timeThisFrame
            }
        }
    }

    private fun update(){

        // Update game timer
        mElapsedTime = (System.currentTimeMillis() - mStartTime) / MILLIS_IN_SECOND


//        TODO - THERE IS A BUG WHERE COLLISION DETECTION WON'T WORK ON FASTER LEVELS I CAN'T FIX
//        Increase speed every 5 seconds
//        var lastDifficultyIncreaseInSeconds = (mLastDifficultyIncrease - mStartTime) / MILLIS_IN_SECOND
//
//        if (mElapsedTime >= 5 && mElapsedTime - lastDifficultyIncreaseInSeconds >= 5){
//            mSpeed *= 1.05f
//           mLastDifficultyIncrease = System.currentTimeMillis()
//        }

        //Add another crow every 10 seconds
        var lastCrowAddedInSeconds = (mLastCrowAdded - mStartTime) / MILLIS_IN_SECOND
        if (mElapsedTime >= 10 && mElapsedTime - lastCrowAddedInSeconds >= 10){
            mCrowList.add(Crow(mScreenY, mScreenX, resources))
            mLastCrowAdded = System.currentTimeMillis()
            mLevel ++
        }


    }

    private fun detectCollisions(){
        for (crow in mCrowList) {
            if (Rect.intersects(mPigeon.getRect(), crow.getRect())) {
                MainActivity.level = mLevel
                MainActivity.time = mElapsedTime
                playActivity.finish()
                // Collision detected between pigeon and crow
                // Handle the collision logic here
            }
        }
    }

    // This method is called by SoaringGame when the player quits the game
    fun pause() {
        // Set mPlaying to false
        // Stopping the thread isn't always instant
        mPlaying = false
        try {
            // Stop the thread
            mGameThread?.join()
        } catch (e: InterruptedException) {
            Log.e("Error:", "joining thread")
        }
    }

    // This method is called when the player starts the game
    fun resume() {
        mPlaying = true
        // Initialize the instance of Thread
        mGameThread = Thread(this)
        // Start the thread
        mGameThread?.start()
    }

      fun draw() {

         if (mOurHolder.surface.isValid){
             // Lock the canvas (graphics memory) ready to draw
             mCanvas = mOurHolder.lockCanvas()


             // Draw Background
             layer1.update(mSpeed, mFPS, mCanvas)
             layer2.update(mSpeed, mFPS, mCanvas)
             layer3.update(mSpeed, mFPS, mCanvas)
             layer4.update(mSpeed, mFPS, mCanvas)
             layer5.update(mSpeed, mFPS, mCanvas)
             layer6.update(mSpeed, mFPS, mCanvas)

             // Choose a color to paint with
             mPaint.color = Color.BLACK

             // Draw the pigeon
             mPigeon.update(mSpeed, mFPS, mCanvas)

             // Draw the crows
             for (crow in mCrowList){
                 crow.update(mSpeed, mFPS, mScreenY, mScreenX, mCanvas)
             }

             // Choose font size
             mPaint.textSize = mFontSize.toFloat()

             // Draw the HUD
             mCanvas.drawText("Time: $mElapsedTime", mFontMargin.toFloat(), mFontSize.toFloat(), mPaint)
             mCanvas.drawText("Level: $mLevel", (mScreenX/2).toFloat(), mFontSize.toFloat(), mPaint)

             if (DEBUGGING) {
                 printDebuggingText()
             }

             // Display the drawing on screen
             mOurHolder.unlockCanvasAndPost(mCanvas)
         }
    }

    private fun printDebuggingText() {
        val debugSize = mFontSize / 2
        val debugStart = 150
        mPaint.textSize = debugSize.toFloat()
        mCanvas.drawText("FPS: $mFPS", 10f, (debugStart + debugSize).toFloat(), mPaint)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mPigeon.jump()
                soundPool?.play(jump, 1.0f, 1.0f, 1, 0, 1.0f) ?: 0
            }
        }
        return true
    }


}