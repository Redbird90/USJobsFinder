package com.studio.jkt.usjobsfinder;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Created by James on 7/1/2016.
 */
public class MyCustWebView  extends WebView {

    CustomizedSelectActionModeCallback actionModeCallback;
    public Context context;
    private String LOG_TAG = MyCustWebView.class.getSimpleName();

    public MyCustWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        Log.i(LOG_TAG, "regWebView constructing...");
        this.context = context;
        //actionModeCallback = new CustomizedSelectActionModeCallback();
        startActionMode(actionModeCallback);
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        // TODO Auto-generated method stub
        //      ViewParent parent = getParent();
        //        if (parent == null) {
        //            return null;
        //        }
        actionModeCallback = new CustomizedSelectActionModeCallback();
        Log.i(LOG_TAG, "regWebView starting action mode...");
        return startActionModeForChild(this, actionModeCallback);

    }

    public class CustomizedSelectActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            mode.getMenuInflater().inflate(R.menu.cust_exitable_menu, menu);
            Log.i(LOG_TAG, "custActionMode creating...");
            return true;

        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            mode.setTitle(context.getString(R.string.custwebview_title));
            Log.i(LOG_TAG, "custActionMode preparing...");
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // TODO Auto-generated method stub
            Log.i(LOG_TAG, "custActionMode item has been clicked...");
            switch (item.getItemId()) {
                case R.id.menuitem_exit:
                    clearFocus();
                    Toast.makeText(getContext(), "This is my test click", Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // TODO Auto-generated method stub

            Log.i(LOG_TAG, "custActionMode onDestroy called...");
            clearFocus(); // this  is not clearing the text in my device having version 4.1.2
            actionModeCallback = null;

        }

    }
}
