package ch.parolini.truxmanager.delivery;


import android.annotation.TargetApi;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

import ch.parolini.truxmanager.delivery.Manager.OrderManager;
import ch.parolini.truxmanager.delivery.model.Order;
import ch.parolini.truxmanager.delivery.model.Picture;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServiceUpload extends IntentService {

    private static final Object LOCK = "";
    private static final String CHANNEL_ID = "15";
    private static final int NOTIFICATION_ID = 15;
    private static final String NOTIFICATION_CHANNEL_ID = "15";
    private static final CharSequence NOTIFICATION_CHANNEL_NAME = "notif";
    private static final String NOTIFICATION_CHANNEL_DESC = "15";
    private static Bitmap bmp;
    private final boolean serviceRun = true;
    private List<String[]> lstimages = new ArrayList<>();
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
    private final boolean asPicture = false;
    private final boolean onclick = false;
    private Menu mMenu;
    private final boolean mShowVisible = false;
    public Snackbar mySnackbar;
    public Snackbar mySnackbar1;
    public boolean cleanPhoto = false;
    private final boolean isSelected = false;
    public Handler handlerClearPhotos;
    public Handler handlerCleanPhotos;
    public Runnable runnableClearPhotos;
    public Runnable runnableCleanPhotos;
    private final String[] paramertes = new String[6];
    private final BroadcastReceiver mMessageReceiver = null;
    private final int nbTotalOrdre = 0;
    private final boolean _blockSynchro = false;
    private final boolean snackBarInfo = true;
    private final BroadcastReceiver _receiverEtatDuTelephone = new BootLoadReceiver();
    int PICK_IMAGE_MULTIPLE = 1;
    int PICK_IMAGE_MULTIPLE1 = 2;
    String imageEncoded;
    List<String> imagesEncodedList;
    public static String serverRootContextURL;
    public static String serverUserName;
    private static String serverPassword;
    public static boolean passwordOK = false;
    private static InputStream inputStream;
    private static final String ftpHostName = "";
    private static String ftpUserName;
    private static String ftpPassword;
    private static MainActivity _activity;
    private static boolean _rename;
    private static boolean efface;
    private static boolean inFTP;
    private static String dateExif;
    private static String idExif;


    String imagePath;

    private static final int serverResponseCode = 0;
    ProgressDialog dialog = null;

    String upLoadServerUri = null;
    private static final String _urlJsonParamerte = "https://www.luginbuhl.ch/X3GR5T/Hv8bcFH9.json";
    private final String Tag = "TransfertPhp";
    private final ArrayList<String[]> _listImages = new ArrayList<>();
    private Bitmap scaledBitmap;
    private Thread one;
    private int nbOrder = 0;
    private final int i = 1;
    private String _adresse = "";
    private String extension;
    private String extension1;
    private String extension2;
    private String tab;
    //private HttpClient client;
    private OkHttpClient client;
    private Response response;
    private static final String FILE_PART_NAME = "file";
    protected Map<String, String> headers;
    private boolean locksend = false;


    public ServiceUpload() {
        super("ServiceUpdate");

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent notificationIntent = new Intent(this, ServiceUpload.class);

            PendingIntent pendingIntent = null;
            if (Build.VERSION.SDK_INT >= 23) {
                pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            } else {
                pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("Trux manager")
                    .setContentText("Envoi image(s) en cours")
                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                    .setContentIntent(pendingIntent);
            Notification notification = builder.build();
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(NOTIFICATION_CHANNEL_DESC);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }
            startForeground(NOTIFICATION_ID, notification);

        } else {

            Intent notificationIntent = new Intent(this, ServiceUpload.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

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

            ////Log.d("ServiceUpdateDemmarer", "Service démarré");


        }


        _adresse = getmacAdress();
        client = new OkHttpClient();
        locksend = false;
        while (serviceRun) {
            if (!locksend) {
                locksend = true;
                List<Order> orders = new ArrayList<>();
                orders = OrderManager.getOrders();
                ////Log.d("ServiceUpdate", "controle photo upload");
                VariablesGlobales._notSendOrders = true;
                for (Order order : orders) {
                    try {
                        lstimages.clear();
                        nbOrder = 0;
                        lstimages = VariablesGlobales.requeteBd.selectImagesNumero(order.getOrderNumber());
                        for (String[] s : lstimages) {
                            File f = new File(s[1]);
                            EmvoiImage(order.getOrderNumber(), f, order);
                        }

                    } catch (Exception e) {
                        //e.printStackTrace();
                    }

                }
                locksend = false;
            }

            synchronized (LOCK) {
                try {
                    LOCK.wait(2000); // LOCK is not held
                } catch (InterruptedException e) {

                }
            }

        }
    }

    public synchronized boolean EmvoiImage(String orderId, File file, Order order) throws IOException {
        String _ServeurPath = "";
        boolean result = false;

        if (VariablesGlobales.message.equals("")) {
            VariablesGlobales.message = "_";
        } else {
            if (VariablesGlobales.message.charAt(0) != '_') {
                VariablesGlobales.message = "_" + VariablesGlobales.message + "_";
            }
        }

        //extension = ".mp4";
        if (file.getName().contains(".")) {

            extension = file.getName().substring(file.getName().length() - 4);
        } else {

        }
        File f = file;
        if (fileExists(this, f)) {
                String date_photo = "";
                //print the original last modified date
                SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd_HH:mm:ss");
                date_photo = sdf.format(f.lastModified());
                String[] files1 = file.getName().split("/");
                String[] name = files1[files1.length - 1].split("_");
                String id_photo = "0";
                try {
                    Long tsLong = System.currentTimeMillis() / 10;
                    String timeStamp = tsLong.toString();
                    id_photo = timeStamp;
                } catch (Exception e) {
                }
                String strDate = date_photo;
                if (strDate.equals("")) {
                    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");//dd/MM/yyyy
                    Date now = new Date();
                    strDate = sdfDate.format(now);
                }
                if (orderId.equals("")) {
                    String orderId1 = file.getName().split("/")[6];
                    orderId = orderId1.split("_")[0];
                }
                _ServeurPath = orderId + "_" + strDate + "_" + id_photo + "_" + VariablesGlobales._versionCode + "_" + "f" + extension;
                //upload("https://www.luginbuhl.ch/pesages/erik-test2.jpg", f, id, order);
                doFileUpload("https://www.luginbuhl.ch/upload-photo_trux_manager.php", f, order, _ServeurPath);

        } else {
            f.delete();
            VariablesGlobales.requeteBd.effacerIamgeByName(file.getPath());
            try {
                order.deleteNumbre();
                notifiyOrderSent(order);
            } catch (IndexOutOfBoundsException e) {
                //e.printStackTrace();
            }
            _activity.updateOrderListView();
        }
        return true;
    }

    public boolean fileExists(Context context, File file) {
        //File file = AppContext.getAppContext().getFileStreamPath(filename);
        return file != null && file.exists();
    }


    private static void notifiyOrderSent(Order order) {
        Intent intent = new Intent("onOrderSent");
        intent.putExtra("order", order);
        LocalBroadcastManager.getInstance(AppContext.getAppContext()).sendBroadcast(intent);
    }


    private static String lectureDesParametres1(String key) {
        String datas = "";
        String prefs = AppContext.getAppContext().getSharedPreferences("params", Context.MODE_PRIVATE).getString(key, datas);
        return prefs;


    }

    /*public  String getmacAdress() {

            WifiManager _wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            if (_wifiManager.isWifiEnabled() == false) {
                _wifiManager.setWifiEnabled(true);
                synchronized(LOCK) {
                    try {
                        LOCK.wait(2000); // LOCK is not held
                    } catch (InterruptedException e) {

                    }
                }
            }
        List<NetworkInterface> all = null;
        try {
            all = Collections.list(NetworkInterface.getNetworkInterfaces());
        } catch (SocketException e) {
            //e.printStackTrace();
        }
        for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

            byte[] macBytes = new byte[0];
            try {
                macBytes = nif.getHardwareAddress();
            } catch (SocketException e) {
                //e.printStackTrace();
            }
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


        return "";
    }*/

     /*public String getmacAdress() throws ParseException {
        return LectureFichierParametres()[0];
     }*/

    public String getmacAdress() {
        try {
            WifiManager _wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            if (!_wifiManager.isWifiEnabled()) {
                _wifiManager.setWifiEnabled(true);
                synchronized (LOCK) {
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
                    res1.append(String.format("%02X:", b));
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


    /*public void EcrtiureFichierParametres(String data) throws ParseException {
        File path = getExternalFilesDir(null).getAbsolutePath();

        File file = new File(path, ".parametres_cle.txt");
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

    public String[] LectureFichierParametres() {
        File path = getExternalFilesDir(null);
        String[] tab = new String[1];
        final File file = new File(path, ".parametres_cle.txt");
        /*if (!file.exists()) {
            try {
                file.createNewFile();
                EcrtiureFichierParametres(UUID.randomUUID().toString().toUpperCase().substring(0,2)+":"+
                        UUID.randomUUID().toString().toUpperCase().substring(3,5)+":"+
                        UUID.randomUUID().toString().toUpperCase().substring(6,8)+":"+
                        UUID.randomUUID().toString().toUpperCase().substring(9,11)+":"+
                        UUID.randomUUID().toString().toUpperCase().substring(14,16)+":"+
                        UUID.randomUUID().toString().toUpperCase().substring(20,22));
            }  catch (Exception e) { ////Log.i("simulation", e.getMessage());
                ////Log.i("simulation", e.getMessage());

            }
        }*/

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

    String getDeviceID() {

        String device_unique_id = Settings.Secure.getString(AppContext.getAppContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return device_unique_id;

    }


    public static String insertPeriodically(
            String text, String insert, int period) {
        StringBuilder builder = new StringBuilder(
                text.length() + insert.length() * (text.length() / period) + 1);

        int index = 0;
        String prefix = "";
        while (index < text.length()) {
            // Don't put the insert in the very first iteration.
            // This is easier than appending it *after* each substring
            builder.append(prefix);
            prefix = insert;
            builder.append(text.substring(index,
                    Math.min(index + period, text.length())));
            index += period;
        }
        return builder.toString();
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
                    //////Log.i("Dated : " + dateString); //Dispaly dateString. You can do/use it your own way
                }
            } catch (IOException e) {
                //e.printStackTrace();


                if (intf == null) {
                    //lastModDate = new Date(file.lastModified());
                    //////Log.i("Dated : " + lastModDate.toString());//Dispaly lastModDate. You can do/use it your own way
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

    /*public static void SelectionDesParametres() throws Exception {

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

        String s;

    }*/

    /*public static HttpResponse GetData(String url) throws IOException {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        String url1 = url;
        HttpPost httppost = new HttpPost(url1);
        return (HttpResponse) getNewHttpClient1().execute(httppost);

    }

    private static String getJson(HttpResponse httpClient) {
        StringBuilder sb = new StringBuilder();

        HttpGet getRequest = new HttpGet(_urlJsonParamerte);
        getRequest.addHeader("accept", "application/json");

        HttpResponse response = httpClient;

        String output = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
        } catch (IOException e) {
            //e.printStackTrace();
        }
        while (true) {
            try {
                if (!((output = br.readLine()) != null)) break;

                sb.append(output);

            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static DefaultHttpClient getNewHttpClient1() {

        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        } catch (KeyStoreException e) {
            //e.printStackTrace();
        }
        try {
            trustStore.load(null, null);
        } catch (CertificateException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            //e.printStackTrace();
        }

        HttpParams params = (HttpParams) new BasicHttpParams();
        HttpProtocolParams.setVersion(params, (ProtocolVersion) HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        registry.register(new Scheme("https", (SocketFactory) new EasySSLSocketFactory(), 443));

        ClientConnectionManager ccm = new ThreadSafeClientConnManager((HttpParams) params, registry);
        return new DefaultHttpClient(ccm, (HttpParams) params);

    }*/

    public static Bitmap decodeFileForDisplay(File f) {


        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        //o.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        }
        DisplayMetrics metrics = AppContext.getAppContext().getResources().getDisplayMetrics();

        //The new size we want to scale to
        //final int REQUIRED_SIZE=180;

        // int scaleW =  o.outWidth / metrics.widthPixels;
        //int scaleH =  o.outHeight / metrics.heightPixels;
        //int scale = Math.max(scaleW,scaleH);
        //////Log.d("CCBitmapUtils", "Scale Factor:"+scale);
        //Find the correct scale value. It should be the power of 2.

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        //o2.inSampleSize=scale;
        Bitmap scaledPhoto = null;
        try {
            scaledPhoto = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        }
        return scaledPhoto;

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
        String temp = "temp";
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
        byte[] bytesArray = new byte[(int) file.length()];

        FileInputStream fis = new FileInputStream(file);
        fis.read(bytesArray); //read file into bytes[]
        fis.close();
        // Open file
        /*RandomAccessFile f = new RandomAccessFile(file, "r");

            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
        f.close();
            return data;*/
        return bytesArray;
    }

    public static Boolean uploadFile(String serverURL, File file, String id, Order order) {
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(),
                            RequestBody.create(MediaType.parse("image/jpg"), file))
                    .addFormDataPart("some-field", "some-value")
                    .build();

            Request request = new Request.Builder()
                    .url(serverURL)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(final Call call, final IOException e) {
                    // Handle the error
                }

                @Override
                public void onResponse(final Call call, final Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        // Handle the error
                    }
                    // Upload successful
                }
            });

            return true;
        } catch (Exception ex) {
            // Handle the error
        }
        return false;
    }

    private void doFileUpload(String url1, File file, Order order, String _ServeurPath) {
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        String exsistingFileName = file.getPath();
        // Is this the place are you doing something wrong.
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        String urlString = url1;
        try {
            ////Log.i("Upload","Inside second Method");
            FileInputStream fileInputStream = new FileInputStream(new File(exsistingFileName));
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            // Allow Outputs
            conn.setDoOutput(true);
            // Don't use a cached copy.
            conn.setUseCaches(false);
            // Use a post method.
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + _ServeurPath + "\"" + lineEnd);
            dos.writeBytes(lineEnd);
            ////Log.i("Upload","Headers are written");
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            BufferedReader br = null;
            boolean uploadresult = false;
            if (conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String strCurrentLine;
                while ((strCurrentLine = br.readLine()) != null) {
                    uploadresult = strCurrentLine.equals("true");
                }
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String strCurrentLine;
                while ((strCurrentLine = br.readLine()) != null) {
                    uploadresult = strCurrentLine.equals("true");
                }
            }
            //boolean existe =  exists("https://www.luginbuhl.ch/pesages/" + _ServeurPath);
            if (uploadresult == true) {
                VariablesGlobales.requeteBd.effacerIamgeByName(file.getPath());
                try {
                    order.deleteNumbre();
                    notifiyOrderSent(order);
                } catch (IndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }
                _activity.updateOrderListView();
            }
            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (MalformedURLException ex) {
            ////Log.i("Upload", "error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            ////Log.i("Upload", "error: " + ioe.getMessage(), ioe);
        }


    }

    public static boolean exists(String URLName) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            boolean b = (con.getResponseCode() == HttpURLConnection.HTTP_OK);
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
    }

    /*public CustomTrust() {
        X509TrustManager trustManager;
        SSLSocketFactory sslSocketFactory;
        try {
            trustManager = trustManagerForCertificates(trustedCertificatesInputStream());
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { trustManager }, null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        client = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustManager)
                .build();
    }

    public void run() throws Exception {
        Request request = new Request.Builder()
                .url("https://publicobject.com/helloworld.txt")
                .build();

        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }

    public InputStream trustedCertificatesInputStream() {
    // Full source omitted. See sample.
        return null;
    }

    public SSLContext sslContextForTrustedCertificates(InputStream in) {
     // Full source omitted. See sample.
        return null;
    }*/


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

}