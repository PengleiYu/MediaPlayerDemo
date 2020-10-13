package com.example.playerdemo.pages

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.example.playerdemo.Constants
import com.example.playerdemo.R
import kotlinx.android.synthetic.main.activity_custom_player.*

class CustomPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_player)

        val mediaController = MediaController(this)
        video_player.mediaController = mediaController

        video_player.setVideoUri(Uri.parse(Constants.VIDEO_SRC))
        video_player.start()
    }
}