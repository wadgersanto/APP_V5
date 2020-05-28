package gnotus.inoveplastika;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ConfigsActivity extends AppCompatActivity implements AsyncRequest.OnAsyncRequestComplete {

    private static final String MYFILE = "configs";
    private static final String CAMINHO = "caminho";
    private static final String LASTUSER = "lastuser";
    private static final String BOSTAMP_CARGA = "bostampcarga";
    private static final String TRUE = "true";
    private static final String FABRICA = "fabrica";

    SharedPreferences sharedpreferences;
    private TextInputLayout textInputLayout_caminho;
    private EditText editText_caminho;
    private ImageView imageView_info;
    private boolean FLAG = false;

    private TextView textViewFabrica;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configs);

        // Dialogos.showToast(ConfigsActivity.this,"Manufacturer: "+ Build.MANUFACTURER,2.0,16);
        //Dialogos.showToast(ConfigsActivity.this,"Model: "+ Build.MODEL,3.0,16);
        Dialogos.showToast(ConfigsActivity.this,"Brand: "+ Build.BRAND,4.0,16);

        Toolbar toolbar;
        Button button_guardar, button_teste;

        sharedpreferences = getSharedPreferences(MYFILE, Context.MODE_PRIVATE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.config_toolbar_titulo));
        toolbar.setTitleTextColor(Color.WHITE);


        textInputLayout_caminho = (TextInputLayout) findViewById(R.id.textInputLayout_config_caminho);
        editText_caminho        = (EditText)        findViewById(R.id.editText_config_caminho);
        imageView_info          = (ImageView)       findViewById(R.id.imageView_config_status);
        button_teste            = (Button)          findViewById(R.id.button_config_teste);
        button_guardar          = (Button)          findViewById(R.id.button_config_save);

        // carregar o caminho que está por defeito carregado

        editText_caminho.setText(sharedpreferences.getString(CAMINHO, ""));
        editText_caminho.addTextChangedListener(new MyTextWatcher(editText_caminho));

        textViewFabrica = findViewById(R.id.TextViewFabrica);

        textViewFabrica.setText(sharedpreferences.getString(FABRICA, ""));

        textViewFabrica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog alertDialogFabrica;

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ConfigsActivity.this, android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add(Globals.FACTORY_BARCELOS);
                arrayAdapter.add(Globals.FACTORY_GAIA);

                AlertDialog.Builder builder = new AlertDialog.Builder(ConfigsActivity.this);
                builder.setTitle("Escolha a fábrica");

                builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        textViewFabrica.setText(arrayAdapter.getItem(which));

                        SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
                        SharedPreferences.Editor prefsEditor;
                        prefsEditor = myPrefs.edit();
                        prefsEditor.putString("fabrica", arrayAdapter.getItem(which));
                        prefsEditor.commit();

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(FABRICA,arrayAdapter.getItem(which));
                        editor.apply();

                        Globals.getInstance().setSelectedFactory(sharedpreferences.getString("fabrica",arrayAdapter.getItem(which)));


                    }
                });


                alertDialogFabrica = builder.create();
                alertDialogFabrica.setCancelable(true);
                alertDialogFabrica.show();


            }
        });






        button_teste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (validaCaminho()) {
                    AsyncRequest verificaligacao = new AsyncRequest(ConfigsActivity.this, 0);
                    verificaligacao.execute("http://" + editText_caminho.getText().toString() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/testeLigacao");
                }

            }
        });


        button_guardar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (sharedpreferences.getString(FABRICA,"").isEmpty())
                {
                    Dialogos.dialogoErro("Dados em falta","É necessário selecionar a fábrica",4,ConfigsActivity.this,false);
                    return;
                }

                if ((validaCaminho()) && (FLAG))
                {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(CAMINHO, editText_caminho.getText().toString());
                    editor.putString(LASTUSER, "");
                    editor.putString(BOSTAMP_CARGA,"");
                    editor.apply();

                    Globals.getInstance().setCaminho(editText_caminho.getText().toString());

                    Intent intent = new Intent(ConfigsActivity.this, MainActivity_Lista.class);
                    startActivity(intent);
                    finish();

                } else {

                    textInputLayout_caminho.setError(getResources().getString(R.string.config_editText_caminho_por_validar));
                    requestFocus(editText_caminho);
                }


            }
        });

    }

    private boolean validaCaminho() {

        if (editText_caminho.getText().toString().trim().isEmpty()) {

            textInputLayout_caminho.setError(getResources().getString(R.string.config_editText_preencher_caminho));
            requestFocus(editText_caminho);

            return false;

        } else {

            textInputLayout_caminho.setErrorEnabled(false);

        }

        return true;

    }

    private void requestFocus(View view) {

        if (view.requestFocus()) {

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

    }

    @Override
    public void asyncResponse(String response, int op) {

        if (op == 0) {


            if (response != null) {

                if (response.equals(TRUE)) {

                    FLAG = true;
                    imageView_info.setImageResource(R.mipmap.ic_ok);
                    Log.e("", response);


                } else {

                    FLAG = false;
                    imageView_info.setImageResource(R.mipmap.ic_notok);
                    textInputLayout_caminho.setError(getResources().getString(R.string.config_editText_caminho_invalido));
                    requestFocus(editText_caminho);

                }


            } else {

                textInputLayout_caminho.setError(getResources().getString(R.string.config_editText_caminho_invalido));
                requestFocus(editText_caminho);

                Log.e("", "Erro a obter informação do servidor");
                Toast.makeText(this, "Erro a obter informação do servidor.", Toast.LENGTH_SHORT).show();


            }

        }

    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {

            this.view = view;
        }


        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            switch (view.getId()) {

                case R.id.editText_config_caminho:

                    validaCaminho();

                    break;

            }

        }

        public void afterTextChanged(Editable editable) {


        }


    }

}



