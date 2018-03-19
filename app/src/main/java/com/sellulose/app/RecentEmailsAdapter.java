package com.sellulose.app;

/**
 * Created by swapn on 19-Dec-17.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecentEmailsAdapter extends RecyclerView.Adapter<RecentEmailsAdapter.MyViewHolder> {

    private ArrayList<RecentEmail> recentEmailsList = new ArrayList<RecentEmail>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView recipient, subject, lastTracked;

        public MyViewHolder(View view) {
            super(view);
            recipient = (TextView) view.findViewById(R.id.text_recipient);
            subject = (TextView) view.findViewById(R.id.text_subject);
            lastTracked = (TextView) view.findViewById(R.id.text_last_tracked);
        }
    }


    public RecentEmailsAdapter(ArrayList<RecentEmail> recentEmailsList) {
        this.recentEmailsList = recentEmailsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recent_email, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        RecentEmail recentEmail = recentEmailsList.get(position);

        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

        String timeElapsed = "";

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(recentEmail.getLastTracked());
            d2 = new Date(System.currentTimeMillis());

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffSeconds != 0) timeElapsed = String.valueOf(diffSeconds) + "s";
            if (diffMinutes != 0) timeElapsed = String.valueOf(diffMinutes) + "m";
            if (diffHours != 0) timeElapsed = String.valueOf(diffHours) + "h";
            if (diffDays != 0) timeElapsed = String.valueOf(diffDays) + "d";

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.subject.setText(recentEmail.getSubject());
        holder.recipient.setText(recentEmail.getRecipient());
        holder.lastTracked.setText(timeElapsed);

    }

    @Override
    public int getItemCount() {
        return recentEmailsList.size();
    }

    public void calculateDiff(String fromTime) {

        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM-dd-YYYY HH:mm:ss");

        DateTime fromDateTime = formatter.parseDateTime(fromTime);

    }

}