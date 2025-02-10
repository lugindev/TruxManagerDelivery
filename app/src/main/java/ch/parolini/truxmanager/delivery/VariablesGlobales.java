package ch.parolini.truxmanager.delivery;

import ch.parolini.truxmanager.delivery.basededonnee.Requetes;
import ch.parolini.truxmanager.delivery.model.Order;

/**
 * Created by salam on 03.05.2019.
 */

public class VariablesGlobales {


    public static boolean _notSendOrders = false;
    public static boolean _isOnBoot = false;
    public static boolean networkInfos = false;
    public static boolean _isShutdown = false;
    public static boolean _blockSendOrders = false;
    public static Order currentOrder;
    public static String _versionCode = "";
    public static String _versionName;
    public static int[] _tabDownload;
    public static boolean _updateList = false;
    public static int sizeTotal = 0;
    public static int downloaded = 0;
    public static boolean syncNoUpdate = true;


    public static String message ="";

    public static String _noOrder;
    public static Requetes requeteBd = null;
    public static Thread _thread = null;
    public static Envoi _task = null;
    public static boolean _lockSync = false;
}
