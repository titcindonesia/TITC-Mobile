package com.example.titcglobaleduka

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var splashLayout: FrameLayout
    private lateinit var splashImageView: ImageView
    private var isSplashFinished = false

    // Warna Navy Blue yang 100% identik persis dengan gambar logo TITC (#092056)
    private val splashBgColor = Color.parseColor("#092056")

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Set window & decorView background to exact navy blue
        window.decorView.setBackgroundColor(splashBgColor)

        // Immersive edge-to-edge full screen experience
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        // Root container
        val rootLayout = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(splashBgColor)
        }

        // SwipeRefreshLayout (Tarik ke bawah untuk refresh)
        swipeRefreshLayout = SwipeRefreshLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setColorSchemeColors(
                Color.parseColor("#6431F6"),
                Color.parseColor("#7345F7")
            )
            setOnRefreshListener {
                webView.reload()
            }
            setOnChildScrollUpCallback { _, _ ->
                webView.scrollY > 0
            }
        }

        // Initialize WebView
        webView = WebView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Progress bar for smooth loading indication
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                8
            )
            max = 100
            visibility = View.GONE
        }

        // Configure WebView settings
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            useWideViewPort = true
            loadWithOverviewMode = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            allowFileAccess = true
            builtInZoomControls = false
            displayZoomControls = false
            userAgentString = userAgentString.replace("; wv", "")
        }

        // Splash Screen Container & Full Screen Navy Blue Background
        splashLayout = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(splashBgColor)
        }

        // Splash Logo centered on screen
        val logoSizePx = (240 * resources.displayMetrics.density).toInt()
        splashImageView = ImageView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                logoSizePx,
                logoSizePx
            ).apply {
                gravity = Gravity.CENTER
            }
            setImageResource(R.drawable.splash_logo)
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
            alpha = 0f
            scaleX = 0.85f
            scaleY = 0.85f
        }

        splashLayout.addView(splashImageView)

        // Function to dismiss splash screen smoothly
        fun dismissSplash() {
            if (isSplashFinished) return
            isSplashFinished = true
            splashLayout.animate()
                .alpha(0f)
                .setDuration(450)
                .withEndAction {
                    splashLayout.visibility = View.GONE
                    rootLayout.removeView(splashLayout)
                }
                .start()
        }

        // WebView Client
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false
                view?.loadUrl(url)
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
            }
        }

        // WebChromeClient
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar.progress = newProgress
                if (newProgress >= 100) {
                    progressBar.visibility = View.GONE
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }

        // Back button dispatcher
        onBackPressedDispatcher.addCallback(this) {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                finish()
            }
        }

        // Load the target TITC Portal URL
        val targetUrl = "https://apps.titcglobaleduka.or.id/"
        webView.loadUrl(targetUrl)

        // Assemble view hierarchy
        swipeRefreshLayout.addView(webView)
        rootLayout.addView(swipeRefreshLayout)
        rootLayout.addView(progressBar)
        rootLayout.addView(splashLayout) // Splash is placed on top of everything
        setContentView(rootLayout)

        // Start Intro Entrance Animation (Smooth Fade-in + Zoom-in)
        splashImageView.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(800)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // Timer to hold splash for ~2.2 seconds then smoothly fade out
        Handler(Looper.getMainLooper()).postDelayed({
            dismissSplash()
        }, 2200)
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
