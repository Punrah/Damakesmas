package com.djinggamedia.damakesmas;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.djinggamedia.damakesmas.AsyncTask.MyAsyncTask;
import com.djinggamedia.damakesmas.app.AppConfig;
import com.djinggamedia.damakesmas.persistence.User;
import com.djinggamedia.damakesmas.persistence.UserGlobal;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class JadwalAdapter extends RecyclerView.Adapter<JadwalAdapter.MyViewHolder> {

    private List<Jadwal> itemList;
    private Context context;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 123 ;
    String norm;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView norm;
        public TextView nama;
        public TextView jk;
        public TextView tanggal;
        public TextView alamat;
        public TextView catatan;
        public TextView daftarkan;
        public LinearLayout telepon;
        public LinearLayout map;
        public LinearLayout daftar;
        public LinearLayout tandai;
        public ImageView pic;

        public MyViewHolder(View view) {
            super(view);
            norm = (TextView) view.findViewById(R.id.norm);
            nama = (TextView) view.findViewById(R.id.name);
            tanggal = (TextView) view.findViewById(R.id.tanggal);
            alamat = (TextView) view.findViewById(R.id.alamat);
            catatan = (TextView) view.findViewById(R.id.catatan);
            telepon = (LinearLayout) view.findViewById(R.id.telepon);
            map = (LinearLayout) view.findViewById(R.id.arah);
            daftarkan=(TextView) view.findViewById(R.id.daftar_text);
            daftar = (LinearLayout) view.findViewById(R.id.daftar);
            tandai = (LinearLayout) view.findViewById(R.id.tandai);
            pic = (ImageView) view.findViewById(R.id.pic);
        }
    }


    public JadwalAdapter(Context context, List<Jadwal> moviesList) {
        this.itemList = moviesList;
        this.context=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_jadwal, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Jadwal item = itemList.get(position);
        holder.norm.setText(item.getNorm());
        holder.nama.setText(item.getNama());

        // *** note that it's "yyyy-MM-dd hh:mm:ss" not "yyyy-mm-dd hh:mm:ss"
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = null;
        try {
            date = dt.parse(item.getTanggal());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // *** same for the format String below
        SimpleDateFormat dt1 = new SimpleDateFormat("dd MMMM yyyy");
        holder.tanggal.setText(dt1.format(date));
        holder.alamat.setText(item.getAlamat());
        holder.catatan.setText(item.getCatatan());

        if(item.jk.contentEquals("l"))
        {
            holder.pic.setImageResource(R.drawable.male);
        }
        else
        {
            holder.pic.setImageResource(R.drawable.female);
        }
        if(item.noregister.contentEquals("null")) {
            holder.daftarkan.setText("Daftarkan");
            holder.daftar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context,WebActivity.class);
                    i.putExtra("id",item.id);
                    context.startActivity(i);
                }
            });

        }
        else
        {
            holder.daftarkan.setText("Rekam Medis");
            holder.daftar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context,WebActivity.class);
                    i.putExtra("noregister",item.noregister);
                    context.startActivity(i);
                }
            });

        }

        holder.telepon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", item.getNohp(), null));
                context.startActivity(intent);
            }
        });
        holder.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr="+item.lat+","+item.lng));
                context.startActivity(intent);
            }
        });
        holder.tandai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                norm = item.norm;
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                    ((Activity) context).startActivityForResult(builder.build(((Activity)context)), PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }

            }
        });


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(context, data);
                LatLng latlng = place.getLatLng();
                new doTandai(UserGlobal.getUser(context),norm,Double.toString(latlng.latitude),Double.toString(latlng.longitude)).execute();


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(context, data);
                // TODO: Handle the error.

                Toast.makeText(context, status.getStatusMessage(), Toast.LENGTH_SHORT).show();


            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }


    }

    private class doTandai extends MyAsyncTask {

        User user;
        String norm;
        String lat;
        String lng;

        public doTandai(User user,String norm,String lat, String lng)
        {
            this.user =user;
            this.norm=norm;
            this.lat=lat;
            this.lng=lng;

        }




        @Override
        public Context getContext () {
            return context;
        }



        @Override
        public void setSuccessPostExecute() {

            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void setFailPostExecute() {

        }

        public void postData() {
            String url = AppConfig.getUrlTandai(user.getTiket(),norm,lat,lng);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            try {
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");

                if (jsonStr != null) {
                    try {
                        JSONObject obj = new JSONObject(jsonStr);
                        status = obj.getString("status");
                        msg=obj.getString("message");
                        if (status.contentEquals("success")) {
                            isSucces = true;

                        }
                        else {
                            msg=obj.getString("message");
                            alertType=DIALOG;
                        }
                    } catch (final JSONException e) {
                        badServerAlert();
                    }
                } else {
                    badServerAlert();
                }
            } catch (IOException e) {
                badInternetAlert();
            }
        }


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}