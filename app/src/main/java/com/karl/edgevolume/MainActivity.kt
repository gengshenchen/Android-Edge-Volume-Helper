package com.karl.edgevolume

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


import android.content.Intent
import android.provider.Settings
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 找到布局文件里的那个按钮
        val button = findViewById<Button>(R.id.button_open_settings)

        // 给按钮设置点击事件
        button.setOnClickListener {
            // 创建一个意图，告诉系统我们要跳转到无障碍设置页面
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            // 执行跳转
            startActivity(intent)
        }
    }
}