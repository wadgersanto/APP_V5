package gnotus.inoveplastika.ComponentesNOK;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import gnotus.inoveplastika.Globals;
import gnotus.inoveplastika.R;

import static gnotus.inoveplastika.Dialogos.showToast;
import static gnotus.inoveplastika.EditTextOps.hideKeyboard;
import static gnotus.inoveplastika.EditTextOps.showKeyboard;
import static gnotus.inoveplastika.PickingBoxes.PickingBoxesActivity.hidekeyboard;

public class ComponentesNOKActivity extends AppCompatActivity
{

    private Toolbar toolbar;

    private ConstraintLayout constraintLayout_codigo, constraintLayout_dados;

    private EditText editText_leitura;

    private TextView textView_titulo_ref, textView_referencia, textView_lote_titulo, textView_lote, textView_of_titulo, textView_of;

    private Button btnSubmeter, btnRef, btnOF, btnLote;

    private ListView list;

    private final String REFERENCIA = "Referência";
    private final String OF         = "OF";
    private final String LOTE       = "Lote";

    private String lote ="";
    private String of ="";
    private String ref="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_componentes_nok);
        hidekeyboard(ComponentesNOKActivity.this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        toolbar                   = findViewById(R.id.toolbar);
        constraintLayout_codigo   = findViewById(R.id.constraintLayout_codigo);
        constraintLayout_dados    = findViewById(R.id.constraintLayout_dados);
        editText_leitura          = findViewById(R.id.editTextLeitura);
        textView_titulo_ref       = findViewById(R.id.textView_carga_titulo_ref);
        textView_referencia       = findViewById(R.id.textView_2a);
        textView_lote_titulo      = findViewById(R.id.textView_lote_titulo);
        textView_lote             = findViewById(R.id.textView_3a);
        textView_of_titulo        = findViewById(R.id.textView_of_titulo);
        textView_of               = findViewById(R.id.textView_of);
        btnSubmeter               = findViewById(R.id.btnSubmeter);
        list                      = findViewById(R.id.list);
        btnRef                    = findViewById(R.id.btnRef);
        btnOF                     = findViewById(R.id.btnOF);
        btnLote                   = findViewById(R.id.btnLote);

        btnSubmeter.setBackgroundColor(Color.parseColor(Globals.getInstance().getDefaultToolbarColour()));
        btnSubmeter.setEnabled(false);
        editText_leitura.setImeOptions(EditorInfo.IME_ACTION_SEND);
        editText_leitura.setMaxLines(1);

        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor(Globals.getInstance().getDefaultToolbarColour()));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Componentes NOK");


        readReferencia();


        textView_referencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText_leitura.setEnabled(true);
                btnSubmeter.setEnabled(false);
                textView_referencia.setText("");
                editText_leitura.setHint("Ler referência");
//                editText_leitura.setFocusable(true);
                editText_leitura.requestFocus();
                readReferencia();


            }
        });

        textView_of.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText_leitura.setEnabled(true);
                btnSubmeter.setEnabled(false);
                textView_of.setText("");
                editText_leitura.setHint("Ler OF");
//                editText_leitura.setFocusable(true);
                editText_leitura.requestFocus();
                readOF();
            }
        });

        textView_lote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText_leitura.setEnabled(true);
                btnSubmeter.setEnabled(false);
                textView_lote.setText("");
                editText_leitura.setHint("Ler Lote");
//                editText_leitura.setFocusable(true);
                editText_leitura.requestFocus();
                readLote();
            }
        });


        btnLote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
//                hidekeyboard(ComponentesNOKActivity.this);
                lote = textView_lote.getText().toString().trim();
                alertDialogEdit(LOTE);

            }
        });
        btnOF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
//                hidekeyboard(ComponentesNOKActivity.this);
                of = textView_of.getText().toString().trim();
                alertDialogEdit(OF);

            }
        });
        btnRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
