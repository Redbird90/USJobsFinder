package com.studio.jkt.usjobsfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

/**
 * Created by James on 7/1/2016.
 */
public class WebActivity extends AppCompatActivity {

    WebView regWebView;
    Bundle currJobBundle;
    String providedURL;
    private FilterPrefs favsDataFiltPrefs3 = new FilterPrefs();
    private FileIO fileIO3;
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
        this.fileIO3 = new AndroidFileIO(this);
        LinearLayout webActRootLay = (LinearLayout) findViewById(R.id.web_lin_lay);
        final ProgressBar webProgBar = (ProgressBar) webActRootLay.findViewById(R.id.webProgressBar);
        regWebView = (WebView) webActRootLay.findViewById(R.id.reg_webview);
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
            LinearLayout webActRootLay2 = (LinearLayout) findViewById(R.id.web_lin_lay);
            Snackbar webErrorSnackbar = Snackbar.make(webActRootLay2, getString(R.string.web_connect_error) + resourceError.toString(), Snackbar.LENGTH_SHORT);
            webErrorSnackbar.show();
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
            case R.id.regwebview_favorite:
                Log.i(LOG_TAG, "fav menu btn clicked");
                /*Intent webResultIntent = new Intent();
                webResultIntent.putExtra(getString(R.string.extra_webview_jobdata_key), currJobBundle);
                setResult(RESULT_OK, webResultIntent);*/
                boolean isJobSavedFromWeb = saveJobBundle3(currJobBundle);
                Snackbar webSaveSnack;
                if (isJobSavedFromWeb) {
                    webSaveSnack = Snackbar.make(findViewById(R.id.web_lin_lay), getString(R.string.favs_jobsaved), Snackbar.LENGTH_LONG);
                } else {
                    webSaveSnack = Snackbar.make(findViewById(R.id.web_lin_lay), getString(R.string.favs_jobremoved), Snackbar.LENGTH_LONG);
                }
                webSaveSnack.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(LOG_TAG, "creating options menu...");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cust_web_menu, menu);
        return true;
    }

    private void exitWebActivity() {
        finish();
        Log.i(LOG_TAG, "exiting webAct...");
    }

    // TODO: Ensure onActResult below is needed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(LOG_TAG, "onActResult in webAct");
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean saveJobBundle3(Bundle bundledJobData3) {
        final String JOB_ID = "id";
        Log.i(LOG_TAG, "saving job, id is " + bundledJobData3.get(JOB_ID));
        // TODO: Ensure error handling so user if notified of errors
        boolean favJobAddResult3 = favsDataFiltPrefs3.addFavoriteJob(bundledJobData3);
        favsDataFiltPrefs3.save(fileIO3);
        return favJobAddResult3;
    }

}
