package gnotus.inoveplastika;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;

import gnotus.inoveplastika.ArrayAdapters.ArrayAdapterMenuList;
import gnotus.inoveplastika.DataModels.DataModelMenuList;
import gnotus.inoveplastika.DataModels.DataModelParams;
import gnotus.inoveplastika.Logistica.TransferenciaActivity;

public class MainActivity_Lista extends AppCompatActivity  implements View.OnClickListener, AsyncRequest.OnAsyncRequestComplete {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    private Activity thisActivity = MainActivity_Lista.this;

    Bundle bundleSent = new Bundle();

    private ProgressDialog progDailog;

    public static String MYFILEVERSION = "20030900";

    public static final String MYFILE = "configs";
    public static final String CAMINHO = "caminho";
    public static final String LASTUSER = "lastuser";
    private static final String USERID = "userid";
    private static final String BOSTAMP_CARGA = "bostampcarga";
    public static final String CRLF = "\r\n";
    private static final String TRUE = "true";

    private ArrayList<DataModelMenuList> menuMain = new ArrayList<>();

    private ArrayAdapterMenuList adapterMenuList;

    private ListView listViewMenuList;

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*Dialogos.showToast(thisActivity,"Manufacturer: "+ Build.MANUFACTURER,2.0,16);
        Dialogos.showToast(thisActivity,"Model: "+ Build.MODEL,3.0,16);
        Dialogos.showToast(thisActivity,"Brand: "+ Build.BRAND,4.0,16);
        Log.d("MainActivity","Manufacturer: "+ Build.MANUFACTURER);
        Log.d("MainActivity","Model: "+ Build.MODEL);
        Log.d("MainActivity","Brand: "+ Build.BRAND);*/


        progDailog = new ProgressDialog(this);
        progDailog.setMessage("Aguarde");
        progDailog.setIndeterminate(true);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        sharedpreferences = getSharedPreferences(MYFILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(USERID, "");
        editor.apply();

        if (sharedpreferences.contains(CAMINHO)) {

            Globals.getInstance().setCaminho(sharedpreferences.getString(CAMINHO, ""));
        }

        if (sharedpreferences.contains(USERID)) {

            Globals.getInstance().setUserid("");
        }

        if(sharedpreferences.contains(BOSTAMP_CARGA)) {

            Globals.getInstance().setTrfCargaBostamp(sharedpreferences.getString(BOSTAMP_CARGA,""));

        }

        if (Globals.getInstance().getSelectedFactory().equals(Globals.FACTORY_BARCELOS)) {

            menuMain.add(new DataModelMenuList("TRANSFERÊNCIAS","1"));
            menuMain.add(new DataModelMenuList("INVENTÁRIOS","2"));
            menuMain.add(new DataModelMenuList("PRODUÇÃO","3"));
            menuMain.add(new DataModelMenuList("CONSULTAS","4"));
            menuMain.add(new DataModelMenuList("PICKING","5"));
            menuMain.add(new DataModelMenuList("MANUTENÇÃO","6"));
            menuMain.add(new DataModelMenuList("QUALIDADE","7"));
            menuMain.add(new DataModelMenuList("OUTRAS OPÇÕES","8"));


        }

        if (Globals.getInstance().getSelectedFactory().equals(Globals.FACTORY_GAIA)) {

            menuMain.add(new DataModelMenuList("TRANSFERÊNCIAS","1"));
            menuMain.add(new DataModelMenuList("INVENTÁRIOS","2"));
            menuMain.add(new DataModelMenuList("PRODUÇÃO","3"));
            menuMain.add(new DataModelMenuList("CONSULTAS","4"));
            menuMain.add(new DataModelMenuList("PICKING","5"));
            // menuMain.add(new DataModelMenuList("MANUTENÇÃO","6"));
            menuMain.add(new DataModelMenuList("OUTRAS OPÇÕES","7"));

        }

        listViewMenuList= (ListView) findViewById(R.id.listViewMenu);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int versionCode = BuildConfig.VERSION_CODE;

        // String mTitulo = "Versão "+versionCode;
        String mTitulo = "Versão "+MYFILEVERSION;

        getSupportActionBar().setTitle(mTitulo);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.parseColor(Globals.getInstance().getDefaultToolbarColour()));

        initNavigationDrawer();

        listViewMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            //handle multiple view click events
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                String selMenuId = adapterMenuList.getItem(position).getMenuid();
                String selMenuOpcao = adapterMenuList.getItem(position).getMenuopcao();
                System.out.println(selMenuId);

                Intent myIntent;

