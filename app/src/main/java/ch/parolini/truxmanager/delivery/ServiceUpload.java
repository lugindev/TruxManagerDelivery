package ch.parolini.truxmanager.delivery;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.NetworkInterface;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import ch.parolini.truxmanager.delivery.Manager.OrderManager;
import ch.parolini.truxmanager.delivery.basededonnee.Requetes;
import ch.parolini.truxmanager.delivery.model.Order;
import ch.parolini.truxmanager.delivery.model.Picture;

public class ServiceUpload extends IntentService {

    private static final Object LOCK = "";
    private static final String CHANNEL_ID = "15" ;
    private static final int NOTIFICATION_ID = 15 ;
    private static final String NOTIFICATION_CHANNEL_ID ="15" ;
    private static final CharSequence NOTIFICATION_CHANNEL_NAME ="notif" ;
    private static final String NOTIFICATION_CHANNEL_DESC ="15" ;
    private boolean serviceRun = true;
    private ArrayList<String[]> images;
    // Static to avoid loosing the ref when screen orientation changes
    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_ACTIVITY_REQUEST_CODE = 200;
    private static final String PASSWORD_KEY_PREF = "pass";
    private static final int REQUEST_READ_STORAGE = 113;
    private static final int REQUEST_CHANGE_NETWORK_STATE = 114;
    private static final int REQUEST_CHANGE_WIFI_STATE = 115;
    private static final int REQUEST_ACCESS_NETWORK_STATE = 116;
    private static final int PERMISSION_RECEIVE_BOOT_COMPLETED = 117;
    public static boolean active;
    public boolean clearPhoto = false;
    private FloatingActionButton addButton;
    private ListView list;
    // Static to avoid loosing the ref when screen orientation changes
    static File currentPictureFile = null;
    SharedPreferences preferences = null;
    MainActivity thisActivity;

    private Bitmap mBitmapToSave;
    private int REQUEST_CODE_CREATOR;
    private boolean asPicture = false;
    private boolean onclick = false;
    private Menu mMenu;
    private boolean mShowVisible = false;
    public Snackbar mySnackbar;
    public Snackbar mySnackbar1;
    public boolean cleanPhoto = false;
    private boolean isSelected = false;
    public Handler handlerClearPhotos;
    public Handler handlerCleanPhotos;
    public Runnable runnableClearPhotos;
    public Runnable runnableCleanPhotos;
    private String[] paramertes = new String[6];
    private BroadcastReceiver mMessageReceiver = null;
    ;
    private int nbTotalOrdre = 0;
    private boolean _blockSynchro = false;
    private boolean snackBarInfo = true;
    private BroadcastReceiver _receiverEtatDuTelephone = new BootLoadReceiver();
    int PICK_IMAGE_MULTIPLE = 1;
    int PICK_IMAGE_MULTIPLE1 = 2;
    String imageEncoded;
    List<String> imagesEncodedList;
    public static String serverRootContextURL;
    public static String serverUserName;
    private static String serverPassword;
    public static boolean passwordOK = false;
    private static InputStream inputStream;
    private static String ftpHostName = "";
    private static String ftpUserName;
    private static String ftpPassword;
    private static MainActivity _activity;
    private static Bitmap original = null;
    private static Bitmap resized = null;
    private static String _path;
    private static boolean _rename;
    private static boolean efface;
    private static boolean inFTP;
    private static FTPClient con = null;
    private static String dateExif;
    private static String idExif;


    String imagePath;

    private static int serverResponseCode = 0;
    ProgressDialog dialog = null;

    String upLoadServerUri = null;
    private static String _urlJsonParamerte = "https://www.dsgsoft.ch/X3GR5T/Hv8bcFH9.json";
    ;
    private String Tag = "TransfertPhp";
    private ArrayList<String[]> _listImages = new ArrayList<>();
    private Requetes requetesBaseDeDonneeInterne;
    private Bitmap scaledBitmap;
    private Thread one;
    private int nbOrder = 0;
    private int i = 1;
    private String _adresse ="";
    //private HttpClient client;


