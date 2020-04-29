package ch.parolini.truxmanager.delivery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import ch.parolini.truxmanager.delivery.Manager.FileManager;
import ch.parolini.truxmanager.delivery.Manager.OrderManager;
import ch.parolini.truxmanager.delivery.Manager.RpcManager;
import ch.parolini.truxmanager.delivery.basededonnee.Requetes;
import ch.parolini.truxmanager.delivery.gui.ListAdapter;
import ch.parolini.truxmanager.delivery.model.ClientConfig;
import ch.parolini.truxmanager.delivery.model.Order;

import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.DisplayMetrics;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_ACTIVITY_REQUEST_CODE = 200;
    private static final String PASSWORD_KEY_PREF = "pass";
    private static final int REQUEST_READ_STORAGE = 113;
    private static final int REQUEST_CHANGE_NETWORK_STATE = 114;
    private static final int REQUEST_CHANGE_WIFI_STATE = 115;
    private static final int REQUEST_ACCESS_NETWORK_STATE = 116;
    private static final int PERMISSION_RECEIVE_BOOT_COMPLETED = 117;
    private static final int PERMISSION_CAMERA = 118;
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
    Uri imageUri;
    private File imageFile;

    //private HttpClient client;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageToast);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mConfigReceiver);

    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        MainActivity.this.setTitle(Html.fromHtml("Liste des bons"));
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }

        boolean hasPermission1 = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission1) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE);
        }

        boolean hasPermission2 = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission2) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                    REQUEST_ACCESS_NETWORK_STATE);
        }

        boolean hasPermission3 = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission3) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                    REQUEST_ACCESS_NETWORK_STATE);
        }

        boolean hasPermission4 = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission4) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CHANGE_WIFI_STATE},
                    REQUEST_CHANGE_WIFI_STATE);
        }


        boolean hasPermission5 = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CHANGE_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission5) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CHANGE_NETWORK_STATE},
                    REQUEST_CHANGE_NETWORK_STATE);
        }

        boolean hasPermission6 = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_BOOT_COMPLETED) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission6) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},
                    PERMISSION_RECEIVE_BOOT_COMPLETED);
        }

        boolean hasPermission7 = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission7) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_CAMERA);
        }

        if (lectureDesParametres("action_default").equals("")) {
            paramertes[1] = "photo_gallerie";
            sauvegardeDesParametres(paramertes);
        }


        if (lectureDesParametres("action_color").equals("")) {
            paramertes[2] = "color";
            sauvegardeDesParametres(paramertes);
        }


        if (lectureDesParametres("action_fichier").equals("")) {
            paramertes[3] = "effacer";
            sauvegardeDesParametres(paramertes);
        }


        if (lectureDesParametres("action_taille").equals("")) {
            paramertes[4] = "grande";
            sauvegardeDesParametres(paramertes);

        }

        if (lectureDesParametres("action_theme").equals("")) {
            paramertes[5] = "color";
            sauvegardeDesParametres(paramertes);

        }

        if (lectureDesParametres("qualite_photos").equals("")) {
            paramertes[0] = "95";
            sauvegardeDesParametres(paramertes);
        }


        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            VariablesGlobales._versionName = pInfo.versionName;
            VariablesGlobales._versionCode = String.valueOf(pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
        }


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        thisActivity = this;

        if (isMyServiceRunning(ServiceUpload.class) == false && lectureDesParametresOrdres("nb_photos") != 0) {
            /*startService(new Intent(getBaseContext(), SerciceRedemarrage.class));
        } else {
            stopService(new Intent(getBaseContext(), SerciceRedemarrage.class));
        }*/
            startService(new Intent(getBaseContext(), ServiceUpload.class));

        }

        if (VariablesGlobales._isOnBoot == true && lectureDesParametresOrdres("nb_photos") == 0) {
            VariablesGlobales._isOnBoot = false;
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                finishAndRemoveTask();
            } else {
                finish();
            }
        } else {
            if (isMyServiceRunning(ServiceUpload.class) == false) {
                startService(new Intent(getBaseContext(), ServiceUpload.class));
            }
            /*if (android.os.Build.VERSION.SDK_INT >= 21) {
                finishAndRemoveTask();
            } else {
                finish();
            }*/
            if (isSelected == true) {
                setMenu();
            }
            if (VariablesGlobales.networkInfos == true) {

                /*if(ServerCommService.getExistingInstance(MainActivity.this)!=null) {
                    ServerCommService.getExistingInstance(MainActivity.this).forceSynchroNow();
                }*/
            }
        }

        OrderManager.readFromLocalStorage(this.getApplicationContext());
        RpcManager.serverRootContextURL = getResources().getString(R.string.SERVER_ROOT_URL);
        RpcManager.serverUserName = getResources().getString(R.string.SERVER_ROOT_USER);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Set the previous password
        RpcManager.setServerPassword(preferences.getString(PASSWORD_KEY_PREF, ""));

        // Will be overrriden by client settings
        OrderManager.setMaxAgeMinutes(getResources().getInteger(R.integer.MAX_WEIGHING_AGE_BEFORE_UPLOAD_MINUTES));

        try {


            File file = getImageFolder();
            Log.d("Files", "Path: " + file.getPath());
            File directory = new File(file.getPath());
            File[] files = directory.listFiles();
            Log.d("Files", "Size: " + files.length);
            for (int i = 0; i < files.length; i++) {
                requetesBaseDeDonneeInterne = new Requetes(AppContext.getAppContext());
                requetesBaseDeDonneeInterne.open();
                if (requetesBaseDeDonneeInterne.selectImagesByName(files[i].getPath()).size() == 0) {
                    requetesBaseDeDonneeInterne.ajouterImage(files[i].getPath());
                    OrderManager.currentOrder.addNewPicture(currentPictureFile);
                    //files[i].delete();
                    Log.d("Files", "FileName:" + files[i].getPath());
                }
            }
        } catch (Exception e) {

        }

        setContentView(R.layout.activity_main);
        list = (ListView) findViewById(R.id.list);
        list.setLongClickable(true);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list.setItemsCanFocus(false);

        TextView emptyText = (TextView) findViewById(R.id.empty);
        list.setEmptyView(emptyText);


        addButton = (FloatingActionButton) findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Toast.makeText(thisActivity, "Nouveau bon de commande. Positionnez le code bar dans le rectangle pour le scanner ", Toast.LENGTH_LONG).show();
                isSelected = true;
                showBarCodeReader();
            }
        });

        addButton.setColorFilter(Color.WHITE);


        //registerForContextMenu(list);

        ContoleEtatDuReseau();
        choixDuTheme();
        // Click event for single list row
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                isSelected = true;
                setMenu();
                switch (lectureDesParametres("action_default")) {
                    case "":
                        break;
                    case "photo_gallerie":
                        break;
                    case "photo":
                        OrderManager.currentOrder = (Order) parent.getItemAtPosition(position);
                        VariablesGlobales.currentOrder = (Order) parent.getItemAtPosition(position);
                        showCamera(OrderManager.currentOrder);
                        break;
                    case "gallerie":
                        OrderManager.currentOrder = (Order) parent.getItemAtPosition(position);
                        VariablesGlobales.currentOrder = (Order) parent.getItemAtPosition(position);
                        showGallery(OrderManager.currentOrder);
                        break;
                }
                OrderManager.currentOrder = (Order) parent.getItemAtPosition(position);
                VariablesGlobales.currentOrder = (Order) parent.getItemAtPosition(position);
                setTitle(Html.fromHtml("N°: " + OrderManager.currentOrder.getOrderNumber()));
                mySnackbar = Snackbar.make(view, R.id.mycoordinatorlayout, 10000);


                //OrderManager.currentOrder = (Order) parent.getItemAtPosition(position);
                //showCamera(OrderManager.currentOrder);
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int stringId = 0;
                mySnackbar = Snackbar.make(view, R.id.mycoordinatorlayout, 10000);
                OrderManager.currentOrder = (Order) parent.getItemAtPosition(position);
                OrderManager.currentOrder.hasPictureFiles();
                VariablesGlobales.currentOrder = (Order) parent.getItemAtPosition(position);
                onclick = false;
                mMenu.findItem(R.id.action_supprimer_photos).setVisible(true);
                mMenu.findItem(R.id.action_supprimerbon).setVisible(true);
                mMenu.findItem(R.id.action_gallery).setVisible(false);
                mMenu.findItem(R.id.action_gallery_mult).setVisible(false);
                mMenu.findItem(R.id.action_photo).setVisible(false);
                setTitle(Html.fromHtml("N°: " + OrderManager.currentOrder.getOrderNumber()));
                return true;
            }
        });


        // We start the backgroud service
        Intent intent = new Intent(this, ServerCommService.class);
        startService(intent);

        // Register to receive messages.
        // We are registering an observer (mWeighingReceiver) to receive Intents
        // with actions named "custom-event-name".
