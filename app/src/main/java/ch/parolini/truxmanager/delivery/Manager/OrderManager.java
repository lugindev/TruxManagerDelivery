package ch.parolini.truxmanager.delivery.Manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ch.parolini.truxmanager.delivery.AppContext;
import ch.parolini.truxmanager.delivery.model.Order;
import ch.parolini.truxmanager.delivery.model.Picture;


/**
 * Manage the order list
 */
public class OrderManager {

    private static final int MAX_ORDER_IN_LIST = 10;
    private static LinkedList<Order> orders = new LinkedList<>() ;
    public static Order currentOrder = null ;
    private static int maxAgeMinutes = 3 ;
    private static List<Picture> pictureList = new ArrayList<>();




    /*
    public static List<Weighing> getWeightingsToShow() {
        List<Weighing> weighingsToSHow = new ArrayList<Weighing>();

        for (Weighing weighing : orders) {
            if (!weighing.hasPictureFiles() || !weighing.areAllPicturesSent()) {
                weighingsToSHow.add(weighing);
            }
        }
        return weighingsToSHow ;
    }
*/
/*    public static Order getWeighingFromOrder(String order) {
        for (Order weighing : orders) {
            if (order.equals(weighing.getOrderNumber())) {
                return weighing ;
            }
        }
        return null ;
    }*/


    /*   *//**
     * When we receive new orders from the server

     *//*
    public static void updateWeightings(List<Order> newOrders) {


        // if an old waiting is still on the list of the new waiting
        // and it hasn't sent its images, it should stay

        for ( Order oldOrder : orders) {
            if (newOrders.contains(oldOrder)) {
                Order newOrder = newOrders.get(newOrders.indexOf(oldOrder));
                oldOrder.setClientName(newOrder.getClientName());
                oldOrder.setPlate(newOrder.getPlate());
                oldOrder.setQuality(newOrder.getQuality());
                newOrders.set(newOrders.indexOf(newOrder), oldOrder);
            } else {
                // if the old weighting is not in server list
                // but we want to send its picture anyway
                if (oldOrder.hasPictureFiles()) {
                    putWeightingToSend(oldOrder) ;
                }
            }
        }

        orders = newOrders;
    }*/


    public static Order createAndSetCurrent(String orderNumber) {
        Order order = new Order(orderNumber.trim(),new Date());
        order.setScanDate(new Date());
        addOrder(order);
        currentOrder = order ;
        return order ;
    }

    public static Order SetCurrent(String orderNumber, Date date) {
        Order order = new Order(orderNumber.trim(),date);
        order.getScanDate();
        //addOrder(order);
        currentOrder = order ;
        return order ;
    }

    /**
     * Remove order from list and remove associated files
     * @param order
     */
    public static void removeOrder(Order order) {
        orders.remove(order) ;
    }

    public static void removeOrderAndPicturePhone(Order order) {
        orders.remove(order) ;
    }


    public static void removeCurrentOrder() {
        if(currentOrder!=null) {
            pictureList = currentOrder.getPictures();
            if (pictureList.size() != 0) {
                for (Picture picture : pictureList) {
                    if(picture.getFile().getPath()!=null) {
                        File target = new File(picture.getFile().getPath());

                        if (lectureDesParametres("action_fichier").equals("effacer")) {
                            if (target.exists() && target.isFile() && target.canWrite()) {
                                target.delete();
                                //Log.d("d_file", "" + target.getName());
                            }
                        }
                    }

                }
            }

            removeOrder(currentOrder);
            currentOrder = null;
        }
    }

    public static int getMaxAgeMinutes() {
        return maxAgeMinutes;
    }

    /**
     * Return a copy of the list to avoid concurent issues, and to avoid external modification
     * @return
     */
    public static List<Order> getOrders() {
        return new LinkedList<>(orders);
    }

    private static void addOrder(Order order) {
        ((LinkedList<Order>)orders).addFirst(order);
    }

    public static boolean hasOrder() {
        return orders.size()>0 ;
    }

    public static void setMaxAgeMinutes(int maxAgeMinutes) {
        OrderManager.maxAgeMinutes = maxAgeMinutes;
        //Log.i(RpcManager.class.getName(),"maxAgeMinutes set to " + maxAgeMinutes) ;
    }

//    public static void putWeightingToSend(Order order) {
//        if (order.hasPictureFiles()
//                && !ordersToSend.contains(order)) {
//            ordersToSend.add(order);
//        }
//    }

/*    public static void updateWeightingsToSend(boolean force) {

        for (Order order : orders) {
            if (!ordersToSend.contains(order)
                    && !order.areAllPicturesSent()
                    && (order.isOldEnoughToBeUploaded() || force)) {
                ordersToSend.add(order);
            }
        }

    }*/

    public static String toJSON(List<Order> orders) {

        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(orders);
    }

    public static List<Order> fromJSON(String orderJSON) throws IOException {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(orderJSON, new TypeToken<LinkedList<Order>>() {
        }.getType());
    }

    public static void persistOrdersToLocalStorage(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("data", toJSON(orders));
        // Commit the edits!
        editor.commit();
    }

    public static void readFromLocalStorage(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonDataContent = settings.getString("data", null);
        if (jsonDataContent!=null) {
            try {
                orders = new LinkedList<>(fromJSON(jsonDataContent)) ;
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }

    /**
     * remove older order only if the picture are sent.
     */
    public static void removeOldOrder() {

        if (orders != null) {
            int size = orders.size() ;
            while (size > MAX_ORDER_IN_LIST) {
                Order orderToRemove = orders.getLast();
                if (orderToRemove.areAllPicturesSent()) {
                    removeOrder(orderToRemove);
                    size = orders.size();
                }
            }
        }
    }

    public static void removeOrders() {
        try {
            if (orders != null) {
                List<Order> orders = getOrders() ;
                for (Order o :orders){
                    //Order orderToRemove = o.get(i);
                    //if (orderToRemove.areAllPicturesSent()) {
                    removeOrder(o);
                    //size = orders.size();
                    //}
                }
            }
        }catch (Exception e){}
    }

    private static String lectureDesParametres(String key) {
        String datas = "";
        String prefs = AppContext.getAppContext().getSharedPreferences("params", Context.MODE_PRIVATE).getString(key, datas);
        return prefs;


    }
}
