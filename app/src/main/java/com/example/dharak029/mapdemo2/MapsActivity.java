/*
* InClass 12
* Dharak Shah(800983321)
* MapActivity.java
* **/
package com.example.dharak029.mapdemo2;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager mLocationManager;
    LocationListener mLocationListener;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    PolylineOptions rectOptions = null;
    LatLng latlng;
    LatLngBounds.Builder bounds;
    String toast = "Start location tracking",title = "Start Location";
    int track = 0;
    boolean check = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        bounds = new LatLngBounds.Builder();


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //
                if(track==0){

                    track = 1;

                }
                else{
                    toast = "Stop location tracking";
                    title = "Stop Location";
                    check = false;
                    track = 1;
                }
                mMap.addMarker(new MarkerOptions().position(latlng).title(title));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                bounds.include(latlng);
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 30));
                Toast.makeText(MapsActivity.this,toast,Toast.LENGTH_LONG).show();
                Polyline polyline = mMap.addPolyline(rectOptions);
            }
        });


         //Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, // Activity
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }



        else if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("GPS not enabled")
                    .setMessage("Would you like to turn on GPS?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    finish();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }

        else{

                mLocationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(final Location location) {
                        if(check){
                            if(rectOptions != null){
                                Log.d("demo1",location.getLatitude()+","+location.getLongitude());
                                latlng  = new LatLng(location.getLatitude(),location.getLongitude());
                                bounds.include(latlng);
                                rectOptions.add(latlng);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 30));
                                Polyline polyline = mMap.addPolyline(rectOptions);
                            }
                            else {
                                rectOptions = new PolylineOptions();
                                latlng  = new LatLng(location.getLatitude(),location.getLongitude());
                                bounds.include(latlng);
                                rectOptions.add(latlng);
                                Polyline polyline = mMap.addPolyline(rectOptions);
                            }
                        }


                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                };

                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, mLocationListener);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted



                } else {
                    // permission was denied
                }
                return;
            }
        }
    }


}
