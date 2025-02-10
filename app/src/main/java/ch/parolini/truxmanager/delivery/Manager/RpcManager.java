package ch.parolini.truxmanager.delivery.Manager;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.conn.ClientConnectionManager;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.conn.scheme.PlainSocketFactory;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.conn.scheme.Scheme;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.conn.scheme.SchemeRegistry;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.conn.scheme.SocketFactory;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import com.google.gson.Gson;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import ch.parolini.truxmanager.delivery.AppContext;
import ch.parolini.truxmanager.delivery.MainActivity;
import ch.parolini.truxmanager.delivery.R;
import ch.parolini.truxmanager.delivery.model.ClientConfig;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by toni on 02.07.14.
 */
public class RpcManager {

    public static String serverRootContextURL;
    public static String serverUserName;
    private static String serverPassword;
    public static boolean passwordOK = false;
    private static InputStream inputStream;
    private static String ftpHostName = "";
    private static String ftpUserName;
    private static String ftpPassword;
    private static MainActivity _activity;
    private static final Bitmap original = null;
    private static final Bitmap resized = null;
    private static String _path;
    private static boolean _rename;
    private static boolean efface;
    private static boolean inFTP;
    private static final FTPClient con = null;
    private static String dateExif;


    String imagePath;

    private static final int serverResponseCode = 0;
    ProgressDialog dialog = null;

    String upLoadServerUri = null;
    private static final String _urlJsonParamerte = "https://www.luginbuhl.ch/X3GR5T/Hv8bcFH9.json";
    private final String Tag = "TransfertPhp";


