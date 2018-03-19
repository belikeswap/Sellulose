package com.sellulose.app;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import self.philbrown.droidQuery.$;
import self.philbrown.droidQuery.AjaxOptions;
import self.philbrown.droidQuery.Function;
import self.philbrown.droidQuery.Headers;

/**
 * Created by swapn on 17-Jan-18.
 */

public class CardFragment extends Fragment {

    private static final String SHARED_PREFS = "SellulosePrefs";

    private Context mContext;
    private SharedPreferences sharedPreferences;

    //Default Constructor
    public CardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = (Context) getArguments().get("context");

        sharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "null");

        if (getArguments().getString("cardType") == "RECENTS") {

            JSONObject headersObject = new JSONObject();

            try {

                headersObject.put("Accept", "application/x.se.v1+json");
                headersObject.put("Access-Control-Allow-Credentials", "true");

                AjaxOptions ajaxOptions = new AjaxOptions().url("https://app.sellulose.com/api/authenticate/?email="+ email)
                        .type("POST")
                        .headers(new Headers(headersObject))
                        .context(mContext)
                        .success(new Function() {
                            @Override
                            public void invoke($ droidQuery, Object... objects) {


                            }
                        })
                        .error(new Function() {
                            @Override
                            public void invoke($ droidQuery, Object... objects) {
                                int statusCode = (Integer) objects[1];
                                String error = (String) objects[2];
                                Log.d("DQ-AJAX-Error", statusCode + "\nError: " + error);

                            }
                        });

                $.ajax(ajaxOptions);



            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("DQ-JSON-Error", e.toString());
            }

        }

        return inflater.inflate(R.layout.login_salesforce, container, false);
    }



}
