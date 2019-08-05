package ch.parolini.truxmanager.delivery;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.CollationElementIterator;

public class SerciceRedemarrage extends Service {
    private static final String EXTRA_RETURN_MESSAGE = "Pas de message";
    public Snackbar mySnackbar;

    public SerciceRedemarrage() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this,"Il reste des photos à télécharger merci de ne pas éteindre l'application",Toast.LENGTH_LONG).show();
        doSendBroadcast("Si il reste des photos à télécharger merci de ne pas fermer l'application.");
        Log.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent mStartActivity = new Intent(this, MainActivity.class);
            if(VariablesGlobales._notSendOrders==true) {
                Log.e("ClearFromRecentService", "END");
                stopSelf();

                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, mStartActivity,
                        PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);

            }else {
                stopSelf();
            }
        super.onTaskRemoved(mStartActivity);


    }

    public void doSendBroadcast(String message) {
        Intent it = new Intent("EVENT_SNACKBAR");
            it.putExtra(EXTRA_RETURN_MESSAGE,message);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(it);
    }

}
