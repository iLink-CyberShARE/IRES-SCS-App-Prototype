package edu.utep.cs.cs4330.ires10.Presentation;

/**
 * <h1> Alert Fragment </h1>
 *
 * Inflation of view for the alert
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */

import androidx.fragment.app.Fragment;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import edu.utep.cs.cs4330.ires10.Location.GPSTracker;
import edu.utep.cs.cs4330.ires10.R;
import edu.utep.cs.cs4330.ires10.Utils.MyAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlertFragment extends Fragment {

    private double lat, lon;
    private TextView addressTextV;
    protected LocationManager locationManager;
    private ImageButton ib;
    private GPSTracker gps;
    private Geocoder geocoder;

    public AlertFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alert, container, false);
        ib = view.findViewById(R.id.panic_button);
        addressTextV = view.findViewById(R.id.addressTextV);

        gps = new GPSTracker(getActivity());
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        // Sends alert with location and timestamp
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ALERT = "alert";
                LatLng coord=getCoordinates();
                lat = coord.latitude;
                lon = coord.longitude;
                updateAddress();
                Toast.makeText(getActivity(), R.string.alert_msg, Toast.LENGTH_SHORT).show();
                new MyAsyncTask(getActivity()).execute(ALERT, lat, lon, System.currentTimeMillis());
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private LatLng getCoordinates() {
        Double lat = null;
        Double lon = null;
        //Get Location
        // check if GPS enabled
        if (gps.canGetLocation()) {
            lat = gps.getLatitude();
            lon = gps.getLongitude();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            // Default UTEP coordinates
            gps.showSettingsAlert();
            lat = 31.76771355145084;
            lon = -106.50184997506044;
        }
        return new LatLng(lat,lon);
    }

    private void updateAddress(){
        new Thread(()->{
            LatLng coord=getCoordinates();
            lat = coord.latitude;
            lon = coord.longitude;

            List<Address> addressList = null;
            try {
                Log.d("TEST", "geocoder: " + lat + " " + lon);
                addressList = geocoder.getFromLocation(lat,lon , 1);
                Log.d("TEST", "SIZE: " + addressList.get(0).getAddressLine(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

