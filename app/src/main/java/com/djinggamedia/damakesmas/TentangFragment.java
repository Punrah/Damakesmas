package com.djinggamedia.damakesmas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class TentangFragment extends Fragment {

TextView avila, jingga, version;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myInflater=inflater.inflate(R.layout.fragment_tentang, container, false);
        avila =(TextView) myInflater.findViewById(R.id.copyright);
        jingga=(TextView) myInflater.findViewById(R.id.supported);
        version=(TextView) myInflater.findViewById(R.id.textView2);

        avila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.denpasarkota.go.id"));
                startActivity(browserIntent);
            }
        });

        jingga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.djinggamedia.com"));
                startActivity(browserIntent);
            }
        });

        String ve="Damakesmas Mobile V"+BuildConfig.VERSION_NAME;

        version.setText(ve);
        return myInflater;
    }


}
