package com.shrobon.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

    TextView latTextView;
    TextView lonTextView;
    TextView altTextView;
    TextView accTextView;
    TextView addressTextView;

    public void updateLocationInfo(Location location) {
        Log.i("Location Info", location.toString());


        latTextView.setText("Latitude: " + location.getLatitude());
        lonTextView.setText("Longitude: " + location.getLongitude());
        altTextView.setText("Altitude: " + location.getAltitude());
        accTextView.setText("Accuracy: " + location.getAccuracy());

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            String address ="Could not find address";
            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            if(listAddresses != null && listAddresses.size()>0)
            {
                Log.i("Place info",listAddresses.get(0).toString());
                address ="Address: \n";
                if(listAddresses.get(0).getSubThoroughfare() !=null)
                {
                    address = address + listAddresses.get(0).getSubThoroughfare()+ " ";
                }

                if(listAddresses.get(0).getThoroughfare() !=null)
                {
                    address = address + listAddresses.get(0).getThoroughfare()+ "\n";
                }

                if(listAddresses.get(0).getLocality() !=null)
                {
                    address = address + listAddresses.get(0).getLocality()+ "\n";
                }

                if(listAddresses.get(0).getPostalCode() !=null)
                {
                    address = address + listAddresses.get(0).getPostalCode()+ "\n";
                }

                if(listAddresses.get(0).getCountryName() !=null)
                {
                    address = address + listAddresses.get(0).getCountryName()+ "\n";
                }
            }

            addressTextView.setText(address);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startListening();
            }
        }
    }

    public void startListening() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latTextView = (TextView)findViewById(R.id.latTextView);
        lonTextView = (TextView)findViewById(R.id.lonTextView);
        altTextView = (TextView)findViewById(R.id.altTextView);
        accTextView = (TextView)findViewById(R.id.accTextView);
        addressTextView = (TextView)findViewById(R.id.addressTextView);

        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);;
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Log.i("Location Info",location.toString());
                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(Build.VERSION.SDK_INT < 23)
        {
            startListening();
        }
        else
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else
            {
                startListening();
                Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                if(location != null)
                {
                    updateLocationInfo(location);
                }
            }
        }
    }
}
