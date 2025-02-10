package ch.parolini.truxmanager.delivery.basededonnee;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by Didier on 19.05.2017.
 */

public class DataBaseWrapper extends SQLiteOpenHelper {




    public DataBaseWrapper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //Colonnes table MESSAGE

    Context ctx;





    public static final String IMAGE = "IMAGE";
    public static final String IMAGE_ID = "_IMAGE_id";
    public static final String IMAGE_NOM = "_IMAGE_nom";
    public static final String IMAGE_NUMERO= "_IMAGE_numero";
    //Nom et vesion de la base de donnée
    public static final String DATABASE_NAME = "trux_manager.db";
    public static final int DATABASE_VERSION = 2;




    private static final String CREATION_TABLE_IMAGE = "create table " + IMAGE
            +"("+IMAGE_ID+" integer primary key autoincrement, "
            + IMAGE_NUMERO+" text , "
            + IMAGE_NOM+"  text );";

    //Table MESSAGE



    public DataBaseWrapper(Context context, String s, int i) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
        ctx=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATION_TABLE_IMAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<newVersion) {
            db.execSQL("DROP TABLE IF EXISTS IMAGE");
            onCreate(db);
        }

    }


}
