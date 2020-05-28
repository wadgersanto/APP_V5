package gnotus.inoveplastika.QualityControl;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import gnotus.inoveplastika.API.Logistica.QualityControlItemsWs;
import gnotus.inoveplastika.DataModels.DataModelQualityControl;
import gnotus.inoveplastika.Dialogos;
import gnotus.inoveplastika.EditTextBarCodeReader;
import gnotus.inoveplastika.R;
import gnotus.inoveplastika.ArrayAdapters.ArrayAdapterDataModelControlQuality;
import gnotus.inoveplastika.ViewsBackStackManager;

import static gnotus.inoveplastika.Dialogos.showToast;
import static gnotus.inoveplastika.EditTextOps.checkEditTextIsEmpty;

public class QualityActivity extends AppCompatActivity {

    private TextView txtTitulo, txt_resul_pesq;
    private ListView listview;
    private Button   btnFiltrar;
    private Toolbar toolbar;
    private ViewsBackStackManager viewsBackStackManager;

    private MenuItem menuItemRefresh = null, menuItemOptions = null, menuItemSearch = null;

    private ArrayList<DataModelQualityControl> controlArrayList;
    private ArrayAdapterDataModelControlQuality arrayAdapterDataModelControlQuality;
    ArrayList<DataModelQualityControl> dataModelQualityControlsSearch;

    private DataModelQualityControl selItemDataModelQualityControl;

    private String checkOk = "";



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qualidade);

        initialize();
        initializeClasses();
        initializeToobar();


        requeryQualidadeControlItems(true);
