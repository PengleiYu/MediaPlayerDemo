package com.example.playerdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.playerdemo.pages.MediaControllerActivity
import com.example.playerdemo.pages.SurfaceActivity
import com.example.playerdemo.pages.VideoActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val itemList = listOf(
        Item("Surface", "简单的SurfaceDemo", SurfaceActivity::class.java)
        ,
        Item("MediaController", "使用MediaController的VideoView", MediaControllerActivity::class.java)
        ,
        Item("CustomerController", "自定义MediaController", VideoActivity::class.java)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val keyTitle = "title"
        val keyDesc = "desc"

        listView.adapter = SimpleAdapter(
            this,
            itemList.map { mapOf(keyTitle to it.title, keyDesc to it.desc) },
            android.R.layout.simple_list_item_2,
            arrayOf(keyTitle, keyDesc),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                startActivity(Intent(this, itemList[position].clz))
            }
    }

    private class Item(
        val title: String,
        val desc: String,
        val clz: Class<out Activity>
    )
}