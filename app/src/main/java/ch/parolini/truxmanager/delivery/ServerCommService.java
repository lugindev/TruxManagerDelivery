package ch.parolini.truxmanager.delivery;


import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.parolini.truxmanager.delivery.Manager.RpcManager;
import ch.parolini.truxmanager.delivery.Manager.OrderManager;
import ch.parolini.truxmanager.delivery.basededonnee.Requetes;
import ch.parolini.truxmanager.delivery.model.ClientConfig;
import ch.parolini.truxmanager.delivery.model.Order;
import ch.parolini.truxmanager.delivery.model.Picture;



public class ServerCommService extends IntentService {

    static ServerCommService _thisService = null ;
    public static int nbTotalOrdre;
    private static MainActivity _activity;
    private ArrayList<String[]> images;
    private Requetes requetesBaseDeDonneeInterne;


    @Override
    public void onCreate() {
        super.onCreate();
        _thisService = this ;
    }

    public static ServerCommService getExistingInstance(MainActivity activity) {
        _activity = activity;
        return _thisService ;

    }

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public ServerCommService() {
        super("ServerCommService");
        Log.i("SRV","Starting thread") ;
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("SRV","Starting thread") ;
     while (true) {
         try {
             serverSynchro(false);
         } catch (FileNotFoundException e) {

         } catch (IOException e) {

         }

         // Wait
            try {
                int timeToSleepInSecondes = getResources().getInteger(R.integer.SERVICE_SLEEPING_TIME_SECONDS) ;
                Thread.sleep( timeToSleepInSecondes * 1000); // wait 20 secs
            } catch (Exception ignored) {}
        }
    }

    private void serverSynchro(boolean forceUpload) throws IOException {
        /*ClientConfig clientConfig = null;
        try {
            sendTextToGUI(getResources().getString(R.string.connecting)) ;
            clientConfig = loadClientConfig();
        } catch (BadCredentialException bce) {
            sendTextToGUI(getResources().getString(R.string.comm_error) + " " + bce.getMessage());
            askPassword() ;
        } catch (Exception e) {
            e.printStackTrace();
            sendTextToGUI(getResources().getString(R.string.comm_error) + " " + e.getMessage());
        }*/

        //if (clientConfig!=null) {
            //notifiyClientConfigToGUI(clientConfig);
        try {
            sendAllWeigtingsImagesToServer(forceUpload);
            OrderManager.removeOldOrder();
            OrderManager.persistOrdersToLocalStorage(this.getApplicationContext());
            //VariablesGlobales._blockSendOrders = false;
        }catch (Exception e){

        }
            //sendTextToGUI(getResources().getString(R.string.connected)) ;
        //}

    }

    public void forceSynchroNow() {

        ForceServerSynchroTask serverSynchroTask = new ForceServerSynchroTask() ;
        serverSynchroTask.execute();
    }

    public class ForceServerSynchroTask extends AsyncTask<Void, Void, Void> {

        Exception lastCallException;

        public Exception getLastCallException() {
            return lastCallException;
        }

        protected Void doInBackground(Void... voids) {
            try {
                serverSynchro(true);
            } catch (FileNotFoundException e) {

            } catch (IOException e) {

            }
            return null;
        }

    }


    private ClientConfig loadClientConfig() throws Exception {
            return RpcManager.getClientConfig();
    }



    /**
     * Try to send images until all's fine.
     * could be optimized by checking file by file...on day.
     * @param forceUpload
     */
    private void sendAllWeigtingsImagesToServer(boolean forceUpload) throws IOException {
        List<Order> sentOrders = new ArrayList<Order>() ;
        for (Order order : OrderManager.getOrders()) {
              sendOrderImagesNotAlreadySent(order, forceUpload) ;
        }
        //forceSynchroNow();

    }

    /**
     * If picture are old enough and not already sent, we send them to te server
     * @param order
     */
    private void sendOrderImagesNotAlreadySent(Order order, boolean forceUpload) throws IOException {
        try {
            //if (order.hasPictureFiles() && !order.areAllPicturesSent() && (order.isOldEnoughToBeUploaded() || forceUpload) && VariablesGlobales.networkInfos == true) {
                //sendTextToGUI("Synchronisation [" + order.getOrderNumber() + "]");
                 //nbTotalOrdre = order.getPictureCount();
                for (Picture picture : order.getPictures()) {
                    try {
                        requetesBaseDeDonneeInterne  = new Requetes(AppContext.getAppContext());
                        requetesBaseDeDonneeInterne.open();
                        images = new ArrayList<>();
                        images = (ArrayList<String[]>) requetesBaseDeDonneeInterne.selectImagesByName(picture.getFile().getPath());
                        if(images.size() == 0){
                            requetesBaseDeDonneeInterne.ajouterImage(picture.getFile().getPath());
                            Log.i("notSend","Image ajoutée "+ picture.getFile().getPath());
                        }
                        for (String[] path : images) {
                            Log.i("notSend","Image envoyer "+ picture.getFile().getPath());
                           // _activity.EmvoiImage(order.getOrderNumber(), path[1], order, path[0]);

                        }

                    } catch (Exception e) {

                    } finally {

                        requetesBaseDeDonneeInterne.close();

                }


                }
                VariablesGlobales._blockSendOrders=false;



        }
        catch (Exception e){
        Log.i("Exception","sendOrderImagesNotAlreadySent" + e.getMessage());
        }
    }

