package gnotus.inoveplastika;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity  implements AsyncRequest.OnAsyncRequestComplete
        , View.OnClickListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    private Activity thisActivity = MainActivity.this;

    Bundle bundleSent = new Bundle();

    public static String MYFILEVERSION = "18091700";

    public static final String MYFILE = "configs";
    public static final String CAMINHO = "caminho";
    public static final String LASTUSER = "lastuser";
    private static final String USERID = "userid";
    private static final String BOSTAMP_CARGA = "bostampcarga";


    AlertDialog alertDialogOpcoes;

    private Button btn_transferencias,btn_producao, btn_inventarios, btn_consultas, btn_expedition;

    ProgressDialog bar;

    CharSequence[] OpcoesExpedicao = {"Picking","Devolução Picking"};

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(MYFILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(USERID, "");
        editor.apply();

        if (sharedpreferences.contains(USERID)) {

            Globals.getInstance().setUserid("");
        }

        if(sharedpreferences.contains(BOSTAMP_CARGA)) {

            Globals.getInstance().setTrfCargaBostamp(sharedpreferences.getString(BOSTAMP_CARGA,""));

        }

        btn_transferencias = (Button) findViewById(R.id.btn_main_transf);
        btn_transferencias.setOnClickListener(this);

        btn_producao = (Button) findViewById(R.id.btn_main_producao);
        btn_producao.setOnClickListener(this);

        btn_inventarios = (Button) findViewById(R.id.btn_main_inventarios);
        btn_inventarios.setOnClickListener(this);

        btn_consultas = (Button) findViewById(R.id.btn_main_consultas);
        btn_consultas.setOnClickListener(this);

        btn_expedition = (Button) findViewById(R.id.btn_main_Picking);
        btn_expedition.setOnClickListener(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int versionCode = BuildConfig.VERSION_CODE;

        // String mTitulo = "Versão "+versionCode;
        String mTitulo = "Versão "+MYFILEVERSION;

        getSupportActionBar().setTitle(mTitulo);
        toolbar.setTitleTextColor(Color.WHITE);

        initNavigationDrawer();

        verificaVersao();

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

        //this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
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

        //get destination to update file and set Uri
        //TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
        //aplication with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better
        //solution, please inform us in comment
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = "appinoveplastika-debug.apk";
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

    public void asyncResponse(String response, int op) {

        if (response == null) {
            Dialogos.dialogoErro("Erro ","Ocorreu um erro no no processamento da informação do webservice",5,MainActivity.this,false);
            return;
        }

        // Pedido para ler a versao da aplicação e atualizar stock disponivel em modo de carga - quando estamos a editar uma linha
        if (op == 0) {

            String tmpResposta = response.substring(1, response.length() - 1);
            // int versionCode = BuildConfig.VERSION_CODE;
            int versionCode = Integer.parseInt(MYFILEVERSION);

            if (versionCode < Integer.parseInt(tmpResposta))
            {
                Dialogos.dialogoInfo("Informação","Existe uma nova versão da aplicação. Execute a " +
                        "actualização através da opção disponível no menu lateral",10.0,MainActivity.this, false);
                btn_transferencias.setEnabled(false);
                btn_producao.setEnabled(false);
                btn_inventarios.setEnabled(false);
                btn_consultas.setEnabled(false);
                btn_expedition.setEnabled(false);

            }

        }

    }


    public void verificaVersao() {

        AsyncRequest processaPedido = new AsyncRequest(MainActivity.this, 0);

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LerVersaoApp?").buildUpon()
                .build();

        processaPedido.execute(builtURI_processaPedido.toString());

    }

}
