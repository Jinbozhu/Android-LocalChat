package com.example.feeling.homework3;

/**
 * Created by feeling on 2/18/16.
 */
public class Message {
    float latitude;
    float longitude;
    String user_id;
    String nickname;
    String message;
    String message_id;

    public Message(float latitude, float longitude, String user_id, String nickname,
                   String message, String message_id) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.user_id = user_id;
        this.nickname = nickname;
        this.message = message;
        this.message_id = message_id;
    }
}
