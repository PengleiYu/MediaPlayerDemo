package com.example.playerdemo.pages

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.annotation.IntegerRes
import androidx.appcompat.app.AppCompatActivity
import com.example.playerdemo.Constants
import com.example.playerdemo.R
import kotlinx.android.synthetic.main.video_main.*
import timber.log.Timber

/**
 * 视频播放页
 *
 * 使用[android.widget.VideoView]实现播放，自定义controller
 */
class CustomControllerActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity";
        private const val SEEK_STEP = 15000
        private const val SEEK_MAX_LEN = 1000

        private const val WHAT_UPDATE_UI = 1
        private const val INTERVAL_UPDATE_UI = 200L
    }

    private val src = Constants.VIDEO_SRC
    private val callback = Handler.Callback {
//        Log.d(TAG, "what=${it.what}")
        when (it.what) {
            WHAT_UPDATE_UI -> {
                val currentPosition = video_player.currentPosition
                val duration = video_player.duration
                val progress = SEEK_MAX_LEN * currentPosition / duration
                tv_current_time.text = getFormatTime(currentPosition)
                tv_total_time.text = getFormatTime(duration)
                progress_timeline.max = SEEK_MAX_LEN
                progress_timeline.progress = progress
                progress_timeline.secondaryProgress = video_player.bufferPercentage * 10

                @IntegerRes
                val playerLevelRes =
                    if (video_player.isPlaying) R.integer.level_media_pause else R.integer.level_media_play
                val playerLevel = resources.getInteger(playerLevelRes)
                btn_play.setImageLevel(playerLevel)

                uiHandler.sendEmptyMessageDelayed(WHAT_UPDATE_UI, INTERVAL_UPDATE_UI)
                true
            }
            else -> false
        }
    }
    private val uiHandler: Handler = Handler(callback)

    private fun getFormatTime(millTime: Int): String {
        val seconds = millTime / 1000
        val ss = seconds % 60
        val mm = seconds / 60
        val hh = seconds / 3600
        return if (hh > 0)
            String.format("%02d:%02d:%02d", hh, mm, ss)
        else
            String.format("%02d:%02d", mm, ss)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_main)

        btn_rew.setOnClickListener(onClickListener)
        btn_play.setOnClickListener(onClickListener)
        btn_ff.setOnClickListener(onClickListener)
    }

    private val onClickListener: View.OnClickListener = View.OnClickListener {
        when (it.id) {
            R.id.btn_play -> {
                if (video_player.isPlaying) pause()
                else play()
            }
            R.id.btn_rew -> {
                if (video_player.canSeekBackward()) {
                    video_player.seekTo(video_player.currentPosition - SEEK_STEP)
                }
            }
            R.id.btn_ff -> {
                if (video_player.canSeekForward()) {
                    video_player.seekTo(video_player.currentPosition + SEEK_STEP)
                }
            }
        }
    }

    private fun play() {
        Timber.tag(TAG).d("play")
        if (!video_player.canPause()) {
            video_player.setVideoURI(Uri.parse(src))
        }
        video_player.start()
        startUpdateUi()
    }

    private fun pause() {
        Timber.tag(TAG).d("pause")
        video_player.pause()
    }

    private fun stop() {
        Timber.tag(TAG).d("stop")
        video_player.stopPlayback()
        stopUpdateUi()
    }

    /**
     * 仅在stop时停止UI
     * pause时不停止，因为可能有seek
     */
    private fun stopUpdateUi() {
        uiHandler.postDelayed({ uiHandler.removeMessages(WHAT_UPDATE_UI) }, INTERVAL_UPDATE_UI * 2)
    }

    private fun startUpdateUi() {
        uiHandler.sendEmptyMessage(WHAT_UPDATE_UI)
    }

    /**
     * todo 有问题，切到后台再切回来从头播放了;
     * 原因是切到后台surface销毁了(销毁原因待查)，在回调中释放了player，再次重建player后重头播放了
     */
    override fun onPause() {
        super.onPause()
        pause()
    }

    override fun onResume() {
        super.onResume()
        play()
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
    }
}