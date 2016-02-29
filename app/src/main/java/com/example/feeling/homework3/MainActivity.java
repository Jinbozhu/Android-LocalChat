package com.example.feeling.homework3;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feeling.homework3.response.MessageResponse;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {
    public static final int LOCATION_ACCURACY_THRESHOLD = 100;
    public static String user_id;
    public static String nickname = "mynickname";
    public String LOG_TAG = "REQUEST LOCATION";
    private LocationData locationData;
    private boolean accuracyOK;
    private boolean nicknameOK;
    EditText editText;
    Button chatButton;
    TextView locationInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationData = LocationData.getLocationData();
        locationInfo = (TextView) findViewById(R.id.locationInfo);

        // Get user_id from SharedPreferences.
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        user_id = settings.getString("user_id", null);

        // If user_id is null, create a random one and persist to SharedPreferences.
        if (user_id == null) {
            SecureRandomString srs = new SecureRandomString();
            user_id = srs.nextString();
            SharedPreferences.Editor e = settings.edit();
            e.putString("user_id", user_id);
            e.apply();
        }

        chatButton = (Button) findViewById(R.id.chatButton);
        editText = (EditText) findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                enableChatButton();
            }
        });
    }

    /**
     * Enable chat button if the word length in editText is not 0
     * and accuracy is good enough, disable it otherwise.
     */
    private void enableChatButton() {
        nicknameOK = editText.getText().toString().length() > 0;
        chatButton.setEnabled(nicknameOK && accuracyOK);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestLocationUpdate();
    }

    /**
     * Request location update. This must be called in onResume
     * if the user has allowed location sharing.
     */
    private void requestLocationUpdate() {
        LocationManager locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null &&
                (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 35000, 10, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 35000, 10, locationListener);

                Log.i(LOG_TAG, "requesting location update");
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        removeLocationUpdate();
    }

    /**
     * Remove location update. This must be called in onPause
     * if the user has allowed location sharing
     */
    private void removeLocationUpdate() {
        LocationManager locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                locationManager.removeUpdates(locationListener);
                Log.i(LOG_TAG, "removing location update");
            }
        }
    }

    /**
     * Listen to the location, get the location accuracy
     * and display latitude, longitude, and accuracy on screen.
     *
     * Disable chat button if accuracy is not good enough.
     */
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double accuracy = location.getAccuracy();
            Log.v(LOG_TAG, Double.toString(accuracy));

            if (accuracy < LOCATION_ACCURACY_THRESHOLD) {
                accuracyOK = true;
                locationData.setLocation(location);
                chatButton.setEnabled(nicknameOK && accuracyOK);
            } else {
                accuracyOK = false;
                chatButton.setEnabled(false);
            }

            /**
             * Have to put this block in locationListener in order to
             * get location on start.
             */
            String loc = "Requesting location...";
            if (locationData != null && locationData.getLocation() != null) {
                double latitude = locationData.getLocation().getLatitude();
                double longitude = locationData.getLocation().getLongitude();
                String lat = String.format("%10.5f", latitude);
                String lgn = String.format("%10.5f", longitude);
                String acc = String.format("%5.1f m", accuracy);
                loc = "Latitude:\t" + lat + ",\nLongitude:\t" + lgn + ",\nAccuracy:\t" + acc;
            }
            locationInfo.setText(loc);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    };
    
    public void chat(View v) {
        editText = (EditText) findViewById(R.id.editText);
        String name = editText.getText().toString();

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(nickname, name);
        startActivity(intent);
    }
}
