package ch.parolini.truxmanager.delivery;

import android.view.View;


/**
 * Created by Didier on 07.10.2016.
 */
public interface ClickListener {
    void onClick(View view, int position) throws Exception;

    void onLongClick(View view, int position);
}
