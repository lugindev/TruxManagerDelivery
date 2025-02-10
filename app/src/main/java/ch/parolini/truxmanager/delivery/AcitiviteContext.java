package ch.parolini.truxmanager.delivery;

import android.app.Application;

/**
 * Created by salam on 04.03.2019.
 */

public class AcitiviteContext {
    private static MainActivity activite;

 public AcitiviteContext(MainActivity activite){
     this.activite = activite;
 }

    public static MainActivity getContext() {
        return activite;
    }
}
