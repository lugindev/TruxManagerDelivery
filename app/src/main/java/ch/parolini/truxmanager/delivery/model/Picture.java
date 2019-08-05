package ch.parolini.truxmanager.delivery.model;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Toni on 02.10.2016.
 */
public class Picture implements Serializable {


    private File file ;
    private boolean isSent = false ;

    public Picture() {
    }

    public File getFile() {
        return file;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean isSent) {
        this.isSent = isSent;
    }


    public Picture(File file) {
        this.file = file;
    }

//    public void delete() {
//        this.file.delete();
//    }
}
