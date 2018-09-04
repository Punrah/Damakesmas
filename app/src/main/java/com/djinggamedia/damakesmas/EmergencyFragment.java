package com.djinggamedia.damakesmas;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.djinggamedia.damakesmas.AsyncTask.MyAsyncTask;
import com.djinggamedia.damakesmas.app.AppConfig;
import com.djinggamedia.damakesmas.helper.SessionManager;
import com.djinggamedia.damakesmas.helper.UserSQLiteHandler;
import com.djinggamedia.damakesmas.persistence.User;
import com.djinggamedia.damakesmas.persistence.UserGlobal;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmergencyFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    EditText search;
ImageView searchButton;
    private List<Jadwal> listItem;
    private static RelativeLayout bottomLayout;
    private RecyclerView recyclerView;
    private JadwalAdapter mAdapter;
    private static LinearLayoutManager mLayoutManager;

    // Variables for scroll listener
    private boolean userScrolled = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    SwipeRefreshLayout swipeRefreshLayout;

    private UserSQLiteHandler db;
    private SessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myInflater = inflater.inflate(R.layout.fragment_emergency, container, false);

        db = new UserSQLiteHandler(getActivity().getApplicationContext());
        // session manager
        session = new SessionManager(getActivity().getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        swipeRefreshLayout = (SwipeRefreshLayout) myInflater.findViewById(R.id.swipe_refresh);

        mLayoutManager = new LinearLayoutManager(getActivity());
        search =(EditText) myInflater.findViewById(R.id.search_edit);
        searchButton =(ImageView) myInflater.findViewById(R.id.search_button);
        recyclerView = (RecyclerView) myInflater.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        listItem =new ArrayList<>();

        swipeRefreshLayout.setOnRefreshListener(this);

        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                search.setFocusable(true);
                search.setFocusableInTouchMode(true);
                return false;
            }
        });
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        new getJadwal(UserGlobal.getUser(getActivity())).execute();
                                    }
                                }
        );

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipeRefreshLayout.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                swipeRefreshLayout.setRefreshing(true);

                                                new getJadwal(UserGlobal.getUser(getActivity())).execute();
                                            }
                                        }
                );
            }
        });






        // Inflate the layout for this fragment
        return myInflater;
    }

    @Override
    public void onRefresh() {
        new getJadwal(UserGlobal.getUser(getActivity())).execute();
    }













    private class getJadwal extends MyAsyncTask {

        User user;

        public getJadwal(User user)
        {
            this.user =user;
        }




        @Override
        public Context getContext () {
            return getActivity();
        }



        @Override
        public void setSuccessPostExecute() {


            mAdapter = new JadwalAdapter(getActivity(),listItem);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);

            if(listItem.size()==0)
                recyclerView.setVisibility(View.INVISIBLE);
            else
                recyclerView.setVisibility(View.VISIBLE);


        }

        @Override
        public void setFailPostExecute() {
            swipeRefreshLayout.setRefreshing(false);
        }

        public void postData() {
            String url = AppConfig.getEmergencyJadwal(user.getTiket());
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

                        if (status.contentEquals("success")) {
                            isSucces = true;

                            JSONArray jsonArray = obj.getJSONArray("result");
                            listItem =new ArrayList<>();

                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Jadwal jadwal = new Jadwal();
                                jadwal.id=jsonObject.getString("id");
                                jadwal.nama=jsonObject.getString("nama");
                                jadwal.catatan=jsonObject.getString("catatan");
                                jadwal.norm=jsonObject.getString("norm");
                                jadwal.status=jsonObject.getString("status");
                                jadwal.tanggal=jsonObject.getString("tanggal");
                                jadwal.dep_id=jsonObject.getString("departemen_id");
                                jadwal.alamat=jsonObject.getString("alamat");
                                jadwal.nohp=jsonObject.getString("nohp");
                                jadwal.jk=jsonObject.getString("jk");
                                jadwal.noregister=jsonObject.getString("noregister");
                                jadwal.lat=jsonObject.getDouble("lat");
                                jadwal.lng=jsonObject.getDouble("lng");
                                listItem.add(jadwal);
                            }
                        }
                        else if(status.contentEquals("error"))
                        {
                            isSucces=true;
                            listItem=new ArrayList<>();
                        }
                        else if(status.contentEquals("loginfailed"))
                        {
                            logoutUser();
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

        @Override
        public void setPreloading() {

        }

        @Override
        public void setPostLoading() {

        }




    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAdapter.onActivityResult(requestCode, resultCode, data);

    }

    public void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }








}