//                hidekeyboard(ComponentesNOKActivity.this);
                ref = textView_referencia.getText().toString().trim();
                alertDialogEdit(REFERENCIA);

            }
        });

        btnSubmeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(ComponentesNOKActivity.this);
                builder.setTitle("Operador");
                builder.setCancelable(false);
                final EditText input = new EditText(ComponentesNOKActivity.this);
                input.setHint("introduza o operador");
                builder.setView(input);
                input.setMaxLines(1);

                showKeyboard(ComponentesNOKActivity.this);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i)
                    {
                        if(!input.getText().toString().trim().isEmpty() || !input.getText().toString().trim().equals("0"))
                        {
                            hideKeyboard(ComponentesNOKActivity.this, input);

                        }
                        showToast(ComponentesNOKActivity.this, "introduza o operador",2.0,20);
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        hideKeyboard(ComponentesNOKActivity.this, input);
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

    }//fim onCreate


    public void alertDialogEdit(final String showAlertDialog)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(ComponentesNOKActivity.this);
        builder.setTitle(showAlertDialog);
        builder.setCancelable(false);
        final EditText input = new EditText(ComponentesNOKActivity.this);
        input.setImeOptions(EditorInfo.IME_ACTION_SEND);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("introduza "+showAlertDialog);
        builder.setView(input);
        input.setMaxLines(1);

        setHint(showAlertDialog, input);

        showKeyboard(ComponentesNOKActivity.this);


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i)
            {
                if(!input.getText().toString().trim().isEmpty() || !input.getText().toString().trim().equals("0"))
                {
                   switch (showAlertDialog)
                   {
                       case REFERENCIA:
                           hideKeyboard(ComponentesNOKActivity.this,input);
                           textView_referencia.setText(input.getText().toString().trim());
                           dialogInterface.dismiss();
                           edtEnable();
                           break;

                       case LOTE:
                           hideKeyboard(ComponentesNOKActivity.this,input);
                           textView_lote.setText(input.getText().toString().trim());
                           dialogInterface.dismiss();
                           edtEnable();
                           break;

                       case OF:
                           hideKeyboard(ComponentesNOKActivity.this,input);
                           textView_of.setText(input.getText().toString().trim());
                           dialogInterface.dismiss();
                           edtEnable();
                           break;
                   }
                }
                else
                    showToast(ComponentesNOKActivity.this, "introduza "+ showAlertDialog,2.0,20);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hideKeyboard(ComponentesNOKActivity.this,input);
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    public void setHint(String showAlertDialog, EditText editText)
    {
        switch (showAlertDialog)
        {
            case REFERENCIA:
                if(!ref.isEmpty())
                {
                    editText.setText(ref);
                    editText.setSelection(0,editText.getText().toString().length());
                }
                break;

            case LOTE:
                if(!lote.isEmpty())
                {
                    editText.setText(lote);
                    editText.setSelection(0,editText.getText().toString().length());
                }
                break;

            case OF:
                if(!of.isEmpty())
                {
                    editText.setText(of);
                    editText.setSelection(0,editText.getText().toString().length());
                }

                break;
        }
    }
    public void readReferencia()
    {
        if(textView_referencia.getText().toString().trim().isEmpty())
        {
            editText_leitura.setHint("Ler referência");
            textView_referencia.requestFocus();

            editText_leitura.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent)
                {
                    if(actionId == EditorInfo.IME_ACTION_SEND || keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                    {
                        if(editText_leitura.getText().toString().trim().isEmpty() || editText_leitura.getText().toString().trim().equals("0"))
                        {
                            editText_leitura.setHint("Ler referência");
                            textView_referencia.requestFocus();
                            return false;
                        }
                        textView_referencia.setText(editText_leitura.getText().toString().trim());
                        editText_leitura.setText("");
                        readOF();
                        edtEnable();
                    }

                    return true;
                }
            });
        }
    }
    public void edtEnable()
    {

        if(  !textView_referencia.getText().toString().trim().isEmpty() &&
             !textView_lote.getText().toString().trim().isEmpty()    &&
             !textView_of.getText().toString().trim().isEmpty())
        {
            btnSubmeter.setEnabled(true);
            hideKeyboard(ComponentesNOKActivity.this, editText_leitura);
        }
    }

    public void readOF()
    {

        if(textView_of.getText().toString().trim().isEmpty())
        {
            editText_leitura.setHint("Ler OF");
            textView_of.requestFocus();

            editText_leitura.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent)
                {
                    if(actionId == EditorInfo.IME_ACTION_SEND || keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                    {
                        if(editText_leitura.getText().toString().trim().isEmpty() || editText_leitura.getText().toString().trim().equals("0"))
                        {
                            editText_leitura.setHint("Ler OF");
                            textView_of.requestFocus();
                            return false;
                        }
                        textView_of.setText(editText_leitura.getText().toString().trim());
                        editText_leitura.setText("");
                        readLote();
                        edtEnable();
                    }
                    return true;
                }
            });
        }

    }


    public void  readLote()
    {
        if (textView_lote.getText().toString().trim().isEmpty())
        {
            editText_leitura.setHint("Ler Lote");
            textView_lote.requestFocus();

            editText_leitura.setOnEditorActionListener(new TextView.OnEditorActionListener()
            {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent)
                {
                    if(actionId == EditorInfo.IME_ACTION_SEND || keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                    {
                        if (editText_leitura.getText().toString().trim().isEmpty() || editText_leitura.getText().toString().trim().equals("0"))
                        {
                            editText_leitura.setHint("Ler Lote");
                            textView_lote.requestFocus();
                            return false;
                        }
                        textView_lote.setText(editText_leitura.getText().toString().trim());
                        editText_leitura.setText("");
                        edtEnable();
                    }

                    return true;
                }

            });

        }
    }

}//fim class ComponentesNOKActivity
