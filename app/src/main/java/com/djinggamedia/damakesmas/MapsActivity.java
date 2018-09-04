package com.djinggamedia.damakesmas;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.djinggamedia.damakesmas.AsyncTask.MyAsyncTask;
import com.djinggamedia.damakesmas.app.AppConfig;
import com.djinggamedia.damakesmas.persistence.User;
import com.djinggamedia.damakesmas.persistence.UserGlobal;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements  OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 123 ;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;

    Marker marker;
    ImageView back;
    Mobil mobil;
    LatLng location;
    ArrayList<Mobil> mobilList;

    DatabaseReference mDatabase;

    private GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    List<Marker> markers;
    boolean isCamNotOver=true;


    Location mLastLocation;
    double lat = 0, lng = 0;
    View mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        buildGoogleApiClient();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapView = mapFragment.getView();
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                onBackPressed();
            }
        });



        mobilList = new ArrayList<>();
        markers = new ArrayList<>();
        new fetchMobile(UserGlobal.getUser(this)).execute();



    }

    private class fetchMobile extends MyAsyncTask {
        User user;

        public fetchMobile(User user)
        {
            this.user=user;

        }



        @Override
        public Context getContext() {
            return MapsActivity.this;
        }

        @Override
        public void setSuccessPostExecute() {

            fetch();

        }

        @Override
        public void setFailPostExecute() {
            finish();
        }

        public void postData() {
            mobilList = new ArrayList<>();
            String url = AppConfig.getUrlGPS(user.getTiket());
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");

                if (jsonStr != null) {
                    try {

                        JSONObject jsonObject = new JSONObject(jsonStr);

                        JSONArray jsonArray= jsonObject.getJSONArray("result");
                        if(jsonArray.length()>0)
                        {
                            isSucces=true;
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonItem = jsonArray.getJSONObject(i);
                                Mobil item = new Mobil();
                                item.id=jsonItem.getString("id");
                                item.nama=jsonItem.getString("name");
                                item.lat=jsonItem.getDouble("lat");
                                item.lng=jsonItem.getDouble("lng");
                                mobilList.add(item);
                            }
                        }
                        else
                        {
                            badServerAlert();
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

    public void fetchMobil()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("mobil");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()) {
                    Mobil value = iterator.next().getValue(Mobil.class);

                    mobilList.add(value);
                }
                fetch();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }



    private void fetch()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date c= new Date();
        String date = sdf.format(c);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.mobil);

        for(int i=0;i<mobilList.size();i++) {
            markers.add(mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mobilList.get(i).getLat(), mobilList.get(i).getLng()))
                    //.icon(icon)
                    .title(mobilList.get(i).getNama())
                    .snippet("Tanggal: "+date+"\nAlamat: Device berada pada koordinat "+mobilList.get(i).getLat()+","+mobilList.get(i).getLng())
            ));

            markers.get(i).setTag(i);
            markers.get(i).showInfoWindow();




        }
            adjustCamera();
        updateMobil();




    }

    public  void updateMobil()
    {
        for(int i=0;i<mobilList.size();i++)
        {
            final int j=i;
            mDatabase = FirebaseDatabase.getInstance().getReference().child("mobil/"+mobilList.get(i).getId());
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Koordinat value = dataSnapshot.getValue(Koordinat.class);
                    markers.get(j).setPosition(new LatLng(value.getLat(),value.getLng()));

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }

    }






    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 0, 500);
        }
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });




    }

    private void adjustCamera()
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        for(Marker marker:markers)
        {
            builder.include(marker.getPosition());
        }



        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (height * 0.05); // offset from edges of the map 12% of screen


        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
mMap.moveCamera(cu);

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION,getApplicationContext(),MapsActivity.this)) {
            getMyLocation();

        }
        else
        {
            requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION,PERMISSION_REQUEST_CODE_LOCATION,getApplicationContext(),MapsActivity.this);
        }

    }

    private void getMyLocation()
    {
        mMap.setMyLocationEnabled(true);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lng = mLastLocation.getLongitude();
//            Intent intent = new Intent(MainActivity.this,InputKopiActivity.class);
//            intent.putExtra("latlang", new LatLng(lat,lng));
//            startActivity(intent);
            //new getODP().execute();


        }
    }


    public  void requestPermission(String strPermission, int perCode, Context _c, Activity _a){

        if (ActivityCompat.shouldShowRequestPermissionRationale(_a,strPermission)){
            Toast.makeText(getApplicationContext(),"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
        } else {

            ActivityCompat.requestPermissions(_a,new String[]{strPermission},perCode);
        }
    }

    public  boolean checkPermission(String strPermission,Context _c,Activity _a){
        int result = ContextCompat.checkSelfPermission(_c, strPermission);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getMyLocation();
                } else {

                    Toast.makeText(getApplicationContext(),"Permission Denied, You cannot access location data.",Toast.LENGTH_LONG).show();

                }
                break;

        }
    }




    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
}
