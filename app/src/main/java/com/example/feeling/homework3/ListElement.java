package com.example.feeling.homework3;

/**
 * Created by feeling on 2/18/16.
 */
public class ListElement {
    String message;
    String nickname;
    boolean self;
    boolean delivered;

    public ListElement(String message, String nickname, boolean self, boolean delivered) {
        this.message = message;
        this.nickname = nickname;
        this.self = self;
        this.delivered = delivered;
    }
}
