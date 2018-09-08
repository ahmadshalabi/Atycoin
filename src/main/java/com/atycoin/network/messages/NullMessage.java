package com.atycoin.network.messages;

import com.google.gson.Gson;

public class NullMessage implements NetworkMessage {
    @Override
    public String makeRequest() {
        StringBuilder request = new StringBuilder();

        String command = "null "; //space, Indicator to find command
        String message = new Gson().toJson(this);

        request.append(command);
        request.append(message);

        return String.valueOf(request);
    }
}