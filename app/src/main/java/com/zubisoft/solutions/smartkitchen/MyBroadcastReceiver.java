package com.zubisoft.solutions.smartkitchen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Action: " + intent.getAction() + "\n");
//        sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
//        String log = sb.toString();
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = manager.getAllNetworkInfo();
        for (int i = 0; i < networkInfo.length; i++) {
            Toast.makeText(context, networkInfo[i].getTypeName() + ":" + (networkInfo[i].isConnected() ? "Connected" : "Not connected"), Toast.LENGTH_SHORT).show();
        }
//        if (networkInfo!=null)
//        if (networkInfo.isConnected()){
//            Toast.makeText(context, "Network connected", Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(context, "Network disconnected", Toast.LENGTH_SHORT).show();
//        }
//
//        Log.d(TAG, log);
//        Toast.makeText(context, log, Toast.LENGTH_LONG).show();
    }
}