package com.meivaldi.youlanda.data.network;

import com.google.gson.annotations.SerializedName;

public class ResponseApi {
    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