    public static String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException {

        String res = "";
        StringBuffer buffer = new StringBuffer();
        inputStream = response.getEntity().getContent();
        int contentLength = (int) response.getEntity().getContentLength(); //getting content length…..


        if (contentLength < 0) {
        } else {
            byte[] data = new byte[512];
            int len = 0;

                while (-1 != (len = inputStream.read(data))) {
                    buffer.append(new String(data, 0, len)); //converting to string and appending  to stringbuffer…..
                }

            try {
                inputStream.close(); // closing the stream…..
            } catch (IOException e) {
                //e.printStackTrace();
            }
            res = buffer.toString();     // converting stringbuffer to string…..


            //System.out.println("Response => " +  EntityUtils.toString(response.getEntity()));
        }
        return res;
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

        HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            registry.register(new Scheme("https",  PlainSocketFactory
                    .getSocketFactory(), 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager((com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.params.HttpParams) params, registry);
            return new DefaultHttpClient(ccm, (com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.params.HttpParams) params);

    }


    /*
    {
    "Weighings": [
        {
            "firstname": "Nazim",
            "lastname": "Benbourahla",
            "login": "n_benbourahla",
            "twitter": "@n_benbourahla",
            "web": ""
        },
        {
            "firstname": "Tutos",
            "lastname": "android",
            "login": "Tutos-android",
            "twitter": "",
            "web": "www.tutos-android.com"
        }
    ]
}
     */

/*    public static List<Order> getCurrentWeighing() throws Exception {

        Order[] orders = null ;
        JsonParser jp = null;
        JsonFactory jsonFactory = new JsonFactory() ;
        ObjectMapper objectMapper = new ObjectMapper();
        String url = serverRootContextURL + "/getjobs" ;

        String jsonString = GET(url, serverUserName, serverPassword) ;
        jp = jsonFactory.createJsonParser(jsonString);
        orders = objectMapper.readValue(jp, Order[].class);
        List<Order> orderList = new ArrayList<Order>(Arrays.asList(orders)) ;

        return orderList;

    }*/

    public static ClientConfig getClientConfig() throws Exception {

        String url = serverRootContextURL + "/getconfig";
        String jsonString = GET(url, serverUserName, serverPassword);

        Gson gson = new Gson();
        return gson.fromJson(jsonString, ClientConfig.class);

    }

    private static String GET(String url, String user, String password) throws Exception {
        InputStream inputStream = null;
        String result = "";

        // create HttpClient
        DefaultHttpClient httpclient = new DefaultHttpClient();
        final HttpParams httpParameters = (HttpParams) httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
        HttpConnectionParams.setSoTimeout(httpParameters, 5000);


        final String basicAuth = "Basic " + Base64.encodeToString((user + ":" + password).getBytes(), Base64.NO_WRAP);
        HttpGet request = new HttpGet(url);
        request.setHeader("Authorization", basicAuth);
        // make GET request to the given URL
        HttpResponse httpResponse = (HttpResponse) httpclient.execute(request);

        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            // convert inputstream to string
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
                passwordOK = true;
            } else {
                result = "Did not work!";
            }
        } else if (httpResponse.getStatusLine().getStatusCode() == 401) {
            passwordOK = false;
            throw new BadCredentialException(AppContext.getAppContext().getResources().getString(R.string.error_bad_credentials));
        } else {
            String message = httpResponse.getStatusLine().getReasonPhrase();
            throw new Exception(AppContext.getAppContext().getResources().getString(R.string.comm_error) + " : " + message);
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public static void setServerPassword(String serverPassword) {
        RpcManager.serverPassword = serverPassword;
    }

    public static void SelectionDesParametres() throws Exception {

            String jsonStr = null;

                jsonStr = getJson(GetData(_urlJsonParamerte));

            if (jsonStr != null) {

                    JSONObject json = new JSONObject(jsonStr);
                    JSONArray jArray = json.getJSONArray("donnees");

                    for (int j = 0; j < jArray.length(); j++) {
                        JSONObject values = jArray.getJSONObject(j);
                        ftpHostName = values.getString("fT6RqE7");
                        ftpUserName = values.getString("M6Hg7ZQ");
                        ftpPassword = values.getString("K9Jd0s4");
                    }

            }

    }

    public static HttpResponse GetData(String url) throws IOException {

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
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            while (true) {

                if ((output = br.readLine()) == null) break;
            }
                } catch (IOException e) {
                    //e.printStackTrace();
                }
                sb.append(output);


        return sb.toString();
    }

    public static int getExifRotation(String imgPath) {

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imgPath);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        String rotationAmount = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            if (!TextUtils.isEmpty(rotationAmount)) {
                int rotationParam = Integer.parseInt(rotationAmount);
                switch (rotationParam) {
                    case ExifInterface.ORIENTATION_NORMAL:
                        return 0;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        //order.isRotationSet=true;
                        return 90;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        return 180;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        return 270;
                    default:
                        return 0;
                }
            } else {
                return 0;
            }

    }

    private static String lectureDesParametres(String key) {
        String datas = "";
        String prefs = AppContext.getAppContext().getSharedPreferences("params", Context.MODE_PRIVATE).getString(key, datas);
        return prefs;


    }

    private static String datePhoto(String filePath) {
        File file = new File(filePath);
        if (file.exists()) //Extra check, Just to validate the given path
        {
            ExifInterface intf = null;

            try {
                intf = new ExifInterface(filePath);
            } catch (IOException e) {
                //e.printStackTrace();
            }
            if (intf != null) {
                    dateExif = intf.getAttribute(ExifInterface.TAG_DATETIME);
                    //intf.get(ExifInterface.)
                    //////Log.i("Dated : " + dateString); //Dispaly dateString. You can do/use it your own way
                }

            if (dateExif == null) {
                dateExif = "";
            }
        }
        return dateExif;
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


    public static Bitmap decodeFileForDisplay(File f){


            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            //o.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
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

    public static Bitmap convertBitmapToCorrectOrientation(Bitmap photo,int rotation) {
        int width = photo.getWidth();
        int height = photo.getHeight();


        Matrix matrix = new Matrix();
        matrix.preRotate(rotation);

        return Bitmap.createBitmap(photo, 0, 0, width, height, matrix, false);

    }






}
