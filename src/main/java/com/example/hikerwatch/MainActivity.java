package com.example.hikerwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    LocationManager lm;
    LocationListener ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        ll = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                generate_location(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }
        };
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,ll);
            Location lastKnown = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
           if(lastKnown!=null){
               generate_location(lastKnown);
           }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,ll);
                }
            }
        }
    }

    void generate_location(Location l){

        Log.i("Location",l.toString());
        TextView lat = findViewById(R.id.lat);
        TextView lon = findViewById(R.id.lon);
        TextView acc = findViewById(R.id.acc);
        TextView alt = findViewById(R.id.alt);
        TextView add = findViewById(R.id.add);

        lat.setText("Latitude: "+Double.toString(l.getLatitude()).substring(0,7));
        lon.setText("Longitude: "+Double.toString(l.getLongitude()).substring(0,7));
        acc.setText("Accuracy: "+Double.toString(l.getAccuracy()));
        alt.setText("Altitude: "+Double.toString(l.getAltitude()));

        String addr = "Address:\n";
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> lst = gc.getFromLocation(l.getLatitude(),l.getLongitude(),1);
            if(lst!=null && lst.size()>0){
                addr += lst.get(0).getAddressLine(0)!=null?lst.get(0).getAddressLine(0):"Not found";
                add.setText(addr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}