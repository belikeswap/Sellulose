package com.sellulose.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.salesforce.androidsdk.smartsync.app.SmartSyncSDKManager;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by swapn on 16-Jan-18.
 */

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.MyViewHolder> {

    private static final String SHARED_PREFS = "SellulosePrefs";

    private Context mContext;
    private ViewGroup rootView;
    private ArrayList<Integer> layoutList;
    private ArrayList<RecentEmail> recentEmails;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView cardLabel, expandCard;
        RelativeLayout empty_view;

        public MyViewHolder(View view) {
            super(view);

            empty_view = (RelativeLayout) view.findViewById(R.id.empty_view_card);
            cardLabel = (TextView) view.findViewById(R.id.card_label);
            expandCard = (TextView) view.findViewById(R.id.text_expand_card);

        }
    }

    //Default constructor
    public CardViewAdapter() {

    }

    public CardViewAdapter(Context context, ArrayList<Integer> layoutList, ArrayList<RecentEmail> recentEmails) {
        this.mContext = context;
        this.layoutList = layoutList;
        this.recentEmails = recentEmails;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);

        rootView = parent;

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        int layoutId = layoutList.get(position);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(layoutId, rootView, false);

        if (layoutId == R.layout.list_recently_opened) {

            holder.cardLabel.setText("RECENTS");

            RecyclerView recentEmailsList = (RecyclerView) v.findViewById(R.id.list_recent_emails);
            RecentEmailsAdapter recentEmailsAdapter = new RecentEmailsAdapter(this.recentEmails);

            recentEmailsList.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);

            recentEmailsList.setLayoutManager(layoutManager);
            recentEmailsList.setItemAnimator(new DefaultItemAnimator());
            recentEmailsList.setAdapter(recentEmailsAdapter);

            holder.expandCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("RecentEmails", CardViewAdapter.this.recentEmails);
                    bundle.putString("args", "Card");

                    Intent expandedRecentEmailsActivity = new Intent(mContext, OpenedEmailsActivity.class);
                    expandedRecentEmailsActivity.putExtra("extras", bundle);
                    mContext.startActivity(expandedRecentEmailsActivity);
                }
            });

        } else if (layoutId == R.layout.login_salesforce) {

            SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            boolean FLAG_SALESFORCE_LOGIN_COMPLETE = prefs.getBoolean("SalesforceLoginComplete", false);

            if (FLAG_SALESFORCE_LOGIN_COMPLETE) {

                v = inflater.inflate(R.layout.list_reminders_salesforce, rootView, false);

            } else {

                v = inflater.inflate(R.layout.login_salesforce, rootView, false);

                Button loginSalesforceButton = (Button) v.findViewById(R.id.button_login_salesforce);
                loginSalesforceButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent salesforceLoginIntent = new Intent(mContext, SalesforceLoginActivity.class);
                        salesforceLoginIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        mContext.startActivity(salesforceLoginIntent);
                    }
                });

            }

        }

        holder.empty_view.addView(v);

    }

    @Override
    public int getItemCount() {
        return layoutList.size();
    }

}
