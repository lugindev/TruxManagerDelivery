package ch.parolini.truxmanager.delivery.Manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import ch.parolini.truxmanager.delivery.VariablesGlobales;

/**
 * Created by toni on 22.06.14.
 */
public class FileManager {

    static File imageFolder = null ;

    /**
     *
     *
     * @return
     */
    public static List<File> getAllFilesInTruxFolder() {
        List<File> filesForCurrentOrder = new ArrayList<File>();
        File[] filesArray = imageFolder.listFiles() ;
        return filesArray!=null?Arrays.asList(filesArray): new ArrayList<File>();

    }

//    public static File createNewImageFileFor(Order order) {
//        File folder = FileManager.getImageFolder();
//
//        return  new File(folder.getPath()
//                + "/" + createNewImageFileNameForOrder(order.getOrderNumber()));
//    }

    /**
     * Create a new file name suing the time HH_mm_ss as the file name.Must not be called in a loop !!!
     * @param orderNumber
     * @return
     */
    public static String createNewImageFileNameForOrder(String orderNumber) {
        //String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS").format(new Date());
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS").format(new Date());
        return orderNumber + "_" + timeStamp + "_" + VariablesGlobales._versionCode + ".jpeg" ;
    }

    public static File createNewImageFileForOrder(String orderNumber) {
        return new File(getImageFolder().getPath() + "/" + FileManager.createNewImageFileNameForOrder(orderNumber));
    }

    /**
     * Copy a gile from the gallery to the application folder, and rename correctly.
     * @param galleryFile
     * @param orderNumber
     * @return
     * @throws IOException
     */
    public static File copyImageFromGallery(File galleryFile, String orderNumber) throws IOException {
        InputStream in = new FileInputStream(galleryFile);
        File outFile = createNewImageFileForOrder(orderNumber);
        OutputStream out = new FileOutputStream(outFile);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
        return outFile;
    }

    public static String findOrderNumberFromFile(File file) {
        String fileName = file.getName() ;
        StringTokenizer tokenizer = new StringTokenizer(fileName,"_") ;
        String orderNumber = null ;
        if (tokenizer.hasMoreTokens()) {
            orderNumber = tokenizer.nextToken() ;
        }
        return orderNumber ;
    }


    public static List<File> getExistingImageInFolderForOrder(String order) {
        List<File> filesForCurrentOrder = new ArrayList<File>();
        List<File> allFiles = getAllFilesInTruxFolder() ;

        for (File file : allFiles ) {
            if (file.isFile()) {
                String fileName = file.getName();
                if (fileName.startsWith(order)) {
                    filesForCurrentOrder.add(file);
                }
            }
        }

        return filesForCurrentOrder ;
    }

    public static void deleteFiles(List<File> fileList) {
        for (File file : fileList) {
            file.delete() ;
        }
    }
    // /storage/emulated/0/Android/data/ch.parolini.truxmanager.android/files/Pictures/TruxManager
    public static File getImageFolder() {
        if (imageFolder==null) {
            imageFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TruxManager");
            if (!imageFolder.exists()) {
                if (!imageFolder.mkdirs()) {
                    Log.e("TRUX", "Directory (" + imageFolder + ") dosen't exist and could not be created");
                }
            }
        }
        return imageFolder ;
    }

    private static String datePhoto(String filePath) {
        String dateString = "";
        File file = new File(filePath);
        if (file.exists()) //Extra check, Just to validate the given path
        {
            ExifInterface intf = null;
            try {
                intf = new ExifInterface(filePath);
                if (intf != null) {
                    dateString = intf.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL);
                    //Log.i("Dated : " + dateString); //Dispaly dateString. You can do/use it your own way
                }
            } catch (IOException e) {
                e.printStackTrace();


                if (intf == null) {
                    //lastModDate = new Date(file.lastModified());
                    //Log.i("Dated : " + lastModDate.toString());//Dispaly lastModDate. You can do/use it your own way
                }
            }
            if (dateString == null) {
                dateString = "";
            }
        }
        return dateString;
    }

}
