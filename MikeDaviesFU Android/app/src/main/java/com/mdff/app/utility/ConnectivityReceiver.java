package com.mdff.app.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;




public class ConnectivityReceiver extends BroadcastReceiver {

    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {

        int status = NetworkUtils.getConnectivityStatus(context);

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(status);
        }
    }

    public static int isConnected(Context context) {
        return NetworkUtils.getConnectivityStatus(context);
    }


    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(int status);
    }
}
