package ch.parolini.truxmanager.delivery;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.SSLCertificateSocketFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import ch.parolini.truxmanager.delivery.Manager.OrderManager;
import ch.parolini.truxmanager.delivery.basededonnee.Requetes;
import ch.parolini.truxmanager.delivery.model.Bon;
import ch.parolini.truxmanager.delivery.model.Order;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
@SuppressLint("ValidFragment")
public class FragmentBon extends Fragment {

    // TODO: Customize parameter argument names
    public static final String ARG_COLUMN_COUNT = "column-count";
    public MainActivity context = null;
    private MainActivity _activity = null;
    public int position;
    // TODO: Customize parameters
    public int mColumnCount = 1;
    public OnListFragmentInteractionListener mListener;
    public List<Bon> _bons = new ArrayList<>();
    public static String url = "https://trux.luginbuhl.ch/pesages_en_cours.json";
    public ProgressDialog pDialog;
    static Bundle bundle = new Bundle();
    private static final String[] _lstParametre=null;
    private final ArrayList<Bon> _lstBon=null;
    private final String _bonNom="";
    private final int _vehiculeNumero=0;
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31";
    // TODO: Customize parameters
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private char current;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @SuppressLint("ValidFragment")
    public FragmentBon(int position, String[] lstParametre, MainActivity activity) {
        this.position = position;
        _activity =activity;
        //_lstParametre= new String[7];
       // _lstParametre=lstParametre;
    }

