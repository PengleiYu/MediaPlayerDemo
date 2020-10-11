package com.example.playerdemo.pages

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.example.playerdemo.Constants
import com.example.playerdemo.R
import kotlinx.android.synthetic.main.activity_media_controller.*

class MediaControllerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_controller)

        initPlayer()
        playVideo()
    }

    private fun initPlayer() {
        val mediaController = MediaController(this)
        video_player.setMediaController(mediaController)
        //        mediaController.setMediaPlayer(video_player)
    }

    private fun playVideo() {
        video_player.setVideoURI(Uri.parse(Constants.VIDEO_SRC))
        video_player.start()
    }
}