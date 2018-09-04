package com.djinggamedia.damakesmas.persistence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings.Secure;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by Startup on 1/27/17.
 */

public class User implements Parcelable {
    public String name;
    public String jabatan;
    public String alamat;
    public String photo;
    public String jk;
    public String nip;

    private String tiket;

    public User()
    {
        name="";
        jabatan ="";
        photo ="";
        setTiket("");
        alamat ="";
        setTiket("");
        jk ="";

    }

    public Bitmap getPhoto()
    {
        InputStream is = new ByteArrayInputStream(photo.getBytes(Charset.forName("UTF-8")));
        return BitmapFactory.decodeStream(is);
    }

    public static String getDeviceId(Context context)
    {
        return Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(name);
        dest.writeString(jabatan);
        dest.writeString(photo);
        dest.writeString(getTiket());
        dest.writeString(alamat);
        dest.writeString(getTiket());
        dest.writeString(jk);

    }
    // Method to recreate a Question from a Parcel
    public static Creator<User> CREATOR = new Creator<User>() {

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }

    };

    public User (Parcel parcel) {

        this.name=parcel.readString();
        this.jabatan = parcel.readString();
        this.setTiket(parcel.readString());
        this.photo=parcel.readString();
        this.alamat =parcel.readString();
        this.setTiket(parcel.readString());
        this.jk =parcel.readString();
    }


    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getTiket() {
        return tiket;
    }

    public void setTiket(String tiket) {
        this.tiket = tiket;
    }
}