//        alertDialog();


        btnFiltrar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                alertDialogFiltro();
            }
        });


    }

    private void alertDialogOperador()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Operador");
        final EditText input = new EditText(this);
        builder.setCancelable(false);
        builder.setView(input);
        input.setInputType(InputType.TYPE_NULL);
        input.requestFocus();
        input.setMaxLines(1);
        input.setHint("Introduza o operador");

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
                finish();

            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        input.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEND || ( event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                {
                    alertDialog.dismiss();
                    boolean check = checkEditTextIsEmpty(input);
                    if(check)
                    {
//                        requeryOperador(input.getText().toString().trim());


//                        requeryQualidadeControlItems(true);
                    }
                    else
                    {
                        Dialogos.dialogoInfo("Info", "Introduza o operador", 3.0,QualityActivity.this,false);
                    }


                }

                return false;
            }
        });



    }

    private void alertDialogFiltro()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filtrar");
        final EditText input = new EditText(this);
        builder.setView(input);
        input.requestFocus();
        input.setMaxLines(1);
        input.setHint("Introduza referência");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {

                if(checkEditTextIsEmpty(input))
                    requeryQualidadeControlItems(true);
                else
                    getFiltration(input.getText().toString().trim());

                dialogInterface.dismiss();

            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        EditTextBarCodeReader editTextBarCodeReader = new EditTextBarCodeReader(input,QualityActivity.this);
        input.setLongClickable(true);
        input.setShowSoftInputOnFocus(true);
        input.setImeOptions(EditorInfo.IME_ACTION_SEND);

        editTextBarCodeReader.setOnGetScannedTextListener(new EditTextBarCodeReader.OnGetScannedTextListener()
        {
            @Override
            public void onGetScannedText(String scannedText, EditText editText)
            {
                alertDialog.cancel();
                if(checkEditTextIsEmpty(input))
                {
                    arrayAdapterDataModelControlQuality = new ArrayAdapterDataModelControlQuality(QualityActivity.this, controlArrayList);
                    arrayAdapterDataModelControlQuality.notifyDataSetChanged();
                    listview.setAdapter(arrayAdapterDataModelControlQuality);
                }
                else
                    getFiltration(input.getText().toString().trim());
            }
        });

    }

    private void getFiltration(String filtro)
    {
        dataModelQualityControlsSearch.clear();

        for(DataModelQualityControl control : controlArrayList)
        {
            if(control.getRef().trim().toLowerCase().startsWith(filtro.trim().toLowerCase()))
                dataModelQualityControlsSearch.add(control);
        }

        if(dataModelQualityControlsSearch.size() == 0)
            showToast(QualityActivity.this,"Sem resultado", 3.0,18);
        else
        {
            txt_resul_pesq.setVisibility(View.VISIBLE);
            txt_resul_pesq.setText("Resultado de pesquisa '"+filtro+"'");
            arrayAdapterDataModelControlQuality = new ArrayAdapterDataModelControlQuality(QualityActivity.this, dataModelQualityControlsSearch);
            arrayAdapterDataModelControlQuality.notifyDataSetChanged();
            listview.setAdapter(arrayAdapterDataModelControlQuality);
        }

    }




    private void requeryQualidadeControlItems(final boolean showView)
    {
        QualityControlItemsWs qualityControlItemsWs = new QualityControlItemsWs(this);
        qualityControlItemsWs.setOnSupplierOpenReception(new QualityControlItemsWs.OnQualityControlItems()
        {
            @Override
            public void onWsResponseSupplierOpenItems(ArrayList<DataModelQualityControl> dataModelQualityControls)
            {
                if(dataModelQualityControls.size() != 0)
                {
                    controlArrayList = dataModelQualityControls;
                    arrayAdapterDataModelControlQuality = new ArrayAdapterDataModelControlQuality(QualityActivity.this, controlArrayList);
                    arrayAdapterDataModelControlQuality.notifyDataSetChanged();
                    listview.setAdapter(arrayAdapterDataModelControlQuality);

                    if(showView)
                    {
                        viewsBackStackManager.setActiveView(BackStackViews.QUALITY_CONTROL);
                        viewsBackStackManager.addViewToBackStack(viewsBackStackManager.getActiveView());
                        Log.d("ActiveView", viewsBackStackManager.getActiveView());
                        Log.d("StackViews",viewsBackStackManager.getViewStack()+"");
                        showQualidadeControlItems();
                    }


                }
                else
                {
                    Dialogos.dialogoInfo("Aviso","Sem resultado",  3.0, QualityActivity.this,false);
                }
            }
        });
        qualityControlItemsWs.execute();

    }

    private void showQualidadeControlItems()
    {
        txt_resul_pesq.setVisibility(View.GONE);

        txtTitulo.setVisibility(View.VISIBLE);
        listview.setVisibility(View.VISIBLE);
        btnFiltrar.setVisibility(View.VISIBLE);

        txtTitulo.setText(StringName.RECEPTIONS_TO_EVALUATE);

        arrayAdapterDataModelControlQuality.notifyDataSetChanged();
        listview.setAdapter(arrayAdapterDataModelControlQuality);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                selItemDataModelQualityControl = controlArrayList.get(i);
                
                alertDialogControleQualidade();

            }
        });



    }

    private void alertDialogControleQualidade()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Controle de Qualidade");

        final View view                 = getLayoutInflater().inflate(R.layout.activity_control_qual, null);
        final CheckBox checkBoxOk       = view.findViewById(R.id.checkBoxOk);
        final CheckBox checkBoxNoOk     = view.findViewById(R.id.checkBoxNoOk);
        EditText edt_comentario         = view.findViewById(R.id.edt_comentario);
        TextView textView_ref           = view.findViewById(R.id.textView_ref);

        edt_comentario.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        builder.setView(view);




        textView_ref.setText(selItemDataModelQualityControl.getRef()+" - "+ selItemDataModelQualityControl.getDesign());

        checkBoxOk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked)
            {
                if(checked)
                {
                    checkBoxOk.setChecked(true);
                    checkOk = "true";
                    checkBoxNoOk.setChecked(false);
                }
                else
                {
                    checkOk = "";
                    checkBoxOk.setChecked(false);
                }

            }
        });

        checkBoxNoOk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked)
            {
                if(checked)
                {
                    checkBoxNoOk.setChecked(true);
                    checkBoxOk.setChecked(false);
                    checkOk = "false";
                }
                else
                {
                    checkOk = "";
                    checkBoxNoOk.setChecked(false);
                }

            }
        });



        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
                if(checkOk.equals("true"))
                {
                    Toast.makeText(QualityActivity.this, "ok", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(checkOk.equals("false"))
                        Toast.makeText(QualityActivity.this, "não ok", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(QualityActivity.this, "nem um nem outro", Toast.LENGTH_SHORT).show();

                }
                checkOk = "";



            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }








    private void initialize()
    {
        txtTitulo         = findViewById(R.id.txtTitulo);
        listview          = findViewById(R.id.listview);
        btnFiltrar        = findViewById(R.id.btnFiltrar);
        toolbar           = findViewById(R.id.toolbar);
        txt_resul_pesq    = findViewById(R.id.txt_resul_pesq);

        txtTitulo.setVisibility(View.GONE);
        listview.setVisibility(View.GONE);
        btnFiltrar.setVisibility(View.GONE);

    }

    private void initializeClasses()
    {
        controlArrayList               = new ArrayList<>();
        viewsBackStackManager          = new ViewsBackStackManager();
        dataModelQualityControlsSearch = new ArrayList<>();

    }

    private void initializeToobar()
    {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                myOnBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(QualityActivity.StringName.TITLE_QUALITY_CONTROL);

        menuItemRefresh                  = toolbar.findViewById(R.id.menu_item_refresh);
        menuItemOptions                  = toolbar.findViewById(R.id.menu_item_list);
        menuItemSearch                   = toolbar.findViewById(R.id.menu_item_search);
    }


    public void myOnBackPressed()
    {

        viewsBackStackManager.removeLastViewOnStack(); // removemos a ultima view do nosso arrayList

        if(viewsBackStackManager.getViewStack().size() == 0)
        {
            finish();
            return;
        }

        Toast toast = Toast.makeText(getApplicationContext(),"Sem nenhuma activity", Toast.LENGTH_SHORT);
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(20);
        toast.show();

    }

    private class BackStackViews
    {
        private final static String QUALITY_CONTROL     = "Controlo de qualidade";

    }


    private class StringName
    {

        private final static String TITLE_QUALITY_CONTROL   = "Controlo de Qualidade";
        private final static String RECEPTIONS_TO_EVALUATE  = "Receções a avaliar";
    }

}













