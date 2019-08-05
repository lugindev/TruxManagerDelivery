package ch.parolini.truxmanager.delivery.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.parolini.truxmanager.delivery.AppContext;
import ch.parolini.truxmanager.delivery.Manager.OrderManager;


/**
 * Implement Serializable to be sent on notification broadcast
 * Created by toni on 21.06.14.
 */
public class Order implements Serializable{


    private String orderNumber ;
    private Date lastPictureUpdatDate = null ;
    private List<Picture> pictureList = new ArrayList<>();
    private Date scanDate ;
    private transient Bitmap bitmap; // should not be serialized
    public static boolean preview=false;

//    @Override
//    public boolean equals(Object o) {
//        Order objToCompare = (Order) o ;
//        if (this.orderNumber!=null) {
//            return this.orderNumber.equals(objToCompare.getOrderNumber());
//        } else {
//            return false ;
//        }
//    }
//
//    @Override
//    public int hashCode() {
//        return orderNumber.hashCode();
//    }

    public Bitmap getBitmap(int i) {
        File imgFile = this.getDefaultPictureFile(i) ;
        if(imgFile!=null && imgFile.exists()) {
            bitmap = builThumbFromFile(imgFile);
        }
        return bitmap;
    }

    public Bitmap getRealBitmapSize(int i) {
        File imgFile = this.getDefaultPictureFile(i) ;
        if(imgFile!=null && imgFile.exists()) {
            bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return bitmap;
    }

    private Bitmap builThumbFromFile(File imgFile) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        //bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bytes);
        return Bitmap.createScaledBitmap(bitmap, 290, 290, false);
    }


    public static File savebitmap(Bitmap bmp, String path) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
        File f = new File(path);
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
        return f;
    }

    public List<Picture> getPictures() {
        return pictureList;
    }

    public Order() {
    }

    public Order(String orderNumber, Date scanDate) {
        this.scanDate = scanDate;
        this.orderNumber = orderNumber ;
    }


    public String getOrderNumber() {
        return orderNumber;
    }


    public void addNewPicture(File file) {
        Picture picture = new Picture(file);
        this.pictureList.add(picture);
        lastPictureUpdatDate = new Date() ;

    }

    public File getDefaultPictureFile() {
        if(getPictureCount()>0) {
            return pictureList.get(0).getFile();
        } else {
            return null ;
        }
    }

    public File getDefaultPictureFile(int i) {
        if(getPictureCount()>0) {
            return pictureList.get(i).getFile();
        } else {
            return null ;
        }
    }


    public boolean hasPictureFiles() {
        return getPictureCount() > 0 ;
    }

    public int getPictureCount() {
        return getPictures().size();
    }


    /**
     * Used to avoid uploading image while taking a picture
     */
    public void touch() {
        if(lastPictureUpdatDate !=null) {
            lastPictureUpdatDate = new Date() ;
        }
    }

    /**
     *
     */
/*    public void deleteFiles() {
        for (Picture picture : getPictures()) {
            picture.delete();
        }
        cleanImageFiles();
    }*/

    public void cleanImageFiles() {
        pictureList = new ArrayList<>();
        lastPictureUpdatDate = null ;
        bitmap = null ;

    }

    public void cleanImageFilesOnPhone(Order ordre) {
        pictureList = ordre.getPictures();
        if(pictureList.size()!=0) {
            for (Picture picture : pictureList) {
                File target = new File(picture.getFile().getPath());
                File target1 = new File(picture.getFile().getPath().substring(0, picture.getFile().getPath().length() - 4) + "_preview.jpeg");
                if (target1.exists() && target1.isFile() && target1.canWrite()) {
                    target1.delete();
                    Log.d("d_file", "" + target1.getName());
                }

                if(lectureDesParametres("action_fichier").equals("effacer")) {
                    if (target.exists() && target.isFile() && target.canWrite()) {
                        target.delete();
                        Log.d("d_file", "" + target.getName());
                    }
                }


            }
            pictureList = new ArrayList<>();
            lastPictureUpdatDate = null;
            bitmap = null;

        }

    }


/*    public void deleteFile(File file) {
        if (pictureList !=null) {
            try {
                file.delete();
                pictureList.remove(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

    public Date getLastPictureUpdatDate() {
        return lastPictureUpdatDate;
    }


    public boolean isOldEnoughToBeUploaded() {

        if (lastPictureUpdatDate ==null) {
            // Weighning never updated
            return false ;
        }
        final int maxAgeInSeconds = 60 * OrderManager.getMaxAgeMinutes(); // 5 minutes
        Date now = new Date() ;
        long ageInSeconds = (now.getTime() - getLastPictureUpdatDate().getTime()) / 1000 ;
        return ( getPictureCount()>0  && ageInSeconds > maxAgeInSeconds ) ;

    }


    public boolean areAllPicturesSent() {
        if (hasPictureFiles()) {
            for (Picture pic : this.pictureList) {
                if (!pic.isSent()) {
                    return false;
                }
            }
            return true ;
        } else {
            return false ;
        }
    }

    public Date getScanDate() {
        return scanDate;
    }


    public void deleteNumbre() {
        pictureList.remove(0);
    }

    private static String lectureDesParametres(String key) {
        String datas = "";
        String prefs = AppContext.getAppContext().getSharedPreferences("params", Context.MODE_PRIVATE).getString(key, datas);
        return prefs;


    }
}
