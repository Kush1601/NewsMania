package eu.tutorials.newsmania

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar

class DetailActivity : AppCompatActivity() {
    lateinit var detailWebView : WebView
    lateinit var ProgressBar  : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val url = intent.getStringExtra("URL")
        if (url != null){
            detailWebView = findViewById<WebView>(R.id.detailWebView)
            ProgressBar  = findViewById<ProgressBar>(R.id.progressBar)
            detailWebView.settings.javaScriptEnabled = true
            detailWebView.webViewClient = object: WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    ProgressBar.visibility = View.GONE
                    detailWebView.visibility = View.VISIBLE
                }
            }
            detailWebView.loadUrl(url)
        }
    }
}
