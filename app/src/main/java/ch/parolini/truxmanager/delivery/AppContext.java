package ch.parolini.truxmanager.delivery;

import android.app.Application;

/**
 * Created by salam on 04.03.2019.
 */

public class AppContext extends Application {
    private static AppContext instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static AppContext getAppContext() {
        return instance;
    }
}
