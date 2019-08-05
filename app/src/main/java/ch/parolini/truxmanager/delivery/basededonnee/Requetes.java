package ch.parolini.truxmanager.delivery.basededonnee;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Didier on 19.05.2017.
 */

public class Requetes {


    public static String Lock = "dblock";
    private DataBaseWrapper dbHelper;

    private SQLiteDatabase database;
    private  String[] IMAGES_TABLE={DataBaseWrapper.IMAGE_ID,DataBaseWrapper.IMAGE_NOM};
    private  String[] SELECT_IMAGES={DataBaseWrapper.IMAGE_NOM};

    public Requetes() {

    }

    public Requetes(Context context) {
        synchronized(Lock) {
            dbHelper = new DataBaseWrapper(context,"trux_manager.db",1);
        }

    }

    public Requetes(Runnable runnable) {

    }

    public void open() {

        synchronized(Lock) {
            database = dbHelper.getWritableDatabase();
        }
    }
    public void close() {

        synchronized(Lock) {
            if(database!=null) {
                database.close();
            }
        }


    }


    public List<String[]> selectImages() {
        List images = new ArrayList();
        try {
            Cursor cursor = database.query(DataBaseWrapper.IMAGE,
                    IMAGES_TABLE, null, null, null, null, DataBaseWrapper.IMAGE_ID);
            if (cursor.moveToFirst()) {
                cursor.moveToFirst();
            }
            while (!cursor.isAfterLast()) {
                String[] image = getImage(cursor);
                images.add(image);
                Log.i("selectImageId",images.get(0).toString());
                Log.i("selectImageName",images.get(1).toString());
                cursor.moveToNext();
            }
            cursor.close();
        }
        catch (Exception e){
            String t=e.getMessage();
            t="";
        }

        return images;
    }


    public List<String[]> selectImagesByName(String nom) {
        List images = new ArrayList();
        try {
            Cursor cursor = database.query(DataBaseWrapper.IMAGE,
                    IMAGES_TABLE, DataBaseWrapper.IMAGE_NOM+ " = ?", new String[]{nom}, null, null,null,null);
            if (cursor.moveToFirst()) {
                cursor.moveToFirst();
            }
            while (!cursor.isAfterLast()) {
                String[] image = getImageByName(cursor);
                images.add(image);
                Log.i("selectImageId",images.get(0).toString());
                Log.i("selectImageName",images.get(1).toString());
                cursor.moveToNext();
            }
            cursor.close();
        }
        catch (Exception e){
            String t=e.getMessage();
            t="";
        }
        return images;
    }


    private String[] getImage(Cursor cursor) {
        String image[] = new String[2];

        image[0] = cursor.getString(0);
        image[1] = cursor.getString(1);


        return image;
    }

    private String[] getImageByName(Cursor cursor) {
        String image[] = new String[2];

        image[0] = cursor.getString(0);
        image[1] = cursor.getString(1);


        return image;
    }





    public void ajouterImage(String path) {
        ContentValues values = new ContentValues();
        values.put(DataBaseWrapper.IMAGE_NOM, path);

        synchronized(Lock) {
            long id = database.insert(DataBaseWrapper.IMAGE, null, values);
        }
        // now that the student is created return it ...
        /*Cursor cursor = database.query(DataBaseWrapper.PESAGE,

                PESAGE_TABLE_AJOUTER, DataBaseWrapper.MESSAGE_ID + " = "
                        + id, null, null, null, null,null);
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
        }

        Pesage message = AjouterPesage(cursor);
        cursor.close();
        return message;*/
    }



    public void effacerImages(){

        //database.delete(DataBaseWrapper.MESSAGE,  "MESSAGE = ?", new String[] { id });
        database.delete(DataBaseWrapper.IMAGE,null,null);
        //database.execSQL("DELETE FROM " + DataBaseWrapper.MESSAGE + " WHERE " + DataBaseWrapper.MESSAGE_ID  + "= '" + id + "'");

    }

    public void effacerIamgeById(String id) {
        synchronized(Lock) {
            database.execSQL("delete from "+DataBaseWrapper.IMAGE+" where "+DataBaseWrapper.IMAGE_ID+"='"+id+"'");
        }

    }

    public void effacerIamgeByName(String path) {
        synchronized(Lock) {
            database.execSQL("delete from "+DataBaseWrapper.IMAGE+" where "+DataBaseWrapper.IMAGE_NOM+"='"+path+"'");
        }
    }
}



