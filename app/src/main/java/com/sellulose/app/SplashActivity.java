package com.sellulose.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 3000; //time of showing the splash screen in milliseconds

    private AlertDialog noInternetError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);

        if (isNetworkConnected()) {

            new Handler().postDelayed(new Runnable() {  //create a new handler to start a delayed function where delay = SPLASH_TIME = 3000ms
                @Override
                public void run() {
                    Intent mainActivityIntent = new Intent(SplashActivity.this, MainActivity.class); //instantiate the MainActivity as an intent
                    startActivity(mainActivityIntent); //start the MainActivity

                    finish(); //finish the SplashActivity
                }
            }, SPLASH_TIME); //delay time defined to the handler using SPLASH_TIME variable

        } else {

            noInternetError = new AlertDialog.Builder(new ContextThemeWrapper(SplashActivity.this,
                    R.style.DialogTheme))
                    .setMessage("Please check your internet connection and try again...")
                    .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            closeDialogAndFinishActivity(noInternetError);
                        }
                    })
                    .create();

            noInternetError.show();

        }
    }

    private void closeDialogAndFinishActivity(AlertDialog noInternetErrorDialog) {

        if (noInternetErrorDialog != null) {
            noInternetErrorDialog.dismiss();
        }

        SplashActivity.this.finish();

    }

    private boolean isNetworkConnected() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null;
    }
}
