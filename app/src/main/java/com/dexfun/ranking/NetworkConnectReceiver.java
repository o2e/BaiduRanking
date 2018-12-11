package com.dexfun.ranking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkConnectReceiver extends BroadcastReceiver {
    private NetWorkListener mNetWorkListener;

    public interface NetWorkListener {
        void networkConnect(boolean z);
    }

    public NetworkConnectReceiver(NetWorkListener netWorkListener) {
        this.mNetWorkListener = netWorkListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService("connectivity");
            Log.i("aaa", "CONNECTIVITY_ACTION");
            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            if (activeNetwork == null) {
                Log.i("1", "\u5f53\u524d\u6ca1\u6709\u7f51\u7edc\u8fde\u63a5\uff0c\u8bf7\u786e\u4fdd\u4f60\u5df2\u7ecf\u6253\u5f00\u7f51\u7edc ");
                this.mNetWorkListener.networkConnect(false);
            } else if (activeNetwork.isConnected()) {
                if (activeNetwork.getType() == 1) {
                    Log.i("2", "\u5f53\u524dWiFi\u8fde\u63a5\u53ef\u7528 ");
                } else if (activeNetwork.getType() == 0) {
                    Log.i("3", "\u5f53\u524d\u79fb\u52a8\u7f51\u7edc\u8fde\u63a5\u53ef\u7528 ");
                }
                this.mNetWorkListener.networkConnect(true);
            } else {
                Log.i("4", "\u5f53\u524d\u6ca1\u6709\u7f51\u7edc\u8fde\u63a5\uff0c\u8bf7\u786e\u4fdd\u4f60\u5df2\u7ecf\u6253\u5f00\u7f51\u7edc ");
                this.mNetWorkListener.networkConnect(false);
            }
        }
    }
}