    @SuppressLint("ValidFragment")
    public FragmentBon(MainActivity context) {
        this.context = context;
        this._activity = context;

    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentBon newInstance(int columnCount) {
        FragmentBon fragment = new FragmentBon(null);
        Bundle args = new Bundle();
        args.putInt( ARG_COLUMN_COUNT, columnCount );
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt( ARG_COLUMN_COUNT );

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_bon, container, false );
        setHasOptionsMenu(true);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.addItemDecoration( new DividerItemDecoration( getActivity(), DividerItemDecoration.VERTICAL_LIST ) );
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager( new LinearLayoutManager( context ) );
            } else {
                recyclerView.setLayoutManager( new GridLayoutManager( context, mColumnCount ) );
            }
            try {
                _activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _activity.setTitle(Html.fromHtml("Liste des pesages"));
                    }
                });

            }catch ( Exception e){ // e.getStackTrace();
                //////Log.i("viewExeception","viewExeception6 " + e.getMessage());
            }
            _bons = new ArrayList<>();
            try {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            _bons = SelectionDesBons();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                thread.start();

            } catch (Exception e) {
                //throw new RuntimeException(e);
            }

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            recyclerView.setAdapter( new ListBonAdapter( getContext(), _bons ) );
            recyclerView.addOnItemTouchListener( new RecyclerTouchListener( getActivity(),
                    recyclerView, new ClickListener() {
                private String plaques;
                private String BonPrenom;
                private String Bonnumero;
                private String BonNom;
                private int BonId;

                @Override
                public void onClick(View view, final int position) {
                    this.Bonnumero = _bons.get(position).getNumero();
                    this.BonNom = _bons.get(position).getNom();
                    this.plaques = _bons.get(position).getPlaque();

                    List<Order> orders = OrderManager.getOrders();
                    boolean exist = false;
                    Date date = null;
                    for (Order order: orders) {

                        if(order.getOrderNumber().equals(this.Bonnumero)){
                            exist = true;
                            OrderManager.currentOrder = order;
                        }
                    }

                    if(!exist) {
                        OrderManager.currentOrder = OrderManager.createAndSetCurrent(this.Bonnumero);
                    }
                    else {

                    }
                    _activity.updateOrderListView();
                    _activity.setTitle(Html.fromHtml("TM"));
                    try {
                        _activity.showCamera2(OrderManager.currentOrder, ".jpg");
                    } catch (ParseException e) {
                        //e.printStackTrace();
                    }
                    getFragmentManager().popBackStack();
                    VariablesGlobales.message = this.BonNom.replaceAll("[^A-Za-z0-9 ]", "_").replace(" ","_");
                    _activity._textViewinfo.setText("N°: " + OrderManager.currentOrder.getOrderNumber() + " - " + this.BonNom);
                    _bons.clear();
                    popBackStackTillEntry(1);

                }

                @Override
                public void onLongClick(View view, int position) {
                    /*Toast.makeText( getContext(), "Long press on position :" + position,
                            Toast.LENGTH_LONG ).show();*/
                }
            } ) );

        }
        return view;

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reload:
                // do something
                FragmentBon fragment = new FragmentBon(_activity);
                FragmentTransaction ft = _activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame_content,fragment);
                ft.addToBackStack(null);
                ft.commit();

                getActivity().getSupportFragmentManager().popBackStack();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        _activity.getMenuInflater().inflate(R.menu.list_menu1, menu);
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach( context );


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Bon item);
    }

   

    public void popBackStackTillEntry(int entryIndex) {

        if (getActivity().getSupportFragmentManager() == null) {
            return;
        }
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() <= entryIndex) {
            return;
        }
        FragmentManager.BackStackEntry entry = getActivity().getSupportFragmentManager().getBackStackEntryAt(
                entryIndex);
        if (entry != null) {
            //Utilities.sDisableFragmentAnimations = true;
            getActivity().getSupportFragmentManager().popBackStackImmediate(entry.getId(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }


    }

    private List<Bon> SelectionDesBons() throws Exception {
        List<Bon> listDesBon = new ArrayList<>();



        try{
            URL url1 = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            if (conn instanceof HttpsURLConnection) {
                HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
                //httpsConn.setSSLSocketFactory(SSLCertificateSocketFactory.getInsecure(0, null));
                //httpsConn.setHostnameVerifier(new AllowAllHostnameVerifier());
            }
            conn.setRequestMethod("GET");

// read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String jsonStr = org.apache.commons.io.IOUtil.toString(in, "UTF-8");
            if (jsonStr != null) {

                JSONObject json = new JSONObject(jsonStr);
                JSONArray jArray = json.getJSONArray("pesages_en_cours");

                for (int j = 0; j < jArray.length(); j++) {
                    JSONObject values = jArray.getJSONObject(j);
                    Bon _bon = new Bon();
                    _bon.setNumero(values.getString("num_bon"));
                    _bon.setNom(values.getString("designation"));
                    _bon.setPlaque(values.getString("immatriculation"));
                    //_bon.setPlaque("VS56432");


                    listDesBon.add(_bon);


                }
            }

        /*try {
            HttpResponse response = GetData1(url);
            //get all headers
            Header[] headers = response.getAllHeaders();
            for (Header header : headers) {
                System.out.println("Key : " + header.getName() +
                        " ,Value : " + header.getValue());
            }
            String jsonStr = null;
            jsonStr = getJson(GetData(url),url);

            if (jsonStr != null) {

                JSONObject json = new JSONObject(jsonStr);
                JSONArray jArray = json.getJSONArray("pesages_en_cours");

                for (int j = 0; j < jArray.length(); j++) {
                    JSONObject values = jArray.getJSONObject(j);
                    Bon _bon = new Bon();
                    _bon.setNumero(values.getString("num_bon"));
                    _bon.setNom(values.getString("designation"));
                    _bon.setPlaque(values.getString("immatriculation"));
                    //_bon.setPlaque("VS56432");


                    listDesBon.add(_bon);


                }


            }*/
        } catch (final Exception e) {
        Log.i("erreur",e.getMessage());

        }
        return listDesBon;
    }

    public HttpResponse GetData (String url) throws IOException {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        String url1 = url;
        HttpPost httppost = new HttpPost(url1);
        return getNewHttpClient1().execute(httppost);

    }

    public HttpResponse GetData1 (String url) throws IOException {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        String url1 = url;
        HttpHead httphead = new HttpHead(url1);
        return getNewHttpClient1().execute(httphead);

    }


    public HttpClient getNewHttpClient1() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            registry.register(new Scheme("https", PlainSocketFactory
                    .getSocketFactory(), 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public String getJson(HttpResponse httpClient, String url) {
        StringBuilder sb = new StringBuilder();
        try {
            HttpGet getRequest = new HttpGet(url);
            getRequest.addHeader("accept", "application/json");

            HttpResponse response = httpClient;

            String output;
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
        }  catch (Exception e) { ////Log.i("simulation", e.getMessage() + "39");

        }
        return sb.toString();
    }


}







