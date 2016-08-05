package com.studio.jkt.usjobsfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by James on 7/1/2016.
 */
public class WebActivity extends AppCompatActivity {

    WebView regWebView;
    Bundle currJobBundle;
    String providedURL;
    Activity currAct = this;
    Toolbar webToolbar;
    String LOG_TAG = WebActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        Intent intent = getIntent();
        providedURL = intent.getStringExtra(getString(R.string.extra_webview_url_key));
        currJobBundle = intent.getBundleExtra(getString(R.string.extra_webview_jobdata_key));
        Log.i(LOG_TAG, "provided URL is " + providedURL);
        LinearLayout webActRootLay = (LinearLayout) findViewById(R.id.web_lin_lay);
        final ProgressBar webProgBar = (ProgressBar) webActRootLay.findViewById(R.id.webProgressBar);
        regWebView = (WebView) webActRootLay.findViewById(R.id.cust_webview);
        WebSettings webSettings = regWebView.getSettings();
        regWebView.setWebViewClient(new MyWebViewClient());
        regWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                Log.i(LOG_TAG, "progress at " + progress);
                if (webProgBar.getVisibility() != View.VISIBLE) {
                    webProgBar.setVisibility(View.VISIBLE);
                }
                if (progress >= 100) {
                    webProgBar.setVisibility(View.INVISIBLE);
                }
            }
        });
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        regWebView.loadUrl(providedURL);

        webToolbar = (Toolbar) webActRootLay.findViewById(R.id.toolbar_webact);
        setSupportActionBar(webToolbar);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // Decides whether user is directed to new URL when they click on a link, return true
            // if host app handles url, false if current webView handles URL
            // TODO: Check return value correctness, consider opening new links in website
            Log.i(LOG_TAG, "webViewClient, overriding URL!");
            if (url.equals(providedURL)) {
                view.loadUrl(url);
                return true;
            } else {
                return true;
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest resourceRequest, WebResourceError resourceError) {
            Log.i(LOG_TAG, "url ERROR: " + resourceError.toString());
            Toast.makeText(getApplicationContext(), "Oh no! " + resourceError.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.i(LOG_TAG, "optionItemSelected...");
        switch (item.getItemId()) {
            case R.id.menuitem_exit:
                exitWebActivity();
                Log.i(LOG_TAG, "exit menu btn clicked");
                return true;
/*            case R.id.regwebview_favorite:
                Log.i(LOG_TAG, "fav menu btn clicked");
                Intent webResultIntent = new Intent();
                webResultIntent.putExtra(getString(R.string.extra_webview_jobdata_key), currJobBundle);
                setResult(RESULT_OK, webResultIntent);*/
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(LOG_TAG, "creating options menu...");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cust_exitable_menu, menu);
        return true;
    }

    private void exitWebActivity() {
        finish();
        Log.i(LOG_TAG, "exiting webAct...");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(LOG_TAG, "onActResult in webAct");
        super.onActivityResult(requestCode, resultCode, data);
    }

}
