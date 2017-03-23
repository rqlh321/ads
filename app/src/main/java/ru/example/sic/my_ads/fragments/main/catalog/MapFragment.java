package ru.example.sic.my_ads.fragments.main.catalog;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;

import static ru.example.sic.my_ads.Parse.Constants.AD_ADDRESS;
import static ru.example.sic.my_ads.Parse.Constants.AD_COORDINATES;
import static ru.example.sic.my_ads.Parse.Constants.AD_TITLE;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SupportMapFragment mSupportMapFragment = SupportMapFragment.newInstance();
        getFragmentManager().beginTransaction()
                .replace(R.id.map, mSupportMapFragment)
                .commit();
        mSupportMapFragment.getMapAsync(this);
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                return false;
            }
        });
        getCoordinates(Parse.Data.categoryAds);
    }

    void addMarker(double lat, double lng, ParseObject ad) {
        LatLng adCoordinates = new LatLng(lat, lng);
        IconGenerator iconGenerator = new IconGenerator(getContext());
        TextView textView = new TextView(getContext());
        textView.setText(ad.getString(AD_TITLE));
        textView.setTextColor(Color.WHITE);
        iconGenerator.setContentView(textView);
        iconGenerator.setColor(Color.rgb(52, 204, 255));
        Bitmap icon = iconGenerator.makeIcon();
        MarkerOptions myMarkerOptions = new MarkerOptions()
                .title(ad.getString(AD_TITLE))
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                .position(adCoordinates)
                .snippet(ad.getObjectId());
        mMap.addMarker(myMarkerOptions);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(adCoordinates)      // Sets the center of the map to ad
                .zoom(3)                   // Sets the zoom
                .bearing(70)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
    }

    void getCoordinates(ArrayList<ParseObject> result) {
        for (ParseObject ad : result) {
            ParseGeoPoint coordinates = ad.getParseGeoPoint(AD_COORDINATES);
            double lat;
            double lng;
            if (coordinates != null && coordinates.getLatitude() != 0
                    && coordinates.getLongitude() != 0) {
                lat = coordinates.getLatitude();
                lng = coordinates.getLongitude();
                addMarker(lat, lng, ad);
            } else {
                try {
                    if (ad.getString(AD_ADDRESS) != null) {
                        Geocoder geocoder = new Geocoder(getContext());
                        List<Address> addresses = geocoder.getFromLocationName(ad.getString(AD_ADDRESS), 1);
                        if (addresses.size() > 0) {
                            lat = addresses.get(0).getLatitude();
                            lng = addresses.get(0).getLongitude();
                            addMarker(lat, lng, ad);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}