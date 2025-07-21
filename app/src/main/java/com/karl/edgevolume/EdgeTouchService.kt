package com.karl.edgevolume

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout
import android.graphics.PixelFormat
import android.media.AudioManager
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent

// 我们的核心服务，继承自 AccessibilityService
class EdgeTouchService : AccessibilityService() {

    private lateinit var windowManager: WindowManager
    private var leftEdgeView: FrameLayout? = null
    private var rightEdgeView: FrameLayout? = null
    // 当服务成功连接后，系统会调用这个方法
    override fun onServiceConnected() {
        super.onServiceConnected()
        // 获取窗口管理器
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        // 创建屏幕边缘的触摸区域
        createEdgeViews()
    }

    private fun createEdgeViews() {
        // 创建一个视图作为我们的触摸区域
        leftEdgeView = FrameLayout(this)

        // 设置这个视图的参数，这是最关键的部分
        val layoutParams = WindowManager.LayoutParams(
            50, // 触摸区域的宽度（厚度），50像素
            WindowManager.LayoutParams.MATCH_PARENT, // 高度占满全屏
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, // 悬浮窗类型，必须是这个才能在任何界面上显示
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // 让它不能获取焦点，这样就不会影响你操作其他应用
            PixelFormat.TRANSLUCENT // 设置为透明
        )

        // 将这个触摸区域放置在屏幕左侧
        layoutParams.gravity = Gravity.START or Gravity.TOP

        // 为这个视图设置手势监听
        setupGestureDetectorForView(leftEdgeView!!)

        // 将我们创建的视图添加到屏幕上
        windowManager.addView(leftEdgeView, layoutParams)
        // --- 新增：创建右侧边缘视图 ---
        rightEdgeView = FrameLayout(this)
        val rightLayoutParams = WindowManager.LayoutParams(
            50, // 宽度和左侧一样
            WindowManager.LayoutParams.MATCH_PARENT, // 高度也一样
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        rightLayoutParams.gravity = Gravity.END or Gravity.TOP // 这是唯一的区别：END 表示右侧
        setupGestureDetectorForView(rightEdgeView!!) // 也给右侧视图设置手势监听
        windowManager.addView(rightEdgeView, rightLayoutParams)
    }

    // 设置手势检测器
    private fun setupGestureDetectorForView(view: FrameLayout) {
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            // 当检测到双击手势时，会调用这个方法
            override fun onDoubleTap(e: MotionEvent): Boolean {
                // 调用显示音量条的方法
                showVolumePanel()
                return true
            }
        })

        // 将触摸事件交由手势检测器处理
        view.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            // 返回 true 表示我们已经处理了这次触摸，不要再传递给其他应用
            true
        }
    }

    // 显示系统音量条的方法
    private fun showVolumePanel() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        // 这行代码的意思是：调整音乐音量，调整幅度为0（即不变），并强制显示UI
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_SAME,
            AudioManager.FLAG_SHOW_UI
        )
    }

    // 当服务被中断时调用
    override fun onInterrupt() {}

    // 当无障碍事件发生时调用（我们用不到，留空即可）
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    // 当服务被销毁时，移除视图，防止内存泄漏
    override fun onDestroy() {
        super.onDestroy()
        leftEdgeView?.let { windowManager.removeView(it) }
        rightEdgeView?.let { windowManager.removeView(it) } // 新增：移除右侧视图
    }
}