package com.gagagus

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils.loadAnimation
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var root: ViewGroup
    private var init = true
    lateinit var webView: WebView


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_fragment)
        init = true
        root = findViewById(R.id.main_bg)

        val loadingLayout = layoutInflater.inflate(R.layout.splash_fragment, root, false)
        val webLayout = layoutInflater.inflate(R.layout.webview_fragment, root, false)

        webView = webLayout.findViewById(R.id.webv)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                if (init) {
                    setRootContent(
                        loadingLayout,
                        webLayout,
                        R.anim.fade_in,
                        R.anim.fade_out,
                        0,
                        1500
                    )
                    init = false
                }
            }
        }

        webView.loadUrl(resources.getString(R.string.url))
        setRootContent(null, loadingLayout, R.anim.fade_in, 0, 500, 0)
    }

    override fun onBackPressed() {
        if (webView.canGoBack())
            webView.goBack()
        else super.onBackPressed()
    }

    private fun setRootContent(
        old: View?,
        new: View,
        inAnim: Int,
        outAnim: Int,
        inDuration: Long,
        outDuration: Long
    ) {

        if (old != null) {
            playAnim(old, this, outAnim, outDuration)
            val idx = root.indexOfChild(old)
            root.removeView(old)
            root.addView(new, idx)
        } else {
            root.addView(new)
        }
        playAnim(new, this, inAnim, inDuration)
    }

    private fun playAnim(view: View, context: Context, animationid: Int, duration: Long) {
        val animation = loadAnimation(context, animationid)
        animation.duration = duration
        view.startAnimation(animation)
    }

}
