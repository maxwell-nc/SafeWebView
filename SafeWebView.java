/**
 * 解决一些低版本安全和内存泄露问题的WebView<br/>
 *
 * @author Created by maxwell-nc on 2016-09-09
 */
public class SafeWebView extends WebView {

    /**
     * 添加的JS接口列表
     */
    private List<String> mJsInterfaceNames;

    public SafeWebView(Context context) {
        super(context);
    }

    public SafeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SafeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 此方法安全移除{@link Build.VERSION_CODES#JELLY_BEAN_MR1}版本下可能导致访问用户本地文件的安全性bug
     */
    @SuppressLint({"JavascriptInterface", "AddJavascriptInterface"})
    public void addJsInterface(Object object, String name) {
        super.addJavascriptInterface(object, name);
        //移除低版本默认添加的Js接口（可能存在安全隐患）
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            removeJavascriptInterface("searchBoxJavaBridge_");
        }

        //记录js接口名称，用于释放
        if (mJsInterfaceNames == null) {
            mJsInterfaceNames = new ArrayList<>();
        }

        mJsInterfaceNames.add(name);
    }

    /**
     * 移除所有已经添加的JS接口
     */
    public void removeAllJavascriptInterface() {
        for (String name : mJsInterfaceNames) {
            super.removeJavascriptInterface(name);
        }
    }

    /**
     * 防止内存泄露<br/>
     * 请接着调用：<b>webView = null;</b>
     */
    public void release() {

        setVisibility(View.GONE);

        //WebView依附移除
        ViewGroup parent = (ViewGroup) getParent();
        parent.removeView(this);

        //移除放大缩小按钮等view
        removeAllViews();

        //移除JS接口
        removeAllJavascriptInterface();

        super.destroy();
    }

    /**
     * 加载HTML文本
     *
     * @param html html内容
     */
    public void loadHtml(String html) {
        if (TextUtils.isEmpty(html)) {
            return;
        }
        loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        invalidate();
    }

    /**
     * 开启加载JavaScript
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void withJs() {
        getSettings().setJavaScriptEnabled(true);
    }

    /**
     * 禁止访问本地文件的WebViewClient<br/>
     * 防止安全性问题
     */
    public static class NoLocalReadWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            //禁止访问本地文件
            return !url.startsWith("file://") || super.shouldOverrideUrlLoading(view, url);

        }
    }

}
