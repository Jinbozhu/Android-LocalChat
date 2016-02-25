package com.example.feeling.homework3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feeling.homework3.response.MessageResponse;
import com.example.feeling.homework3.response.ResultList;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by feeling on 2/17/16.
 */
public class ChatActivity extends AppCompatActivity {
    private LocationData locationData;
    private float latitude;
    private float longitude;
    public static String LOG_TAG = "My Logggggggg";

    SharedPreferences prefs;
    private static String nickname;
    private static String user_id;

    public static MyAdapter myAdapter;
    public static ArrayList<ListElement> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        user_id = prefs.getString("user_id", null);
        locationData = LocationData.getLocationData();

        /**
         * For debug purpose.
         */
        if (locationData != null && locationData.getLocation() != null) {
            latitude = (float) locationData.getLocation().getLatitude();
            longitude = (float) locationData.getLocation().getLongitude();

            String ll = Double.toString(locationData.getLocation().getLatitude()) + ","
                    + Double.toString(locationData.getLocation().getLongitude());
            Toast.makeText(this, ll, Toast.LENGTH_SHORT).show();
        }

        refresh();
    }

    @Override
    protected void onResume(){
        super.onResume();

        Intent intent = getIntent();
        nickname = intent.getStringExtra(MainActivity.nickname);

        arrayList = new ArrayList<>();
        myAdapter = new MyAdapter(this, R.layout.list_element, arrayList);
        final ListView myListView = (ListView) findViewById(R.id.listView);
        myListView.setAdapter(myAdapter);
    }


    public void refresh(View v) {
        refresh();
    }

    private void refresh() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://luca-teaching.appspot.com/localmessages/default/")
                .addConverterFactory(GsonConverterFactory.create())     //parse Gson string
                .client(httpClient)     //add logging
                .build();

        GetMessageService service = retrofit.create(GetMessageService.class);
        Call<MessageResponse> getMessageCall = service.getMessage(latitude, longitude, user_id);

        // Call retrofit asynchronously
        getMessageCall.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Response<MessageResponse> response) {
                Log.i(LOG_TAG, "Code is: " + response.code());
                if (response.body() != null)
                    Log.i(LOG_TAG, "The result is: " + response.body().getResultList());

                List<ResultList> messageList = response.body().getResultList();
                arrayList.clear();
                for (int i = messageList.size() - 1; i >= 0; i--) {
                    ResultList resultList = messageList.get(i);
                    String userId = resultList.getUserId();
                    String nickname = resultList.getNickname();
                    boolean self = false;
                    if (userId.equals(MainActivity.user_id)) {
                        nickname = nickname + "(You)";
                        self = true;
                    }
                    String message = resultList.getMessage();
                    String content = nickname + ": " + message;
                    arrayList.add(new ListElement(content, self, ""));
                }

                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public interface GetMessageService {
        @GET("get_messages")
        Call<MessageResponse> getMessage(
                @Query("lat") float latitude,
                @Query("lng") float longitude,
                @Query("user_id") String user_id
        );
    }

    public void send(View v) {
        send();
        clearChatBox();
    }

    private void send() {
        EditText chatBox = (EditText) findViewById(R.id.chatBox);
        String message = chatBox.getText().toString();
        SecureRandomString srs = new SecureRandomString();
        String message_id = srs.nextString();

        /**
         * add the posted message to arrayList, and call notifyDataSetChanged()
         * on ArrayAdapter, then it will be shown on the screen immediately.
         */
        arrayList.add(new ListElement(message, true, ""));
        myAdapter.notifyDataSetChanged();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://luca-teaching.appspot.com/localmessages/default/")
                .addConverterFactory(GsonConverterFactory.create())     //parse Gson string
                .client(httpClient)     //add logging
                .build();

        PostMessageService service = retrofit.create(PostMessageService.class);
        Call<Message> postMessageCall
                = service.postMessage(latitude, longitude, user_id, nickname, message, message_id);
        // Call retrofit asynchronously
        postMessageCall.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Response<Message> response) {
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void clearChatBox() {
        EditText chatBox = (EditText) findViewById(R.id.chatBox);
        chatBox.getText().clear();
    }

    public interface PostMessageService {
        @POST("post_message")
        Call<Message> postMessage(
                @Query("lat") float latitude,
                @Query("lng") float longitude,
                @Query("user_id") String user_id,
                @Query("nickname") String nickname,
                @Query("message") String message,
                @Query("message_id") String message_id
        );
    }
}
