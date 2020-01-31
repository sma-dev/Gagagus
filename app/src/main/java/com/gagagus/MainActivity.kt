package com.gagagus

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.Button
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


class MainActivity : AppCompatActivity() {


    private var attachWeb = false
    lateinit var webView: WebView
    lateinit var viewFlipper: ViewFlipper


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_container)

        viewFlipper = findViewById(R.id.container)

        val errorLayout: View = findViewById(R.id.error_fr)
        val webLayout: SwipeRefreshLayout = findViewById(R.id.web_fr)

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

        viewFlipper.displayedChild = 0
    }
}