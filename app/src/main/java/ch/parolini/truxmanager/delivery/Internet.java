package ch.parolini.truxmanager.delivery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class Internet {

    private final Context _context;
    private BroadcastReceiver mWifiReceiver;

    public Internet(Context context) {
        _context = context;
    }

    public void registerWifiReceiver(BroadcastReceiver mWifiReceiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        _context.registerReceiver(mWifiReceiver, filter);
    }

    public void unregisterWifiReceiver(BroadcastReceiver mWifiReceiver) {
        _context.unregisterReceiver(mWifiReceiver);
    }

    public boolean checkWifiConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) AppContext.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null;
        } else {
            return false;
        }
    }




}