    private void notifiyOrderSent(Order order) {
        Intent intent = new Intent("onOrderSent");
        intent.putExtra("order", order);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


//    /**
//     *
//     * @param orders
//     */
//    private void sendWeigthingsToGUI(List<Order> orders) {
//        Intent intent = new Intent("onWeightingLoaded");
//        intent.putExtra("message", (ArrayList) orders);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//    }

    private void notifiyClientConfigToGUI(ClientConfig clientConfig) {
        Intent intent = new Intent("onConfigReceived");
        intent.putExtra("message", clientConfig);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     *
     * @param message
     */
    private void sendTextToGUI(String message) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("onToast");
        intent.putExtra("message", (String) message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void askPassword() {
        Intent intent = new Intent("onPasswordAsk");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    /*
    private void sendAllImages() {
        final List<File> allFiles= FileManager.getAllFilesInTruxFolder();
        for ( File file : allFiles) {
            String orderNumber = FileManager.findOrderNumberFromFile(file) ;
            if (orderNumber!=null) {
                if (RpcManager.sendPicture(orderNumber, file)) {
                    Weighing weighing = WeighingManager.getWeighingFromOrder(orderNumber) ;
                };
            } else {
                Log.e(RpcManager.class.getName(),"order number is null from this file:"+ file.getName()) ;
            }
        }


    }
*/



/*
        private List<Weighing> loadJobsFromServer() {
        List<Weighing> weighings = new ArrayList<Weighing>() ;

        // Simulation
       // weighings.add(new Weighing("VS 00000000000","","Inerte","21234","Client A"));
        // weighings.add(new Weighing("VS 11111111111","","Dechets","32334A","Client B"));
        //weighings.add(new Weighing("VS 22222222222","","Terre","451234","Client C"));
        //weighings.add(new Weighing("VS 33333333333","","Gravas","121234","Client D"));

        GetWeightingsTasks getWeightingsTasks = new GetWeightingsTasks();

        try {
            Log.i("ServerCommService","Récupération des pesages sur le serveur");
            sendTextToGUI(getResources().getString(R.string.connecting));
            weighings = getWeightingsTasks.execute().get(getResources().getInteger(R.integer.CONNECTION_TIMEOUT_SECONDES), TimeUnit.SECONDS);
            if (getWeightingsTasks.getLastCallException() != null) {
                throw getWeightingsTasks.getLastCallException();
            }
            Log.i("ServerCommService",weighings.size() + " Taches du jours récupérées.");
            sendTextToGUI(getResources().getString(R.string.connected)) ;
            if (weighings==null || weighings.size()==0) {
                sendTextToGUI(getResources().getString(R.string.nothingtodo)) ;
            }

        } catch (TimeoutException timeoutException) {
            timeoutException.printStackTrace();
            sendTextToGUI("Impossible d'accéder au serveur ( " + RpcManager.serverRootContextURL + "). Vérifiez votre connexion internet.");

        } catch (Exception e) {
            e.printStackTrace();
            sendTextToGUI("Impossible de récupérer les données du serveur: " + e.getMessage());

        }



        // foreach pesage call ImageManager.getExistingImageInFolderForOrder
        if (weighings!=null) {
            for (Weighing weighing : weighings) {
                weighing.setPictureFiles(FileManager.getExistingImageInFolderForOrder(weighing.getOrderNumber()));
            }
        }

        return weighings;
    }

    private class GetWeightingsTasks extends AsyncTask<Void, Void, List<Weighing>> {

        Exception lastCallException;

        public Exception getLastCallException() {
            return lastCallException;
        }

        protected List<Weighing> doInBackground(Void... urls) {
            try {
                List<Weighing> weighings = RpcManager.getCurrentWeighing();
                lastCallException = null;
                return weighings;
            } catch (Exception e) {
                lastCallException = e;
                e.printStackTrace();
                return new ArrayList<Weighing>();
            }
        }
    }
*/
        // Send an Intent with an action named "custom-event-name". The Intent sent should
    // be received by the ReceiverActivity.
    /*
    private void sendWeigthingsToGUI() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("custom-event-name");
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
*/

}
