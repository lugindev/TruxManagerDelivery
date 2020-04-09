package ch.parolini.truxmanager.delivery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentParametres.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentParametres#newInstance} factory method to
 * create an instance of this fragment.
 */
@SuppressLint("ValidFragment")
public class FragmentParametres extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final MainActivity _activite;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String[] paramertes = new String[6];

    @SuppressLint("ValidFragment")
    public FragmentParametres(MainActivity activity) {
        // Required empty public constructor
        _activite = activity;



    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentParametres.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentParametres newInstance(String param1, String param2) {
        FragmentParametres fragment = new FragmentParametres(null);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_parametre, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PackageManager manager = _activite.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(_activite.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {

        }


        TextView txt_view_name = (TextView) getView().findViewById(R.id.version_name);
        txt_view_name.setText( "Infos app: " + "Version: " + info.versionName +" "+ "Code: " + info.versionCode);
        Spinner spinner = (Spinner) getView().findViewById(R.id.qualite_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.qualite_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Spinner spinnerDefault1 = (Spinner) getView().findViewById(R.id.default_action_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(),
                R.array.default_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerDefault1.setAdapter(adapter1);

        Spinner spinnerDefault2 = (Spinner) getView().findViewById(R.id.image_action_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.image_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerDefault2.setAdapter(adapter2);

        Spinner spinnerDefault3 = (Spinner) getView().findViewById(R.id.fichier_action_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getContext(),
                R.array.fichier_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerDefault3.setAdapter(adapter3);

        Spinner spinnerDefault4 = (Spinner) getView().findViewById(R.id.taille_action_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(getContext(),
                R.array.taille_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerDefault4.setAdapter(adapter4);

        Spinner spinnerDefault5 = (Spinner) getView().findViewById(R.id.theme_action_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(getContext(),
                R.array.theme_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerDefault5.setAdapter(adapter5);



        switch (lectureDesParametres("qualite_photos")) {
            case "":
                spinner.setSelection(1);
                break;
            case "90":
                spinner.setSelection(2);
                break;
            case "95":
                spinner.setSelection(1);
                break;
            case "100":
                spinner.setSelection(0);
                break;
        }

        switch (lectureDesParametres("action_default")) {
            case "":
                spinnerDefault1.setSelection(0);
                break;
            case "photo_gallerie":
                spinnerDefault1.setSelection(0);
                break;
            case "photo":
                spinnerDefault1.setSelection(1);
                break;
            case "gallerie":
                spinnerDefault1.setSelection(2);
                break;
        }

        switch (lectureDesParametres("action_color")) {
            case "":
                spinnerDefault2.setSelection(0);
                break;
            case "color":
                spinnerDefault2.setSelection(0);
                break;
            case "gray":
                spinnerDefault2.setSelection(1);
                break;
            case "none":
                spinnerDefault2.setSelection(2);
                break;
        }

        switch (lectureDesParametres("action_fichier")) {
            case "":
                spinnerDefault3.setSelection(0);
                break;
            case "effacer":
                spinnerDefault3.setSelection(0);
                break;
            case "garder":
                spinnerDefault3.setSelection(1);
                break;
        }

        switch (lectureDesParametres("action_taille")) {
            case "":
                spinnerDefault4.setSelection(0);
                break;
            case "grande":
                spinnerDefault4.setSelection(0);
                break;
            case "petite":
                spinnerDefault4.setSelection(1);
                break;
        }

        switch (lectureDesParametres("action_theme")) {
            case "":
                spinnerDefault5.setSelection(0);
                break;
            case "couleur":
                spinnerDefault5.setSelection(0);
                break;
            case "gray":
                spinnerDefault5.setSelection(1);
                break;
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Log.i("Qualite", "Haut");
                        paramertes[0] = "100";
                        sauvegardeDesParametres(paramertes);
                        break;
                    case 1:
                        Log.i("Qualite", "Moyenne");
                        paramertes[0] = "95";
                        sauvegardeDesParametres(paramertes);
                        break;

                    case 2:
                        Log.i("Qualite", "Basse");
                        paramertes[0] = "90";
                        sauvegardeDesParametres(paramertes);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });

        spinnerDefault1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Log.i("DefaultAction", "photo_gallerie");
                        paramertes[1] = "photo_gallerie";
                        sauvegardeDesParametres(paramertes);
                        break;
                    case 1:
                        Log.i("DefaultAction", "photo");
                        paramertes[1] = "photo";
                        sauvegardeDesParametres(paramertes);
                        break;

                    case 2:
                        Log.i("DefaultAction", "gallerie");
                        paramertes[1] = "gallerie";
                        sauvegardeDesParametres(paramertes);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });

        spinnerDefault2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Log.i("DefaultAction", "color");
                        paramertes[2] = "color";
                        sauvegardeDesParametres(paramertes);
                        break;
                    case 1:
                        Log.i("DefaultAction", "gray");
                        paramertes[2] = "gray";
                        sauvegardeDesParametres(paramertes);
                        break;

                    case 2:
                        Log.i("DefaultAction", "none");
                        paramertes[2] = "none";
                        sauvegardeDesParametres(paramertes);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });

        spinnerDefault3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:

                        paramertes[3] = "effacer";
                        sauvegardeDesParametres(paramertes);
                        break;
                    case 1:

                        paramertes[3] = "garder";
                        sauvegardeDesParametres(paramertes);
                        break;

                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });


        spinnerDefault4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:

                        paramertes[4] = "grande";
                        sauvegardeDesParametres(paramertes);
                        break;
                    case 1:

                        paramertes[4] = "petite";
                        sauvegardeDesParametres(paramertes);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });

        spinnerDefault5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:

                        paramertes[5] = "color";
                        sauvegardeDesParametres(paramertes);
                        _activite.choixDuTheme();
                        break;
                    case 1:

                        paramertes[5] = "gray";
                        sauvegardeDesParametres(paramertes);
                        _activite.choixDuTheme();
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_supprimer_photos).setVisible(false);
        menu.findItem(R.id.action_supprimerbon).setVisible(false);
        menu.findItem(R.id.action_gallery).setVisible(false);
        menu.findItem(R.id.action_photo).setVisible(false);
        menu.findItem(R.id.action_parametres).setVisible(false);
    }


    public void sauvegardeDesParametres(String[] parametres) {
        boolean prefs = getActivity().getSharedPreferences("params", Context.MODE_PRIVATE).edit().putString("qualite_photos", parametres[0]).commit();
        boolean prefs1 = getActivity().getSharedPreferences("params", Context.MODE_PRIVATE).edit().putString("action_default", parametres[1]).commit();
        boolean prefs2 = getActivity().getSharedPreferences("params", Context.MODE_PRIVATE).edit().putString("action_color", parametres[2]).commit();
        boolean prefs3 = getActivity().getSharedPreferences("params", Context.MODE_PRIVATE).edit().putString("action_fichier", parametres[3]).commit();
        boolean prefs4 = getActivity().getSharedPreferences("params", Context.MODE_PRIVATE).edit().putString("action_taille", parametres[4]).commit();
        boolean prefs5 = getActivity().getSharedPreferences("params", Context.MODE_PRIVATE).edit().putString("action_theme", parametres[5]).commit();



    }

    private String lectureDesParametres(String key) {
        String datas = "";
        String prefs = getActivity().getSharedPreferences("params", Context.MODE_PRIVATE).getString(key, datas);
        return prefs;


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _activite.updateOrderListView();
    }
}
