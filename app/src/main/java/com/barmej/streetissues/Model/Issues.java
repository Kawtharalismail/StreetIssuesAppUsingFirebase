package com.barmej.streetissues.Model;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class Issues  implements Parcelable {
    public String name;
    public String description;
    public String photo;
    public GeoPoint location;
    public Timestamp date;



    public Issues(){}


    protected Issues(Parcel in) {
        name = in.readString();
        description = in.readString();
        photo = in.readString();
        date = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Creator<Issues> CREATOR = new Creator<Issues>() {
        @Override
        public Issues createFromParcel(Parcel in) {
            return new Issues(in);
        }

        @Override
        public Issues[] newArray(int size) {
            return new Issues[size];
        }
    };

    public Timestamp getDate() {
        return date;
    }


    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getFormattedDate(){
        return DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(date.toDate());
    }

    public Issues(String name, String description, String photo, GeoPoint location,Timestamp date) {
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.location = location;
        this.date=date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(photo);
        parcel.writeParcelable(date, i);
    }
}
