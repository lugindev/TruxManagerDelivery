package ch.parolini.truxmanager.delivery.Manager;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
import android.net.wifi.WifiManager;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import ch.parolini.truxmanager.delivery.AppContext;
import ch.parolini.truxmanager.delivery.Context1;
import ch.parolini.truxmanager.delivery.VariablesGlobales;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by toni on 22.06.14.
 */
public class FileManager {

    private static final Object LOCK = "" ;
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
    public static String createNewImageFileNameForOrder(String orderNumber,String extension) throws ParseException {
        //String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS").format(new Date());
        Long tsLong = System.currentTimeMillis()/10;
        String timeStamp = tsLong.toString();
        PackageInfo info =  isPackageExisted1("ch.parolini.truxmanager.delivery");
        String adresse = getmacAdress().replace(":","_");
        //String adresse = "";
        String path = orderNumber + "_" + timeStamp + "_" + adresse +"_"+ info.versionCode + "_" + VariablesGlobales.message  +  extension;
        return orderNumber + "_" + timeStamp + "_" + adresse +"_"+ info.versionCode + "_" + VariablesGlobales.message  +  extension ;
    }

    /*public static String getmacAdress() throws ParseException {
        return LectureFichierParametres()[0];
    }*/

    public  static String getmacAdress() {
        try {
            WifiManager _wifiManager = (WifiManager) new Context1().getContext().getApplicationContext().getSystemService(WIFI_SERVICE);
            if (!_wifiManager.isWifiEnabled()) {
                _wifiManager.setWifiEnabled(true);
                synchronized(LOCK) {
                    try {
                        LOCK.wait(2000); // LOCK is not held
                    } catch (InterruptedException e) {

                    }
                }
            }
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }

        return "";
    }


    /*public static void EcrtiureFichierParametres(String data) throws ParseException {
        File path = getExternalFilesDir(null).getAbsolutePath();

        File file = new File(path, ".parametres.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            }  catch (Exception e) { ////Log.i("simulation", e.getMessage() + "10");
                ////Log.i("simulation", e.getMessage());

            }
        }
        try {
            FileWriter fstream = new FileWriter(file, true);
            BufferedWriter fbw = new BufferedWriter(fstream);
            fbw.write(data);
            fbw.newLine();
            fbw.close();
        }  catch (Exception e) { ////Log.i("simulation", e.getMessage() + "11");
            ////Log.i("simulation", e.getMessage());

        }

    }*/

    public static String[] LectureFichierParametres() {
        File path = new Context1().getContext().getExternalFilesDir(null);
        String[] tab = new String[1];
        final File file = new File(path, ".parametres_cle.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            }  catch (Exception e) { ////Log.i("simulation", e.getMessage());
                ////Log.i("simulation", e.getMessage());

            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);


            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                    tab = receiveString.split(";");

                }

                inputStream.close();

            }
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return tab;

    }

    public static PackageInfo isPackageExisted1(String targetPackage){
        PackageManager pm= AppContext.getAppContext().getPackageManager();
        PackageInfo info = null;
        try {
            info= pm.getPackageInfo(targetPackage,PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        return info;
    }

    public static  String randomString( int len ){
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    public static File createNewImageFileForOrder(String orderNumber,String extention) throws ParseException {
        return new File(getImageFolder().getPath() + "/" + FileManager.createNewImageFileNameForOrder(orderNumber,extention));
    }

    /**
     * Copy a gile from the gallery to the application folder, and rename correctly.
     * @param galleryFile
     * @param orderNumber
     * @return
     * @throws IOException
     */
    public static File copyImageFromGallery(File galleryFile, String orderNumber,String extention) throws IOException, ParseException {
        InputStream in = new FileInputStream(galleryFile);
        File outFile = createNewImageFileForOrder(orderNumber,extention);
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
        File dir = new File(AppContext.getAppContext().getExternalFilesDir(Environment.DIRECTORY_DCIM), "trux_manager");
        if(!dir.exists()) {
            dir.mkdir();
        }
        Boolean d = dir.exists();
        if (imageFolder==null) {
            imageFolder = dir;
            if (!imageFolder.exists()) {
                if (!imageFolder.mkdirs()) {
                    ////Log.e("TRUX", "Directory (" + imageFolder + ") dosen't exist and could not be created");
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
                    //////Log.i("Dated : " + dateString); //Dispaly dateString. You can do/use it your own way
                }
            } catch (IOException e) {
                //e.printStackTrace();


                if (intf == null) {
                    //lastModDate = new Date(file.lastModified());
                    //////Log.i("Dated : " + lastModDate.toString());//Dispaly lastModDate. You can do/use it your own way
                }
            }
            if (dateString == null) {
                dateString = "";
            }
        }
        return dateString;
    }

}
