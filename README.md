# FastWebView

#### 项目介绍

FastWebView 是封装了ProgressBar、WebView的组合控件，快速实现android中浏览网页，使用简单；方便二次扩展
#### 引用说明
~~~
Step 1. 在项目的 build.gradle中引用

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. 在model中引用

	dependencies {
	        implementation 'com.gitee.wasdzy:WebViewLoadHtml:1.0.0'
	}
~~~
#### 使用说明

~~~
public class FastWVActivity extends AppCompatActivity {

    private FastWebView fastWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_wv);
        LinearLayout fwv = findViewById(R.id.fwv);
        fastWebView = FastWebView.builder(FastWVActivity.this)
                .setParentView(fwv, new FrameLayout.LayoutParams(//设置外部容器 使用LinearLayout
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ))
                .setInterceptBackEvent(true)//拦截返回按钮事件
                .setJSEnabled(true)//设置是否运行js交互
                .setHorizontalIndicatior(Color.RED, 2)//设置水平进度条
                .initWV()//初始化  设置操作之后，请求地址之前
                .go("http://www.jd.com");//设置请求地址
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != fastWebView) {
            fastWebView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != fastWebView) {
            fastWebView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != fastWebView) {
            fastWebView.onDestroy();
        }
    }
}

~~~

#### 参与贡献

1. angetweb