//        LocalBroadcastManager.getInstance(this).registerReceiver(mWeighingReceiver,
//                new IntentFilter("onWeightingLoaded"));


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageToast,
                new IntentFilter("onToast"));

        LocalBroadcastManager.getInstance(this).registerReceiver(mConfigReceiver,
                new IntentFilter("onConfigReceived"));


        IntentFilter filter1 = new IntentFilter();
        filter1.addAction("android.net.conn.CONNECTIVITY_ACTION");
        filter1.addAction("android.net.conn.CONNECTIVITY_CHANGE");


        broadcastReceiverEtatDuReseau = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ContoleEtatDuReseau();


            }

        };

        try {
            this.registerReceiver(broadcastReceiverEtatDuReseau, filter1);
        } catch (Exception e) {

        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mOrderSentReceiver,
                new IntentFilter("onOrderSent"));


        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                /*if (VariablesGlobales._notSendOrders == true) {
                    if (isMyServiceRunning(ServiceUpload.class) == false) {
                        //startService(new Intent(getBaseContext(), SerciceRedemarrage.class));
                        startService(new Intent(getBaseContext(), ServiceUpload.class));
                    }

                    View view1 = getWindow().getDecorView().getRootView();
                    if (snackBarInfo == true) {
                        mySnackbar1 = Snackbar.make(view1, R.id.mycoordinatorlayout, 30000);
                        mySnackbar1.setText("Si il reste des photos à télécharger merci de ne pas fermer l'application.");
                        mySnackbar1.setAction(R.string.ok, new MyUndoListener2(MainActivity.this));
                        mySnackbar1.setActionTextColor(Color.parseColor("#ffffff"));
                        mySnackbar1.show();
                    }
                } else {
                    if (isMyServiceRunning(ServiceUpload.class) == true) {
                        stopService(new Intent(getBaseContext(), ServiceUpload.class));
                    }

                    //finish();
                }*/
            }
        };

    }

    /**
     * Apres chaque envoi d'ordre vers le serveur
     */
    private BroadcastReceiver mOrderSentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Order order = (Order) intent.getSerializableExtra("order");
            Log.d("receiver", "Got message: " + order);
            updateOrderListView();
        }
    };


    private BroadcastReceiver mConfigReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ClientConfig clientConfig = (ClientConfig) intent.getSerializableExtra("message");
            Log.d("receiver", "Got message: " + clientConfig);
            OrderManager.setMaxAgeMinutes(clientConfig.MAX_WEIGHING_AGE_BEFORE_UPLOAD_MINUTES);
        }
    };


    private BroadcastReceiver broadcastReceiverEtatDuReseau = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ContoleEtatDuReseau();
        }

    };




    private BroadcastReceiver mMessageToast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intentg
            String message = (String) intent.getSerializableExtra("message");
            Log.d("receiver", "Got message: " + message);
            ////Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            showMessage(message);
        }
    };




    public void updateOrderListView() {
        ListView list = (ListView) findViewById(R.id.list);

        // avoid loosing scroll
        //http://vikinghammer.com/2011/06/17/android-listview-maintain-your-scroll-position-when-you-refresh/
        // if (OrderManager.hasOrder()) {
        if (list.getAdapter() == null) {
            list.setAdapter(new ListAdapter(thisActivity, OrderManager.getOrders()));
        } else {
            ((ListAdapter) list.getAdapter()).refill(OrderManager.getOrders());
        }
        //}
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.list) {
            if (onclick == true) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.menu_click, menu);

            } else {
                if (OrderManager.currentOrder.hasPictureFiles()) {
                    MenuInflater inflater = getMenuInflater();
                    inflater.inflate(R.menu.menu_long_click, menu);
                } else {
                    MenuInflater inflater = getMenuInflater();
                    inflater.inflate(R.menu.menu_long_click1, menu);
                }
            }

        }
        onclick = false;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Order ordre = OrderManager.currentOrder;
        switch (item.getItemId()) {
            case R.id.supprimer_photos:


                mShowVisible = false; // or false
                invalidateOptionsMenu();
                ordre.cleanImageFilesOnPhone(ordre);
                ////Toast.makeText(thisActivity, "Toutes les photos du bon: " + ordre.getOrderNumber() + " effacées.", Toast.LENGTH_LONG).show();
                updateOrderListView();


                // add stuff here
                return true;
            case R.id.supprimer_ordre:
                mShowVisible = false; // or false
                invalidateOptionsMenu();
                OrderManager.removeCurrentOrder();
                ////Toast.makeText(thisActivity, "Bon de commande " + ordre.getOrderNumber() + " supprimé", Toast.LENGTH_LONG).show();
                updateOrderListView();

                // edit stuff here
                return true;

            case R.id.action_gallery:
                showGallery(OrderManager.currentOrder);

                // add stuff here
                return true;

            case R.id.action_gallery_mult:
                showGallery1(OrderManager.currentOrder);

                // add stuff here
                return true;
            case R.id.action_image:
                VariablesGlobales.syncNoUpdate = true;
                showCamera(OrderManager.currentOrder);

                // edit stuff here
                return true;

            default:
                return super.onContextItemSelected(item);

        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        setMenu();


        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    protected void onResume() {
        Log.i("TRUX", "Resume called");

        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("EVENT_SNACKBAR"));

    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        OrderManager.persistOrdersToLocalStorage(this.getApplicationContext());

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        OrderManager.readFromLocalStorage(this.getApplicationContext());
        /*if(VariablesGlobales._blockSendOrders == false) {
            VariablesGlobales._blockSendOrders = true;
            ServerCommService.getExistingInstance().forceSynchroNow();
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        choixDuTheme();
        //updateOrderListView();
        if (isSelected == true) {
            setMenu();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
        choixDuTheme();
        //updateOrderListView();
        if (isSelected == true) {
            setMenu();
        }
        if (VariablesGlobales.networkInfos == true) {

            if (ServerCommService.getExistingInstance(MainActivity.this) != null) {
                //ServerCommService.getExistingInstance(MainActivity.this).forceSynchroNow();
            }
        }
        //unregisterReceiver(broadcastReceiverEtatDuReseau);
    }

    @Override
    public void onBackPressed() {



        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
            return;
        }

        if (mMenu.findItem(R.id.action_supprimerbon).isVisible() == false) {
            super.onBackPressed();
        }

        if (mMenu.findItem(R.id.action_supprimerbon).isVisible() == true) {
            mMenu.findItem(R.id.action_supprimerbon).setVisible(false);
            mMenu.findItem(R.id.action_supprimer_photos).setVisible(false);
        }


    }


    private void showBarCodeReader() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(BarCodeActivity.class);
        integrator.setOrientationLocked(false);
        integrator.addExtra("SCAN_WIDTH", 800);
        integrator.addExtra("SCAN_HEIGHT", 200);
        //integrator.addExtra("RESULT_DISPLAY_DURATION_MS",3000L);
        integrator.addExtra("PROMPT_MESSAGE", "Positionnez le code bar sur la ligne rouge");
        integrator.initiateScan();
    }

    /**
     * @param message
     */
    private void showMessage(String message) {
        TextView textView = (TextView) findViewById(R.id.messageText);
        textView.setText(message);
    }


    /**
     * Call the camera intent
     *
     * @param order
     */
    private void showCamera(Order order) {
        //  Bundle extras=getIntent().getExtras();

        File folder = FileManager.getImageFolder();

        // avoid to start upload pics to server during the picture taken process
       // order.touch();*/

        currentPictureFile = new File(folder.getPath() + "/" + FileManager.createNewImageFileNameForOrder(order.getOrderNumber()));
        currentPictureFile = FileManager.createNewImageFileForOrder(order.getOrderNumber());
        Uri outputFileUri = Uri.fromFile(currentPictureFile); // create a file to save the image

        //(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        //ContentValues values = new ContentValues();
        String deviceName = android.os.Build.MODEL;
        String deviceMan = android.os.Build.MANUFACTURER;
        Log.i("Manufacturer",deviceName);
        Log.i("Manufacturer",deviceMan);
        imageUri = outputFileUri;
        imageFile = new File(imageUri.getPath());
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent,CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * @param order
     */
    private void showGallery(Order order) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        // Start the Intent
        startActivityForResult(galleryIntent, PICK_IMAGE_MULTIPLE);


    }

    private void showGallery1(Order order) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        // Start the Intent
        startActivityForResult(galleryIntent, PICK_IMAGE_MULTIPLE1);

        /*Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_MULTIPLE);*/


    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && (resultCode == 0 || resultCode == RESULT_OK) && currentPictureFile.length()!=0) {
            //Toast.makeText(this, "Nouvelle image rajoutée! ", Toast.LENGTH_SHORT).show();
            one = new Thread() {
                public void run() {
                    OrderManager.currentOrder.addNewPicture(currentPictureFile);
                    try {
                        requetesBaseDeDonneeInterne = new Requetes(AppContext.getAppContext());
                        requetesBaseDeDonneeInterne.open();
                        requetesBaseDeDonneeInterne.ajouterImage(currentPictureFile.getPath());
                        currentPictureFile = null;

                    } catch (Exception e) {

                    } finally {
                    }

                    try {
                        VariablesGlobales.syncNoUpdate = true;
                        showCamera(OrderManager.currentOrder);
                    } catch (RuntimeException e) {

                    }
                }

            };

            one.start();
            Log.d("adding_path", "adding path: " + currentPictureFile.toString());

            // From gallery
        }


        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            String scanCode = scanningResult.getContents();
            if (scanCode != null) {
                //Toast.makeText(this, "Veuillez rajouter des images pour le bon de commande No  " + scanCode, Toast.LENGTH_SHORT).show();
                OrderManager.createAndSetCurrent(scanCode);
                try {
                    showCamera(OrderManager.currentOrder);
                } catch (RuntimeException e) {

                }

            } else {
                //Toast.makeText(this, "Scan annulé.", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            //Toast.makeText(this, "Nouvelle image rajoutée! ", Toast.LENGTH_SHORT).show();

        }


        try {
            // When an Image is picked
            one = new Thread() {
                public void run() {
                    if (requestCode == PICK_IMAGE_MULTIPLE1 && resultCode == RESULT_OK
                            && null != data) {
                        // Get the Image from data
                        imagesEncodedList = new ArrayList<String>();
                        if (data.getClipData() != null) {
                            /*try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {

                            }*/
                            Uri mImageUri = null;
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                mImageUri = data.getClipData().getItemAt(i).getUri();
                                Log.i("mImageUri", String.valueOf(mImageUri));

                                String wholeID = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    wholeID = DocumentsContract.getDocumentId(mImageUri);
                                }

                                String id = wholeID.split(":")[1];
                                String[] column = {MediaStore.Images.Media.DATA};

                                String sel = MediaStore.Images.Media._ID + "=?";

                                Cursor cursor = getContentResolver().
                                        query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                column, sel, new String[]{id}, null);

                                String filePath = "";

                                int columnIndex = cursor.getColumnIndex(column[0]);

                                if (cursor.moveToFirst()) {
                                    filePath = cursor.getString(columnIndex);
                                    try {
                                        // Log.i("mImageUri", filePath);
                                        //String[] tab = filePath.split("/");
                                        //filePath = "/storage/emulated/0/Pictures/TruxManager/" + tab[tab.length-1];
                                        //Log.i("mImageUri", filePath);

                                        File imageCopy = FileManager.copyImageFromGallery(new File(filePath), VariablesGlobales.currentOrder.getOrderNumber());
                                        VariablesGlobales.currentOrder.addNewPicture(imageCopy);

                                        try {
                                            requetesBaseDeDonneeInterne = new Requetes(AppContext.getAppContext());
                                            requetesBaseDeDonneeInterne.open();
                                            requetesBaseDeDonneeInterne.ajouterImage(imageCopy.getPath());
                                            requetesBaseDeDonneeInterne.close();
                                            // imageCopy.delete();

                                        } catch (Exception e) {

                                        } finally {
                                        }


                                        //Toast.makeText(this, "Nouvelle image rajoutée! ", Toast.LENGTH_SHORT).show();
                                    } catch(Exception e){

                                    }

                                }

                                cursor.close();
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        updateOrderListView();

                                    }
                                });

                                OrderManager.persistOrdersToLocalStorage(getBaseContext());

                                //cursor.close();
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {

                                }
                            }
                            if (isMyServiceRunning(ServiceUpload.class) == false) {
                                startService(new Intent(getBaseContext(), ServiceUpload.class));
                            }

                        } else {

                            imagesEncodedList = new ArrayList<String>();
                            if (data.getData() != null) {

                                Uri mImageUri = data.getData();
                                Log.i("mImageUri", String.valueOf(mImageUri));

                                String wholeID = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                    wholeID = DocumentsContract.getDocumentId(mImageUri);
                                }

                                String id = wholeID.split(":")[1];
                                String[] column = {MediaStore.Images.Media.DATA};

                                String sel = MediaStore.Images.Media._ID + "=?";

                                Cursor cursor = getContentResolver().
                                        query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                column, sel, new String[]{id}, null);

                                String filePath = "";

                                int columnIndex = cursor.getColumnIndex(column[0]);

                                if (cursor.moveToFirst()) {
                                    filePath = cursor.getString(columnIndex);
                                    try {
                                        // Log.i("mImageUri", filePath);
                                        //String[] tab = filePath.split("/");
                                        //filePath = "/storage/emulated/0/Pictures/TruxManager/" + tab[tab.length-1];
                                        //Log.i("mImageUri", filePath);
                                        File imageCopy = FileManager.copyImageFromGallery(new File(filePath), VariablesGlobales.currentOrder.getOrderNumber());
                                        VariablesGlobales.currentOrder.addNewPicture(imageCopy);
                                        try {
                                            requetesBaseDeDonneeInterne = new Requetes(AppContext.getAppContext());
                                            requetesBaseDeDonneeInterne.open();
                                            requetesBaseDeDonneeInterne.ajouterImage(imageCopy.getPath());
                                            // imageCopy.delete();
                                            runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {

                                                    updateOrderListView();

                                                }
                                            });

                                        } catch (Exception e) {

                                        } finally {
                                        }

                                        if (isMyServiceRunning(ServiceUpload.class) == false) {
                                            startService(new Intent(getBaseContext(), ServiceUpload.class));
                                        }
                                        Log.i("mImageUri", imageCopy.getPath());
                                        //Toast.makeText(this, "Nouvelle image rajoutée! ", Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                cursor.close();

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        updateOrderListView();

                                    }
                                });

                                OrderManager.persistOrdersToLocalStorage(getBaseContext());
                                //cursor.close();
                            }
                        }
                    }
                }
            };

            one.start();
        } catch (Exception e) {

        }

        try {
            // When an Image is picked
            one = new Thread() {
                public void run() {
                    if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                            && null != data) {
                        // Get the Image from data
                        imagesEncodedList = new ArrayList<String>();
                        if (data.getData() != null) {

                            Uri mImageUri = data.getData();
                            Log.i("mImageUri", String.valueOf(mImageUri));

                            String wholeID = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                wholeID = DocumentsContract.getDocumentId(mImageUri);
                            }

                            String id = wholeID.split(":")[1];
                            String[] column = {MediaStore.Images.Media.DATA};

                            String sel = MediaStore.Images.Media._ID + "=?";

                            Cursor cursor = getContentResolver().
                                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            column, sel, new String[]{id}, null);

                            String filePath = "";

                            int columnIndex = cursor.getColumnIndex(column[0]);

                            if (cursor.moveToFirst()) {
                                filePath = cursor.getString(columnIndex);
                                try {
                                    // Log.i("mImageUri", filePath);
                                    //String[] tab = filePath.split("/");
                                    //filePath = "/storage/emulated/0/Pictures/TruxManager/" + tab[tab.length-1];
                                    //Log.i("mImageUri", filePath);
                                    File imageCopy = FileManager.copyImageFromGallery(new File(filePath), VariablesGlobales.currentOrder.getOrderNumber());
                                    VariablesGlobales.currentOrder.addNewPicture(imageCopy);
                                    try {
                                        requetesBaseDeDonneeInterne = new Requetes(AppContext.getAppContext());
                                        requetesBaseDeDonneeInterne.open();
                                        requetesBaseDeDonneeInterne.ajouterImage(imageCopy.getPath());
                                        // imageCopy.delete();
                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {

                                                updateOrderListView();

                                            }
                                        });

                                    } catch (Exception e) {

                                    } finally {
                                    }

                                    if (isMyServiceRunning(ServiceUpload.class) == false) {
                                        startService(new Intent(getBaseContext(), ServiceUpload.class));
                                    }
                                    Log.i("mImageUri", imageCopy.getPath());
                                    //Toast.makeText(this, "Nouvelle image rajoutée! ", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            cursor.close();

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    updateOrderListView();

                                }
                            });

                            OrderManager.persistOrdersToLocalStorage(getBaseContext());
                            //cursor.close();
                        }
                    } else {
                        //Toast.makeText(this, "You haven't picked Image",
                        //Toast.LENGTH_LONG).show();
                    }


                }
            };

            one.start();
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    updateOrderListView();

                }
            });

        } catch (Exception e) {
            //Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
            //.show();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        setMenu();


        return true;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        choixDuTheme();
        //updateOrderListView();
        if(isSelected==true) {
            setMenu();
        }

        snackBarInfo=false;


        if(isMyServiceRunning(ServiceUpload.class)==false) {
            startService(new Intent(this, ServiceUpload.class));
        }




    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
            // do what you want with the power button
            Toast.makeText(MainActivity.this, "Your LongPress Power Button", Toast.LENGTH_SHORT).show();
        }
        return super.dispatchKeyEvent(event);
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final Order ordre = OrderManager.currentOrder;
        int id = item.getItemId();
        if (id == R.id.action_supprimerbon) {
            mShowVisible = false; // or false
            mySnackbar.setText("Suppression definitive bon "+ ordre.getOrderNumber());
            mySnackbar.setAction(R.string.annuler, new MyUndoListener1(MainActivity.this,ordre));
            if(VariablesGlobales.networkInfos ==true){
                mySnackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            }
            else {
                mySnackbar.setActionTextColor(Color.parseColor("#9b0000"));
            }
            mySnackbar.show();
            invalidateOptionsMenu();
            clearPhoto=true;
            handlerClearPhotos = new Handler();
            handlerClearPhotos.postDelayed(runnableClearPhotos = new Runnable() {
                @Override
                public void run() {
                    if(clearPhoto==true) {
                        OrderManager.removeCurrentOrder();
                        ////Toast.makeText(thisActivity, "Bon de commande " + ordre.getOrderNumber() + " supprimé", Toast.LENGTH_LONG).show();
                        if (VariablesGlobales.syncNoUpdate == true) {
                            VariablesGlobales.syncNoUpdate = false;
                            if(ServerCommService.getExistingInstance(MainActivity.this)!=null) {
                                ServerCommService.getExistingInstance(MainActivity.this).forceSynchroNow();
                            }
                        }
                        updateOrderListView();
                        mySnackbar.dismiss();
                    }
                }
            }, 10000);

            return true;
        }

        if (id == R.id.action_supprimer_photos) {
            mShowVisible = false; // or false
            mySnackbar.setText("Suppression photos bon " + ordre.getOrderNumber());
            mySnackbar.setAction(R.string.annuler, new MyUndoListener(MainActivity.this, ordre));
            if(lectureDesParametres("action_theme").equals("color")) {
                if (VariablesGlobales.networkInfos == true) {
                    mySnackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                } else {
                    mySnackbar.setActionTextColor(Color.parseColor("#9b0000"));
                }
            }
            else {
                if (VariablesGlobales.networkInfos == true) {
                    mySnackbar.setActionTextColor(Color.parseColor("#ffffff"));
                } else {
                    mySnackbar.setActionTextColor(Color.parseColor("#9b0000"));
                }
            }
            mySnackbar.show();
            mMenu.findItem(R.id.action_gallery).setVisible(true);
            mMenu.findItem(R.id.action_gallery_mult).setVisible(true);
            mMenu.findItem(R.id.action_photo).setVisible(true);
            invalidateOptionsMenu();
            cleanPhoto=true;
            handlerCleanPhotos = new Handler();
            handlerCleanPhotos.postDelayed( runnableCleanPhotos = new Runnable() {
                @Override
                public void run() {
                    if(cleanPhoto==true) {
                        ordre.cleanImageFilesOnPhone(ordre);
                        if (VariablesGlobales.syncNoUpdate == true) {
                            VariablesGlobales.syncNoUpdate = false;
                            if(ServerCommService.getExistingInstance(MainActivity.this)!=null) {
                                ServerCommService.getExistingInstance(MainActivity.this).forceSynchroNow();
                            }
                        }
                        ////Toast.makeText(thisActivity, "Toutes les photos du bon: " + ordre.getOrderNumber() + " effacées.", Toast.LENGTH_LONG).show();
                        updateOrderListView();
                    }
                }
            }, 10000);

            return true;
        }

        if (id == R.id.action_gallery) {
            /*Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/
            //if(isSelected) {
            showGallery(OrderManager.currentOrder);
            /*}
            else {
                mySnackbar.setText("Aucun bon selectionné "+ ordre.getOrderNumber());
                mySnackbar.setAction(R.string.ok, new MyUndoListener(MainActivity.this,ordre));
                if(VariablesGlobales.networkInfos ==true){
                    mySnackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                }
                else {
                    mySnackbar.setActionTextColor(Color.parseColor("#9b0000"));
                }
            }*/
            return true;
        }

        if (id == R.id.action_gallery_mult) {
            /*Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/
            //if(isSelected) {
            showGallery1(OrderManager.currentOrder);
            /*}
            else {
                mySnackbar.setText("Aucun bon selectionné "+ ordre.getOrderNumber());
                mySnackbar.setAction(R.string.ok, new MyUndoListener(MainActivity.this,ordre));
                if(VariablesGlobales.networkInfos ==true){
                    mySnackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                }
                else {
                    mySnackbar.setActionTextColor(Color.parseColor("#9b0000"));
                }
            }*/
            return true;
        }



        if (id == R.id.action_photo) {
            /*Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/
            //if(isSelected) {
            //OrderManager.currentOrder.addNewPicture(currentPictureFile);
            if(OrderManager.currentOrder!=null) {
                showCamera(OrderManager.currentOrder);
            }
            else {
                View view1 = getWindow().getDecorView().getRootView();
                mySnackbar1 = Snackbar.make(view1, R.id.mycoordinatorlayout, 10000);
                mySnackbar1.setText("Aucun ticket séléctionné.");
                mySnackbar1.setAction(R.string.ok, new MyUndoListener2(MainActivity.this));
                mySnackbar1.setActionTextColor(Color.parseColor("#ffffff"));
                mySnackbar1.show();
            }
            /*}
            else {
                mySnackbar.setText("Aucun bon selectionné "+ ordre.getOrderNumber());
                mySnackbar.setAction(R.string.ok, new MyUndoListener(MainActivity.this,ordre));
                if(VariablesGlobales.networkInfos ==true){
                    mySnackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                }
                else {
                    mySnackbar.setActionTextColor(Color.parseColor("#9b0000"));
                }
            }*/
            return true;
        }

        if (id == R.id.action_parametres) {
            FragmentParametres fragment = new FragmentParametres(MainActivity.this);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.frame_content, fragment).addToBackStack(null);
            fragmentTransaction.commit();
            return true;
        }



        /*if (id == R.id.action_sychronizer) {
            ServerCommService.getExistingInstance().forceSynchroNow();
            return true;
        }*/



        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //reload my activity with permission granted or use the features what required the permission
                } else {
                    //Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:{
                if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //openCamera();
                } else {
                }
                }
        }

    }


    public void ContoleEtatDuReseau() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            Log.i("infoReseau","Internet disponible");
            VariablesGlobales.networkInfos = true;
            setTitle(Html.fromHtml("Trux Manager"));

        } else {
            setTitle(Html.fromHtml("Trux Manager"));
            VariablesGlobales.networkInfos = false;
            VariablesGlobales.syncNoUpdate = true;
            Log.i("infoReseau","Internet indisponible");

        }

        choixDuTheme();
        updateOrderListView();

    }

    public void setStatusBarColor(String color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = MainActivity.this.getWindow();
            int statusBarColor = Color.parseColor(color);

            if (statusBarColor == Color.BLACK && window.getNavigationBarColor() == Color.BLACK) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            window.setStatusBarColor(statusBarColor);
        }
    }

    private String lectureDesParametres(String key) {
        String datas = "";
        String prefs = getSharedPreferences("params", Context.MODE_PRIVATE).getString(key, datas);
        return prefs;


    }

    private int lectureDesParametresOrdres(String key) {
        int datas = 0;
        Integer prefs = getSharedPreferences("params", Context.MODE_PRIVATE).getInt(key, datas);
        return prefs;


    }




    private void setMenu(){
        if(isSelected==true) {
            switch (lectureDesParametres("action_default")) {
                case "":
                    break;
                case "photo_gallerie":
                    mMenu.findItem(R.id.action_supprimer_photos).setVisible(false);
                    mMenu.findItem(R.id.action_supprimerbon).setVisible(false);
                    mMenu.findItem(R.id.action_gallery).setVisible(true);
                    mMenu.findItem(R.id.action_gallery_mult).setVisible(true);
                    mMenu.findItem(R.id.action_photo).setVisible(true);
                    break;
                case "photo":
                    mMenu.findItem(R.id.action_supprimer_photos).setVisible(false);
                    mMenu.findItem(R.id.action_supprimerbon).setVisible(false);
                    mMenu.findItem(R.id.action_gallery).setVisible(true);
                    mMenu.findItem(R.id.action_gallery_mult).setVisible(true);
                    mMenu.findItem(R.id.action_photo).setVisible(false);
                    break;
                case "gallerie":
                    mMenu.findItem(R.id.action_supprimer_photos).setVisible(false);
                    mMenu.findItem(R.id.action_supprimerbon).setVisible(false);
                    mMenu.findItem(R.id.action_gallery).setVisible(false);
                    mMenu.findItem(R.id.action_gallery_mult).setVisible(false);
                    mMenu.findItem(R.id.action_photo).setVisible(true);
                    break;
            }
        }
        else{
            mMenu.findItem(R.id.action_supprimer_photos).setVisible(false);
            mMenu.findItem(R.id.action_supprimerbon).setVisible(false);
            mMenu.findItem(R.id.action_gallery).setVisible(false);
            mMenu.findItem(R.id.action_gallery_mult).setVisible(false);
            mMenu.findItem(R.id.action_photo).setVisible(false);
        }
    }

    public void choixDuTheme()

    {

        if ( VariablesGlobales.networkInfos == false) {
            Toolbar actionBarToolbar = (Toolbar) findViewById(R.id.action_bar);
            if (actionBarToolbar != null)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    actionBarToolbar.setBackgroundColor(Color.parseColor("#d50000"));
                }
            setStatusBarColor("#9b0000");
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(Color.RED));

            addButton.getBackground().setColorFilter(Color.parseColor("#9b0000"), PorterDuff.Mode.MULTIPLY);
        }
        else{
            if (lectureDesParametres("action_theme").equals("color") || lectureDesParametres("action_theme").equals("")  ){
                Toolbar actionBarToolbar = (Toolbar)findViewById(R.id.action_bar);
                if (actionBarToolbar != null)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        actionBarToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    }
                setStatusBarColor("#005407");

                addButton.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
            }
            else {
                Toolbar actionBarToolbar = (Toolbar)findViewById(R.id.action_bar);
                if (actionBarToolbar != null)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        actionBarToolbar.setBackgroundColor(Color.parseColor("#616161"));
                    }
                setStatusBarColor("#373737");

                addButton.getBackground().setColorFilter(Color.parseColor("#616161"), PorterDuff.Mode.MULTIPLY);
            }

        }

    }

    public void sauvegardeDesParametres(String[] parametres) {
        boolean prefs = getSharedPreferences("params", Context.MODE_PRIVATE).edit().putString("qualite_photos", parametres[0]).commit();
        boolean prefs1 = getSharedPreferences("params", Context.MODE_PRIVATE).edit().putString("action_default", parametres[1]).commit();
        boolean prefs2 = getSharedPreferences("params", Context.MODE_PRIVATE).edit().putString("action_color", parametres[2]).commit();
        boolean prefs3 = getSharedPreferences("params", Context.MODE_PRIVATE).edit().putString("action_fichier", parametres[3]).commit();
        boolean prefs4 = getSharedPreferences("params", Context.MODE_PRIVATE).edit().putString("action_taille", parametres[4]).commit();
        boolean prefs5 = getSharedPreferences("params", Context.MODE_PRIVATE).edit().putString("action_theme", parametres[5]).commit();



    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /*public synchronized boolean  EmvoiImage(String orderId, String file, Order order, String id) throws IOException {
        original = null;
        resized = null;
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


            con.connect(ftpHostName);


            if (con.login(ftpUserName, ftpPassword)) {
                con.enterLocalPassiveMode(); // important!
                con.setFileType(FTP.BINARY_FILE_TYPE);
            }

            FTPFile[] files = new FTPFile[0];
        FTPFile[] filesExist = new FTPFile[0];
        Order order1 = new Order();
        requetesBaseDeDonneeInterne = new Requetes(AppContext.getAppContext());
        requetesBaseDeDonneeInterne.open();
        String[] file2 = requetesBaseDeDonneeInterne.selectImagesByName(file).get(0);
        file = file2[1];
        File f = new File(file);
        if(!f.exists()) {
            Log.i("Inrouvable","Fichier introuvable");
            try {
                requetesBaseDeDonneeInterne.effacerIamgeByName(file);
                Log.i("Inrouvable","Fichier effacer bd");
                String date_photo = datePhoto(file);
                String date = "date";
                String heure = "heure";
                String[] files1 = file.split("/");
                String newPath = files1[0] +"/"+ files1[1] +"/"+ files1[2] +"/"+ files1[3] +"/"+ files1[4] +"/"+ files1[5];
                String _ServeurPath = "";
                if (!date_photo.equals("")) {
                    date = date_photo.substring(0, 10).replace(":", "-");
                    heure = date_photo.substring(11, date_photo.length());
                    date_photo = date + "_" + heure;
                    if(!orderId.equals("")) {
                        renameFile(file, orderId + "_" + date_photo + "_" + VariablesGlobales._versionCode + ".jpeg");
                        _path = newPath + "/" + orderId + "_" + date_photo + "_" + VariablesGlobales._versionCode + ".jpeg";
                        _ServeurPath = orderId + "_" + date_photo + "_" + VariablesGlobales._versionCode + ".jpeg";
                    }else {
                        String orderId1 = file.split("/")[6];
                        orderId = orderId1.split("_")[0];
                        renameFile(file, orderId + "_" + date_photo + "_" + VariablesGlobales._versionCode + ".jpeg");
                        _path = newPath + "/" + orderId + "_" + date_photo + "_" + VariablesGlobales._versionCode + ".jpeg";
                        _ServeurPath = orderId + "_" + date_photo + "_" + VariablesGlobales._versionCode + ".jpeg";
                    }
                } else {
                    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HH:mm.SSS");//dd/MM/yyyy
                    Date now = new Date();
                    String strDate = sdfDate.format(now);
                    date = strDate.substring(0, 10).replace(":", "-");
                    heure = strDate.substring(11, strDate.length());
                    date_photo = date + "_" + heure;
                    if(!orderId.equals("")) {
                        renameFile(file, orderId + "_" + strDate + "_" + VariablesGlobales._versionCode + ".jpeg");
                        _path = newPath +"/"+ orderId + "_" + strDate + "_" + VariablesGlobales._versionCode + ".jpeg";
                        _ServeurPath = orderId + "_" + date_photo + "_" + VariablesGlobales._versionCode + ".jpeg";
                    }else {
                        String orderId1 = file.split("/")[6];
                        orderId = orderId1.split("_")[0];
                        renameFile(file, orderId + "_" + strDate + "_" + VariablesGlobales._versionCode + ".jpeg");
                        _path = newPath + "/" + orderId + "_" + strDate + "_" + VariablesGlobales._versionCode + ".jpeg";
                        _ServeurPath = orderId + "_" + date_photo + "_" + VariablesGlobales._versionCode + ".jpeg";
                    }

                }
                Log.i("Inrouvable","_ServeurPath" +_ServeurPath);
                con.rename("/../photo_trux_manager/" + _ServeurPath," /../photo_trux_manager/" + _ServeurPath.substring(0,_ServeurPath.length()-5)+"e.jpeg");

            } catch (Exception e) {
                Log.i("Inrouvable","Fichier introuvable bd" +e.getMessage());
            } finally {
                requetesBaseDeDonneeInterne.close();
            }
        }



        Log.i("FileLength",String.valueOf(f.getName()));
        if(f.length()>500000){

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
            String newPath = files1[0] +"/"+ files1[1] +"/"+ files1[2] +"/"+ files1[3] +"/"+ files1[4] +"/"+ files1[5];
            String _ServeurPath = "";
            if (!date_photo.equals("")) {
                date = date_photo.substring(0, 10).replace(":", "-");
                heure = date_photo.substring(11, date_photo.length());
                date_photo = date + "_" + heure;
                if(!orderId.equals("")) {
                    renameFile(file, orderId + "_" + date_photo + "_" + VariablesGlobales._versionCode + ".jpeg");
                    _path = newPath + "/" + orderId + "_" + date_photo + "_" + VariablesGlobales._versionCode + ".jpeg";
                    _ServeurPath = orderId + "_" + date_photo + "_" + VariablesGlobales._versionCode + ".jpeg";
                }else {
                   String orderId1 = file.split("/")[6];
                    orderId = orderId1.split("_")[0];
                    renameFile(file, orderId + "_" + date_photo + "_" + VariablesGlobales._versionCode + ".jpeg");
                    _path = newPath + "/" + orderId + "_" + date_photo + "_" + VariablesGlobales._versionCode + ".jpeg";
                    _ServeurPath = orderId + "_" + date_photo + "_" + VariablesGlobales._versionCode + ".jpeg";
                }
            } else {
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HH:mm.SSS");//dd/MM/yyyy
                Date now = new Date();
                String strDate = sdfDate.format(now);
                date = strDate.substring(0, 10).replace(":", "-");
                heure = strDate.substring(11, strDate.length());
                date_photo = date + "_" + heure;
                if(!orderId.equals("")) {
                renameFile(file, orderId + "_" + strDate + "_" + VariablesGlobales._versionCode + ".jpeg");
                _path = newPath +"/"+ orderId + "_" + strDate + "_" + VariablesGlobales._versionCode + ".jpeg";
                _ServeurPath = orderId + "_" + date_photo + "_" + VariablesGlobales._versionCode + ".jpeg";
                }else {
                    String orderId1 = file.split("/")[6];
                    orderId = orderId1.split("_")[0];
                    renameFile(file, orderId + "_" + strDate + "_" + VariablesGlobales._versionCode + ".jpeg");
                    _path = newPath + "/" + orderId + "_" + strDate + "_" + VariablesGlobales._versionCode + ".jpeg";
                    _ServeurPath = orderId + "_" + date_photo + "_" + VariablesGlobales._versionCode + ".jpeg";
                }
            }

            Log.i("FileName",file);
            Log.i("FIleName","_path" +_path);
            String tempFilePath = getTempFilePath(_path);
            File tempFile = new File(tempFilePath);
            filesExist = con.listFiles("/../photo_trux_manager/" + _ServeurPath.substring(0,_ServeurPath.length()-5) +"f.jpeg");

            for (FTPFile file1 : filesExist) {
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

            /*files = con.listFiles("/../photo_trux_manager/" + _ServeurPath);

            long fileSizeInBytes = 0;
            long fileSizeInKB = 0;


            for (FTPFile file1 : files) {

                // Get file from file name
                //File file2 = new File(file.getPath());
                inFTP = true;

                // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                fileSizeInKB =  file1.getSize();
                byte[] byteFile = readFile(tempFile);
                if(fileSizeInBytes==fileSizeInKB || fileSizeInKB > fileSizeInBytes ) {


                }

            }*/
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



            /*try {
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


            if(result == true){
                con.rename("/../photo_trux_manager/" + _ServeurPath," /../photo_trux_manager/" + _ServeurPath.substring(0,_ServeurPath.length()-5)+"f.jpeg");
                boolean deleted = tempFile.delete();
            }

            filesExist = con.listFiles("/../photo_trux_manager/" + _ServeurPath.substring(0,_ServeurPath.length()-5) +"f.jpeg");

            File target;
            File target1;
            String[] files2 = _path.split("/");
           /* target = new File(getImageFolder()+"/"+files2[1]);
            target1 = new File(getImageFolder()+"/"+files2[1].substring(0, (getImageFolder()+"/"+files2[1]).length() - 4) + "_preview.jpeg");
            if(filesExist.length!=0) {
                if (target1.exists() && target1.isFile() && target1.canWrite()) {
                    target1.delete();
                    Log.d("Files", "Delete" + target1.getName());
                }
            }*/

            /*if (lectureDesParametres1("action_fichier").equals("effacer") && filesExist.length!=0) {
                if (target.exists() && target.isFile() && target.canWrite()) {
                    target.delete();
                    Log.d("Files", "Delete" + target.getName());
                }
            }

            if (original != null && original.isRecycled() == false) {
                original.recycle();
            }
            if (resized != null && resized.isRecycled() == false) {
                resized.recycle();
            }


        }else{
            try {
                requetesBaseDeDonneeInterne  = new Requetes(AppContext.getAppContext());
                requetesBaseDeDonneeInterne.open();
                requetesBaseDeDonneeInterne.effacerIamgeById(id);
                File target;
                File target1;
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
        }



        if(result == true) {
            try {
                requetesBaseDeDonneeInterne  = new Requetes(AppContext.getAppContext());
                requetesBaseDeDonneeInterne.open();
                requetesBaseDeDonneeInterne.effacerIamgeById(id);

                File target;
                File target1;
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
        }


        Log.i("BaseDeDonnee", result + "_" + _path);



        } catch (Exception e) {
            Log.i("resultat", false + "_" + e.getMessage() + "_" + _path);
            //return false;
        } finally {
            if (con != null) {
                con.logout();
                con.disconnect();
            }
        }
        return result;
    }*/


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
                        ExifInterface.TAG_WHITE_BALANCE
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


}


