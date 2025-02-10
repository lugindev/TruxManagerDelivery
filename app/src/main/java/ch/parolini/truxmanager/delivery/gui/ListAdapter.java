package ch.parolini.truxmanager.delivery.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.media.ExifInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ch.parolini.truxmanager.delivery.AcitiviteContext;
import ch.parolini.truxmanager.delivery.Envoi;
import ch.parolini.truxmanager.delivery.MainActivity;
import ch.parolini.truxmanager.delivery.R;
import ch.parolini.truxmanager.delivery.VariablesGlobales;
import ch.parolini.truxmanager.delivery.model.Order;


public class ListAdapter extends BaseAdapter {
    private final MainActivity activity;
    private static List<Order> dataList;
    private static LayoutInflater inflater=null;
    private View view;
    private    Order order = null;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Bitmap bmp=null;
    private final Integer counter = 0;
    private final boolean lsiteActualisaton=true;
    private final boolean EnSychronisation =false;
    public  int nbOrder = 0;
    private int nbrImage=0;
    private final int nbOrders = 0;
    private final java.lang.Object LOCK = "";
    private Handler mViewDidLoadHanlder;


    //public ImageLoader imageLoader;

    public ListAdapter(MainActivity activity, List<Order> dataList) {
        this.activity = activity;
        ListAdapter.dataList = dataList;
        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void refill(List<Order> newDataList) {
        dataList = newDataList ;
        notifyDataSetChanged();
    }

    public int getCount() {
        return dataList.size();
    }

    public Object getItem(int position) {
        return dataList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        view = convertView;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        AcitiviteContext.getContext().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        if (convertView == null)
            view = inflater.inflate(R.layout.list_row2, null);

        TextView title = view.findViewById(R.id.title);
        TextView detail = view.findViewById(R.id.detail);
        TextView photoNumber = view.findViewById(R.id.photoNumber);
        final TextView status = view.findViewById(R.id.status);
        final TextView thumbImageView = view.findViewById(R.id.list_image); // thumb image
        //ImageView synchroImageView=(ImageView)view.findViewById(R.id.synchoImage); // thumb image
        RelativeLayout relativeLayout = view.findViewById(R.id.rowLayout);
        try {
            order = dataList.get(position);
            List<String[]> lstimages = VariablesGlobales.requeteBd.selectImagesNumero(order.getOrderNumber());
            title.setText(order.getOrderNumber());
            detail.setText(new SimpleDateFormat("dd MMMM 'à' HH:mm", new Locale("FR", "CH")).format(order.getScanDate()));
            if (lstimages.size() != 0) {
                status.setText("ENVOI IMAGE(S)");
                status.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
                if (lstimages.size() >= 10) {
                    thumbImageView.setPadding(0, 0, 0, 0);
                } else {
                    thumbImageView.setTextSize(height / 20);
                    thumbImageView.setPadding(0, 0, 0, 0);
                }
                thumbImageView.setText(String.valueOf(lstimages.size()));
                thumbImageView.setBackgroundResource(R.drawable.img_bon1);
                //activity.updateOrderListView();
            } else {
                status.setText("PAS D'IMAGE(S)");
                status.setTextColor(Color.GRAY);
                if (lstimages.size() >= 10) {
                    thumbImageView.setPadding(0, 0, 0, 0);
                } else {
                    thumbImageView.setTextSize(height / 20);
                    thumbImageView.setPadding(0, 0, 0, 0);
                }
                thumbImageView.setText(String.valueOf(lstimages.size()));
                thumbImageView.setBackgroundResource(R.drawable.img_bon1);
            }
            if (position == dataList.size() - 1) {
                if (VariablesGlobales._thread == null) {
                    VariablesGlobales._thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (VariablesGlobales._task == null) {
                                VariablesGlobales._task = new Envoi();
                                VariablesGlobales._task.execute();
                            } else {
                                if (VariablesGlobales._lockSync == false) {
                                    VariablesGlobales._lockSync = true;
                                    VariablesGlobales._task.execute();
                                }
                            }
                        }
                    });
                    VariablesGlobales._thread.start();
                } else {
                    try {
                        //VariablesGlobales._thread.start();
                        if (VariablesGlobales._lockSync == false) {
                            VariablesGlobales._lockSync = true;
                            VariablesGlobales._task.execute();
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }catch (ConcurrentModificationException e){}
        return view;
    }

    public int getExifRotation(String imgPath) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imgPath);

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
        } catch (Exception e) {
            ////e.printStackTrace();
        }
        return 0;
    }
    public Bitmap getBitmap(String path) {

        Bitmap bitmap = null;
        File f = new File(path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap result = null;
        try {
            result = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(new FileInputStream(f), null, options),
                    24, 24, false);
        } catch (FileNotFoundException e) {
        }
        return result;
        //image.setImageBitmap(bitmap);
    }

    private Bitmap decodeFile(String imgPath)
    {
        File mSaveBit; // Your image file

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);

             return Bitmap.createScaledBitmap(bitmap,
                    100, 100, false);
    }

    private String lectureDesParametres(String key) {
        String datas = "";
        String prefs = activity.getSharedPreferences("params", Context.MODE_PRIVATE).getString(key, datas);
        return prefs;


    }

    public static void setGrayScale(ImageView v){
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0); //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        v.setColorFilter(cf);
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void sauvegardeDesParametres(int parametres) {
        boolean prefs = activity.getSharedPreferences("params", Context.MODE_PRIVATE).edit().putInt("nb_photos", parametres).commit();

    }

    private Handler mListViewDidLoadHanlder = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            //Do whatever you need here the listview is loaded

            return false;
        }
    });
}

