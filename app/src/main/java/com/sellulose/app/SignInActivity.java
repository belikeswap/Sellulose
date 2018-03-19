package com.sellulose.app;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.SignInButton;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SignInActivity extends AppCompatActivity {

    private static final int SIGN_IN = 148;
    private static String EMAIL = "";

    private SignInButton signInButton;
    private ImageView googleIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_in);

        signInButton = (SignInButton) findViewById(R.id.button_sign_in);
        googleIcon = (ImageView) findViewById(R.id.image_google_icon);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        googleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    private void signIn() {

        Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                false, null, null, null, null);
        startActivityForResult(intent, SIGN_IN+1);

    }

    private void handleSignInResult(Intent result) {

        EMAIL = result.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("EMAIL", EMAIL);
        setResult(SIGN_IN, resultIntent);
        finish();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN+1) {
            handleSignInResult(data);
        }

    }

}