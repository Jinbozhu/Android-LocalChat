package com.example.feeling.homework3.response;

/**
 * Created by feeling on 2/17/16.
 */
//import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("org.jsonschema2pojo")
public class ResultList {

    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("nickname")
    @Expose
    private String nickname;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("message_id")
    @Expose
    private String messageId;

    /**
     *
     * @return
     * The timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     *
     * @param timestamp
     * The timestamp
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * @return
     * The nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     *
     * @param nickname
     * The nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     *
     * @return
     * The userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     *
     * @param userId
     * The user_id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     *
     * @return
     * The messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     *
     * @param messageId
     * The message_id
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

}