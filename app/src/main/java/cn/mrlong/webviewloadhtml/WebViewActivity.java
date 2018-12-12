package cn.mrlong.webviewloadhtml;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.ProgressBar;

public class WebViewActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView = findViewById(R.id.wv);
        progressBar = findViewById(R.id.progressbar);
        WebSettings webSettings = webView.getSettings();
        Log.e("===>",webSettings.getUserAgentString());

        // webview启用javascript支持 用于访问页面中的javascript
        webSettings.setJavaScriptEnabled(true);
        //设置WebView缓存模式 默认断网情况下不缓存
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //支持定位
        webSettings.setGeolocationEnabled(true);
        // 页面通过`<meta name="viewport" ... />`自适应手机屏幕
        webSettings.setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 5.0以上允许加载http和https混合的页面(5.0以下默认允许，5.0+默认禁止)
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.loadUrl("http://www.jd.com");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
            // 页面(url)开始加载
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

            // 页面(url)完成加载
            public void onPageFinished(WebView view, String url) {
            }

            // 将要加载资源(url)
            public void onLoadResource(WebView view, String url) {
            }
        });
        webView.requestFocus();
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setProgress(newProgress);
                }
                Log.e("===>", newProgress + "");
            }
            // 接收文档标题
            public void onReceivedTitle(WebView view, String title) {
            }

            // 接收图标(favicon)
            public void onReceivedIcon(WebView view, Bitmap icon) {
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.setWebViewClient(null);
            webView.setWebChromeClient(null);
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }

    }
}
