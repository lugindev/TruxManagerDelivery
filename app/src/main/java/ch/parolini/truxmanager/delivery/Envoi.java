package ch.parolini.truxmanager.delivery;

import android.content.Context;
import android.content.Intent;
import android.net.SSLCertificateSocketFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import ch.parolini.truxmanager.delivery.Manager.OrderManager;
import ch.parolini.truxmanager.delivery.model.Order;

public class Envoi extends AsyncTask<String, String, String> {
    private String resp;
    private List<String[]> lstimages = new ArrayList<>();

    private static MainActivity _activity;
    private int nbOrder = 0;
    private String extension;
    private boolean locksend = false;

    @Override
    protected String doInBackground(String... params) {
        /*AcitiviteContext.getContext().runOnUiThread(new Runnable() {
            public void run() {
                AcitiviteContext.getContext().updateOrderListView();
            }
        });*/
        String _ServeurPath = "";
        boolean result = false;
        locksend = true;
        List<Order> orders = new ArrayList<>();
        orders = OrderManager.getOrders();
        ////Log.d("ServiceUpdate", "controle photo upload");
        VariablesGlobales._notSendOrders = true;
        for (Order order : orders) {
            if (isCancelled())
                break;
            String orderId = order.getOrderNumber();
            try {
                lstimages.clear();
                nbOrder = 0;
                lstimages = VariablesGlobales.requeteBd.selectImagesNumero(order.getOrderNumber());
                for (String[] s : lstimages) {
                    if (isCancelled())
                        break;
                    File f = new File(s[1]);
                    if (VariablesGlobales.message.equals("")) {
                        VariablesGlobales.message = "_";
                    } else {
                        if (VariablesGlobales.message.charAt(0) != '_') {
                            VariablesGlobales.message = "_" + VariablesGlobales.message + "_";
                        }
                    }

                    //extension = ".mp4";
                    if (f.getName().contains(".")) {

                        extension = f.getName().substring(f.getName().length() - 4);
                    } else {

                    }
                    if (fileExists(AppContext.getAppContext(), f)) {
                        String date_photo = "";
                        //print the original last modified date
                        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd_HH:mm:ss");
                        date_photo = sdf.format(f.lastModified());
                        String[] fs1 = f.getName().split("/");
                        String[] name = fs1[fs1.length - 1].split("_");
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
                            String orderId1 = f.getName().split("/")[6];
                            orderId = orderId1.split("_")[0];
                        }
                        //changer t en f pour production!!!
                        _ServeurPath = orderId + "_" + strDate + "_" + id_photo + "_" + VariablesGlobales._versionCode + "_" + "f" + extension;
                        //upload("https://www.luginbuhl.ch/pesages/erik-test2.jpg", f, id, order);
                        doFileUpload("https://www.luginbuhl.ch/upload-photo_trux_manager.php", f, order, _ServeurPath,s[0]);

                    } else {
                        f.delete();
                        VariablesGlobales.requeteBd.effacerIamgeById(s[0]);
                        try {
                            order.deleteNumbre();
                            notifiyOrderSent(order);
                        } catch (IndexOutOfBoundsException e) {
                            //e.printStackTrace();
                        }
                    }
                }

            } catch (Exception e) {
                //e.printStackTrace();
            }

        }
        locksend = false;
       
        return resp;
    }


    @Override
    protected void onPostExecute(String result) {
        // execution of result of Long time consuming operation
        VariablesGlobales._lockSync = false;
        AcitiviteContext.getContext().runOnUiThread(new Runnable() {
            public void run() {
                AcitiviteContext.getContext().updateOrderListView();
            }
        });

    }


    @Override
    protected void onPreExecute() {
        
    }


    @Override
    protected void onProgressUpdate(String... text) {
        
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

    private synchronized void doFileUpload(String url1, File file, Order order, String _ServeurPath, String id) {
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
            if (conn instanceof HttpsURLConnection) {
                HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
                httpsConn.setSSLSocketFactory(SSLCertificateSocketFactory.getInsecure(0, null));
                httpsConn.setHostnameVerifier(new AllowAllHostnameVerifier());
            }
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
                VariablesGlobales.requeteBd.effacerIamgeById(id);
                try {
                    order.deleteNumbre();
                    notifiyOrderSent(order);
                } catch (IndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }
            }
            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (Exception ex) {
            Log.i("Upload", "error: " + ex.getMessage(), ex);

        }
    }
}