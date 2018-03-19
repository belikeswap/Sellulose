package com.sellulose.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

public class OpenedEmailsActivity extends AppCompatActivity {

    private ArrayList<RecentEmail> recentEmails = new ArrayList<RecentEmail>();

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RecentEmailsAdapter recentEmailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opened_emails);

        toolbar = (Toolbar) findViewById(R.id.toolbar_opened_emails);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.list_opened_emails);

        String args = getIntent().getBundleExtra("extras").getString("args");

        switch (args) {
            case "Notif":
                recentEmailsAdapter = new RecentEmailsAdapter(new NotificationAlarmBroadcastReceiver().getUnnotifiedEmails());
                break;
            case "Card":
                TextView textView = (TextView) findViewById(R.id.text_toolbar_title_opened_emails);
                textView.setText("Recent Emails");
                recentEmails = getIntent().getBundleExtra("extras").getParcelableArrayList("RecentEmails");
                recentEmailsAdapter = new RecentEmailsAdapter(recentEmails);
                break;
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recentEmailsAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(OpenedEmailsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Default Constructor
    public OpenedEmailsActivity() {

    }

}
