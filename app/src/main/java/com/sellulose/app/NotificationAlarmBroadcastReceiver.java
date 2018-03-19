package com.sellulose.app;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import self.philbrown.droidQuery.$;
import self.philbrown.droidQuery.AjaxOptions;
import self.philbrown.droidQuery.Function;
import self.philbrown.droidQuery.Headers;

/**
 * Created by swapn on 03-Jan-18.
 */

public class NotificationAlarmBroadcastReceiver extends BroadcastReceiver {

    private static final String SHARED_PREFS = "SellulosePrefs";
    private static final int NOTIFICATION_ID = 007;
    private Context mContext;
    private static ArrayList<RecentEmail> mRecentEmails;
    private static JSONObject response;
    private static JSONObject user;
    private static int unseen_count;
    private static ArrayList<RecentEmail> unnotifiedEmails = new ArrayList<RecentEmail>();

    @Override
    public void onReceive(final Context context, final Intent intent) {

        SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Notification");
        wakeLock.acquire(7500);

        JSONObject headersObject = new JSONObject();

        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        try {

            headersObject.put("Access-Control-Allow-Credentials", "true");
            headersObject.put("Accept", "application/x.se.v1+json");

            AjaxOptions ajaxOptions = new AjaxOptions().url("https://app.sellulose.com/api/authenticate/?email=" + sharedPrefs.getString("EMAIL", "null"))
                    .type("POST")
                    .headers(new Headers(headersObject))
                    .context(mContext)
                    .success(new Function() {
                        @Override
                        public void invoke($ droidQuery, Object... objects) {

                            NotificationCompat.Builder mBuilder = null;

                            try {
                                response = new JSONObject((String) objects[0]);

                                user = new JSONObject((response.getString("user")));

                                unseen_count = user.getInt("unseen_count");

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("COUNT_ERROR", e.toString());
                            }

                            if (unseen_count > 0) {
                                getRecentEmails(objects);

                                unnotifiedEmails = null;
                                unnotifiedEmails = new ArrayList<RecentEmail>();

                                for (int i = 0; i < mRecentEmails.size(); i++) {
                                    if (mRecentEmails.get(i).isNew()) {
                                        unnotifiedEmails.add(mRecentEmails.get(i));
                                    }
                                }

                                ArrayList<String> issuedMessages = new ArrayList<String>();

                                Intent openedEmails =  new Intent(context, OpenedEmailsActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("args", "Notif");
                                openedEmails.putExtra("extras", bundle);
                                PendingIntent pi = PendingIntent.getActivity(context, 0, openedEmails, PendingIntent.FLAG_UPDATE_CURRENT);

                                for (int i = 0; i < unnotifiedEmails.size(); i++) {

                                    issuedMessages.add(unnotifiedEmails.get(i).getRecipient() + " Opened Email");

                                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                                    for (int i1 = 0; i1 < issuedMessages.size(); i1++) {
                                        inboxStyle.addLine(issuedMessages.get(i1));
                                    }
                                    mBuilder = new NotificationCompat.Builder(context)
                                            .setStyle(inboxStyle)
                                            .setContentTitle(issuedMessages.size() + " Emails Opened")
                                            .setContentText(unnotifiedEmails.get(0).mRecipient +
                                                    " +" + (unnotifiedEmails.size() - 1) + " more...")
                                            .setSmallIcon(R.drawable.ic_noti_icon)
                                            .setAutoCancel(true)
                                            .setContentIntent(pi);
                                    notificationManager.cancel(NOTIFICATION_ID);
                                    notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

                                }
                            }
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



        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Noti-Fetch-Error", e.toString());
        }

        wakeLock.release();

    }

    public void setAlarm(Context context, int frequency) {

        mContext = context;

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationAlarmBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * frequency, pi);

    }

    public void cancelAlarm(Context context) {

        Intent intent = new Intent(context, NotificationAlarmBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);

    }

    private void getRecentEmails(Object... objects) {

        try {

            response = null;
            user = null;

            response = new JSONObject((String) objects[0]);

            user = new JSONObject((response.getString("user")));

            JSONObject recent = new JSONObject(user.getString("recent"));

            JSONArray recentData = new JSONArray(recent.getString("data"));

            mRecentEmails =  new ArrayList<RecentEmail>();

            for (int i = 0; i < recentData.length(); i++) {

                String recipient = recentData.getJSONObject(i).getString("receipients");
                String subject = recentData.getJSONObject(i).getString("subject");
                String lastTracked = recentData.getJSONObject(i).getString("last_tracked");
                boolean isNew = recentData.getJSONObject(i).getBoolean("new");

                mRecentEmails.add(new RecentEmail(recipient, subject, lastTracked, isNew));

            }

        } catch (JSONException e) {
            Log.d("getRecentError", e.toString());
            e.printStackTrace();
        }

    }

    public ArrayList<RecentEmail> getUnnotifiedEmails() {

        return unnotifiedEmails;
    }

}
