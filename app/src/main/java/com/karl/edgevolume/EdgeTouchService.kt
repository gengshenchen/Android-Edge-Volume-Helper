package com.karl.edgevolume

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Context
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout
import android.graphics.PixelFormat
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.util.Log

// 核心服务，继承自 AccessibilityService
class EdgeTouchService : AccessibilityService() {

    private val TAG = "EdgeTouchDebug"
    // window
    private lateinit var windowManager: WindowManager
    private var leftEdgeView: FrameLayout? = null
    private var rightEdgeView: FrameLayout? = null

    // audio slide up and down
    private lateinit var audioManager: AudioManager
    private val handler = Handler(Looper.getMainLooper())
    private var isVolumeAdjustEnabled = false
    private val SCROLL_THRESHOLD = 30f
    private var accumulatedScrollY = 0f

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "服務已連接並啟動。")
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        createEdgeViews()
    }

    private fun createEdgeViews() {
        // left side view for touch
        leftEdgeView = FrameLayout(this)
        val layoutParams = WindowManager.LayoutParams(
            50, // 50 pixel width
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

        // right side
        rightEdgeView = FrameLayout(this)
        val rightLayoutParams = WindowManager.LayoutParams(
            50, // 宽度和左侧一样
            WindowManager.LayoutParams.MATCH_PARENT, // 高度也一样
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        rightLayoutParams.gravity = Gravity.END or Gravity.TOP // 这是唯一的区别：END 表示右侧
        setupGestureDetectorForView(rightEdgeView!!)
        windowManager.addView(rightEdgeView, rightLayoutParams)
    }
    // 定義一個「任務」，它的作用是在3秒後將調節模式關閉
    private val disableVolumeAdjustRunnable = Runnable {
        isVolumeAdjustEnabled = false
    }
    // 设置手势检测器
    @SuppressLint("ClickableViewAccessibility")
    private fun setupGestureDetectorForView(view: FrameLayout) {
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {

            // 当检测到双击手势时，会调用这个方法
            override fun onDoubleTap(e: MotionEvent): Boolean {
                Log.d(TAG, "onDoubleTap: 事件觸發！準備進入調節模式...")
                // 1. 立即進入「音量調節模式」
                isVolumeAdjustEnabled = true
                // 2. 顯示音量條
                showVolumePanel()
                // 3. 設置一個3秒的計時器，3秒後自動退出調節模式
                //    (如果計時器已存在，先移除舊的)
                handler.removeCallbacks(disableVolumeAdjustRunnable)
                handler.postDelayed(disableVolumeAdjustRunnable, 3000L) // 3000毫秒 = 3秒
                //4.累加像素距离init
                accumulatedScrollY = 0f

                return true
            }
            // 當檢測到滑動手勢時
            override fun onScroll(
                e1: MotionEvent?, // 起始點事件 (本次用不到)
                e2: MotionEvent, // 當前點事件 (本次用不到)
                distanceX: Float, // 水平滑動距離 (本次用不到)
                distanceY: Float  // 垂直滑動距離 (核心！)
            ): Boolean {
                // 首先，檢查是否處於「音量調節模式」，如果不是，直接忽略滑動
                if (!isVolumeAdjustEnabled) {
                    return false
                }

                // 將本次滑動的垂直距離累加到累加器中
                accumulatedScrollY += distanceY

                // 檢查向上滑動的距離是否達到了閾值
                // 使用 while 迴圈是為了處理快速滑動，一次滑動很長的距離時，可以觸發多次音量調整
                while (accumulatedScrollY >= SCROLL_THRESHOLD) {
                    adjustVolume(AudioManager.ADJUST_RAISE) // 增加音量
                    accumulatedScrollY -= SCROLL_THRESHOLD // 從累加器中減去一個閾值的量，表示已處理
                }

                // 檢查向下滑動的距離是否達到了閾值
                while (accumulatedScrollY <= -SCROLL_THRESHOLD) {
                    adjustVolume(AudioManager.ADJUST_LOWER) // 減小音量
                    accumulatedScrollY += SCROLL_THRESHOLD // 向累加器加回一個閾值的量，表示已處理
                }

                // 關鍵一步：只要用戶在滑動，就不斷重置那個3秒的「退出計時器」
                // 確保用戶可以連續滑動調整，不會中途退出模式
                handler.removeCallbacks(disableVolumeAdjustRunnable)
                handler.postDelayed(disableVolumeAdjustRunnable, 3000L)

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
    // 音量調節方法
    private fun adjustVolume(direction: Int) {
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            direction, // direction 可以是 ADJUST_RAISE 或 ADJUST_LOWER
            AudioManager.FLAG_SHOW_UI // 保持UI顯示
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