class MyUndoListener implements View.OnClickListener{
    MainActivity activity;
    Order _order;
    public MyUndoListener(MainActivity mainActivity, Order ordre) {
        activity=mainActivity;
        _order=ordre;

    }

    @Override
    public void onClick(View v) {

        activity.cleanPhoto=false;
        activity.handlerCleanPhotos.removeCallbacks(activity.runnableCleanPhotos);
        activity.mySnackbar.setText("Suppression images annulé bon "+ _order.getOrderNumber());

        activity. mySnackbar.isShown();
        // Code to undo the user's last action
    }
}


class MyUndoListener1 implements View.OnClickListener {

    MainActivity activity;
    Order _order;

    public MyUndoListener1(MainActivity mainActivity, Order ordre) {
        activity = mainActivity;
        _order = ordre;
    }

    @Override
    public void onClick(View v) {
        activity.clearPhoto = false;
        activity.handlerClearPhotos.removeCallbacks(activity.runnableClearPhotos);
        activity.mySnackbar.setText("Suppression Annulée bon " + _order.getOrderNumber());
        activity.mySnackbar.show();
        // Code to undo the user's last action
    }
}

class MyUndoListener2 implements View.OnClickListener{

    MainActivity activity;

    public MyUndoListener2(MainActivity mainActivity) {
        activity=mainActivity;

    }

    @Override
    public void onClick(View v) {


        activity.mySnackbar1.dismiss();
        // Code to undo the user's last action
    }


}