                switch (selMenuId) {
                    case "1":
                        Intent intent_transferencias = new Intent(thisActivity, TransferenciaActivity.class);
                        startActivity(intent_transferencias);
                        break;


                    default:
                        bundleSent.clear();
                        bundleSent.putString("menuid",selMenuId);
                        bundleSent.putString("menuopcao",selMenuOpcao);
                        myIntent = new Intent(thisActivity,SMenuLista.class);
                        myIntent.putExtras(bundleSent);
                        startActivity(myIntent);

                }

            }
        });

        testConnection();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {


        }
    }

    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.navigation_inicio:
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.navigation_config_ip:

                        Intent myIntent = new Intent(getApplication(), ConfigsActivity.class);
                        startActivityForResult(myIntent, 1);

                        drawerLayout.closeDrawers();
                        break;


                    case R.id.navigation_update_apk:

                        updateApk();
                        //new DownloadNewVersion().execute();

                        break;


                    case R.id.navigation_logout:

                        //Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        //startActivity(intent);
                        finishAffinity();
                        finish();

                }
                return true;
            }
        });

        View header = navigationView.getHeaderView(0);
        navigationView.getMenu().getItem(0).setChecked(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {

                super.onDrawerOpened(v);
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    public void onBackPressed() {


        finishAffinity();
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        if (requestCode == 1) {

            navigationView.getMenu().getItem(0).setChecked(true);

            if (resultCode == 1) {

                finish();
            }

        }
        if (requestCode == 2) {

            navigationView.getMenu().getItem(0).setChecked(true);

            if (resultCode == 1) {

                finish();
            }

        }
        if (requestCode == 3) {

            navigationView.getMenu().getItem(0).setChecked(true);

            if (resultCode == 1) {

                finish();
            }

        }

        if (requestCode == 4) {

            navigationView.getMenu().getItem(0).setChecked(true);

            if (resultCode == 1) {

                finish();
            }

        }

        if (requestCode == 5) {

            navigationView.getMenu().getItem(0).setChecked(true);

            if (resultCode == 1) {

                finish();
            }

        }

    }

    private void updateApk () {


        if (Build.BRAND.toUpperCase().equals("NEWLAND")) {

            String url = "http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/update/appinoveplastika.apk";
            // String url = "https://dev.inforcavado.com/Picking/app-tokens.apk";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

            finish();
            return;
        }

        //get destination to update file and set Uri
        //TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
        //aplication with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better
        //solution, please inform us in comment
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = "appinoveplastika.apk";
        destination += fileName;
        final Uri uri = Uri.parse("file://" + destination);

        //Delete update file if exists
        File file = new File(destination);
        if (file.exists())
            //file.delete() - test this, I think sometimes it doesnt work
            file.delete();

        //get url of app on server
        String url ="http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/"+fileName;


        //set downloadmanager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("A actualizar aplicação...aguarde...");
        request.setTitle("Atualizar aplicação");

        //set destination
        request.setDestinationUri(uri);

        // get download service and enqueue file
        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = manager.enqueue(request);

        //set BroadcastReceiver to install app when .apk is downloaded
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                install.setDataAndType(uri,
                        manager.getMimeTypeForDownloadedFile(downloadId));
                startActivity(install);

                unregisterReceiver(this);
                finish();
            }
        };
        //register receiver for when .apk download is compete
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    public void verificaVersao() {


        RequestQueue queue = Volley.newRequestQueue(thisActivity);

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LerVersaoApp?").buildUpon()
                .build();

        progDailog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progDailog.dismiss();

                        String tmpResposta = response.substring(1, response.length() - 1);

                        int versionCode = Integer.parseInt(MYFILEVERSION);

                        if (versionCode < Integer.parseInt(tmpResposta))
                        {
                            Dialogos.dialogoInfo("Informação","Existe uma nova versão da aplicação. Execute a " +
                                    "actualização através da opção disponível no menu lateral",10.0,MainActivity_Lista.this, false);

                        }
                        else {

                            adapterMenuList = new ArrayAdapterMenuList(thisActivity,0,menuMain);
                            adapterMenuList.notifyDataSetChanged();
                            listViewMenuList.setAdapter(adapterMenuList);

                            carregaParametros(thisActivity);
                        }


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progDailog.dismiss();
                Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage(),4,thisActivity,false);

            }
        });

        // Add the request to the RequestQueue.
        System.out.println(uri.toString());

        Integer mRequestTimeout = 5 * 1000;
        Integer mMaxNumRetries = 1;

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(mRequestTimeout,
                mMaxNumRetries,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);

    }


    public static void carregaParametros(final Activity activity){

        final ProgressDialog progDailog;

        progDailog = new ProgressDialog(activity);
        progDailog.setMessage("Aguarde");
        progDailog.setIndeterminate(true);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(false);

        RequestQueue queue = Volley.newRequestQueue(activity);

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/ObterParametros?").buildUpon()
                .build();

        progDailog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    Gson gson = new Gson();

                    ArrayList<DataModelParams> paramsArrayList;
                    paramsArrayList = gson.fromJson(response,new TypeToken<ArrayList<DataModelParams>>() {}.getType());

                    for (int i = 0; i < paramsArrayList.size(); i++) {


                        if (paramsArrayList.get(i).getDescricao().equals("obrigaturnoep")) {
                            Globals.PARAM_OBRIGA_TURNO_EP = paramsArrayList.get(i).getValorBoolean();
                        }

                        if (paramsArrayList.get(i).getDescricao().equals("turnomaxhextra")) {
                            Globals.PARAM_TURNOS_MAX_HEXTRA = paramsArrayList.get(i).getValorDouble();
                        }
                    }

                    System.out.println(Globals.PARAM_OBRIGA_TURNO_EP);

                    progDailog.dismiss();

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progDailog.dismiss();
                Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage(),4,activity,false);
            }
        });

        // Add the request to the RequestQueue.
        System.out.println(uri.toString());



        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),
                0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);

    }

    private void testConnection() {


        AsyncRequest verificaligacao = new AsyncRequest(thisActivity, 0);

        verificaligacao.setOnAsyncRequestComplete(new AsyncRequest.OnAsyncRequestComplete() {
            @Override
            public void asyncResponse(String response, int op) {

                if (response != null) {

                    if (response.equals(TRUE)) {

                        verificaVersao();

                    } else {
                        Dialogos.dialogoErro("Erro de conexão ao serviço","Não consegui comunicar com o serviço " +
                                Globals.getInstance().getCaminho(),5,thisActivity,false);
                    }


                } else {

                    Dialogos.dialogoErro("Erro de conexão ao serviço","Não consegui comunicar com o serviço " +
                            Globals.getInstance().getCaminho(),5,thisActivity,false);

                }

            }
        });

        verificaligacao.execute("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/testeLigacao");

    }

    @Override
    public void asyncResponse(String response, int op) {

    }
}
