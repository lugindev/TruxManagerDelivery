package ch.parolini.truxmanager.delivery.Manager;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Credentials;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import com.google.gson.Gson;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ch.parolini.truxmanager.delivery.AppContext;
import ch.parolini.truxmanager.delivery.Application;
import ch.parolini.truxmanager.delivery.EasySSLSocketFactory;
import ch.parolini.truxmanager.delivery.MainActivity;
import ch.parolini.truxmanager.delivery.R;
import ch.parolini.truxmanager.delivery.ServerCommService;
import ch.parolini.truxmanager.delivery.VariablesGlobales;
import ch.parolini.truxmanager.delivery.model.ClientConfig;

import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
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
    private static Bitmap original = null;
    private static Bitmap resized = null;
    private static String _path;
    private static boolean _rename;
    private static boolean efface;
    private static boolean inFTP;
    private static FTPClient con = null;
    private static String dateExif;


    String imagePath;

    private static int serverResponseCode = 0;
    ProgressDialog dialog = null;

    String upLoadServerUri = null;
    private static String _urlJsonParamerte = "https://www.dsgsoft.ch/X3GR5T/Hv8bcFH9.json";
    ;
    private String Tag = "TransfertPhp";
    //private HttpClient client;


    public static boolean sendPicture(final String orderId, final File file, ServerCommService serverCommService, MainActivity activity) throws IOException {
        if(file.exists()) {
            Log.i("resultat", "exist");
            Log.i("resultat", "exist" +file.getPath());
            _activity = activity;
            original = null;
            resized = null;
            FTPFile[] files = new FTPFile[0];
            FTPFile[] filesExist = new FTPFile[0];
            boolean result = false;
            try {

                if (ftpHostName.equals("")) {
                    try {
                        SelectionDesParametres();
                    } catch (Exception e) {

                    }
                }

                if(con == null){
                con = new FTPSClient();
                }

                if (!con.isConnected()) {
                    con.connect(ftpHostName);


                    if (con.login(ftpUserName, ftpPassword)) {
                        con.enterLocalPassiveMode(); // important!
                        con.setFileType(FTP.BINARY_FILE_TYPE);
                    }
                }
                    String data = file.getPath();


                    //FileInputStream in = new FileInputStream(new File(data));
                    //original = BitmapFactory.decodeStream(in);
                    original = decodeFileForDisplay(new File(data));
                    //original.eraseColor(Color.TRANSPARENT);
                    String date_photo = datePhoto(data);
                    String date = "date";
                    String heure = "heure";
                    if (!date_photo.equals("")) {
                        date = date_photo.substring(0, 10).replace(":", "-");
                        heure = date_photo.substring(11, date_photo.length());
                        date_photo = date + "_" + heure;
                        renameFile(file.getPath(), orderId + "_" + date_photo + "_" + VariablesGlobales._versionCode + ".jpeg");
                        _path = orderId + "_" + date_photo + "_" + VariablesGlobales._versionCode + ".jpeg";
                    } else {
                        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HH:mm.SSS");//dd/MM/yyyy
                        Date now = new Date();
                        String strDate = sdfDate.format(now);
                        date = strDate.substring(0, 10).replace(":", "-");
                        heure = strDate.substring(11, strDate.length());
                        date_photo = date + "_" + heure;
                        //_path = orderId + "_" + date_photo + ".jpeg";
                        if (!file.exists()) {
                            renameFile(file.getPath(), orderId + "_" + strDate + "_" + VariablesGlobales._versionCode + ".jpeg");
                            _path = orderId + "_" + strDate + "_" + VariablesGlobales._versionCode + ".jpeg";
                        } else {
                            _path = file.getPath();
                        }
                    }

                    filesExist = con.listFiles("/../photo_trux_manager/" + _path.substring(0,_path.length()-5) +"f.jpeg");

                    for (FTPFile file1 : filesExist) {
                        Log.d("UploadTrue",file1.getName());
                        File target;
                        File target1;
                        target = new File(file.getPath());
                        target1 = new File(file.getPath().substring(0, file.getPath().length() - 4) + "_preview.jpeg");
                        if (target1.exists() && target1.isFile() && target1.canWrite()) {
                            target1.delete();
                            Log.d("d_file", "" + target1.getName());
                        }
                            if (target.exists() && target.isFile() && target.canWrite()) {
                                target.delete();
                                Log.d("d_file", "" + target.getName());
                            }

                        return true;
                    }


                    ByteArrayOutputStream out = new ByteArrayOutputStream();


                    String compression = lectureDesParametres("qualite_photos");
                    if(compression.equals("")) {
                        original.compress(Bitmap.CompressFormat.JPEG, Integer.parseInt(compression), out);
                    }
                    else {
                        original.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    }

                    files = con.listFiles("/../photo_trux_manager/" + _path);

                    long fileSizeInBytes = 0;
                    long fileSizeInKB = 0;
                    byte[] b = null;
                    for (FTPFile file1 : files) {
                        String[] tab = file1.getName().split("/");
                        inFTP = false;
                        if (tab[3].equals(_path)) {
                            // Get file from file name
                            //File file2 = new File(file.getPath());
                            inFTP = true;
                            // Get length of file in bytes
                            fileSizeInBytes = file.length();
                            // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                            fileSizeInKB = file1.getSize();


                            if (fileSizeInBytes == fileSizeInKB || fileSizeInKB > fileSizeInBytes) {

                            } else {
                                b = Arrays.copyOfRange(out.toByteArray(), (int) fileSizeInKB, (int) fileSizeInBytes);


                            }
                            Log.i("fileeeee", String.valueOf(fileSizeInBytes - fileSizeInKB));
                            //
                        }
                    }


                    if (b == null) {
                        b = out.toByteArray();
                    }

                    Log.i("QualiteEnvoi", lectureDesParametres("qualite_photos"));

                    con.setRestartOffset(fileSizeInKB);


                    result = con.storeFile("/../photo_trux_manager/" + _path, new ByteArrayInputStream(b));


                if(result == true){
                    con.rename("/../photo_trux_manager/" + _path," /../photo_trux_manager/" + _path.substring(0,_path.length()-5)+"f.jpeg");
                }

                filesExist = con.listFiles("/../photo_trux_manager/" + _path.substring(0,_path.length()-5) +"f.jpeg");

                File target;
                File target1;
                target = new File(file.getPath());
                target1 = new File(file.getPath().substring(0, file.getPath().length() - 4) + "_preview.jpeg");
                if(filesExist.length!=0) {
                    if (target1.exists() && target1.isFile() && target1.canWrite()) {
                        target1.delete();
                        Log.d("d_file", "" + target1.getName());
                    }
                }

                if (lectureDesParametres("action_fichier").equals("effacer") && filesExist.length!=0) {
                    if (target.exists() && target.isFile() && target.canWrite()) {
                        target.delete();
                        Log.d("d_file", "" + target.getName());
                    }
                }

                if (original != null && original.isRecycled() == false) {
                    original.recycle();
                }
                if (resized != null && resized.isRecycled() == false) {
                    resized.recycle();
                }

            } catch (Exception e) {
                Log.i("resultat", false + "_" + e.getMessage() + "_" + _path);
                return false;
            } finally {
                /*if (con != null) {
                    con.logout();
                    con.disconnect();
                }*/
            }
            Log.i("resultat", result + "_" + _path);

            return result;
        }
        else {
            Log.i("resultat", "not exist");
            Log.i("resultat", "not exist" +file.getPath());
            return true;
        }
    }




    public Boolean sendPictureHttpPostPhp(String path) {
        String exsistingFileName = path;

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        HttpURLConnection conn = null;
        try {
            // ------------------ CLIENT REQUEST

            Log.e(Tag, "Inside second Method");

            FileInputStream fileInputStream = new FileInputStream(new File(
                    exsistingFileName));

            // open a URL connection to the Servlet

            URL url = new URL("https://www.dsgsoft.ch//www/test_trux/");

            // Open a HTTP connection to the URL

            conn = (HttpURLConnection) url.openConnection();

            // Allow Inputs
            conn.setDoInput(true);

            // Allow Outputs
            conn.setDoOutput(true);

            // Don't use a cached copy.
            conn.setUseCaches(false);

            // Use a post method.
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection", "Keep-Alive");

            conn.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos
                    .writeBytes("Content-Disposition: post-data; name=uploadedfile;filename="
                            + exsistingFileName + "" + lineEnd);
            dos.writeBytes(lineEnd);

            Log.e(Tag, "Headers are written");

            // create a buffer of maximum size

            int bytesAvailable = fileInputStream.available();
            int maxBufferSize = 1000;
            // int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bytesAvailable];

            // read file and write it into form...

            int bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bytesAvailable);
                bytesAvailable = fileInputStream.available();
                bytesAvailable = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);
            }

            // send multipart form data necesssary after file data...

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            Log.e(Tag, "File is written");
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {
            Log.e(Tag, "error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            Log.e(Tag, "error: " + ioe.getMessage(), ioe);
        }

        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                    .getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                Log.e("Dialoge Box", "Message: " + line);
            }
            rd.close();

        } catch (IOException ioex) {
            Log.e("MediaPlayer", "error: " + ioex.getMessage(), ioex);
        }




        return true;
    }

        /*public boolean sendPictureWebDav(File flie) throws IOException {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("http://targethost/homepage");
            CloseableHttpResponse response1 = httpclient.execute(httpGet);
            try {
                HttpEntity entity1 = response1.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed
                EntityUtils.consume(entity1);
            } finally {
                response1.close();
            }
            HttpPost httpPost = new HttpPost("http://targethost/login");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("username", "vip"));
            nvps.add(new BasicNameValuePair("password", "secret"));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            CloseableHttpResponse response2 = httpclient.execute(httpPost);

            try {
                HttpEntity entity2 = response2.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed
                EntityUtils.consume(entity2);
            } finally {
                response2.close();
            }



        return true;

        }*/


    public static String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException {

        String res = "";
        StringBuffer buffer = new StringBuffer();
        inputStream = response.getEntity().getContent();
        int contentLength = (int) response.getEntity().getContentLength(); //getting content length…..


        if (contentLength < 0) {
        } else {
            byte[] data = new byte[512];
            int len = 0;
            try {
                while (-1 != (len = inputStream.read(data))) {
                    buffer.append(new String(data, 0, len)); //converting to string and appending  to stringbuffer…..
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close(); // closing the stream…..
            } catch (IOException e) {
                e.printStackTrace();
            }
            res = buffer.toString();     // converting stringbuffer to string…..


            //System.out.println("Response => " +  EntityUtils.toString(response.getEntity()));
        }
        return res;
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
        final HttpParams httpParameters = httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
        HttpConnectionParams.setSoTimeout(httpParameters, 5000);


        final String basicAuth = "Basic " + Base64.encodeToString((user + ":" + password).getBytes(), Base64.NO_WRAP);
        HttpGet request = new HttpGet(url);
        request.setHeader("Authorization", basicAuth);
        // make GET request to the given URL
        HttpResponse httpResponse = httpclient.execute(request);

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
            throw new BadCredentialException(Application.getAppContext().getResources().getString(R.string.error_bad_credentials));
        } else {
            String message = httpResponse.getStatusLine().getReasonPhrase();
            throw new Exception(Application.getAppContext().getResources().getString(R.string.comm_error) + " : " + message);
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

    public static int getExifRotation(String imgPath) {
        try {
            ExifInterface exif = new ExifInterface(imgPath);
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
        } catch (Exception ex) {
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

    public static Bitmap convertBitmapToCorrectOrientation(Bitmap photo,int rotation) {
        int width = photo.getWidth();
        int height = photo.getHeight();


        Matrix matrix = new Matrix();
        matrix.preRotate(rotation);

        return Bitmap.createBitmap(photo, 0, 0, width, height, matrix, false);

    }






}
