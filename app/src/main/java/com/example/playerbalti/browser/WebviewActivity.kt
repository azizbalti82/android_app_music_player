package com.example.playerbalti.browser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.playerbalti.databinding.ActivityWebviewBinding


class webviewActivity: AppCompatActivity() {
    lateinit var b: ActivityWebviewBinding
    lateinit var webView: WebView
    lateinit var progressBar:ProgressBar

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(b.root)

        val title = intent.getStringExtra("title")
        val url = intent.getStringExtra("url")

        // Get a reference to the WebView in your layout
        webView = b.container
        progressBar = b.bar

        // Configure WebView settings
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true // Enable JavaScript (optional)
        webSettings.domStorageEnabled = true
        webSettings.loadsImagesAutomatically = true
        webSettings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false


        // Set up WebViewClient to handle links within the WebView
        webView.webViewClient = WebViewClient()


        // Set up WebChromeClient to track progress updates
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                // Update the progress bar with the loading progress
                progressBar.progress = newProgress
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                // Page loading has started
                b.bar.progress = 0
                b.bar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                // Page loading has finished
                b.bar.visibility = View.INVISIBLE
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                // Override URL loading if needed
                return false
            }
        }

        // Load a URL into the WebView
        webView.loadUrl("$url$title/")

        b.cancelButton.setOnClickListener{
            this.finish()
        }

        b.reload.setOnClickListener{
            // Reload the current page
            webView.reload()
        }

        b.previous.setOnClickListener {
            webView.goBack()
        }

        b.next.setOnClickListener {
            webView.goForward()
            Log.d("browser", webView.url.toString())
        }
    }

}