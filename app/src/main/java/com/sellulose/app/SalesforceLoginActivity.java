package com.sellulose.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ApiVersionStrings;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.security.auth.Subject;

public class SalesforceLoginActivity extends SalesforceActivity {

    private static final String SHARED_PREFS = "SellulosePrefs";
    private static final String SALESFORCE_LOGIN_FLAG = "SalesforceLoggedIn";

    private RestClient restClient;

    private ArrayList<SalesforceTask> tasksList = new ArrayList<SalesforceTask>();
    private TasksAdapter tasksAdapter;

    private ProgressBar progressBar;
    private TextView loadingText;

    private String person = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesforce_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_salesforce_login);
        toolbar.inflateMenu(R.menu.salesforce_login_activity_menu);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);

        progressBar = (ProgressBar) SalesforceLoginActivity.this.findViewById(R.id.spinner_salesforce_login);
        loadingText = (TextView) SalesforceLoginActivity.this.findViewById(R.id.text_loading_salesforce_login);

    }

    private void logoutSalesforce() {

        SalesforceSDKManager.getInstance().logout(this, false);
        SalesforceSDKManager.getInstance().removeAllCookies();

    }

    @Override
    public void onResume(RestClient client) {

        this.restClient = client;

        progressBar.setVisibility(View.VISIBLE);
        loadingText.setVisibility(View.VISIBLE);

        if (tasksAdapter != null) {
            tasksAdapter.clearAdapter();
        }

        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();

        RestClient.ClientInfo ci = this.restClient.getClientInfo();
        String userId = ci.userId;

        if (userId.equals("null")) {
            sharedPreferencesEditor.putBoolean(SALESFORCE_LOGIN_FLAG, false);
            sharedPreferencesEditor.apply();
        } else {
            sharedPreferencesEditor.putBoolean(SALESFORCE_LOGIN_FLAG, true);
            sharedPreferencesEditor.apply();
        }

        start();

    }

    private void start() {

        final String query = "SELECT WhoId, Subject, ActivityDate, ReminderDateTime FROM Task";

           try {

               RestRequest restRequest = RestRequest.getRequestForQuery(ApiVersionStrings.getVersionNumber(SalesforceLoginActivity.this), query);
               restClient.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                   @Override
                   public void onSuccess(RestRequest request, RestResponse response) {
                       response.consumeQuietly();
                        try {

                            JSONArray records = response.asJSONObject().getJSONArray("records");
                            final int size = response.asJSONObject().getInt("totalSize");

                            ArrayList<String> whoIds = new ArrayList<String>();
                            ArrayList<String> subjects = new ArrayList<String>();
                            ArrayList<String> dates = new ArrayList<String>();

                            for (int i = 0; i < size; i++) {

                                whoIds.add("'" + records.getJSONObject(i).getString("WhoId") + "'");
                                subjects.add(records.getJSONObject(i).getString("Subject"));

                                String activityDate = records.getJSONObject(i).getString("ActivityDate");
                                if (activityDate.equals("null")) activityDate = "No due date";

                                dates.add(activityDate);

                            }

                            for (int i = 0; i < size; i++) {

                                matchWhoIdWithId(whoIds.get(i));

                                tasksList.add(new SalesforceTask(subjects.get(i), person, dates.get(i)));
                                final int finalI = i;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateUIText("(" + String.valueOf(finalI +1) + " of " + String.valueOf(size) + ")");
                                    }
                                });

                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateUI();
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                   }

                   @Override
                   public void onError(Exception exception) {

                   }
               });

           } catch (UnsupportedEncodingException e) {
               e.printStackTrace();
           }

    }

    private void matchWhoIdWithId(String whoId) {

        boolean foundInContact;

        foundInContact = findInContact(whoId);

        if (!foundInContact) {
            findInLead(whoId);
        }

    }

    private boolean findInContact(String whoId) {

        person = "";

        final RestResponse checkResponse[] = {null};

        final boolean result[] = {false};
        String query = "SELECT Name FROM Contact WHERE ID=" + whoId;

        try {
            RestRequest restRequest = RestRequest.getRequestForQuery(ApiVersionStrings.getVersionNumber(SalesforceLoginActivity.this), query);
            restClient.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, RestResponse response) {
                    checkResponse[0] = response;
                    response.consumeQuietly();
                    try {

                        int size = response.asJSONObject().getInt("totalSize");

                        if (size != 0) {
                            result[0] = true;

                            person = response.asJSONObject().getJSONArray("records")
                                    .getJSONObject(0).getString("Name")
                                    + " (Contact)";

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception exception) {

                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            do {
                Thread.sleep(100);
            } while (checkResponse[0] == null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];
    }

    private boolean findInLead(String whoId) {

        person = "";

        final RestResponse[] checkResponse = {null};

        final boolean[] result = {false};
        String query = "SELECT Name FROM Lead WHERE ID=" + whoId;

        try {
            RestRequest restRequest = RestRequest.getRequestForQuery(ApiVersionStrings.getVersionNumber(SalesforceLoginActivity.this), query);
            restClient.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, RestResponse response) {
                    checkResponse[0] = response;
                    response.consumeQuietly();
                    try {

                        int size = response.asJSONObject().getInt("totalSize");

                        if (size != 0) {
                            result[0] = true;

                            person = response.asJSONObject().getJSONArray("records")
                                    .getJSONObject(0).getString("Name")
                                    + " (Lead)";

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception exception) {

                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            do {
                Thread.sleep(100);
            } while (checkResponse[0] == null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result[0];
    }

    private void updateUI() {

        progressBar.setVisibility(View.INVISIBLE);
        loadingText.setVisibility(View.INVISIBLE);

        Collections.reverse(tasksList);

        RecyclerView tasksView = (RecyclerView) SalesforceLoginActivity.this.findViewById(R.id.list_salesforce_tasks);
        tasksAdapter = new TasksAdapter(tasksList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        tasksView.setLayoutManager(layoutManager);
        tasksView.setItemAnimator(new DefaultItemAnimator());
        tasksView.setAdapter(tasksAdapter);

    }

    private void updateUIText(String text) {
        loadingText.setText("Loading tasks...\n" + text);
    }

    public boolean compareDates(String date) {
        boolean
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.salesforce_login_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_logout_button:
                logoutSalesforce();
                return true;
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(SalesforceLoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
