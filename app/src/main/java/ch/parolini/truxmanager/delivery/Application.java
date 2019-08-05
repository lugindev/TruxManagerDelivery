package ch.parolini.truxmanager.delivery;

import android.content.Context;

/**
 * Created by toni on 05.10.14.
 */
public class Application extends android.app.Application{

    private static Context context;

    public void onCreate(){
        super.onCreate();
        Application.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return Application.context;
    }
}
