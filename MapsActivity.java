package map.mymapretry;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener,GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ArrayList<Markers>markersList;
    private SharedPreferences preferences;
    private List<String>mylocationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mylocationList =new ArrayList<>();
        preferences = this.getPreferences(Context.MODE_PRIVATE);
        if (preferences!=null) {
            try {
                mylocationList = new ArrayList<>(preferences.getStringSet("key", null));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        markersList = new ArrayList<>();
        LoadMarkers load = new LoadMarkers();
        load.execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        String[] splited;
        if (mylocationList!=null) {
            try {
                for (String item : mylocationList) {
                    splited = item.split(",");
                    mMap.addMarker(new MarkerOptions().position(new LatLng((Double.valueOf(splited[0])), (Double.valueOf(splited[1])))));
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mylocationList.add(latLng.latitude+","+latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(latLng));
        Markers markers=null;
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address>addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            String countryName = addresses.get(0).getCountryName();
            String streetAddress = addresses.get(0).getAddressLine(0);
            markers = new Markers(countryName,latLng.longitude,latLng.latitude,streetAddress);

        } catch (IOException e) {
            e.printStackTrace();
        }
        markersList.add(markers);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (Markers item:markersList) {
            if ( item.getLatitude()== marker.getPosition().latitude&&item.getLongitude()== marker.getPosition().longitude){
                Intent intent = new Intent(this,map.mymapretry.MapActivity.class);
                intent.putExtra("countryName",item.getCountryName());
                intent.putExtra("streetAddress",item.getStreetAddress());
                intent.putExtra("latitude",item.getLatitude());
                intent.putExtra("longitude", item.getLongitude());
                startActivity(intent);
            }else{
                Log.v("MainActivity","emptyObjext");
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
            preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("key",new HashSet<>(mylocationList));
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataBaseHelper db = new DataBaseHelper(this,null,null,1);
        db.InsertMarker(markersList);


    }
    public class LoadMarkers extends AsyncTask<Void,Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                DataBaseHelper db = new DataBaseHelper(getApplicationContext(), null, null, 1);
                markersList = db.getMarkers();
            }catch (NullPointerException e ){
                e.printStackTrace();
            }
            return null;
        }
    }
}
