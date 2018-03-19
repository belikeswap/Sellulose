package com.sellulose.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by swapn on 19-Dec-17.
 */

public class RecentEmail implements Parcelable{

    String mRecipient, mSubject, mLastTracked;
    boolean mIsNew;

    public RecentEmail() {
    }

    public RecentEmail(String recipient, String subject, String lastTracked, boolean isNew) {

        this.mRecipient = recipient;
        this.mSubject = subject;
        this.mLastTracked = lastTracked;
        this.mIsNew = isNew;

    }

    public String getRecipient() {
        return mRecipient;
    }

    public void setRecipient(String recipient) {
        this.mRecipient = recipient;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String subject) {
        this.mSubject = subject;
    }

    public String getLastTracked() {
        return mLastTracked;
    }

    public void setLastTracked(String lastTracked) {
        this.mLastTracked = lastTracked;
    }

    public boolean isNew() {
        return mIsNew;
    }

    public void setNew(boolean aNew) {
        mIsNew = aNew;
    }

    public RecentEmail(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.mRecipient = data[0];
        this.mSubject = data[1];
        this.mLastTracked = data[2];
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.mRecipient,
                this.mSubject,
                this.mLastTracked});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public RecentEmail createFromParcel(Parcel in) {
            return new RecentEmail(in);
        }

        public RecentEmail[] newArray(int size) {
            return new RecentEmail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
