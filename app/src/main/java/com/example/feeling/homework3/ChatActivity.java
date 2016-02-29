package com.example.feeling.homework3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
    public static String LOG_TAG = "My Logggggggg";

    private LocationData locationData;
    private float latitude;
    private float longitude;

    SharedPreferences prefs;
    private String nickname;
    private String user_id;

    public MyAdapter myAdapter;
    public ArrayList<ListElement> arrayList;
    ListView myListView;
    EditText chatBox;
    Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get user_id from SharedPreferences.
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        user_id = prefs.getString("user_id", null);

        // Get nickname from Intent.
        Intent intent = getIntent();
        nickname = intent.getStringExtra(MainActivity.nickname);

        // Get latitude and longitude from Singleton class locationData.
        locationData = LocationData.getLocationData();
        if (locationData != null && locationData.getLocation() != null) {
            latitude = (float) locationData.getLocation().getLatitude();
            longitude = (float) locationData.getLocation().getLongitude();
        }

        arrayList = new ArrayList<>();
        myAdapter = new MyAdapter(this, R.layout.list_element, arrayList);
        myListView = (ListView) findViewById(R.id.listView);
        myListView.setAdapter(myAdapter);

        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setEnabled(false);
        chatBox = (EditText) findViewById(R.id.chatBox);
        chatBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                checkMessageLength();
            }
        });

        refresh();
    }

    /**
     * Check the length of message in the chat box.
     * If the length is not 0, enable send button.
     */
    private void checkMessageLength() {
        if (chatBox.getText().toString().length() > 0) {
            sendButton.setEnabled(true);
        } else {
            sendButton.setEnabled(false);
        }
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

        MessageService service = retrofit.create(MessageService.class);
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
                    String message = resultList.getMessage();
                    String nickname = resultList.getNickname();
                    boolean self = false;
                    if (userId.equals(MainActivity.user_id)) {
                        nickname += "(You)";
                        self = true;
                    }

                    arrayList.add(new ListElement(message, nickname, self, true));
                }

                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public interface MessageService {
        @GET("get_messages")
        Call<MessageResponse> getMessage(
                @Query("lat") float latitude,
                @Query("lng") float longitude,
                @Query("user_id") String user_id
        );

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

    /**
     * This function is called when send button is clicked.
     * After call send(), it will clear the message in chat box.
     *
     * @param v
     */
    public void send(View v) {
        send();
        clearChatBox();
    }

    private void send() {
        final String message = chatBox.getText().toString();
        SecureRandomString srs = new SecureRandomString();
        String message_id = srs.nextString();

        /**
         * Add the posted message to arrayList, and call notifyDataSetChanged()
         * on ArrayAdapter, then it will be shown on the screen immediately.
         */
        arrayList.add(new ListElement(message, nickname, true, false));
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

        MessageService service = retrofit.create(MessageService.class);
        Call<Message> postMessageCall
                = service.postMessage(latitude, longitude, user_id, nickname, message, message_id);

        // Call retrofit asynchronously
        postMessageCall.enqueue(new Callback<Message>() {
            /**
             * Update arrayList and notify arrayAdapter when we get
             * response from HTTP server. In this way, we can change
             * the status of message on the screen.
             *
             * @param response
             */
            @Override
            public void onResponse(Response<Message> response) {
                arrayList.set(arrayList.size() - 1, new ListElement(message, nickname, true, true));
                myAdapter.notifyDataSetChanged();

                /**
                 * Call refresh after we get response from the server
                 * to update the messages on the screen again.
                 *
                 * What it does is append "(You)" to nickname.
                 */
                refresh();
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * Clear chat box when send button is clicked.
     */
    private void clearChatBox() {
        chatBox.getText().clear();
    }
}
