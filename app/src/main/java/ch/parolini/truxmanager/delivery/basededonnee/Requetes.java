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
    private final String[] IMAGES_TABLE={DataBaseWrapper.IMAGE_ID,DataBaseWrapper.IMAGE_NOM,DataBaseWrapper.IMAGE_NUMERO};
    private final String[] SELECT_IMAGES={DataBaseWrapper.IMAGE_NOM};

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

            /*Cursor cursor = database.query(DataBaseWrapper.IMAGE,
                    IMAGES_TABLE, null, null, null, null, DataBaseWrapper.IMAGE_ID);
            if (cursor.moveToFirst()) {
                cursor.moveToFirst();
            }
            while (!cursor.isAfterLast()) {
                String[] image = getImage(cursor);
                images.add(image);
                //////Log.i("selectImageId",images.get(0).toString());
                //////Log.i("selectImageName",images.get(1).toString());
                cursor.moveToNext();
            }
            cursor.close();*/

        Cursor cursor = database.rawQuery(" SELECT * FROM " + DataBaseWrapper.IMAGE + " Order BY " + DataBaseWrapper.IMAGE_ID , null);

        //int position1 =  countRow ();

        cursor.moveToPosition(-1);
        while(cursor.moveToNext()){
            String[] image = getImage(cursor);
            images.add(image);
        }

        cursor.close();



        return images;
    }

    public List<String[]> selectImagesNumero(String numero) {
        List images = new ArrayList();

        Cursor cursor = database.query(DataBaseWrapper.IMAGE,
                IMAGES_TABLE, DataBaseWrapper.IMAGE_NUMERO+ " = ?", new String[]{numero}, null, null,null,null);
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
        }
        while (!cursor.isAfterLast()) {
            String[] image = getImageByName(cursor);
            images.add(image);
            //////Log.i("selectImageId",images.get(0).toString());
            //////Log.i("selectImageName",images.get(1).toString());
            cursor.moveToNext();
        }
        cursor.close();

        return images;
    }


    public List<String[]> selectImagesByName(String nom) {
        List images = new ArrayList();

            Cursor cursor = database.query(DataBaseWrapper.IMAGE,
                    IMAGES_TABLE, DataBaseWrapper.IMAGE_NOM+ " = ?", new String[]{nom}, null, null,null,null);
            if (cursor.moveToFirst()) {
                cursor.moveToFirst();
            }
            while (!cursor.isAfterLast()) {
                String[] image = getImageByName(cursor);
                images.add(image);
                //////Log.i("selectImageId",images.get(0).toString());
                //////Log.i("selectImageName",images.get(1).toString());
                cursor.moveToNext();
            }
            cursor.close();

        return images;
    }


    private String[] getImage(Cursor cursor) {
        String[] image = new String[3];

        image[0] = cursor.getString(0);
        image[1] = cursor.getString(2);
        image[2] = cursor.getString(1);


        return image;
    }

    private String[] getImageByName(Cursor cursor) {
        String[] image = new String[2];

        image[0] = cursor.getString(0);
        image[1] = cursor.getString(1);


        return image;
    }





    public void ajouterImage(String path , String numero) {
        ContentValues values = new ContentValues();
        values.put(DataBaseWrapper.IMAGE_NOM, path);
        values.put(DataBaseWrapper.IMAGE_NUMERO, numero);

        synchronized(Lock) {
            long id = database.insert(DataBaseWrapper.IMAGE, null, values);
            //Log.i("id", String.valueOf(id));
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



    public void effacerImages(String id){

        //database.delete(DataBaseWrapper.MESSAGE,  "MESSAGE = ?", new String[] { id });
        database.delete(DataBaseWrapper.IMAGE,DataBaseWrapper.IMAGE_NUMERO+" = ? ", new String[] { id });
        //database.execSQL("DELETE FROM " + DataBaseWrapper.IMAGE);

    }

    public void effacerImages1(){
        database.execSQL("DELETE FROM " + DataBaseWrapper.IMAGE);
    }

    public int  effacerIamgeById(String id) {
        synchronized(Lock) {
            //database.execSQL("delete from "+DataBaseWrapper.IMAGE+" where "+DataBaseWrapper.IMAGE_ID+"='"+id+"'");
           int i =  database.delete(DataBaseWrapper.IMAGE,  DataBaseWrapper.IMAGE_ID+" = ? ", new String[] { id });
           return i;
        }

    }

    public void effacerIamgeByName(String path) {
        synchronized(Lock) {
            database.execSQL("delete from "+DataBaseWrapper.IMAGE+" where "+DataBaseWrapper.IMAGE_NOM+"='"+path+"'");
        }
    }
}



