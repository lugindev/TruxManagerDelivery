package ch.parolini.truxmanager.delivery.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Path;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ch.parolini.truxmanager.delivery.AppContext;
import ch.parolini.truxmanager.delivery.MainActivity;
import ch.parolini.truxmanager.delivery.Manager.FileManager;
import ch.parolini.truxmanager.delivery.R;
import ch.parolini.truxmanager.delivery.SerciceRedemarrage;
import ch.parolini.truxmanager.delivery.ServerCommService;
import ch.parolini.truxmanager.delivery.VariablesGlobales;
import ch.parolini.truxmanager.delivery.model.Order;


public class ListAdapter extends BaseAdapter {
        private MainActivity activity;
        private static List<Order> dataList;
        private static LayoutInflater inflater=null;
        private View view;
        private    Order order = null;
        private ExecutorService executorService = Executors.newSingleThreadExecutor();
        private     Bitmap bmp=null;
        private Integer counter = 0;
        private boolean lsiteActualisaton=true;
        private boolean EnSychronisation =false;
        public  int nbOrder = 0;
        private int nbrImage=0;
        private int nbOrders = 0;
       private java.lang.Object LOCK = "";


    //public ImageLoader imageLoader;

        public ListAdapter(MainActivity activity, List<Order> dataList) {
            this.activity = activity;
            this.dataList = dataList;
            inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
          //  imageLoader=new ImageLoader(activity.getApplicationContext());
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
            if (convertView == null)
                view = inflater.inflate(R.layout.list_row2, null);

            TextView title = (TextView) view.findViewById(R.id.title);
            TextView detail = (TextView) view.findViewById(R.id.detail);
            TextView photoNumber = (TextView) view.findViewById(R.id.photoNumber);
            final TextView status = (TextView) view.findViewById(R.id.status);
            final ImageView thumbImageView = (ImageView) view.findViewById(R.id.list_image); // thumb image


            //ImageView synchroImageView=(ImageView)view.findViewById(R.id.synchoImage); // thumb image
            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.rowLayout);
            order = this.dataList.get(position);
            if (VariablesGlobales.networkInfos == false) {
                title.setTextColor(Color.parseColor("#d50000"));
                status.setTextColor(Color.parseColor("#d50000"));
            } else {
                title.setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));
                status.setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));
            }
            // Setting all values in listview
            title.setText("" + order.getOrderNumber());
            detail.setText(new SimpleDateFormat("dd MMMM 'à' HH:mm", new Locale("FR", "CH")).format(order.getScanDate()));
            if (order.getPictureCount() > 0) {
                //status.setTextColor(Color.BLUE);

                if (order.getPictureCount() > 1) {
                    int nbImage = order.getPictureCount() - ServerCommService.nbTotalOrdre + 1;
                    status.setText("ENVOI EN COURS:  " + order.getPictureCount());


                } else {

                    status.setText("ENVOI EN COURS: " + order.getPictureCount());
                }
                nbOrders = nbOrders + order.getPictureCount();
            } else {
                    status.setText("IMAGE A ENVOYER: " + order.getPictureCount());
                    status.setTextColor(Color.GRAY);
            }

            //imageLoader.DisplayImage(song.get(CustomizedListView.KEY_THUMB_URL), thumb_image);

            ListView listView = (ListView) parent;

            if(VariablesGlobales.networkInfos==false){
                title.setTextColor(Color.parseColor("#9b0000"));
                status.setTextColor(Color.parseColor("#9b0000"));
            }else {

                    if (lectureDesParametres("action_theme").equals("color") || lectureDesParametres("action_theme").equals("")) {
                        title.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));


                    } else {
                        title.setTextColor(Color.parseColor("#000000"));

                    }


                if(order.getPictureCount()>0) {
                    if (lectureDesParametres("action_theme").equals("color") || lectureDesParametres("action_theme").equals("")) {

                        status.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));

                    } else {
                        status.setTextColor(Color.parseColor("#000000"));
                    }
                }
            }


            if (lectureDesParametres("action_taille").equals("petite")){
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                int margeTop= (int) AppContext.getAppContext().getResources().getDimension(R.dimen.image_marge_top);
                int margeBottom= (int) AppContext.getAppContext().getResources().getDimension(R.dimen.image_marge_bottom);
                int margeLeft= (int) AppContext.getAppContext().getResources().getDimension(R.dimen.image_marge_left);
                lp.setMargins(margeLeft, margeTop, 0, margeBottom);
                thumbImageView.setLayoutParams(lp);
                thumbImageView.getLayoutParams().height = (int) AppContext.getAppContext().getResources().getDimension(R.dimen.petite_image_hauteur);
                thumbImageView.getLayoutParams().width = (int) AppContext.getAppContext().getResources().getDimension(R.dimen.petite_image_largeur);

            }
            else {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                //int margeTop= (int) AppContext.getAppContext().getResources().getDimension(R.dimen.image_marge_top_grand);
                int margeBottom= (int) AppContext.getAppContext().getResources().getDimension(R.dimen.image_marge_bottom_grand);
                int margeLeft= (int) AppContext.getAppContext().getResources().getDimension(R.dimen.image_marge_left_grand);
                lp.setMargins(0, 0, 0, 0);
                thumbImageView.setLayoutParams(lp);
                thumbImageView.getLayoutParams().height = (int) AppContext.getAppContext().getResources().getDimension(R.dimen.grande_image_hauteur);
                thumbImageView.getLayoutParams().width = (int) AppContext.getAppContext().getResources().getDimension(R.dimen.grande_image_largeur);

            }
            if (order.hasPictureFiles()) {
                //if (order.isDirectionOk == false) {
                if (order.getPictureCount() != nbrImage)
                    nbrImage = order.getPictureCount();
                    Bitmap bitmap = null;
                    Bitmap smallBitmap = null;
                    Bitmap resized = null;
                    int angle;
                    final String path = order.getDefaultPictureFile(0).getPath();
                    File f = new File(path.substring(0, path.length() - 4) + "_preview.jpeg");
                    if (f.exists()) {
                        switch (lectureDesParametres("action_color")) {
                            case "":
                                smallBitmap = getBitmap(path.substring(0, path.length() - 4) + "_preview.jpeg");
                                thumbImageView.setImageBitmap(smallBitmap);
                                break;
                            case "color":
                                smallBitmap = getBitmap(path.substring(0, path.length() - 4) + "_preview.jpeg");
                                thumbImageView.setImageBitmap(smallBitmap);
                                break;
                            case "gray":
                                //setGrayScale(thumbImageView);
                                smallBitmap = getBitmap(path.substring(0, path.length() - 4) + "_preview.jpeg");
                                thumbImageView.setImageBitmap(smallBitmap);
                                setGrayScale(thumbImageView);
                                break;

                            case "none":
                                smallBitmap = getBitmap(path.substring(0, path.length() - 4) + "_preview.jpeg");
                                thumbImageView.setImageBitmap(smallBitmap);
                                thumbImageView.setImageResource(R.drawable.img_photo);
                                break;
                        }
                    } else {
                        // do something
                        bitmap = order.getBitmap(0);
                        switch (lectureDesParametres("action_color")) {
                            case "":

                                //(Color.TRANSPARENT);
                                if (bitmap != null) {
                                    if (getExifRotation(path) == 90) {
                                        angle = 90;

                                    } else {
                                        angle = 0;
                                    }
                                    Matrix matrix = new Matrix();
                                    //setup rotation degree
                                    matrix.postRotate(angle);
                                    resized = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                    thumbImageView.setImageBitmap(resized);
                                    try {
                                        order.savebitmap(resized, path.substring(0, path.length() - 4) + "_preview.jpeg");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            case "color":
                                //(Color.TRANSPARENT);
                                if (bitmap != null) {
                                    if (getExifRotation(path) == 90) {
                                        angle = 90;

                                    } else {
                                        angle = 0;
                                    }
                                    Matrix matrix = new Matrix();
                                    //setup rotation degree
                                    matrix.postRotate(angle);
                                    resized = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                    thumbImageView.setImageBitmap(resized);

                                    try {
                                        order.savebitmap(resized, path.substring(0, path.length() - 4) + "_preview.jpeg");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }

                                break;
                            case "gray":
                                //setGrayScale(thumbImageView);
                                //(Color.TRANSPARENT);
                                if (bitmap != null) {
                                    if (getExifRotation(path) == 90) {
                                        angle = 90;

                                    } else {
                                        angle = 0;
                                    }
                                    Matrix matrix = new Matrix();
                                    //setup rotation degree
                                    matrix.postRotate(angle);
                                    resized = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                                    thumbImageView.setImageBitmap(resized);
                                    setGrayScale(thumbImageView);
                                    try {
                                        order.savebitmap(resized, path.substring(0, path.length() - 4) + "_preview.jpeg");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                break;

                            case "none":
                                if (bitmap != null) {
                                    if (getExifRotation(path) == 90) {
                                        angle = 90;

                                    } else {
                                        angle = 0;
                                    }
                                    Matrix matrix = new Matrix();
                                    //setup rotation degree
                                    matrix.postRotate(angle);
                                    resized = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                    thumbImageView.setImageBitmap(resized);
                                    try {
                                        order.savebitmap(resized, path.substring(0, path.length() - 4) + "_preview.jpeg");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    thumbImageView.setImageResource(R.drawable.img_bon1);
                                    break;
                                }
                        }

                    }
                } else {
                    thumbImageView.setImageResource(R.drawable.img_photo);
                    if (lectureDesParametres("action_color").equals("none")) {
                        thumbImageView.setImageResource(R.drawable.img_bon1);
                    }


                }

                if(nbOrders>0){
                VariablesGlobales._notSendOrders=true;
                }
                else {
                VariablesGlobales._notSendOrders=false;
                }

            sauvegardeDesParametres(nbOrders);

            return view;
        }

    public int getExifRotation(String imgPath)
    {
        try
        {
            ExifInterface exif = new ExifInterface(imgPath);
            String rotationAmount = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            if (!TextUtils.isEmpty(rotationAmount))
            {
                int rotationParam = Integer.parseInt(rotationAmount);
                switch (rotationParam)
                {
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
            }
            else
            {
                return 0;
            }
        }
        catch (Exception ex)
        {
            return 0;
        }
    }

    public Bitmap getBitmap(String path) {
        try {
            Bitmap bitmap=null;
            File f= new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            //image.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }}

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

}


