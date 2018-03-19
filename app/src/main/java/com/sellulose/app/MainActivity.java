package com.sellulose.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import self.philbrown.droidQuery.$;
import self.philbrown.droidQuery.AjaxOptions;
import self.philbrown.droidQuery.Function;
import self.philbrown.droidQuery.Headers;

public class MainActivity extends AppCompatActivity {

    private static final String SHARED_PREFS = "SellulosePrefs";
    private static final String TOKEN = "Token";
    private static boolean FLAG_FIRST_RUN = true; //First run status flag
    private static boolean FLAG_ACCOUNT_SETUP_COMPLETE = false;
    private static final int SIGN_IN = 148;

    private static String strEmail;

    private ArrayList<RecentEmail> recentEmails =  new ArrayList<RecentEmail>();;
    private ArrayList<Integer> layoutIds;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private TextView name, email, plan;

    private RecyclerView cardsList;
    private CardViewAdapter cardViewAdapter;

    private JSONObject rawResponse, userDetails;

    private NotificationAlarmBroadcastReceiver notificationAlarmBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (TextView) findViewById(R.id.text_name);
        email = (TextView) findViewById(R.id.text_email);
        plan = (TextView) findViewById(R.id.text_plan);

        /* We need to check whether the app is being run for the first time for logging in the user using Google Sign-In */
        prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE); //Helps fetch SharedPreferences elements
        editor = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit(); //Helps edit SharedPreferences elements

        editor.putBoolean("SalesforceLoginComplete", false);

        if (prefs.getBoolean("FirstRun", true)) {   //Check for the 'FirstRun' boolean, default value is true
            editor.putBoolean("FirstRun", false);   //If default value is true, set 'FirstRun' to false
            editor.apply();
        } else {
            FLAG_FIRST_RUN = false; //Set the First run status flag to false, if it is not a first run
        }

        FLAG_ACCOUNT_SETUP_COMPLETE = prefs.getBoolean("ACCOUNT_SETUP_COMPLETE", false);

        /* Check first run status and implement Google Sign-In */
        if (FLAG_FIRST_RUN || !FLAG_ACCOUNT_SETUP_COMPLETE) {   //If first run is true or account setup is incomplete, proceed to Google Sign-In
            Intent signInIntent = new Intent(MainActivity.this, SignInActivity.class);
            startActivityForResult(signInIntent, SIGN_IN);
        } else {
            prefs = null;
            prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            String email = prefs.getString("EMAIL", "null");
            if (!email.equals("null")) apiCallForAuthentication(email);
        }

        layoutIds = new ArrayList<Integer>();
        layoutIds.add(R.layout.login_salesforce);
        layoutIds.add(R.layout.list_recently_opened);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN) {

            if (data != null) {

                strEmail = data.getStringExtra("EMAIL");

                if(!strEmail.equals("null")) {

                    editor = null;
                    editor = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
                    editor.putString("EMAIL", strEmail);
                    editor.putBoolean("ACCOUNT_SETUP_COMPLETE", true);
                    editor.apply();
                    apiCallForAuthentication(strEmail);

                } else {
                    Toast.makeText(MainActivity.this, "Failed to get account info!" +
                            "\nPlease try again with a different account...", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void apiCallForAuthentication(String email) {

        JSONObject headersObject = new JSONObject();

            try {

                headersObject.put("Accept", "application/x.se.v1+json");
                headersObject.put("Access-Control-Allow-Credentials", "true");

                AjaxOptions ajaxOptions = new AjaxOptions().url("https://app.sellulose.com/api/authenticate/?email="+ email)
                        .type("POST")
                        .headers(new Headers(headersObject))
                        .context(MainActivity.this)
                        .success(new Function() {
                            @Override
                            public void invoke($ droidQuery, Object... objects) {

                                updateUserDetailsUI(objects);
                                getRecentEmails(objects);
                                notificationAlarmBroadcastReceiver = new NotificationAlarmBroadcastReceiver();
                                notificationAlarmBroadcastReceiver.setAlarm(getApplicationContext(), 30);

                            }
                        })
                        .error(new Function() {
                            @Override
                            public void invoke($ droidQuery, Object... objects) {
                                int statusCode = (Integer) objects[1];
                                String error = (String) objects[2];
                                Log.d("DQ-AJAX-Error", statusCode + "\nError: " + error);

                                Toast.makeText(getBaseContext(), String.valueOf(statusCode) + "\nError: " + error, Toast.LENGTH_LONG).show();
                            }
                        });

                $.ajax(ajaxOptions);



            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("DQ-JSON-Error", e.toString());
            }

    }

    private void updateUserDetailsUI(Object... objects) {

        try {

            editor = null;
            editor = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();

            rawResponse = new JSONObject((String) objects[0]);
            userDetails = new JSONObject(rawResponse.getString("user"));

            editor.putString(TOKEN, userDetails.getString("tracking_key"));
            editor.apply();

            MainActivity.this.name.setText(WordUtils.capitalize(userDetails.getString("name")));
            MainActivity.this.email.setText(userDetails.getString("email"));
            MainActivity.this.plan.setText(userDetails.getJSONObject("plan").getString("plan_name").toUpperCase());

        } catch (JSONException e) {

            e.printStackTrace();

        }

    }

    private void getRecentEmails(Object... objects) {

        try {
            JSONObject response = new JSONObject((String) objects[0]);

            JSONObject user = new JSONObject((response.getString("user")));
            JSONObject recent = new JSONObject(user.getString("recent"));

            JSONArray recentData = new JSONArray(recent.getString("data"));

            for (int i = 0; i < recentData.length(); i++) {

                String recipient = recentData.getJSONObject(i).getString("receipients");
                String subject = recentData.getJSONObject(i).getString("subject");
                String lastTracked = recentData.getJSONObject(i).getString("last_tracked");
                boolean isNew = recentData.getJSONObject(i).getBoolean("new");

                recentEmails.add(new RecentEmail(recipient, subject, lastTracked, isNew));

            }

            updateUI();

        } catch (JSONException e) {
            Log.d("getRecentError", e.toString());
            e.printStackTrace();
        }

    }

    private void updateUI() {

        cardsList = (RecyclerView) MainActivity.this.findViewById(R.id.list_cards);
        cardViewAdapter = new CardViewAdapter(MainActivity.this, MainActivity.this.layoutIds, recentEmails);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        cardsList.setLayoutManager(layoutManager);
        cardsList.setItemAnimator(new DefaultItemAnimator());
        cardsList.setAdapter(cardViewAdapter);

    }

    //Empty constructor for MainActivity class
    public MainActivity() {

    }

}
