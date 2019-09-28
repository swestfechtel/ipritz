package com.fhaachen.ip_ritz.prototyp.data;

import android.os.AsyncTask;
import android.util.Log;

import com.fhaachen.ip_ritz.prototyp.data.model.Order;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class OrderDataUpdateTarget extends AsyncTask<Order, Float, Boolean> {
    @Override
    public Boolean doInBackground(Order... params) {
        Order order = params[0];
        try {
            URL server = new URL(LoginDataSource.serverAddress + "/orders/" + order.getId().get$oid());
            HttpURLConnection connection = (HttpURLConnection) server.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");

            Gson gson = new Gson();
            String payload = gson.toJson(order);
            Log.i("OrderDataUpdateTarget", payload);

            OutputStream os = connection.getOutputStream();
            os.write(payload.getBytes());
            os.flush();

            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + connection.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (connection.getInputStream())));

            int responseCount = 0;
            String output = "";
            while ((output = br.readLine()) != null) {
                responseCount += Integer.parseInt(output);
            }
            if (responseCount < 1)
                throw new RuntimeException("Error updating order: getMatchedCount was less than 1.");

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("OrderDataUpdateTarget", "URL connection error. " + e.getMessage());
            return false;
        }
        return true;
    }
}
