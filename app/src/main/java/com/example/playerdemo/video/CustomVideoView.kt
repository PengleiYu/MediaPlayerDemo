package com.example.playerdemo.video

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.MediaController

class CustomVideoView : SurfaceView, MediaController.MediaPlayerControl {
    companion object {
        private const val STATE_ERROR = -1
        private const val STATE_IDLE = 0
        private const val STATE_PREPARING = 1
        private const val STATE_PREPARED = 2
        private const val STATE_PLAYING = 3
        private const val STATE_PAUSE = 4
        private const val STATE_PLAYBACK_COMPLETED = 5
    }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context, attrs, defStyleAttr, 0
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        holder.addCallback(mSHCallback)
    }

    private var mUri: Uri? = null
    private var mSurfaceHolder: SurfaceHolder? = null
    private var mMediaPlayer: MediaPlayer? = null

    private var mCurrentState = STATE_IDLE
    private var mTargetState = STATE_IDLE

    private var mCanPause = false
    private var mCanSeekBack = false
    private var mCanSeekForward = false

    private var mSeekWhenPrepared = 0

    private var mVideoWidth = 0
    private var mVideoHeight = 0

    private var mSurfaceWidth = 0
    private var mSurfaceHeight = 0

    private var mCurrentBufferPercentage = 0

    private var mSHCallback = object : SurfaceHolder.Callback {
        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            mSurfaceWidth = width
            mSurfaceHeight = height
            val hasValidState = mTargetState == STATE_PLAYING
            val hasValidSize = mVideoWidth == width && mVideoHeight == height
            mMediaPlayer?.let {
                if (hasValidState && hasValidSize) {
                    if (mSeekWhenPrepared > 0) {
                        it.seekTo(mSeekWhenPrepared)
                    }
                    start()
                }
            }
        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            mSurfaceHolder = null
            release(true)
        }

        override fun surfaceCreated(holder: SurfaceHolder?) {
            mSurfaceHolder = holder
            openVideo()
        }
    }
    private val onPreparedListener = MediaPlayer.OnPreparedListener {
        mCurrentState = STATE_PREPARED

        // TODO: 2020/10/13 从播放器中获取元数据
        mCanPause = true
        mCanSeekBack = true
        mCanSeekForward = true

        val seekToPosition = mSeekWhenPrepared
        if (seekToPosition > 0) {
            seekTo(seekToPosition)
        }

        if (mTargetState == STATE_PLAYING) {
            start()
        }
    }
    private val onVideoSizeChangedListener =
        MediaPlayer.OnVideoSizeChangedListener { mp, width, height ->
            mVideoWidth = mp.videoWidth
            mVideoHeight = mp.videoHeight
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                holder.setFixedSize(mVideoWidth, mVideoHeight)
                requestLayout()
            }
        }
    private val onCompletionListener = MediaPlayer.OnCompletionListener { }
    private val onErrorListener = MediaPlayer.OnErrorListener { mp, what, extra -> true }
    private val onInfoListener = MediaPlayer.OnInfoListener { mp, what, extra -> true }
    private val onBufferingUpdateListener = MediaPlayer.OnBufferingUpdateListener { mp, percent ->
        mCurrentBufferPercentage = percent
    }


    private fun release(clearTargetState: Boolean) {
        val player = mMediaPlayer ?: return
        player.reset()
        player.release()
        mMediaPlayer = null
        mCurrentState = STATE_IDLE
        if (clearTargetState) {
            mTargetState = STATE_IDLE
        }
    }

    fun setVideoUri(uri: Uri) {
        mUri = uri
        mSeekWhenPrepared = 0
        openVideo()
        requestLayout()
        invalidate()
    }

    private fun openVideo() {
        val uri = mUri ?: return
        val surfaceHolder = mSurfaceHolder ?: return
        release(false)
        try {
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setOnPreparedListener(onPreparedListener)
            mediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener)
            mediaPlayer.setOnCompletionListener(onCompletionListener)
            mediaPlayer.setOnErrorListener(onErrorListener)
            mediaPlayer.setOnInfoListener(onInfoListener)
            mediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener)

            mediaPlayer.setDisplay(surfaceHolder)
            mediaPlayer.setDataSource(context, uri)
            mediaPlayer.setScreenOnWhilePlaying(true)
            mediaPlayer.prepareAsync()

            mMediaPlayer = mediaPlayer

            mCurrentState = STATE_PREPARING
        } catch (e: Exception) {

        }
    }

    override fun isPlaying(): Boolean {
        return isInPlaybackState() && mMediaPlayer!!.isPlaying
    }

    override fun canSeekForward(): Boolean {
        return mCanSeekForward
    }

    override fun getDuration(): Int {
        if (isInPlaybackState()) {
            return mMediaPlayer!!.duration
        }
        return -1
    }

    override fun pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer!!.isPlaying) {
                mMediaPlayer!!.pause()
                mCurrentState = STATE_PAUSE
            }
        }
        mTargetState = STATE_PAUSE
    }

    override fun getBufferPercentage(): Int {
        return if (mMediaPlayer != null) mCurrentBufferPercentage else 0
    }

    override fun seekTo(pos: Int) {
        if (isInPlaybackState()) {
            mSeekWhenPrepared = 0
            mMediaPlayer!!.seekTo(pos)
        } else {
            mSeekWhenPrepared = pos
        }
    }

    override fun getCurrentPosition(): Int {
        if (isInPlaybackState()) {
            return mMediaPlayer!!.currentPosition
        }
        return -1
    }

    override fun canSeekBackward(): Boolean {
        return mCanSeekBack
    }

    override fun start() {
        if (isInPlaybackState()) {
            mMediaPlayer!!.start()
            mCurrentState = STATE_PLAYING
        }
        mTargetState = STATE_PLAYING
    }

    /**
     * Get the audio session id for the player used by this VideoView. This can be used to
     * apply audio effects to the audio track of a video.
     * @return The audio session, or 0 if there was an error.
     */
    override fun getAudioSessionId(): Int {
        TODO("Not yet implemented")
    }

    override fun canPause(): Boolean {
        return mCanPause
    }

    private fun isInPlaybackState(): Boolean {
        return mMediaPlayer != null
                && mCurrentState != STATE_ERROR
                && mCurrentState != STATE_IDLE
                && mCurrentState != STATE_PREPARING
    }
}