    public ServiceUpload() {
        super("ServiceUpdate");
        _adresse = getmacAdress();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent notificationIntent = new Intent(this, ServiceUpload.class);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
                        .setContentTitle("Trux manager")
                        .setContentText("Envoi image(s) en cours")
                        .setColor(getResources().getColor(R.color.colorPrimaryDark))
                        .setContentIntent(pendingIntent);
                Notification notification=builder.build();
                if(Build.VERSION.SDK_INT>=26) {
                    NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                    channel.setDescription(NOTIFICATION_CHANNEL_DESC);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.createNotificationChannel(channel);
                }
                startForeground(NOTIFICATION_ID, notification);

            }else {

                Intent notificationIntent = new Intent(this, ServiceUpload.class);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

                Notification notification = new Notification.Builder(this)
                        .setContentTitle("Trux manager")
                        .setContentText("Envoi image(s) en cours")
                        .setColor(getResources().getColor(R.color.colorPrimaryDark))

                        .setOnlyAlertOnce(true)
                        .setSmallIcon(R.drawable.truck_green)

                        //.setContentIntent(pendingIntent)
                        //.setTicker("Title")
                        //.setPriority(Notification.PRIORITY_MIN)
                        .build();


                startForeground(15, notification);


                String ns = Context.NOTIFICATION_SERVICE;
                NotificationManager nMgr = (NotificationManager) getSystemService(ns);
                nMgr.cancel(15);

                Log.d("ServiceUpdateDemmarer", "Service démarré");


            }
        }catch (Exception e){

        }
        while (serviceRun == true) {
            try {
                List<Order> orders = new ArrayList<>();
                orders = OrderManager.getOrders();
                Log.d("ServiceUpdate", "controle photo upload");
                for (Order order : orders) {
                    for (Picture picture : order.getPictures()) {

                        requetesBaseDeDonneeInterne = new Requetes(AppContext.getAppContext());
                        requetesBaseDeDonneeInterne.open();
                        images = new ArrayList<>();
                        images = (ArrayList<String[]>) requetesBaseDeDonneeInterne.selectImagesByName(picture.getFile().getPath());
                        if (images.size() == 0) {
                            requetesBaseDeDonneeInterne.ajouterImage(picture.getFile().getPath());
                            Log.i("notSend", "Image ajoutée " + picture.getFile().getPath());
                        }
                        for (String[] path : images) {
                            Log.i("notSend", "Image envoyer " + picture.getFile().getPath());
                            EmvoiImage(order.getOrderNumber(), path[1], order, path[0]);

                        }


                    }
                }

            } catch (Exception e) {

            } finally {
                if(requetesBaseDeDonneeInterne != null) {
                    requetesBaseDeDonneeInterne.close();
                }

            }

            List<Order> orders1= OrderManager.getOrders();
            nbOrder=0;
            for (Order order : orders1) {

                nbOrder = nbOrder + order.getPictureCount();
            }

            if (nbOrder == 0) {
                Log.i("StopSelf", "StopSelf");
                //NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                //notificationManager.cancel(15);
                serviceRun = false;
                stopSelf();
                break;
            }



            /*synchronized (LOCK) {
                try {
                    LOCK.wait(20000); // LOCK is not held
                } catch (InterruptedException e) {

                }
            }*/

        }
    }

    public synchronized boolean  EmvoiImage(String orderId, String file, Order order, String id) throws IOException {
        original = null;
        resized = null;
        String _ServeurPath = "";
        boolean result = false;
        try {

            if (ftpHostName.equals("")) {
                try {
                    SelectionDesParametres();
                } catch (Exception e) {

                }
            }

            if (con == null) {
                con = new FTPSClient();
            }


            con.connect(ftpHostName);


            if (con.login(ftpUserName, ftpPassword)) {
                con.enterLocalPassiveMode(); // important!
                con.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
                con.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            }

            FTPFile[] files = new FTPFile[0];
            FTPFile[] filesExist = new FTPFile[0];
            Order order1 = new Order();
            requetesBaseDeDonneeInterne = new Requetes(AppContext.getAppContext());
            requetesBaseDeDonneeInterne.open();
            String[] file2 = requetesBaseDeDonneeInterne.selectImagesByName(file).get(0);
            file = file2[1];
            File f = new File(file);
            if (!f.exists()) {
                Log.i("Inrouvable", "Fichier introuvable");
                try {
                    requetesBaseDeDonneeInterne.effacerIamgeById(id);
                    Log.i("Inrouvable", "Fichier effacer bd");
                    String date_photo = datePhoto(file);
                    String date = "date";
                    String heure = "heure";
                    String[] files1 = file.split("/");
                    String newPath = files1[0] + "/" + files1[1] + "/" + files1[2] + "/" + files1[3] + "/" + files1[4] + "/" + files1[5];
                    String[] name = files1[6].split("_");
                    String id_photo = name[1];
                    if (!date_photo.equals("")) {
                        date = date_photo.substring(0, 10).replace(":", "-");
                        heure = date_photo.substring(11, date_photo.length());
                        date_photo = date + "_" + heure;
                        if (!orderId.equals("")) {
                            renameFile(file, orderId + "_" + date_photo + "_" + _adresse + "_" +  "_" + id_photo + "_" +VariablesGlobales._versionCode + ".jpeg");
                            _path = newPath + "/" + orderId + "_" + date_photo + "_" + _adresse + "_" +  "_" + id_photo + "_" + VariablesGlobales._versionCode + ".jpeg";
                            _ServeurPath = orderId + "_" + date_photo + "_" + _adresse + "_" +  "_" + id_photo + "_" + VariablesGlobales._versionCode + ".jpeg";
                        } else {
                            String orderId1 = file.split("/")[6];
                            orderId = orderId1.split("_")[0];
                            renameFile(file, orderId + "_" + date_photo + "_" +  "_" + id_photo + "_" + _adresse + "_" + VariablesGlobales._versionCode + ".jpeg");
                            _path = newPath + "/" + orderId + "_" + date_photo + "_" +  "_" + id_photo + "_" + _adresse + "_" + VariablesGlobales._versionCode + ".jpeg";
                            _ServeurPath = orderId + "_" + date_photo + "_" +  "_" + id_photo + "_" + _adresse + "_" + VariablesGlobales._versionCode + ".jpeg";
                        }
                    } else {
                        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HH:mm.SSS");//dd/MM/yyyy
                        Date now = new Date();
                        String strDate = sdfDate.format(now);
                        date = strDate.substring(0, 10).replace(":", "-");
                        heure = strDate.substring(11, strDate.length());
                        date_photo = date + "_" + heure;
                        if (!orderId.equals("")) {
                            renameFile(file, orderId + "_" + strDate + "_" + id_photo + "_" + _adresse + "_" + VariablesGlobales._versionCode + ".jpeg");
                            _path = newPath + "/" + orderId + "_" + strDate + "_" +  id_photo + "_" + _adresse + "_" + VariablesGlobales._versionCode + ".jpeg";
                            _ServeurPath = orderId + "_" + date_photo + "_" +  id_photo + "_" + _adresse + "_" + VariablesGlobales._versionCode + ".jpeg";
                        } else {
                            String orderId1 = file.split("/")[6];
                            orderId = orderId1.split("_")[0];
                            renameFile(file, orderId + "_" + strDate + "_" +  id_photo + "_" + _adresse + "_" + VariablesGlobales._versionCode + ".jpeg");
                            _path = newPath + "/" + orderId + "_" + strDate + "_" +  id_photo + "_" + _adresse + "_" + VariablesGlobales._versionCode + ".jpeg";
                            _ServeurPath = orderId + "_" + date_photo + "_" +  id_photo + "_" + _adresse + "_" + VariablesGlobales._versionCode + ".jpeg";
                        }

                    }
                    Log.i("Inrouvable", "_ServeurPath" + _ServeurPath);
                    con.rename("/../photo_trux_manager/" + _ServeurPath, " /../photo_trux_manager/" + _ServeurPath.substring(0, _ServeurPath.length() - 5) + "e.jpeg");

                } catch (Exception e) {
                    Log.i("Inrouvable", "Fichier introuvable bd" + e.getMessage());
                } finally {
                    requetesBaseDeDonneeInterne.close();
                }
            }


            Log.i("FileLength", String.valueOf(f.getName()));


            String data = file;

            byte[] b = null;


            String compression = lectureDesParametres1("qualite_photos");


            //FileInputStream in = new FileInputStream(new File(data));
            //original = BitmapFactory.decodeStream(in);
            original = decodeFileForDisplay(new File(data));
            //original.eraseColor(Color.TRANSPARENT);
            String date_photo = datePhoto(data);
            String date = "date";
            String heure = "heure";
            String[] files1 = file.split("/");
            String newPath = files1[0] + "/" + files1[1] + "/" + files1[2] + "/" + files1[3] + "/" + files1[4] + "/" + files1[5];
            String[] name = files1[6].split("_");
            String id_photo = name[1];
            if (!date_photo.equals("")) {
                date = date_photo.substring(0, 10).replace(":", "-");
                heure = date_photo.substring(11, date_photo.length());
                date_photo = date + "_" + heure;
                if (!orderId.equals("")) {
                    renameFile(file, orderId + "_" + date_photo +  "_" + id_photo  + "_" + _adresse + "_" + VariablesGlobales._versionCode + ".jpeg");
                    _path = newPath + "/" + orderId + "_" + date_photo + "_" + id_photo  + "_" + _adresse +   "_"  + VariablesGlobales._versionCode + ".jpeg";
                    _ServeurPath = orderId + "_" + date_photo + "_" + id_photo + "_" + _adresse + "_"   + VariablesGlobales._versionCode + ".jpeg";
                } else {
                    String orderId1 = file.split("/")[6];
                    orderId = orderId1.split("_")[0];
                    renameFile(file, orderId + "_" + date_photo +  "_" + id_photo  + "_" + _adresse + "_" + VariablesGlobales._versionCode + ".jpeg");
                    _path = newPath + "/" + orderId + "_" + date_photo + "_" + id_photo  + "_" + _adresse +   "_"  + VariablesGlobales._versionCode + ".jpeg";
                    _ServeurPath = orderId + "_" + date_photo + "_" + id_photo + "_" + _adresse + "_"   + VariablesGlobales._versionCode + ".jpeg";
                }
            } else {
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HH:mm.SSS");//dd/MM/yyyy
                Date now = new Date();
                String strDate = sdfDate.format(now);
                date = strDate.substring(0, 10).replace(":", "-");
                heure = strDate.substring(11, strDate.length());
                date_photo = date + "_" + heure;
                if (!orderId.equals("")) {
                    renameFile(file, orderId + "_" + strDate + "_" + _adresse +  "_" + id_photo + "_" + VariablesGlobales._versionCode + ".jpeg");
                    _path = newPath + "/" + orderId + "_" + strDate + "_" + _adresse +  "_" + id_photo + "_" + VariablesGlobales._versionCode + ".jpeg";
                    _ServeurPath = orderId + "_" + date_photo + "_" + _adresse +  "_" + id_photo + "_" + VariablesGlobales._versionCode + ".jpeg";
                } else {
                    String orderId1 = file.split("/")[6];
                    orderId = orderId1.split("_")[0];
                    renameFile(file, orderId + "_" + strDate + "_" + _adresse + "_" + VariablesGlobales._versionCode + ".jpeg");
                    _path = newPath + "/" + orderId + "_" + strDate + "_" + _adresse + "_" + VariablesGlobales._versionCode + ".jpeg";
                    _ServeurPath = orderId + "_" + date_photo + "_" + _adresse + "_" + VariablesGlobales._versionCode + ".jpeg";
                }
            }

            Log.i("FileName", file);
            Log.i("FIleName", "_path" + _path);
            String tempFilePath = getTempFilePath(_path);
            File tempFile = new File(tempFilePath);
            //filesExist = con.listFiles("/../photo_trux_manager/" + _ServeurPath.substring(0,_ServeurPath.length()-5) +"f.jpeg");

                /*for (FTPFile file1 : filesExist) {
                    Log.d("UploadTrue",file1.getName());
                    File target;
                    File target1;
                    String[] files2 = _path.split("/");
                    try {
                        requetesBaseDeDonneeInterne  = new Requetes(AppContext.getAppContext());
                        requetesBaseDeDonneeInterne.open();
                        requetesBaseDeDonneeInterne.effacerIamgeById(id);

                        target = new File(file);
                        target1 = new File(file.substring(0, file.length() - 4) + "_preview.jpeg");
                        if (target1.exists() && target1.isFile() && target1.canWrite()) {
                            target1.delete();
                            Log.d("d_file", "" + target1.getName());
                        }
                        if (target.exists() && target.isFile() && target.canWrite()) {
                            target.delete();
                            Log.d("d_file", "" + target.getName());
                        }
                        order.deleteNumbre();
                        notifiyOrderSent(order);


                    } catch (Exception e) {

                    } finally {
                    }

                    return true;
                }*/


            //ByteArrayOutputStream out = new ByteArrayOutputStream();


// Get stream from temp (exif loaded) file




            /*if(compression.equals("")) {
                original.compress(Bitmap.CompressFormat.JPEG, Integer.parseInt(compression), out);
            }
            else {
                original.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }*/

            files = con.listFiles("/../photo_trux_manager/" + _ServeurPath);

            long fileSizeInBytes = 0;
            long fileSizeInKB = 0;


            for (FTPFile file1 : files) {

                // Get file from file name
                //File file2 = new File(file.getPath());
                inFTP = true;

                // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                fileSizeInKB = file1.getSize();
                byte[] byteFile = readFile(tempFile);
                if (fileSizeInBytes == fileSizeInKB || fileSizeInKB > fileSizeInBytes) {


                }

            }
            //fis.read(out.toByteArray());

            // Log.i("QualiteEnvoi", lectureDesParametres1("qualite_photos"));

            // Get length of file in bytes
            //fileSizeInBytes = file.length();
            // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)


            //b = fis1.

// Remove the temp file

// Finalize


// Get stream from temp (exif loaded) file

// Remove the temp file
            //boolean deleted = tempFilePath.delete();

// Finalize

            /*if (b == null) {
                b = out.toByteArray();
            }*/


            try {
                FileOutputStream out = new FileOutputStream(tempFilePath);
                original.compress(Bitmap.CompressFormat.JPEG, Integer.parseInt(compression), out);
                copyExif(file, tempFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }

// Get stream from temp (exif loaded) file

            byte[] byteFile = readFile(tempFile);
            ByteArrayInputStream fis1 = new ByteArrayInputStream(byteFile);

// Remove the temp file


// Finalize
            int fileSize = byteFile.length;
            con.setRestartOffset(fileSizeInKB);
            //result = con.storeFile("/../photo_trux_manager/" + _path, new ByteArrayInputStream(b));

            result = con.appendFile("/../photo_trux_manager/" + _ServeurPath, fis1);


            if (result == true) {
                con.rename("/../photo_trux_manager/" + _ServeurPath, " /../photo_trux_manager/" + _ServeurPath.substring(0, _ServeurPath.length() - 5) + "f.jpeg");
                filesExist = con.listFiles("/../photo_trux_manager/" + _ServeurPath.substring(0,_ServeurPath.length()-5)+"f.jpeg");

                for (FTPFile file1 : filesExist) {
                    Log.d("UploadTrue",file1.getName());
                    try {
                        requetesBaseDeDonneeInterne  = new Requetes(AppContext.getAppContext());
                        requetesBaseDeDonneeInterne.open();
                        requetesBaseDeDonneeInterne.effacerIamgeById(id);

                        File target;
                        File target1;
                        String[] files2 = _path.split("/");

                        target = new File(file);
                        target1 = new File(file.substring(0, file.length() - 4) + "_preview.jpeg");
                        if (target1.exists() && target1.isFile() && target1.canWrite()) {
                            target1.delete();
                            Log.d("d_file", "" + target1.getName());
                        }
                        if (target.exists() && target.isFile() && target.canWrite()) {
                            target.delete();
                            Log.d("d_file", "" + target.getName());
                        }
                        order.deleteNumbre();
                        notifiyOrderSent(order);


                    } catch (Exception e) {

                    } finally {
                    }

                    if (original != null && original.isRecycled() == false) {
                        original.recycle();
                    }
                    if (resized != null && resized.isRecycled() == false) {
                        resized.recycle();
                    }


                    return true;
                }
                //boolean deleted = tempFile.delete();
            }

            //filesExist = con.listFiles("/../photo_trux_manager/" + _ServeurPath.substring(0,_ServeurPath.length()-5)+"f.jpeg");


           /* target = new File(getImageFolder()+"/"+files2[1]);
            target1 = new File(getImageFolder()+"/"+files2[1].substring(0, (getImageFolder()+"/"+files2[1]).length() - 4) + "_preview.jpeg");
            if(filesExist.length!=0) {
                if (target1.exists() && target1.isFile() && target1.canWrite()) {
                    target1.delete();
                    Log.d("Files", "Delete" + target1.getName());
                }
            }

            if (lectureDesParametres1("action_fichier").equals("effacer") && filesExist.length!=0) {
                if (target.exists() && target.isFile() && target.canWrite()) {
                    target.delete();
                    Log.d("Files", "Delete" + target.getName());
                }
            }*/


            Log.i("BaseDeDonnee", result + "_" + _path);



        } catch (Exception e) {
            Log.i("resultat", false + "_" + e.getMessage() + "_" + _path);
            con.logout();
            con.disconnect();
            con=null;
            //return false;
        } finally {
            if (con != null) {
                con.logout();
                con.disconnect();
            }
        }
        return result;
    }

    private void notifiyOrderSent(Order order) {
        Intent intent = new Intent("onOrderSent");
        intent.putExtra("order", order);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }



    private static String lectureDesParametres1(String key) {
        String datas = "";
        String prefs = AppContext.getAppContext().getSharedPreferences("params", Context.MODE_PRIVATE).getString(key, datas);
        return prefs;


    }

    public String getmacAdress() {
        try {
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
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString().replace(":","-");
            }
        } catch (Exception ex) {
        }

        return "";
    }

    private static String datePhoto(String filePath) {
        File file = new File(filePath);
        if (file.exists()) //Extra check, Just to validate the given path
        {
            ExifInterface intf = null;
            try {
                intf = new ExifInterface(filePath);
                if (intf != null) {
                    dateExif = intf.getAttribute(ExifInterface.TAG_DATETIME);
                    //intf.get(ExifInterface.)
                    //Log.i("Dated : " + dateString); //Dispaly dateString. You can do/use it your own way
                }
            } catch (IOException e) {
                e.printStackTrace();


                if (intf == null) {
                    //lastModDate = new Date(file.lastModified());
                    //Log.i("Dated : " + lastModDate.toString());//Dispaly lastModDate. You can do/use it your own way
                }
            }
            if (dateExif == null) {
                dateExif = "";
            }
        }
        return dateExif;
    }

    private static String idPhoto(String filePath) {

        return filePath;
    }

    public static void SelectionDesParametres() throws Exception {
        try {
            String jsonStr = null;
            try {
                jsonStr = getJson(GetData(_urlJsonParamerte));
            } catch (IOException e) {

            }
            if (jsonStr != null) {
                try {
                    JSONObject json = new JSONObject(jsonStr);
                    JSONArray jArray = json.getJSONArray("donnees");

                    for (int j = 0; j < jArray.length(); j++) {
                        JSONObject values = jArray.getJSONObject(j);
                        ftpHostName = values.getString("fT6RqE7");
                        ftpUserName = values.getString("M6Hg7ZQ");
                        ftpPassword = values.getString("K9Jd0s4");
                    }
                } catch (final JSONException e) {
                    String s;
                }
            }
        } catch (Exception e) {
            String s;
        }
    }

    public static HttpResponse GetData(String url) throws IOException {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        String url1 = url;
        HttpPost httppost = new HttpPost(url1);
        return getNewHttpClient1().execute(httppost);

    }

    private static String getJson(HttpResponse httpClient) {
        StringBuilder sb = new StringBuilder();
        try {
            HttpGet getRequest = new HttpGet(_urlJsonParamerte);
            getRequest.addHeader("accept", "application/json");

            HttpResponse response = httpClient;

            String output;
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
        } catch (Exception e) {
            String s;
        }
        return sb.toString();
    }

    public static DefaultHttpClient getNewHttpClient1() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            registry.register(new Scheme("https", new EasySSLSocketFactory(), 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public static Bitmap decodeFileForDisplay(File f){

        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            //o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            DisplayMetrics metrics = AppContext.getAppContext().getResources().getDisplayMetrics();

            //The new size we want to scale to
            //final int REQUIRED_SIZE=180;

            // int scaleW =  o.outWidth / metrics.widthPixels;
            //int scaleH =  o.outHeight / metrics.heightPixels;
            //int scale = Math.max(scaleW,scaleH);
            //Log.d("CCBitmapUtils", "Scale Factor:"+scale);
            //Find the correct scale value. It should be the power of 2.

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            //o2.inSampleSize=scale;
            Bitmap scaledPhoto = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            return scaledPhoto;

        } catch (FileNotFoundException e) {}
        return null;
    }

    public static void renameFile(String oldFile, String newFile) throws IOException {
// File (or directory) with old name
        File file = new File(oldFile);

// File (or directory) with new name
        File file2 = new File(newFile);

        if (file2.exists())
            throw new java.io.IOException("file exists");

// Rename file (or directory)
        boolean success = file.renameTo(file2);

        if (!success) {
            // File was not successfully renamed
        }



    }

    private String getTempFilePath(String filename) {
        String temp = "_temp";
        int dot = filename.lastIndexOf(".");
        String ext = filename.substring(dot + 1);

        if (dot == -1 || !ext.matches("\\w+")) {
            filename += temp;
        } else {
            filename = filename.substring(0, dot) + temp + "." + ext;
        }

        return filename;
    }

    public static void copyExif(String originalPath, String newPath) throws IOException {

        String[] attributes = new String[]
                {
                        ExifInterface.TAG_DATETIME,
                        ExifInterface.TAG_DATETIME_DIGITIZED,
                        ExifInterface.TAG_EXPOSURE_TIME,
                        ExifInterface.TAG_FLASH,
                        ExifInterface.TAG_FOCAL_LENGTH,
                        ExifInterface.TAG_GPS_ALTITUDE,
                        ExifInterface.TAG_GPS_ALTITUDE_REF,
                        ExifInterface.TAG_GPS_DATESTAMP,
                        ExifInterface.TAG_GPS_LATITUDE,
                        ExifInterface.TAG_GPS_LATITUDE_REF,
                        ExifInterface.TAG_GPS_LONGITUDE,
                        ExifInterface.TAG_GPS_LONGITUDE_REF,
                        ExifInterface.TAG_GPS_PROCESSING_METHOD,
                        ExifInterface.TAG_GPS_TIMESTAMP,
                        ExifInterface.TAG_MAKE,
                        ExifInterface.TAG_MODEL,
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.TAG_SUBSEC_TIME,
                        ExifInterface.TAG_WHITE_BALANCE,
                        ExifInterface.TAG_ARTIST

                };



        ExifInterface oldExif = new ExifInterface(originalPath);
        ExifInterface newExif = new ExifInterface(newPath);

        if (attributes.length > 0) {
            for (int i = 0; i < attributes.length; i++) {
                String value = oldExif.getAttribute(attributes[i]);
                if (value != null)
                    newExif.setAttribute(attributes[i], value);
            }
            newExif.saveAttributes();
        }
    }

    public static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }

    public static File getImageFolder() {
        File imageFolder=null;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }
}


