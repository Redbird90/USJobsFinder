package com.studio.jkt.usjobsfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by JDK on 5/25/2016.
 */
public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewAndJobHolder> {

    private ArrayList<Bundle> jobTsil;
    private Context mainContext;
    private Activity mainActivityJA;
    private FilterPrefs favsDataFiltPrefs2 = new FilterPrefs();
    private FileIO fileIO2;
    final private int LOAD_WEB_REQUEST = 1003;
    private final String LOG_TAG = JobAdapter.class.getSimpleName();

    public JobAdapter(Context context, Activity activity) {
        super();
        this.jobTsil = new ArrayList<Bundle>();
        this.mainContext = context;
        this.mainActivityJA = activity;
        this.fileIO2 = new AndroidFileIO(mainActivityJA);
    }

    public static class ViewAndJobHolder extends RecyclerView.ViewHolder {

        private final String LOG_TAG = "cust" + ViewAndJobHolder.class.getSimpleName();
        public String url;
        public Context vhContext;
        public Activity vhActivity;
        public ViewAndJobHolder(View viewArg, Context contextArg, Activity activityArg) {
            super(viewArg);
            Log.i(LOG_TAG, "custom ViewHolder class and constructor in use");
            vhContext = contextArg;
            vhActivity = activityArg;
            url = "";
        }

/*        public Bundle getJobFromHolder() {
            return jobBundle2;
        }

        public void setJobInHolder(Bundle jobBundle3) {
            this.jobBundle2 = jobBundle3;
        }*/
    }

/*    @Override
    public int getCount() {
        int myInt = this.jobTsil.size();
        return myInt;
    }*/

/*    @Override
    public Object getItem(int position) {
        return this.jobTsil.get(position);
    }*/

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return jobTsil.size();
    }

    @Override
    public ViewAndJobHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Create new views (invoked by the layout manager)
        View viewToHold;
        LayoutInflater inflater = (LayoutInflater) mainContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewToHold = inflater.inflate(R.layout.list_item_reljob, parent, false);
        return new ViewAndJobHolder(viewToHold, mainContext, mainActivityJA);

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewAndJobHolder holder, int position) {

        final View viewToPopulate;
        viewToPopulate = holder.itemView;
        final Bundle job = jobTsil.get(position);
        //holder.setJobInHolder(job);
        final String JOB_URL = "url";
        holder.url = job.getString(JOB_URL);
        final Context holderContext = holder.vhContext;
        final Activity holderActivity = holder.vhActivity;

        viewToPopulate.setClickable(true);
        viewToPopulate.setFocusable(true);
        //.setBackground

        viewToPopulate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String JOB_TITLE = "position_title";
                Log.i(LOG_TAG, "itemClicked: " + job.getString(JOB_TITLE));

                final String JOB_URL = "url";
                SharedPreferences vhSharedPrefs = PreferenceManager.getDefaultSharedPreferences(holderContext);
                boolean openLinksInBrowser2 = vhSharedPrefs.getBoolean(holderContext.getString(R.string.prefs_openlinks_key), false);
                if (openLinksInBrowser2) {
                    Log.i(LOG_TAG, "open links in browser true");
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(job.getString(JOB_URL)));
                    //mainContext.startActivity(browserIntent);
                    holderActivity.startActivity(browserIntent);
                } else {
                    Log.i(LOG_TAG, "open links in browser false");
                    Intent jobWebLoadIntent = new Intent(holderContext, WebActivity.class);
                    jobWebLoadIntent.putExtra(holderContext.getString(R.string.extra_webview_url_key), job.getString(JOB_URL));
                    jobWebLoadIntent.putExtra(holderContext.getString(R.string.extra_webview_jobdata_key), job);
                    //startActivityForResult(jobWebLoadIntent, LOAD_WEB_REQUEST);
                    //vhContext.startActivityForResult(jobWebLoadIntent, LOAD_WEB_REQUEST);
                    holderActivity.startActivityForResult(jobWebLoadIntent, LOAD_WEB_REQUEST);
                }
            }
        });

        viewToPopulate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.i(LOG_TAG, "on long click called inside viewholder");
                // TODO: Add check if at favs menu, if so remove fav
                //Bundle jobBundle2 = firstAdapter.getJob(position);
                final String JOB_ID = "id";
                final String JOB_TITLE2 = "position_title";
                Log.i(LOG_TAG, "itemLONGClicked: " + job.getString(JOB_TITLE2));
                Log.i(LOG_TAG, "long clicked to save job, id is " + job.getString(JOB_ID));

                boolean saveResult = saveJobBundle2(job);
                Snackbar saveSnack;
                //Toast saveToast;
                if (saveResult) {
                    saveSnack = Snackbar.make(viewToPopulate, holderContext.getString(R.string.favs_jobsaved), Snackbar.LENGTH_LONG);
                    //saveToast = Toast.makeText(holderContext, holderContext.getString(R.string.favs_jobsaved), Toast.LENGTH_LONG);
                } else {
                    saveSnack = Snackbar.make(viewToPopulate, holderContext.getString(R.string.favs_jobremoved), Snackbar.LENGTH_LONG);
                    //saveToast = Toast.makeText(holderContext, holderContext.getString(R.string.favs_jobremoved), Toast.LENGTH_LONG);
                }
                saveSnack.show();
                //saveToast.show();
                return true;
            }
        });

        //final String JOB_ID = "id";
        final String JOB_TITLE = "position_title";
        final String JOB_ORGNAME = "organization_name";
        final String JOB_CODE = "rate_interval_code";
        final String JOB_MIN = "minimum";
        final String JOB_MAX = "maximum";
        final String JOB_START = "start_date";
        final String JOB_END = "end_date";
        final String JOB_LOC_array = "locations";

        TextView jobTitleTV = (TextView) viewToPopulate.findViewById(R.id.list_item_job_title_tv);
        TextView jobSalaryTV = (TextView) viewToPopulate.findViewById(R.id.list_item_job_salary_tv);
        TextView jobStartTV = (TextView) viewToPopulate.findViewById(R.id.list_item_job_start_tv);
        TextView jobEndTV = (TextView) viewToPopulate.findViewById(R.id.list_item_job_end_tv);
        //TextView jobIdTV = (TextView) v.findViewById(R.id.list_item_job_id_tv);
        TextView jobOrgnameTV = (TextView) viewToPopulate.findViewById(R.id.list_item_job_orgname_tv);
        TextView jobLocationsTV = (TextView) viewToPopulate.findViewById(R.id.list_item_job_locations_tv);

        NumberFormat currStrFormat = NumberFormat.getCurrencyInstance();
        currStrFormat.setMinimumFractionDigits(0);
        String modSalaryStr = currStrFormat.format(job.getInt(JOB_MIN)) + " to " + currStrFormat.format(job.getInt(JOB_MAX));
        //String modSalaryStr = "$" + Integer.toString(job.getInt(JOB_MIN)) + " to $" + Integer.toString(job.getInt(JOB_MAX));
        String modLocStr = "";
        String[] jobLocArray = job.getStringArray(JOB_LOC_array);
        // TODO: add null check for jobLocArray, prob in fetchDataFromJson
        if (jobLocArray.length == 1) {
            modLocStr = jobLocArray[0];
        } else if (jobLocArray.length > 2) {
            modLocStr = mainContext.getString(R.string.job_toomanylocations);
        } else {
            modLocStr = "";
            for (int i = 0; i < jobLocArray.length; i++) {
                modLocStr += jobLocArray[i] + ", ";
            }
            Log.i(LOG_TAG, "String prior formatting: " + modLocStr);
            //TODO: Debug below line to remove ","
            Log.i(LOG_TAG, "len is " + String.valueOf(modLocStr.length()));
            modLocStr = modLocStr.substring(0, modLocStr.length()-2);
            Log.i(LOG_TAG, "String after formatting: " + modLocStr);
        }

        jobTitleTV.setText(job.getString(JOB_TITLE));
        jobSalaryTV.setText(modSalaryStr);
        String jobStartStr = holder.vhContext.getString(R.string.jobstsil_opened) + job.getString(JOB_START);
        String jobEndStr = holder.vhContext.getString(R.string.jobstsil_closing) + job.getString(JOB_END);
        jobStartTV.setText(jobStartStr);
        jobEndTV.setText(jobEndStr);
        //jobIdTV.setText(job.getString(JOB_ID));
        jobOrgnameTV.setText(job.getString(JOB_ORGNAME));
        // TODO: If multiple locations for the listing, allow user to see all locations with a tap
        jobLocationsTV.setText(modLocStr);

        Log.i(LOG_TAG, "onBindViewHolder RUN, view populated");

    }

    private boolean saveJobBundle2(Bundle bundledJobData2) {
        final String JOB_ID = "id";
        Log.i(LOG_TAG, "saving job, id is " + bundledJobData2.get(JOB_ID));
        // TODO: Ensure error handling so user if notified of errors
        boolean favJobAddResult2 = favsDataFiltPrefs2.addFavoriteJob(bundledJobData2);
        favsDataFiltPrefs2.save(fileIO2);
        return favJobAddResult2;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    public void addJob(Bundle bundle) {
        this.jobTsil.add(bundle);
    }

    public Bundle getJob(int position) {
        return jobTsil.get(position);
    }

    public void clearJobs() {
        this.jobTsil.clear();
    }

    public ArrayList<Bundle> getTsil() {
        return this.jobTsil;
    }

    public void setJobTsil(ArrayList<Bundle> updatedJobTsil) {
        this.jobTsil = updatedJobTsil;
        notifyDataSetChanged();
    }

}