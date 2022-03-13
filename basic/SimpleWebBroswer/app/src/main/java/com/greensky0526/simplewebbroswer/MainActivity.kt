package com.greensky0526.simplewebbroswer

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    companion object{
        private const val DEFAULT_URL = "https://www.google.com"
    }

    private val webView: WebView by lazy{
        findViewById(R.id.web_view)
    }
    private val addressBar: EditText by lazy {
        findViewById(R.id.address_bar)
    }
    private val homeBtn: ImageButton by lazy{
        findViewById(R.id.go_home_btn)
    }
    private val backBtn: ImageButton by lazy{
        findViewById(R.id.go_back_btn)
    }
    private val forwardBtn: ImageButton by lazy{
        findViewById(R.id.go_forward_btn)
    }
    private val refreshLayout: SwipeRefreshLayout by lazy {
        findViewById(R.id.refresh_layout)
    }
    private val progressBar: ContentLoadingProgressBar by lazy {
        findViewById(R.id.profress_bar)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    override fun onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews(){
        webView.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            loadUrl(DEFAULT_URL)
        }
    }

    private fun bindViews(){
        addressBar.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                val loadingUrl = v.text.toString()

                if(URLUtil.isNetworkUrl(loadingUrl)){
                    webView.loadUrl(loadingUrl)
                }else {
                    webView.loadUrl("https://$loadingUrl")
                }
            }

            return@setOnEditorActionListener false
        }

        homeBtn.setOnClickListener {
            webView.loadUrl(DEFAULT_URL)
        }

        backBtn.setOnClickListener {
            webView.goBack()
        }

        forwardBtn.setOnClickListener {
            webView.goForward()
        }

        refreshLayout.setOnRefreshListener {
            webView.reload()
        }
    }
    inner class WebChromeClient: android.webkit.WebChromeClient(){
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            progressBar.progress = newProgress
        }
    }

    inner class WebViewClient: android.webkit.WebViewClient(){

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar.show()
        }
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            refreshLayout.isRefreshing = false
            progressBar.hide()
            //이동가능여부를 버튼에 적용
            backBtn.isEnabled = webView.canGoBack()
            forwardBtn.isEnabled = webView.canGoForward()
            //실제주소로 주소창에 매칭
            addressBar.setText(url)
        }
    }
 }