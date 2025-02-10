package ch.parolini.truxmanager.delivery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.net.ConnectivityManager;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.parolini.truxmanager.delivery.Manager.FileManager;
import ch.parolini.truxmanager.delivery.Manager.OrderManager;
import ch.parolini.truxmanager.delivery.Manager.RpcManager;
import ch.parolini.truxmanager.delivery.basededonnee.Requetes;
import ch.parolini.truxmanager.delivery.gui.ListAdapter;
import ch.parolini.truxmanager.delivery.model.ClientConfig;
import ch.parolini.truxmanager.delivery.model.Order;

import android.widget.Toast;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


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
    private static final int SYSTEM_ALERT_WINDOW = 300;
    private static final int FOREGROUND_SERVICE = 400;

    private static final int REQUEST_OVERLAY_PERMISSIONS = 400;
    private static final int PERMISSION_MANAGE_EXTERNAL_STORAGE = 122;
    private static final int REQUEST_IMAGE_CAPTURE = 200;
    private static final Bitmap original = null;
    private static final Bitmap resized = null;
    private static final int SELECT_VIDEO = 1;
    private static final int serverResponseCode = 0;
    private static final String _urlJsonParamerte = "https://www.luginbuhl.ch/X3GR5T/Hv8bcFH9.json";
    private static final String TAG = "VideoPickerActivity";
    private static final int SELECT_VIDEOS = 1;
    private static final int SELECT_VIDEOS_KITKAT = 10;
    public static boolean active;
    public static String serverRootContextURL;
    public static String serverUserName;
    public static boolean passwordOK = false;
    // Static to avoid loosing the ref when screen orientation changes
    static File currentPictureFile = null;
    private static String serverPassword;
    private static InputStream inputStream;
    private static MainActivity _activity;
    private static String _path;
    private static boolean _rename;
    private static boolean efface;
    private static boolean inFTP;
    private static String dateExif;
    private final boolean asPicture = false;
    private final String[] paramertes = new String[6];
    private final BroadcastReceiver mMessageReceiver = null;
    private final int nbTotalOrdre = 0;
    private final boolean _blockSynchro = false;
    private final String Tag = "TransfertPhp";
    private final ArrayList<String[]> _listImages = new ArrayList<>();
    private final boolean userPrenste = false;
    private final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE1 = 10;
    private final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE2 = 11;
    private final BroadcastReceiver mConfigReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ClientConfig clientConfig = (ClientConfig) intent.getSerializableExtra("message");
            //Log.d("receiver", "Got message: " + clientConfig);
            OrderManager.setMaxAgeMinutes(clientConfig.MAX_WEIGHING_AGE_BEFORE_UPLOAD_MINUTES);
        }
    };
    private final BroadcastReceiver mMessageToast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intentg
            String message = (String) intent.getSerializableExtra("message");
            //Log.d("receiver", "Got message: " + message);
            ////Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            showMessage(message);
        }
    };
    public boolean clearPhoto = false;
    public Snackbar mySnackbar;
    public Snackbar mySnackbar1;
    public boolean cleanPhoto = false;
    public Handler handlerClearPhotos;
    public Handler handlerCleanPhotos;
    public Runnable runnableClearPhotos;
    public Runnable runnableCleanPhotos;
    public TextView _textViewinfo;
    SharedPreferences preferences = null;
    MainActivity thisActivity;
    /**
     * Apres chaque envoi d'ordre vers le serveur
     */
    private final BroadcastReceiver mOrderSentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Order order = (Order) intent.getSerializableExtra("order");
            //Log.d("receiver", "Got message: " + order);
            updateOrderListView(); // utile
        }
    };
    BroadcastReceiver _receiverEtatDuTelephone = new BootLoadReceiver();
    int PICK_IMAGE_MULTIPLE = 1;
    int PICK_IMAGE_MULTIPLE1 = 2;
    String imageEncoded;
    List<String> imagesEncodedList;
    String imagePath;
    ProgressDialog dialog = null;
    String upLoadServerUri = null;
    Uri imageUri;
    ActivityResultLauncher<String> result = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    //DO whatever with received image content scheme Uri
                    createFile(result);
                }
            });
    String currentPhotoPath;
    private FloatingActionButton addButton;
    private FloatingActionButton addBon;
    private ListView list;
    private Bitmap mBitmapToSave;
    private int REQUEST_CODE_CREATOR;
    private boolean onclick = false;
    private Menu mMenu;
    private boolean mShowVisible = false;
    private boolean isSelected = false;
    private boolean snackBarInfo = true;
    private String selectedVideoPath;
    private Bitmap scaledBitmap;

    private File imageFile;
    private boolean powerPressed = false;

    //private HttpClient client;
    private List<String> selectedVideos;
    private boolean video = false;
    private Intent _intent = null;
    private boolean _repete = false;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia;
    private BroadcastReceiver broadcastReceiverEtatDuReseau;
    private ImageButton whatsapp;

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    //contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.medias.content".equals(uri.getAuthority());
    }

    public static void copyFile(String inputPath, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

            //Log.i("copy", "Copied file to " + outputPath);

        } catch (FileNotFoundException fnfe1) {
            //Log.i("copy", fnfe1.getMessage());
        } catch (Exception e) {
            //Log.i("copy", e.getMessage());
        }
    }

    public static File getImageFolder() {
        File imageFolder = null;
        if (imageFolder == null) {
            File folder = new File(AppContext.getAppContext().getFilesDir(), "/truxmanager");
            imageFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/");
            if (!imageFolder.exists()) {
                if (!imageFolder.mkdirs()) {
                    //Log.e("TRUX", "Directory (" + imageFolder + ") dosen't exist and could not be created");
                }
            }
        }
        return imageFolder;
    }

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
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        } else {
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
        boolean hasPermission8 = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission8) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
                    SYSTEM_ALERT_WINDOW);
        }

        boolean hasPermission9 = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission9) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.FOREGROUND_SERVICE},
                    FOREGROUND_SERVICE);
        }

        if (!Settings.canDrawOverlays(MainActivity.this)) {
            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            Uri uri = Uri.fromParts("package", getPackageName(), null);

            myIntent.setData(uri);
            startActivityForResult(myIntent, REQUEST_OVERLAY_PERMISSIONS);
            //return;
        }else{
            if(VariablesGlobales._isOnBoot == true){
                moveTaskToBack(true);
            }

        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        if (VariablesGlobales._isOnBoot && lectureDesParametresOrdres("nb_photos") == 0) {
            VariablesGlobales._isOnBoot = false;
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                finishAndRemoveTask();
            } else {
                finish();
            }
        } else {
            if (isSelected) {
                setMenu();
            }
        }
        new Context1(MainActivity.this);
        new AcitiviteContext(this);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            //throw new RuntimeException(e);
        }
        VariablesGlobales._versionName = pInfo.versionName;
        VariablesGlobales._versionCode = String.valueOf(pInfo.versionCode);

        thisActivity = MainActivity.this;
        OrderManager.readFromLocalStorage(this.getApplicationContext());
        RpcManager.serverRootContextURL = getResources().getString(R.string.SERVER_ROOT_URL);
        RpcManager.serverUserName = getResources().getString(R.string.SERVER_ROOT_USER);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Set the previous password
        RpcManager.setServerPassword(preferences.getString(PASSWORD_KEY_PREF, ""));

        // Will be overrriden by client settings
        OrderManager.setMaxAgeMinutes(getResources().getInteger(R.integer.MAX_WEIGHING_AGE_BEFORE_UPLOAD_MINUTES));


        setContentView(R.layout.activity_main);

        FrameLayout mRootView = findViewById(R.id.frame_content);

        mySnackbar = Snackbar.make(mRootView, R.id.mycoordinatorlayout, 10000);

        list = findViewById(R.id.list);
        list.setLongClickable(true);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list.setItemsCanFocus(false);

        TextView emptyText = findViewById(R.id.empty);
        list.setEmptyView(emptyText);
        _textViewinfo = findViewById(R.id.info);

        addButton = findViewById(R.id.addButton);


        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Toast.makeText(thisActivity, "Nouveau bon de commande. Positionnez le code bar dans le rectangle pour le scanner ", Toast.LENGTH_LONG).show();
                isSelected = true;
                try {
                    VariablesGlobales._thread.join();
                } catch (Exception e) {
                }
                VariablesGlobales._thread = null;
                try {
                    VariablesGlobales._task.cancel(true);
                }catch (Exception e){
                }
                VariablesGlobales._task = null;
                showBarCodeReader();
                //showGallery(OrderManager.currentOrder);
            }
        });

        addBon = findViewById(R.id.addBon);

        addBon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(thisActivity, "Nouveau bon de commande. Positionnez le code bar dans le rectangle pour le scanner ", Toast.LENGTH_LONG).show();
                isSelected = false;
                setMenu();
                FragmentBon fragment = new FragmentBon(MainActivity.this);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.frame_content, fragment).addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();

            }
        });

        addButton.setColorFilter(Color.WHITE);
        addBon.setColorFilter(Color.WHITE);



        //registerForContextMenu(list);

        VariablesGlobales.requeteBd = new Requetes(AppContext.getAppContext());
        VariablesGlobales.requeteBd.open();
        //startService(new Intent(getBaseContext(), SerciceRedemarrage.class));

        // Click event for single list row
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                isSelected = true;
                VariablesGlobales.message = "";
                setMenu();

                if (_intent != null) {
                    String myString = _intent.getStringExtra("message");
                    if (myString != null) {
                        String[] tab = myString.split(";");

                        VariablesGlobales.message = tab[1];

                        //Log.i("message", myString);
                        //Log.i("message", tab[0]);
                        //Log.i("message", tab[1]);

                    }
                }

                OrderManager.currentOrder = (Order) parent.getItemAtPosition(position);
                OrderManager.currentOrder = (Order) parent.getItemAtPosition(position);

                try {
                    VariablesGlobales._thread.join();
                } catch (Exception e) {
                }
                VariablesGlobales._thread = null;
                try {
                    VariablesGlobales._task.cancel(true);
                }catch (Exception e){
                }
                VariablesGlobales._task = null;
                ////setTitle();
                String message = VariablesGlobales.message.replace("_", "");
                if (message.equals("")) {
                    _textViewinfo.setText("N°: " + OrderManager.currentOrder.getOrderNumber());
                } else {
                    _textViewinfo.setText("N°: " + OrderManager.currentOrder.getOrderNumber() + " - " + message);
                }
                mySnackbar = Snackbar.make(view, R.id.mycoordinatorlayout, 10000);
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Back is pressed... Finishing the activity
                _repete = false;
                powerPressed = false;
                updateOrderListView();
                if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    setTitle(Html.fromHtml("TM"));
                    getSupportFragmentManager().popBackStack();
                } else {
                    if (!mMenu.findItem(R.id.action_supprimerbon).isVisible()) {
                        moveTaskToBack(true);
                    }
                    if (!mMenu.findItem(R.id.menu_reload).isVisible()) {
                        moveTaskToBack(true);
                    }
                }
                if (mMenu.findItem(R.id.action_supprimerbon).isVisible()) {
                    mMenu.findItem(R.id.action_supprimerbon).setVisible(false);
                    mMenu.findItem(R.id.action_supprimer_photos).setVisible(false);
                }
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int stringId = 0;
                mySnackbar = Snackbar.make(view, R.id.mycoordinatorlayout, 10000);
                OrderManager.currentOrder = (Order) parent.getItemAtPosition(position);
                OrderManager.currentOrder.hasPictureFiles();
                OrderManager.currentOrder = (Order) parent.getItemAtPosition(position);
                onclick = false;
                mMenu.findItem(R.id.action_supprimer_photos).setVisible(true);
                mMenu.findItem(R.id.action_supprimerbon).setVisible(true);
                mMenu.findItem(R.id.action_gallery).setVisible(false);
                mMenu.findItem(R.id.action_video_mult).setVisible(false);
                mMenu.findItem(R.id.action_video_simple).setVisible(false);
                mMenu.findItem(R.id.action_photo).setVisible(false);
                //mMenu.findItem(R.id.whatsapp).setVisible(false);
                //setTitle(Html.fromHtml("N°: " + OrderManager.currentOrder.getOrderNumber().toString(); + " - " + VariablesGlobales.message));
                return true;
            }
        });

        setTitle(Html.fromHtml("TM"));
        // We start the backgroud service



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
        this.registerReceiver(broadcastReceiverEtatDuReseau, filter1);


        LocalBroadcastManager.getInstance(this).registerReceiver(mOrderSentReceiver,
                new IntentFilter("onOrderSent"));

        _intent = getIntent();
        String myString = getIntent().getStringExtra("message");
        if (myString != null) {
            String[] tab = myString.split(";");

            VariablesGlobales.message = tab[1];
            //Log.i("message", myString);
            //Log.i("message", tab[0]);
            //Log.i("message", tab[1]);
            boolean orderExiste = false;
            for (Order order : OrderManager.getOrders()) {
                //Log.i("message", String.valueOf(OrderManager.getOrders()));
                if (tab[0].equals(order.getOrderNumber())) {
                    orderExiste = true;
                    //Log.i("message", "orderExiste" + tab[0]);
                    OrderManager.currentOrder = order;
                    break;
                }
            }
            if (!orderExiste) {
                OrderManager.currentOrder = OrderManager.createAndSetCurrent(tab[0]);
            }

            if (!video) {
                try {
                    VariablesGlobales.syncNoUpdate = true;
                    try {
                        showCamera2(OrderManager.currentOrder, ".jpg");
                    } catch (ParseException e) {
                        //e.printStackTrace();
                    }
                } catch (RuntimeException e) {

                }
            } else {
                try {
                    VariablesGlobales.syncNoUpdate = true;
                    try {
                        showCamera3(OrderManager.currentOrder, ".mp4");
                    } catch (ParseException e) {
                        //e.printStackTrace();
                    }
                } catch (RuntimeException e) {

                }

            }

        } else {
            //Log.i("message", "null");
        }

        pickMultipleMedia = registerForActivityResult(
                new ActivityResultContracts.PickMultipleVisualMedia(100), gfgPhotoPicker -> {
                    if (!gfgPhotoPicker.isEmpty()) {
                        //Log.d("PhotoPicker",
                                //"Number of items selected: "
                                        //+ gfgPhotoPicker.size());
                    } else {
                        //Log.d("Opened Picker",
                                //"Choose something Geek");
                    }
                });
        updateOrderListView();

    }

    public void updateOrderListView() {
                ListView list = findViewById(R.id.list);
                if (list.getAdapter() == null) {
                    list.setAdapter(new ListAdapter(thisActivity, OrderManager.getOrders()));
                } else {
                    ((ListAdapter) list.getAdapter()).refill(OrderManager.getOrders());
                }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.list) {
            if (onclick) {
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
                ////Toast.makeText(thisActivity, "Toutes les medias du bon: " + ordre.getOrderNumber() + " effacées.", Toast.LENGTH_LONG).show();


                // add stuff here
                return true;
            case R.id.supprimer_ordre:
                mShowVisible = false; // or false

                invalidateOptionsMenu();
                OrderManager.removeCurrentOrder();
                ////Toast.makeText(thisActivity, "Bon de commande " + ordre.getOrderNumber() + " supprimé", Toast.LENGTH_LONG).show();


                // edit stuff here
                return true;

            case R.id.action_gallery:
                video = false;
                showGallery(OrderManager.currentOrder);

                // add stuff here
                return true;

            case R.id.action_video_mult:
                video = true;
                showGallery1(OrderManager.currentOrder);

                // add stuff here
                return true;
            case R.id.action_image:
                video = false;
                VariablesGlobales.syncNoUpdate = true;
                try {
                    showCamera2(OrderManager.currentOrder, ".jpg");
                } catch (ParseException e) {
                    //e.printStackTrace();
                }

                // edit stuff here
                return true;

            case R.id.action_video_simple:
                video = true;
                VariablesGlobales.syncNoUpdate = true;
                try {
                    showCamera3(OrderManager.currentOrder, ".mp4");
                } catch (ParseException e) {
                    //e.printStackTrace();
                }

                // edit stuff here
                return true;

            /*case R.id.whatsapp:
                // Choose a directory using the system's file picker.
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                // Optionally, specify a URI for the directory that should be opened in
                // the system file picker when it loads.

                Uri wa_status_uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses");
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, wa_status_uri);
                startActivityForResult(intent, 10001);
                return true;*/

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

        super.onResume();
        //Log.i("TRUX", "Resume called");
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSelected) {
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
        if (isSelected) {
            setMenu();
        }
    }

    @Override
    public void onBackPressed() {
        _repete = false;
        powerPressed = false;
        setTitle(Html.fromHtml("TM"));
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
            return;
        }

        FragmentManager manager = getFragmentManager();
        int count = manager.getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        } else {
            manager.popBackStack();
        }

        if (!mMenu.findItem(R.id.action_supprimerbon).isVisible()) {
            super.onBackPressed();
        }

        if (mMenu.findItem(R.id.action_supprimerbon).isVisible()) {
            mMenu.findItem(R.id.action_supprimerbon).setVisible(false);
            mMenu.findItem(R.id.action_supprimer_photos).setVisible(false);
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    void updateBackInvokedCallbackState() {
        _repete = false;
        powerPressed = false;

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
            return;
        }

        if (!mMenu.findItem(R.id.action_supprimerbon).isVisible()) {
            super.onBackPressed();
        }

        if (mMenu.findItem(R.id.action_supprimerbon).isVisible()) {
            mMenu.findItem(R.id.action_supprimerbon).setVisible(false);
            mMenu.findItem(R.id.action_supprimer_photos).setVisible(false);
        }
    }

    @NonNull
    @Override
    public OnBackInvokedDispatcher getOnBackInvokedDispatcher() {


        return super.getOnBackInvokedDispatcher();
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    public void setOnBackInvokedDispatcher(@NonNull OnBackInvokedDispatcher invoker) {
        _repete = false;
        powerPressed = false;

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
            return;
        }

        if (!mMenu.findItem(R.id.action_supprimerbon).isVisible()) {
            super.onBackPressed();
        }

        if (mMenu.findItem(R.id.action_supprimerbon).isVisible()) {
            mMenu.findItem(R.id.action_supprimerbon).setVisible(false);
            mMenu.findItem(R.id.action_supprimer_photos).setVisible(false);
        }
        updateBackInvokedCallbackState();
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
        TextView textView = findViewById(R.id.messageText);
        textView.setText(message);
    }

    public void showCamera2(Order order, String extension) throws ParseException {
        _repete = true;
        video = false;
        dispatchTakePictureIntent(order, extension);
        //startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE1);
    }

    private void showCamera3(Order order, String extension) throws ParseException {
        _repete = true;
        video = true;
        dispatchTakePictureIntent1(order, extension);
        //startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE1);
    }

    /**
     * @param order
     */
    private void showGallery(Order order) {
        // Create intent to Open Image applications like Gallery, Google Photos
        if (Build.VERSION.SDK_INT < 19) {

            // start the image capture Intent
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Prise images"), SELECT_VIDEO);
        } else {
            pickMultipleMedia.launch(
                    new PickVisualMediaRequest.Builder()
                            .setMediaType(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
        }
    }

    private void showGallery1(Order order) {

        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Prise videos"), SELECT_VIDEOS);
        } else {
            // Launch the photo picker and let the user choose only videos.
            pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE)
                    .build());
        }


    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 0){
            AcitiviteContext.getContext().runOnUiThread(new Runnable() {
                public void run() {
                    AcitiviteContext.getContext().updateOrderListView();
                }
            });
        }
        switch (requestCode) {
            case 1:
                VariablesGlobales._noOrder = OrderManager.currentOrder.getOrderNumber();
                try {
                    selectedVideos = getSelectedVideos(requestCode, data);
                } catch (NullPointerException e) {
                    ////Log.i("message",e.getMessage());

                }
                OrderManager.persistOrdersToLocalStorage(getBaseContext());
                break;
            case 10:
                AsyncTask.execute(new Runnable() {
                                          @Override
                                          public void run() {
                                              if (RESULT_OK != 0) {
                                                  VariablesGlobales._noOrder = OrderManager.currentOrder.getOrderNumber();
                                                  File imageCopy = null;
                                                  try {
                                                      imageCopy = FileManager.copyImageFromGallery(currentPictureFile, OrderManager.currentOrder.getOrderNumber(), currentPictureFile.getPath().substring(currentPictureFile.getPath().length() - 4));
                                                  } catch (IOException | ParseException e) {
                                                      //e.printStackTrace();
                                                  }

                                                  Path path = null;
                                                  if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                      path = Paths.get(imageCopy.getPath());
                                                      try {
                                                          long size = Files.size(path);
                                                          if (size == 0) {
                                                              imageCopy.delete();
                                                          } else {
                                                              OrderManager.currentOrder.addNewPicture(imageCopy);
                                                              //OrderManager.currentOrder.addNewPicture(currentPictureFile);
                                                              VariablesGlobales._noOrder = OrderManager.currentOrder.getOrderNumber();
                                                              VariablesGlobales.requeteBd.ajouterImage(currentPictureFile.getPath(), VariablesGlobales._noOrder);
                                                              currentPictureFile = null;
                                                              OrderManager.persistOrdersToLocalStorage(getBaseContext());
                                                              try {
                                                                  VariablesGlobales.syncNoUpdate = true;
                                                                  try {
                                                                      //if (resultCode != 0) {
                                                                      showCamera2(OrderManager.currentOrder, ".jpg");
                                                                      //}
                                                                      //showCamera1(OrderManager.currentOrder, ".jpg");
                                                                  } catch (ParseException e) {
                                                                      //e.printStackTrace();
                                                                  }
                                                              } catch (RuntimeException e) {

                                                              }

                                                          }
                                                      } catch (IOException e) {
                                                          ////e.printStackTrace();
                                                      }
                                                  } else {

                                                      long size = imageCopy.length();

                                                      if (size == 0) {
                                                          imageCopy.delete();
                                                      } else {
                                                          OrderManager.currentOrder.addNewPicture(imageCopy);
                                                          //OrderManager.currentOrder.addNewPicture(currentPictureFile);
                                                          VariablesGlobales._noOrder = OrderManager.currentOrder.getOrderNumber();
                                                          VariablesGlobales.requeteBd.ajouterImage(currentPictureFile.getPath(), VariablesGlobales._noOrder);
                                                          currentPictureFile = null;
                                                          OrderManager.persistOrdersToLocalStorage(getBaseContext());
                                                          try {
                                                              VariablesGlobales.syncNoUpdate = true;
                                                              try {
                                                                  //if (resultCode != 0) {
                                                                  showCamera2(OrderManager.currentOrder, ".jpg");
                                                                  //}
                                                                  //showCamera1(OrderManager.currentOrder, ".jpg");
                                                              } catch (ParseException e) {
                                                                  //e.printStackTrace();
                                                              }
                                                          } catch (RuntimeException e) {

                                                          }

                                                      }


                                                  }

                                              }

                                          }
                                      });
                break;

            case 2000:
                        if (RESULT_OK != 0) {
                            VariablesGlobales._noOrder = OrderManager.currentOrder.getOrderNumber();
                            File imageCopy = null;
                            try {
                                imageCopy = FileManager.copyImageFromGallery(currentPictureFile, OrderManager.currentOrder.getOrderNumber(), currentPictureFile.getPath().substring(currentPictureFile.getPath().length() - 4));
                            } catch (IOException | ParseException e) {
                                //e.printStackTrace();
                            }

                            Path path = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                path = Paths.get(imageCopy.getPath());
                                try {
                                    long size = Files.size(path);
                                    if (size == 0) {
                                        imageCopy.delete();
                                    } else {
                                        OrderManager.currentOrder.addNewPicture(imageCopy);
                                        VariablesGlobales._noOrder = OrderManager.currentOrder.getOrderNumber();
                                        VariablesGlobales.requeteBd.ajouterImage(currentPictureFile.getPath(), VariablesGlobales._noOrder);
                                        OrderManager.persistOrdersToLocalStorage(getBaseContext());
                                        try {
                                            VariablesGlobales.syncNoUpdate = true;
                                            try {
                                                //if (resultCode != 0) {
                                                showCamera3(OrderManager.currentOrder, ".mp4");
                                                //}
                                                //showCamera1(OrderManager.currentOrder, ".jpg");
                                            } catch (ParseException e) {
                                                //e.printStackTrace();
                                            }
                                        } catch (RuntimeException e) {

                                        }

                                    }
                                } catch (IOException e) {
                                    ////e.printStackTrace();
                                }
                            } else {
                                long size = imageCopy.length();

                                if (size == 0) {
                                    imageCopy.delete();
                                } else {
                                    OrderManager.currentOrder.addNewPicture(imageCopy);
                                    //OrderManager.currentOrder.addNewPicture(currentPictureFile);
                                    VariablesGlobales._noOrder = OrderManager.currentOrder.getOrderNumber();


                                    VariablesGlobales.requeteBd.ajouterImage(currentPictureFile.getPath(), VariablesGlobales._noOrder);
                                    currentPictureFile = null;
                                    OrderManager.persistOrdersToLocalStorage(getBaseContext());
                                    try {
                                        VariablesGlobales.syncNoUpdate = true;
                                        try {
                                            //if (resultCode != 0) {
                                            showCamera3(OrderManager.currentOrder, ".jpg");
                                            //}
                                            //showCamera1(OrderManager.currentOrder, ".jpg");
                                        } catch (ParseException e) {
                                            //e.printStackTrace();
                                        }
                                    } catch (RuntimeException e) {

                                    }

                                }


                            }

                        }
                break;
            default:

                        if(OrderManager.currentOrder !=null) {
                            VariablesGlobales._noOrder = OrderManager.currentOrder.getOrderNumber();
                            try {
                                selectedVideos = getSelectedVideos(requestCode, data);
                            } catch (NullPointerException e) {
                                ////Log.i("message",e.getMessage());
                            }
                            OrderManager.persistOrdersToLocalStorage(getBaseContext());

                        }

        }

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            String scanCode = scanningResult.getContents();
            if (scanCode != null) {
                //Toast.makeText(this, "Veuillez rajouter des images pour le bon de commande No  " + scanCode, Toast.LENGTH_SHORT).show();
                boolean orderExiste = false;
                for (Order order : OrderManager.getOrders()) {
                    //Log.i("message", String.valueOf(OrderManager.getOrders()));
                    if (scanCode.substring(0, scanCode.length() - 1).equals(order.getOrderNumber())) {
                        orderExiste = true;
                        //Log.i("message", "orderExiste" + scanCode.substring(0, scanCode.length() - 1));
                        OrderManager.currentOrder = order;
                        break;
                    }
                }
                if (!orderExiste) {
                     OrderManager.createAndSetCurrent(scanCode.substring(0, scanCode.length() - 1));
                }
                if (!video) {
                    try {
                        VariablesGlobales.syncNoUpdate = true;
                        try {
                            if (resultCode != 0) {
                                showCamera2(OrderManager.currentOrder, ".jpg");
                            }
                        } catch (ParseException e) {
                            //e.printStackTrace();
                        }
                    } catch (RuntimeException e) {

                    }
                } else {
                    try {
                        VariablesGlobales.syncNoUpdate = true;
                        try {
                            if (resultCode != 0) {
                                showCamera3(OrderManager.currentOrder, ".mp4");
                            }
                        } catch (ParseException e) {
                            //e.printStackTrace();
                        }
                    } catch (RuntimeException e) {

                    }

                }

            } else {
                //Toast.makeText(this, "Scan annulé.", Toast.LENGTH_SHORT).show();

                //updateOrderListView();
            }

        } else {
            try {
                VariablesGlobales._thread.join();
            } catch (Exception e) {
            }
            VariablesGlobales._thread = null;
            try {
                VariablesGlobales._task.cancel(true);
            }catch (Exception e){
            }
            VariablesGlobales._task = null;
            updateOrderListView();
        }


    }

    private void createFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/jpg");
        intent.putExtra(Intent.EXTRA_TITLE, "Choix des photos");

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, SELECT_VIDEOS);
    }

    private List<String> getSelectedVideos(int requestCode, Intent data) {
        List<String> result = new ArrayList<>();
        AsyncTask.execute(new Runnable() {
                              @Override
                              public void run() {
                                  try {
                                      ClipData clipData = data.getClipData();
                                      if (clipData != null) {
                                          for (int i = 0; i < clipData.getItemCount(); i++) {
                                              ClipData.Item videoItem = clipData.getItemAt(i);
                                              Uri videoURI = videoItem.getUri();
                                              String filePath = getPath(AcitiviteContext.getContext(), videoURI);
                                              result.add(filePath);
                                              File imageCopy = new File(filePath);
                                              OrderManager.currentOrder.addNewPicture(imageCopy);
                                              VariablesGlobales.requeteBd.ajouterImage(imageCopy.getPath(), VariablesGlobales._noOrder);
                                          }
                                      } else {
                                          Uri videoURI = data.getData();
                                          String filePath = getPath(AcitiviteContext.getContext(), videoURI);
                                          result.add(filePath);
                                          File imageCopy = new File(filePath);
                                          OrderManager.currentOrder.addNewPicture(imageCopy);
                                          OrderManager.persistOrdersToLocalStorage(getBaseContext());
                                          VariablesGlobales.requeteBd.ajouterImage(imageCopy.getPath(), VariablesGlobales._noOrder);
                                          //Log.i("mImageUri", imageCopy.getPath());
                                      }
                                  }catch (Exception e){}
                                  AcitiviteContext.getContext().runOnUiThread(new Runnable() {
                                      public void run() {
                                          AcitiviteContext.getContext().updateOrderListView();
                                      }
                                  });
                              }
                          });


        return  result;
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
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
        if (isSelected) {
            setMenu();
        }
        snackBarInfo = false;

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyPressed = event.getKeyCode();
        if (keyPressed == KeyEvent.KEYCODE_POWER) {
            //Log.d("###", "Power button long click");
            Toast.makeText(MainActivity.this, "Clicked: " + keyPressed, Toast.LENGTH_SHORT).show();
            return true;
        } else
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
            mySnackbar.setText("Suppression definitive bon " + ordre.getOrderNumber());
            mySnackbar.setAction(R.string.annuler, new MyUndoListener1(MainActivity.this, ordre));
            mySnackbar.show();
            invalidateOptionsMenu();
            clearPhoto = true;
            handlerClearPhotos = new Handler();
            handlerClearPhotos.postDelayed(runnableClearPhotos = new Runnable() {
                @Override
                public void run() {
                    if (clearPhoto) {
                        VariablesGlobales.requeteBd.effacerImages(ordre.getOrderNumber());
                        OrderManager.removeCurrentOrder();
                        mySnackbar.dismiss();
                        updateOrderListView();
                    }
                }
            }, 10000);

            return true;
        }

        if (id == R.id.action_supprimer_photos) {
            mShowVisible = false; // or false
            mySnackbar.setText("Suppression medias bon " + ordre.getOrderNumber());
            mySnackbar.setAction(R.string.annuler, new MyUndoListener(MainActivity.this, ordre));
            mySnackbar.show();
            mMenu.findItem(R.id.action_gallery).setVisible(true);
            mMenu.findItem(R.id.action_video_mult).setVisible(true);
            mMenu.findItem(R.id.action_video_simple).setVisible(true);
            mMenu.findItem(R.id.action_photo).setVisible(true);
            invalidateOptionsMenu();
            cleanPhoto = true;
            handlerCleanPhotos = new Handler();
            handlerCleanPhotos.postDelayed(runnableCleanPhotos = new Runnable() {
                @Override
                public void run() {
                    if (cleanPhoto) {
                        ArrayList<String[]> lstimages = new ArrayList<>();
                        lstimages = (ArrayList<String[]>) VariablesGlobales.requeteBd.selectImagesNumero(ordre.getOrderNumber());
                        for (String[] picture : lstimages) {
                                VariablesGlobales.requeteBd.effacerIamgeById(picture[0]);
                        }
                        ordre.cleanImageFilesOnPhone(ordre);
                        updateOrderListView();
                        ////Toast.makeText(thisActivity, "Toutes les medias du bon: " + ordre.getOrderNumber() + " effacées.", Toast.LENGTH_LONG).show();
                    }
                }
            }, 10000);

            return true;
        }

        if (id == R.id.action_gallery) {
            showGallery(OrderManager.currentOrder);
            return true;
        }

        if (id == R.id.action_supprimer_photos_all) {
            mShowVisible = false; // or false
            mySnackbar.setText("Suppression medias et bon.");
            mySnackbar.setAction(R.string.annuler, new MyUndoListener3(MainActivity.this, ordre));
            mySnackbar.show();
            mMenu.findItem(R.id.action_gallery).setVisible(true);
            mMenu.findItem(R.id.action_video_mult).setVisible(true);
            mMenu.findItem(R.id.action_video_simple).setVisible(true);
            mMenu.findItem(R.id.action_photo).setVisible(true);
            invalidateOptionsMenu();
            cleanPhoto = true;
            handlerCleanPhotos = new Handler();
            handlerCleanPhotos.postDelayed(runnableCleanPhotos = new Runnable() {
                @Override
                public void run() {
                    if (cleanPhoto) {


                        ArrayList<String[]> lstimages = new ArrayList<>();
                        VariablesGlobales.requeteBd.effacerImages1();
                        OrderManager.removeOrders();
                        updateOrderListView();
                        ////Toast.makeText(thisActivity, "Toutes les medias du bon: " + ordre.getOrderNumber() + " effacées.", Toast.LENGTH_LONG).show();
                    }
                }
            }, 10000);

            return true;
        }

        if (id == R.id.action_video_simple) {
            if (OrderManager.currentOrder != null) {
                try {
                    showCamera3(OrderManager.currentOrder, ".mp4");
                } catch (ParseException e) {
                }
            }
            return true;
        }

        if (id == R.id.action_video_mult) {
            showGallery1(OrderManager.currentOrder);
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
            if (OrderManager.currentOrder != null) {
                try {
                    VariablesGlobales.syncNoUpdate = true;
                    try {
                        showCamera2(OrderManager.currentOrder, ".jpg");
                    } catch (ParseException e) {
                        //e.printStackTrace();
                    }
                } catch (RuntimeException e) {

                }
            } else {
                View view1 = getWindow().getDecorView().getRootView();
                mySnackbar1 = Snackbar.make(view1, R.id.mycoordinatorlayout, 10000);
                mySnackbar1.setText("Aucun ticket séléctionné.");
                mySnackbar1.setAction(R.string.ok, new MyUndoListener2(MainActivity.this));
                mySnackbar1.setActionTextColor(Color.parseColor("#ffffff"));
                mySnackbar1.show();
            }
            return true;
        }

        if (id == R.id.action_parametres) {
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;
                _textViewinfo.setText("Vesion: " + pInfo.versionName + " - Code: " + pInfo.versionCode);

            } catch (PackageManager.NameNotFoundException e) {
            }
            return true;
        }
        /*if (id == R.id.whatsapp) {
            // Choose a directory using the system's file picker.
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker when it loads.

            Uri wa_status_uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2FWhatsApp%20Images");
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, wa_status_uri);
            startActivityForResult(intent, SELECT_VIDEOS);
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
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //openCamera();
                } else {
                }
            }
        }

    }

    public void ContoleEtatDuReseau() {
        if (new Internet(MainActivity.this).checkWifiConnect()) {
            //Log.i("infoReseau", "Internet disponible");
            VariablesGlobales.networkInfos = true;
            AcitiviteContext.getContext().updateOrderListView();
        } else {
            try {
                VariablesGlobales._thread.join();
            } catch (Exception e) {
            }
            VariablesGlobales._thread = null;
            try {
                VariablesGlobales._task.cancel(true);
            }catch (Exception e){
            }
            VariablesGlobales._task = null;
            VariablesGlobales.networkInfos = false;
            VariablesGlobales.syncNoUpdate = true;
        }

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

    private void setMenu() {
        if (isSelected) {
                    mMenu.findItem(R.id.action_supprimer_photos).setVisible(false);
                    mMenu.findItem(R.id.action_supprimerbon).setVisible(false);
                    mMenu.findItem(R.id.action_gallery).setVisible(true);
                    mMenu.findItem(R.id.action_video_mult).setVisible(true);
                    mMenu.findItem(R.id.action_video_simple).setVisible(true);
                    mMenu.findItem(R.id.action_photo).setVisible(true);
                    mMenu.findItem(R.id.action_parametres).setVisible(true);
                    mMenu.findItem(R.id.action_supprimer_photos_all).setVisible(true);
        } else {
            mMenu.findItem(R.id.action_supprimer_photos).setVisible(false);
            mMenu.findItem(R.id.action_supprimerbon).setVisible(false);
            mMenu.findItem(R.id.action_gallery).setVisible(false);
            mMenu.findItem(R.id.action_video_mult).setVisible(false);
            mMenu.findItem(R.id.action_video_simple).setVisible(false);
            mMenu.findItem(R.id.action_photo).setVisible(false);
            //mMenu.findItem(R.id.action_parametres).setVisible(false);
            //mMenu.findItem(R.id.action_supprimer_photos_all).setVisible(false);

        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                //Log.i(TAG, serviceClass + " is Running");
                return true;
            }
        }
        //Log.i(TAG, serviceClass + " Not Running");
        return false;
    }

    private File createImageFile(String extention) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = extention + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                extention,         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent(Order order, String extension) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE).addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION |
                        Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
        );

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(extension);
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ch.parolini.truxmanager.delivery.fileprovider",
                        photoFile);
                String path = photoURI.toString();// "file:///mnt/sdcard/FileName.mp3"

                //File file = new File(new URI(path));
                currentPictureFile = photoFile;
                //_filePath = file.getPath();

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE1);
            }
        }
    }

    private void dispatchTakePictureIntent1(Order order, String extension) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE).addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION |
                        Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
        );
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(extension);
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ch.parolini.truxmanager.delivery.fileprovider",
                        photoFile);
                String path = photoURI.toString();// "file:///mnt/sdcard/FileName.mp3"

                //File file = new File(new URI(path));
                currentPictureFile = photoFile;
                //_filePath = file.getPath();

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE1);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_POWER: {
                //Toast.makeText(getBaseContext(), "Power button pressed", Toast.LENGTH_LONG).show();
                //Log.i("forground", ".S. Power button pressed");
                return true;
            }
            case KeyEvent.KEYCODE_MENU:
                //Toast.makeText(getBaseContext(), "Menu Button Pressed", Toast.LENGTH_LONG).show();
                //Log.i("forground", ".S. Menu Button Pressed");
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        //Log.i("forground", ".S. onKeyLongPress PRESSED");
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            // Do something here...
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }


}


class MyUndoListener implements View.OnClickListener {
    MainActivity activity;
    Order _order;

    public MyUndoListener(MainActivity mainActivity, Order ordre) {
        activity = mainActivity;
        _order = ordre;

    }

    @Override
    public void onClick(View v) {

        activity.cleanPhoto = false;
        activity.handlerCleanPhotos.removeCallbacks(activity.runnableCleanPhotos);
        activity.mySnackbar.setText("Suppression images annulé bon " + _order.getOrderNumber() + ".");

        activity.mySnackbar.isShown();
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
        activity.mySnackbar.setText("Suppression Annulée bon " + _order.getOrderNumber() + ".");
        activity.mySnackbar.show();
        // Code to undo the user's last action
    }
}

class MyUndoListener2 implements View.OnClickListener {

    MainActivity activity;

    public MyUndoListener2(MainActivity mainActivity) {
        activity = mainActivity;

    }


    @Override
    public void onClick(View v) {


        activity.mySnackbar1.dismiss();
        // Code to undo the user's last action
    }


}

class MyUndoListener3 implements View.OnClickListener {
    MainActivity activity;
    Order _order;

    public MyUndoListener3(MainActivity mainActivity, Order ordre) {
        activity = mainActivity;
        _order = ordre;

    }

    @Override
    public void onClick(View v) {

        activity.cleanPhoto = false;
        activity.handlerCleanPhotos.removeCallbacks(activity.runnableCleanPhotos);
        activity.mySnackbar.setText("Suppression images annulé bon.");

        activity.mySnackbar.isShown();
        // Code to undo the user's last action
    }
}








