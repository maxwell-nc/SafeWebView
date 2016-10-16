# SafeWebView
Android extended Webview with safe release method and fix JavascriptInterface bugs

# Usage
First,copy code to your project

Use below code fragment in Activity onFinish method,Don't use in onDestory(No effect!):
```
mWebView.release();
mWebView = null;
```

Set JavaScript enabled with below method:
```
mWebView.withJs();
```

Add/Remove Javascript Interface with below method:
```
mWebView.addJsInterface(Object object, String name);
mWebView.removeAllJavascriptInterface();
```

See more method in code.
