package cn.mrlong.webviewloadhtml;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout view = findViewById(R.id.main);
        AgentWeb.with(this)
                .setAgentWebParent((LinearLayout) view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator(-1, 3)
                //.setAgentWebWebSettings(getSettings())
                .setWebViewClient(mWebViewClient)
                .setWebChromeClient(mWebChromeClient)
                // .setPermissionInterceptor(mPermissionInterceptor)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                //.setAgentWebUIController(new UIController(getActivity()))
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                //.useMiddlewareWebChrome(getMiddlewareWebChrome())
                //.useMiddlewareWebClient(getMiddlewareWebClient())
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                .interceptUnkownUrl()
                .createAgentWeb()
                .ready()
                .go("http://www.jd.com");

    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //do you  work
            Log.e("WebViewClient===>",view.getUrl());
        }
    };
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //do you work
            Log.e("WebChromeClient===>",view.getUrl());
        }
    };
}
