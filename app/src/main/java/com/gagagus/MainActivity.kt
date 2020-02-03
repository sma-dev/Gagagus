package com.gagagus

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.webkit.*
import android.widget.Button
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.main_container.*


class MainActivity : AppCompatActivity() {


    private var attachWeb = false
    lateinit var webView: WebView
    lateinit var viewFlipper: ViewFlipper


    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        fullscreen_content.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
    }

    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */


    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        mVisible = false

        //window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        window.statusBarColor =
            ContextCompat.getColor(applicationContext, R.color.colorAccent)
        fullscreen_content.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mVisible = true





        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_container)

        viewFlipper = findViewById(R.id.container)

        val errorLayout: View = findViewById(R.id.error_fr)
        val webLayout: SwipeRefreshLayout = findViewById(R.id.web_fr)

        webLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark)


        val btnReload: Button = errorLayout.findViewById(R.id.btn_reload)

        webView = webLayout.findViewById(R.id.webv)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadsImagesAutomatically = true
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webView.settings.setAppCacheEnabled(true)


        webView.webViewClient = object : WebViewClient() {


            override fun onPageFinished(view: WebView?, url: String?) {

                if (attachWeb) {
                    viewFlipper.displayedChild = 1
                    show()
                    attachWeb = false
                }
                webLayout.isRefreshing = false
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                attachWeb = false
                webView.visibility = View.INVISIBLE
                hide()
                viewFlipper.displayedChild = 2
            }

            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                Toast.makeText(
                    applicationContext,
                    errorResponse?.statusCode.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        btnReload.setOnClickListener {
            webThrowLoading(true)
        }

        webLayout.setOnRefreshListener {
            webView.reload()
        }

        webThrowLoading(false)
    }

    override fun onBackPressed() {
        if (webView.canGoBack())
            webView.goBack()
        else super.onBackPressed()
    }

    private fun webThrowLoading(reload: Boolean) {
        attachWeb = true

        webView.visibility = View.VISIBLE

        if (reload)
            webView.reload()
        else
            webView.loadUrl(resources.getString(R.string.url))

        hide()
        viewFlipper.displayedChild = 0
    }
}