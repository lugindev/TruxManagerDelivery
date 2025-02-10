package ch.parolini.truxmanager.delivery;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class receiver2 extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = (level *100) / (float)scale;


        //messageSysteme(MainActivity.this, ".S.", "BATTERY_STATUS_CHARGING " +isCharging);
        //messageSysteme(MainActivity.this, ".S.", "BATTERY_PLUGGED_USB " +usbCharge);
        //messageSysteme(MainActivity.this, ".S.", "BATTERY_PLUGGED_AC " +acCharge);
        //messageSysteme(MainActivity.this, ".S.", "NIVEAU DE CHARGE " +batteryPct);
        ////Log.i("forground", intent.getAction());

        switch (intent.getAction()) {

            case Intent.ACTION_CLOSE_SYSTEM_DIALOGS:
                //messageSysteme(MainActivity.this, ".S.", "ACTION_SCREEN_OFF");
                ////Log.i("forground", ".S. ACTION_CLOSE_SYSTEM_DIALOGS Recever");

                break;

            case Intent.ACTION_SCREEN_OFF:
                //messageSysteme(MainActivity.this, ".S.", "ACTION_SCREEN_OFF");
                ////Log.i("forground", ".S. ACTION_SCREEN_OFF");

                break;

            case Intent.ACTION_SCREEN_ON:
                //messageSysteme(MainActivity.this, ".S.", "ACTION_SCREEN_ON");
                ////Log.i("forground", ".S. ACTION_SCREEN_ON");

                break;

            case Intent.ACTION_USER_PRESENT:
                //messageSysteme(MainActivity.this, ".S.", "ACTION_USER_PRESENT");
                ////Log.i("forground", "ACTION_USER_PRESENT");


                break;

            case Intent.ACTION_BOOT_COMPLETED:
                //messageSysteme(MainActivity.this, ".S.", "ACTION_BOOT_COMPLETED");
                ////Log.i("forground", ".S. ACTION_BOOT_COMPLETED");

                break;

            case Intent.ACTION_SHUTDOWN:

                break;

            case Intent.ACTION_REBOOT:
                //messageSysteme(MainActivity.this, ".S.", "ACTION_REBOOT");


                ////Log.i("forground", ".S. ACTION_REBOOT");

                break;

            case Intent.ACTION_BATTERY_LOW:
                //messageSysteme(MainActivity.this, ".S.", "ACTION_BATTERY_LOW");
                ////Log.i("forground", ".S. ACTION_BATTERY_LOW");

                break;

            case Intent.ACTION_BATTERY_OKAY:
                //messageSysteme(MainActivity.this, ".S.", "ACTION_BATTERY_OKAY");
                ////Log.i("forground", ".S. ACTION_USER_PRESENT");

                break;

            case Intent.ACTION_POWER_CONNECTED:
                //messageSysteme(MainActivity.this, ".S.", "ACTION_POWER_CONNECTED");
                ////Log.i("forground", ".S. ACTION_POWER_CONNECTED");
                //ConfigurationConnection();

                break;

            case Intent.ACTION_POWER_DISCONNECTED:
                //messageSysteme(MainActivity.this, ".S.", "ACTION_POWER_DISCONNECTED");
                ////Log.i("forground", ".S. ACTION_POWER_DISCONNECTED");
                //ConfigurationConnection();

                break;

            case Intent.ACTION_DATE_CHANGED:
                //messageSysteme(MainActivity.this, ".S.", "ACTION_POWER_DISCONNECTED");
                ////Log.i("forground", ".S. ACTION_DATE_CHANGED");
                //ConfigurationConnection();

                break;



            default:
                break;
        }
    }


}
