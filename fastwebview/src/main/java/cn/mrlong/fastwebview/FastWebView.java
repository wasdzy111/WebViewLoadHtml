package cn.mrlong.fastwebview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


public final class FastWebView {
    private static final String TAG = "FastWebView";
    //容器
    private LinearLayout parentView;
    //指示器颜色
    private int indicatiorColor = Color.RED;
    //指示器高度
    private int indicatorHight;
    //是否显示指示器
    private boolean horizontalIndicatior = false;
    private Context mContext;
    private WebView webView;
    //是否支持js调用
    private boolean javaScriptEnabled = false;
    //水平指示器
    private ProgressBar progressBar;
    //背景颜色
    private int backgroundColor = Color.WHITE;
    //设置是否启用CacheMode 默认启用
    private int cacheMode = WebSettings.LOAD_NO_CACHE;
    //WebViewClient主要帮助WebView处理各种通知、请求事件的
    private WebViewClient webViewClient;
    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient;
    //监听下载
    private DownloadListener downloadListener;
    //请求文件跳转code
    public static final int FILE_CHOOSER_RESULT_CODE = 0x9999;
    //文件返回路径
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;

    public WebView getWebView() {
        return webView;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    protected FastWebView(Context applicationContext) {
        this.mContext = applicationContext;
    }

    public FastWebView setCacheMode(int WebSettings_cacheMode) {
        this.cacheMode = WebSettings_cacheMode;
        return this;
    }

    //初始化
    public static FastWebView builder(@NonNull Context applicationContext) {
        if (null == applicationContext) {
            throw new NullPointerException("初始化Context不能为空...");
        }
        return new FastWebView(applicationContext);
    }

    //销毁
    public void onDestroy() {
        if (null != webView) {
            webView.setWebViewClient(null);
            webView.setWebChromeClient(null);
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
    }

    public void onPause() {
        if (null != webView) {
            webView.onPause();
            try {
                webView.pauseTimers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onResume() {
        if (null != webView) {
            webView.onResume();
            try {
                webView.resumeTimers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //设置显示父容器
    public FastWebView setParentView(@NonNull LinearLayout parentView, @Nullable FrameLayout.LayoutParams params) {
        //设置垂直布局
        parentView.setOrientation(LinearLayout.VERTICAL);
        //设置布局撑满
        if (null == params) {
            params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
        }
        parentView.setLayoutParams(params);
        this.parentView = parentView;
        return this;
    }

    //设置指示器的颜色 高度
    public FastWebView setHorizontalIndicatior(int color, int hight_dp) {
        horizontalIndicatior = true;
        this.indicatiorColor = color;
        this.indicatorHight = DipUtil.dip2px(mContext, hight_dp);
        return this;
    }

    /**
     * 设置是否js交互
     *
     * @param javaScriptEnabled 默认不允许
     * @return
     */
    public FastWebView setJSEnabled(boolean javaScriptEnabled) {
        this.javaScriptEnabled = javaScriptEnabled;
        return this;
    }

    /**
     * 初始化这个对象
     *
     * @return
     */
    public FastWebView initWV() {
        if (null == parentView) {
            throw new NullPointerException(TAG + "父容器不能为null");
        }
        //创建指示器
        if (horizontalIndicatior) {
            initIndicatior();
        }
        initWebView();
        return this;
    }

    /**
     * 设置请求地址
     *
     * @param url
     * @return
     */
    public FastWebView go(String url) {
        if (null == parentView) {
            throw new NullPointerException(TAG + "请求地址不能为空");
        }
        if (null == webView) {
            throw new NullPointerException(TAG + "请先调用createWV()");
        }

        webView.loadUrl(url);
        return this;
    }

    public FastWebView setWebViewClient(WebViewClient webViewClient) {
        this.webViewClient = webViewClient;
        return this;
    }

    public FastWebView setWebChromeClient(WebChromeClient webChromeClient) {
        this.webChromeClient = webChromeClient;
        return this;
    }

    /**
     * 设置下载监听器
     *
     * @param downloadListener
     * @return
     */
    public FastWebView setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
        return this;
    }

    /**
     * 初始化WebView
     */
    private void initWebView() {
        webView = new WebView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        webView.setLayoutParams(params);
        WebSettings settings = webView.getSettings();
        //设置是否支持js
        settings.setJavaScriptEnabled(javaScriptEnabled);
        settings.setCacheMode(cacheMode);
        if (null == webViewClient)
            webView.setWebViewClient(new WebViewClient() {
                //设置使用WebView请求网页 否则会打开浏览器
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
        //设置View 响应焦点
        webView.requestFocus();
        if (null == webChromeClient)
            webView.setWebChromeClient(new WebChromeClient() {
                //设置进度变化
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    if (null != progressBar) {
                        if (newProgress == 100) {
                            progressBar.setVisibility(View.GONE);
                        } else {
                            progressBar.setProgress(newProgress);
                        }
                    }
                    Log.e("===>", newProgress + "");
                }

                // 接收文档标题
                public void onReceivedTitle(WebView view, String title) {
                }

                // 接收图标(favicon)
                public void onReceivedIcon(WebView view, Bitmap icon) {
                }

                //For Android API < 11 (3.0 OS)
                public void openFileChooser(ValueCallback<Uri> valueCallback) {
                    uploadMessage = valueCallback;
                    openImageChooserActivity();
                }

                //For Android API >= 11 (3.0 OS)
                public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                    uploadMessage = valueCallback;
                    openImageChooserActivity();
                }

                //For Android API >= 21 (5.0 OS)
                @Override
                public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                    uploadMessageAboveL = filePathCallback;
                    openImageChooserActivity();
                    return true;
                }

            });
        //下载监听，如果设置回调 执行回调，没设置 打开浏览器下载
        if (null != downloadListener) {
            webView.setDownloadListener(downloadListener);
        } else {
            webView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(url));
                    mContext.startActivity(intent);
                }
            });
        }
        //拦截返回键时间
        if (interceptBackEvent) {
            webView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {  //表示按返回键
                            // 时的操作
                            webView.goBack();   //后退
                            //webview.goForward();//前进
                            return true;    //已处理
                        }
                    }
                    return false;
                }
            });
        }
        try {
            webView.setVisibility(View.VISIBLE);
            parentView.addView(webView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        ((Activity) mContext).startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    /**
     * 修改指示器背景色
     *
     * @param backgroundColor
     * @return
     */
    public FastWebView setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    /**
     * 创建指示器
     */
    private void initIndicatior() {
        //设置水平进度条
        progressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleHorizontal);
        //设置高度
        progressBar.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, indicatorHight
        ));
        setColors(progressBar, backgroundColor, indicatiorColor);
        progressBar.setMinimumHeight(indicatorHight);
        progressBar.setVisibility(View.VISIBLE);
        parentView.addView(progressBar);
    }

    /**
     * 修改进度条颜色和高度
     *
     * @param progressBar
     * @param backgroundColor
     * @param progressColor
     */
    private void setColors(ProgressBar progressBar, int backgroundColor, int progressColor) {
        //设置背景颜色
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setColor(backgroundColor);
        bg.setSize(1, indicatorHight);
        //bg.setCornerRadius(FormatUtils.dip2px(1.5f,context));
        //设置进度条颜色
        GradientDrawable second = new GradientDrawable();
        second.setShape(GradientDrawable.RECTANGLE);
        second.setColor(progressColor);
        second.setSize(1, indicatorHight);
        //roundRect.setCornerRadius( FormatUtils.dip2px(1.5f,context));
        ClipDrawable secondClipDrawable = new ClipDrawable(second, Gravity.LEFT, ClipDrawable.HORIZONTAL);

        Drawable[] progressDrawables = {bg, secondClipDrawable/*第二条颜色*/, second/*第一条颜色*/};
        LayerDrawable progressLayerDrawable = new LayerDrawable(progressDrawables);
        progressLayerDrawable.setId(0, android.R.id.background);
        progressLayerDrawable.setId(1, android.R.id.secondaryProgress);
        progressLayerDrawable.setId(2, android.R.id.progress);
        //设置在6.0之后的高度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            progressLayerDrawable.setLayerHeight(0, indicatorHight);
            progressLayerDrawable.setLayerHeight(1, indicatorHight);
            progressLayerDrawable.setLayerHeight(2, indicatorHight);
            progressLayerDrawable.setLayerGravity(0, Gravity.CENTER_VERTICAL);
            progressLayerDrawable.setLayerGravity(1, Gravity.CENTER_VERTICAL);
            progressLayerDrawable.setLayerGravity(2, Gravity.CENTER_VERTICAL);
        }

        progressBar.setProgressDrawable(progressLayerDrawable);
    }

    /**
     * 是否拦截返回键
     */
    private boolean interceptBackEvent = false;

    public FastWebView setInterceptBackEvent(boolean interceptBackEvent) {
        this.interceptBackEvent = interceptBackEvent;
        return this;
    }


}
