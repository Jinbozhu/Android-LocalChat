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
    public static final int LOCATION_ACCURACY = 100;
    public static String user_id;
    public static String nickname = "mynickname";
    public String LOG_TAG = "REQUEST LOCATION";
    private LocationData locationData;
    private boolean accuracyOK = false;
    private boolean nicknameOK = false;
    EditText editText;
    Button chatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationData = LocationData.getLocationData();

        // Get the settings, and create a random user id if missing.
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        user_id = settings.getString("user_id", null);

        if (user_id == null) {
            // Create a random user_id, and persist to SharedPreferences.
            SecureRandomString srs = new SecureRandomString();
            user_id = srs.nextString();
            Toast.makeText(this, user_id, Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor e = settings.edit();
            e.putString("user_id", user_id);
            e.apply();
        }

        editText = (EditText) findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                validateNickname();
            }
        });

        // Disable chatButton on start
        chatButton = (Button) findViewById(R.id.chatButton);
        if (editText.getText().length() == 0) chatButton.setEnabled(false);
    }

    /**
     * Enable chat button if the word length in editText is not 0.
     * Disable it otherwise.
     */
    public void enableChatIfReady() {
        editText = (EditText) findViewById(R.id.editText);
        chatButton = (Button) findViewById(R.id.chatButton);
        boolean isReady = (editText.getText().toString().length() > 0) && accuracyOK;
        chatButton.setEnabled(isReady);
    }

    private void validateNickname() {
        nicknameOK = editText.getText().toString().length() > 0;
        Toast.makeText(this, String.valueOf(nicknameOK), Toast.LENGTH_SHORT).show();
        chatButton.setEnabled(nicknameOK && accuracyOK);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestLocationUpdate();

//        String acc = String.valueOf(locationData.getLocation().getAccuracy());
//        Toast.makeText(this, acc, Toast.LENGTH_SHORT).show();
    }

    /**
     * Request location update. This must be called in onResume
     * if the user has allowed location sharing.
     */
    private void requestLocationUpdate(){
        LocationManager locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Log.i(LOG_TAG, "requesting location update");
            }
        }
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

    @Override
    public void onPause(){
        super.onPause();
        removeLocationUpdate();
    }

    /**
     * Listens to the location, and gets the most precise recent location.
     * Copied from Prof. Luca class code
     */
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double newAccuracy = location.getAccuracy();
            Toast.makeText(
                    getApplicationContext(),
                    "Accuracy: " + Double.toString(newAccuracy),
                    Toast.LENGTH_SHORT).show();
            Log.v(LOG_TAG, Double.toString(newAccuracy));

            // TO DO, THIS DOES NOT WORK
            if (newAccuracy < LOCATION_ACCURACY) {
                accuracyOK = true;
                locationData.setLocation(location);
//                chatButton.setClickable(nicknameOK && accuracyOK);
            }
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
