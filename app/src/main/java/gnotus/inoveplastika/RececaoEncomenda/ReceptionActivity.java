package gnotus.inoveplastika.RececaoEncomenda;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.ArrayList;

import gnotus.inoveplastika.API.Logistica.GetDossierWs;
import gnotus.inoveplastika.API.Logistica.RegisterSupplierReceptionWs;
import gnotus.inoveplastika.API.Logistica.SupplierOpenItemsWs;
import gnotus.inoveplastika.API.Logistica.SupplierOpenReceptionWs;
import gnotus.inoveplastika.API.Logistica.SuppliersWithOpenOrdersWs;
import gnotus.inoveplastika.API.Phc.CreateBoWs;
import gnotus.inoveplastika.ArrayAdapters.ArrayAdapterDataModelSuppliersItems;
import gnotus.inoveplastika.ArrayAdapters.ArrayAdapterDataModelSuppliersOpenReceptions;
import gnotus.inoveplastika.ArrayAdapters.ArrayAdapterDataModelSuppliersWithOpenOrders;
import gnotus.inoveplastika.ArrayAdapters.ArrayAdapterDataModelValidate;
import gnotus.inoveplastika.ArrayAdapters.ArrayAdapterDossierLines;
import gnotus.inoveplastika.DataModels.DataModelBi;
import gnotus.inoveplastika.DataModels.DataModelBo;
import gnotus.inoveplastika.DataModels.DataModelEditActivity;
import gnotus.inoveplastika.DataModels.DataModelValidate;
import gnotus.inoveplastika.DataModels.DataModelWsResponse;
import gnotus.inoveplastika.Dialogos;
import gnotus.inoveplastika.EditTextOps;
import gnotus.inoveplastika.R;
import gnotus.inoveplastika.ViewsBackStackManager;

import static gnotus.inoveplastika.Dialogos.dialogoInfo;
import static gnotus.inoveplastika.Dialogos.retiraDecimais;
import static gnotus.inoveplastika.Dialogos.showToast;
import static gnotus.inoveplastika.PickingBoxes.PickingBoxesActivity.hidekeyboard;
import static gnotus.inoveplastika.PickingBoxes.PickingBoxesActivity.showKeyboard;

public class ReceptionActivity extends AppCompatActivity {

    private ListView listView;

    private TextView txtconstraintLayoutTitulo, textViewA1, textViewA2, textViewB1, textViewB2,textViewC1,textViewC2;
    private ConstraintLayout constraintLayoutTitulo, constraintLayout1, constraintLayout1a, constraintLayout1b, constraintLayoutC;
    private Button button;

    private ProgressDialog progressDialog;

    private ViewsBackStackManager viewsBackStackManager;

    private DataModelEditActivity dataModelEditActivity;

    private Toolbar toolbarReception;

    private MenuItem menuItemRefresh = null, menuItemOptions = null, menuItemSearch = null;

    private ArrayList<DataModelBo>       listDataModelSuppliersWithOpenOrders;
    private ArrayList<DataModelBo>       listDataModelSuppliersReceptions;
    private ArrayList<DataModelBi>       listDataModelSupplierItems;
    private ArrayList<DataModelValidate> arrayListObjModelValidate;
    private ArrayList<String>            arrayValidateList;
    private ArrayList<String>            arrayOpcoes;


    private ArrayAdapterDataModelSuppliersWithOpenOrders adapterDataModelSuppliersWithOpenOrders;
    private ArrayAdapterDataModelSuppliersItems adapterDataModelSuppliersItems;
    private ArrayAdapterDataModelSuppliersOpenReceptions adapterDataModelSuppliersOpenReceptions;
    private ArrayAdapterDossierLines arrayAdapterDossierLines;
    private ArrayAdapterDataModelValidate arrayAdapterDataModelValidate;

    private DataModelBo         selItemSuppliersWithOpenOrders;
    private DataModelBo         selItemSuppliersOpenReceptions;
    private DataModelBi         selItemSuppliersOpenItems;
    private DataModelValidate   selValidate;

    private String fabrica, lote = "", date;

    private int ndos;

    private FrameLayout fragmentReception, fragmentVerConformidade;







    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reception);

        SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
        fabrica                   = myPrefs.getString("fabrica", "");

        if(fabrica.equals(StringName.FABRICA_BARCELOS))
            ndos = 19;

        if(fabrica.equals(StringName.FABRICA_GAIA))
            ndos = 219;

        initialize();
        initializeToobar();
        progressDialog();
        initializeClasses();


        teste();






        requerySuppliersWithOpenOrders(true);

    }

    private void teste()
    {


        DataModelValidate objModelValidate = new DataModelValidate();
        objModelValidate.setDescricao("Verificação da conformidade das embalagens (sujidade, humidade e danos)");
        objModelValidate.setTipo("L");
        objModelValidate.setObrigatorio("Sim");
        objModelValidate.setLogicYes("Ok");
        objModelValidate.setLogicNo("Não Ok");
        objModelValidate.setObservacao("Obs: ok não ok");

        DataModelValidate objModelValidate1 = new DataModelValidate();
        objModelValidate1.setDescricao("Pontuação da embalagem");
        objModelValidate1.setTipo("N");
        objModelValidate1.setDecimais(1);
        objModelValidate1.setObrigatorio("Sim");
        objModelValidate1.setLimInf(1);
        objModelValidate1.setLimSup(10);
        objModelValidate1.setObservacao("Limite é de 1 a 10");

        DataModelValidate objModelValidate2 = new DataModelValidate();
        objModelValidate2.setDescricao("Avalação da qualidade do produto");
        objModelValidate2.setTipo("T");
        objModelValidate2.setObrigatorio("Não");
        objModelValidate2.setListaValores("MUITO EXCELENTE MUITO EXCELENTE EXECELENTE, EXECELENTE, MUITO BOM, BOM, RAZOÁVEL, MAU, MUITO MAU");

        arrayListObjModelValidate.add(objModelValidate);
        arrayListObjModelValidate.add(objModelValidate1);
        arrayListObjModelValidate.add(objModelValidate2);

       for( DataModelValidate modelValidate : arrayListObjModelValidate)
       {
           if(modelValidate.getTipo().trim().toLowerCase().equals(StringName.LIST.toLowerCase()))
           {
               String [] strings = modelValidate.getListaValores().split("\\,");
               for(int i = 0; i < strings.length; i ++)
               {
                   arrayValidateList.add(strings[i].trim());
               }
           }
       }

    }

    private void requerySuppliersWithOpenOrders(final boolean showView)
    {


        SuppliersWithOpenOrdersWs suppliersWithOpenOrdersWs = new SuppliersWithOpenOrdersWs(this);
        suppliersWithOpenOrdersWs.setOnSuppliersWithOpenOrders(new SuppliersWithOpenOrdersWs.OnSuppliersWithOpenOrders()
        {
            @Override
            public void onWsResponseSuppliersWithOpenOrders(ArrayList<DataModelBo> arrayListDataModelBO)
            {
                listDataModelSuppliersWithOpenOrders = arrayListDataModelBO;

                if(listDataModelSuppliersWithOpenOrders.size() == 0)
                {
                    dialogoInfo("Aviso", "Sem resultado",4.0, ReceptionActivity.this,false);
                    return;
                }

                adapterDataModelSuppliersWithOpenOrders = new ArrayAdapterDataModelSuppliersWithOpenOrders(ReceptionActivity.this, listDataModelSuppliersWithOpenOrders);
                adapterDataModelSuppliersWithOpenOrders.notifyDataSetChanged();
                listView.setAdapter(adapterDataModelSuppliersWithOpenOrders);

                if(showView)
                {
                    viewsBackStackManager.setActiveView(BackStackViews.SUPPLIERS_WITH_OPEN_ORDERS);
                    viewsBackStackManager.addViewToBackStack(viewsBackStackManager.getActiveView());
                    Log.d("ActiveView", viewsBackStackManager.getActiveView());
                    Log.d("StackViews",viewsBackStackManager.getViewStack()+"");
                    showSuppliersWithOpenOrders();
                }
            }
        });
        suppliersWithOpenOrdersWs.execute(fabrica);

    }

    private void showSuppliersWithOpenOrders()
    {
        getSupportActionBar().setTitle(StringName.TITLE_SUPPLIERS_RECEPTION);
        getSupportActionBar().setSubtitle(StringName.SUTITLE_SUPPLIERS_RECEPTION);

        txtconstraintLayoutTitulo.setText(StringName.SUPPLIERS_WITH_OPEN_RECEPTIONS);
        constraintLayout1.setVisibility(View.GONE);
        constraintLayoutC.setVisibility(View.GONE);
        fragmentReception.setVisibility(View.GONE);
        fragmentVerConformidade.setVisibility(View.GONE);
        button.setVisibility(View.GONE);

        listView.setVisibility(View.VISIBLE);
        menuItemSearch.setVisible(true);

        adapterDataModelSuppliersWithOpenOrders.notifyDataSetChanged();
        listView.setAdapter(adapterDataModelSuppliersWithOpenOrders);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                selItemSuppliersWithOpenOrders = adapterDataModelSuppliersWithOpenOrders.getItem(i);
                requerySupplierOpenReceptions(true);

            }
        });

    }

    private void requerySupplierOpenReceptions(final boolean showView)
    {
        SupplierOpenReceptionWs supplierOpenReceptionWs = new SupplierOpenReceptionWs(this);
        supplierOpenReceptionWs.setOnSupplierOpenReception(new SupplierOpenReceptionWs.OnSupplierOpenReception() {
            @Override
            public void onWsResponseSupplierOpenReception(ArrayList<DataModelBo> arrayListDataModelBO)
            {
                listDataModelSuppliersReceptions = arrayListDataModelBO;

                if(listDataModelSuppliersReceptions.size() == 0)
                {
                    dialogoInfo("Aviso", "Sem resultado",4.0, ReceptionActivity.this,false);
                    return;
                }

                adapterDataModelSuppliersOpenReceptions = new ArrayAdapterDataModelSuppliersOpenReceptions(ReceptionActivity.this, listDataModelSuppliersReceptions);
                adapterDataModelSuppliersOpenReceptions.notifyDataSetChanged();
                listView.setAdapter(adapterDataModelSuppliersOpenReceptions);

                if(showView)
                {
                    viewsBackStackManager.setActiveView(BackStackViews.SUPPLIERS_OPEN_RECEPTIONS);
                    viewsBackStackManager.addViewToBackStack(viewsBackStackManager.getActiveView());
                    Log.d("ActiveView", viewsBackStackManager.getActiveView());
                    Log.d("StackViews",viewsBackStackManager.getViewStack()+"");
                    showSupplierOpenReception();
                }

            }
        });
        supplierOpenReceptionWs.execute(fabrica,selItemSuppliersWithOpenOrders.getNo());





    }

    private void showSupplierOpenReception()
    {
        constraintLayoutC.setVisibility(View.GONE);
        constraintLayout1b.setVisibility(View.GONE);
        fragmentReception.setVisibility(View.GONE);
        fragmentVerConformidade.setVisibility(View.GONE);
        menuItemSearch.setVisible(false);

        menuItemRefresh.setVisible(true);
        constraintLayout1.setVisibility(View.VISIBLE);
        constraintLayout1a.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);

        getSupportActionBar().setTitle(StringName.TITLE_SUPPLIERS_RECEPTION);
        getSupportActionBar().setSubtitle(StringName.SUTITLE_SUPPLIERS);

        txtconstraintLayoutTitulo.setText(StringName.SUPPLIERS_OPEN_RECEPTIONS);

        textViewA1.setText(selItemSuppliersWithOpenOrders.getNome());
        textViewA2.setText("nº"+ selItemSuppliersWithOpenOrders.getNo());

        adapterDataModelSuppliersOpenReceptions.notifyDataSetChanged();
        listView.setAdapter(adapterDataModelSuppliersOpenReceptions);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                if(viewsBackStackManager.getLastViewOnStack().equals(BackStackViews.SUPPLIERS_OPEN_RECEPTIONS))
                {
                    selItemSuppliersOpenReceptions = adapterDataModelSuppliersOpenReceptions.getItem(i);
                    requerySuppliersOpenItems(true);
                }


            }
        });

        if(viewsBackStackManager.getLastViewOnStack().equals(BackStackViews.SUPPLIERS_OPEN_RECEPTIONS))
        {
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ReceptionActivity.this);
                    builder.setTitle("Nova Receção");

                    final View customAlertDialog = getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
                    builder.setView(customAlertDialog);
                    builder.setCancelable(false);



                    final EditText edtnDoc           = customAlertDialog.findViewById(R.id.edtNumDoc);
                    final TextView txtData           = customAlertDialog.findViewById(R.id.txtData);
                    final CalendarView calendarView  = customAlertDialog.findViewById(R.id.calendar);
                    final TextView textView4         = customAlertDialog.findViewById(R.id.textView4);

                    txtData.setVisibility(View.VISIBLE);
                    textView4.setVisibility(View.VISIBLE);
                    calendarView.setVisibility(View.GONE);

                    txtData.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            EditTextOps.hideKeyboard(ReceptionActivity.this,edtnDoc);
                            txtData.setVisibility(View.GONE);
                            textView4.setVisibility(View.GONE);
                            calendarView.setVisibility(View.VISIBLE);
                        }
                    });


                    calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                        @Override
                        public void onSelectedDayChange(@NonNull CalendarView calendar, int i, int i1, int i2)
                        {
                            date = i+"-"+(i1+1)+"-"+i2;
                            txtData.setText(date);
                            txtData.setVisibility(View.VISIBLE);
                            textView4.setVisibility(View.VISIBLE);
                            calendarView.setVisibility(View.GONE);
                            edtnDoc.setFocusable(true);
                            edtnDoc.requestFocus();
                        }
                    });
                    InputMethodManager inputMethodManager = (InputMethodManager) ReceptionActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                    {

                        public void onClick(DialogInterface dialog, int id) {

                            EditTextOps.hideKeyboard(ReceptionActivity.this,edtnDoc);

                            if( edtnDoc.getText().toString().trim().isEmpty() || edtnDoc.getText().toString().trim().equals("0") || txtData.getText().toString().trim().isEmpty())
                            {
                                Dialogos.dialogoErro( "Nova Receção","Dados em falta(Nº Documento ou a Data)" ,4,ReceptionActivity.this,false);
                                return;
                            }
                            else
                                createRececao(edtnDoc.getText().toString().trim());
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            ReceptionActivity.this.getWindow().setSoftInputMode(
                                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        }
                    });
                    android.support.v7.app.AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

        }

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                if(viewsBackStackManager.getLastViewOnStack().equals(BackStackViews.SUPPLIERS_OPEN_RECEPTIONS))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReceptionActivity.this);
                    builder.setTitle("Opções");

                    LayoutInflater inflater     = ReceptionActivity.this.getLayoutInflater();
                    View           _view        = inflater.inflate(R.layout.activity_validate, null);
                    ListView       listV        = _view.findViewById(R.id.listV);

                    builder.setView(_view);

                    listV.setVisibility(View.VISIBLE);

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ReceptionActivity.this, android.R.layout.simple_list_item_1,arrayOpcoes);
                    arrayAdapter.notifyDataSetChanged();
                    listV.setAdapter(arrayAdapter);

                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialogInterface.dismiss();
                        }
                    });

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    listV.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                        {
                            if(arrayAdapter.getItem(i).toString().toLowerCase().equals(StringName.SEE_DOSSIER_LINE.toLowerCase()))
                            {
                                GetDossierWs getDossierWs = new GetDossierWs(ReceptionActivity.this);
                                getDossierWs.setOnGetBoListener(new GetDossierWs.OnGetBoListener()
                                {
                                    @Override
                                    public void onResponseDossier(ArrayList<DataModelBo> dataModelDossier)
                                    {
                                        alertDialog.cancel();
                                        if(dataModelDossier.size() == 0)
                                        {
                                            Dialogos.dialogoInfo("Info", "Sem Linhas de Dossier", 3.0, ReceptionActivity.this, false);
                                        }
                                        else
                                        {
                                            txtconstraintLayoutTitulo.setText(StringName.LINHAS_DE_DOSSIER);
                                            arrayAdapterDossierLines = new ArrayAdapterDossierLines(ReceptionActivity.this, dataModelDossier);
                                            arrayAdapterDossierLines.notifyDataSetChanged();
                                            listView.setAdapter(arrayAdapterDossierLines);
                                            button.setVisibility(View.GONE);
                                            viewsBackStackManager.setActiveView(BackStackViews.SUPPLIERS_OPEN_DOSSIER);
                                            viewsBackStackManager.addViewToBackStack(viewsBackStackManager.getActiveView());
                                            Log.d("ActiveView", viewsBackStackManager.getActiveView());
                                            Log.d("StackViews",viewsBackStackManager.getViewStack()+"");
                                            menuItemRefresh.setVisible(false);
                                        }
                                    }
                                });
                                getDossierWs.execute(listDataModelSuppliersReceptions.get(i).getBostamp().trim());
                            }
                        }
                    });
                    return true;
                }
                return false;
            }
        });



    }

    private void createRececao(String nVdocum)
    {
        DataModelBo dataModelRececao = new DataModelBo();
        dataModelRececao.setNdos(ndos);
        dataModelRececao.setNo(selItemSuppliersWithOpenOrders.getNo());
        dataModelRececao.setU_vdocumet(nVdocum);
        dataModelRececao.setDataobra(date);

        CreateBoWs createBoWs = new CreateBoWs(ReceptionActivity.this);
        createBoWs.setOnCreateListener(new CreateBoWs.OnCreateBoListener()
        {
            @Override
            public void onSuccess(DataModelWsResponse wsResponse)
            {
                if(wsResponse.getCodigo() != 0)
                    showToast(ReceptionActivity.this,wsResponse.getDescricao(),3.0,18);

                requerySupplierOpenReceptions(true);

            }

            @Override
            public void onError(VolleyError error)
            {
                Dialogos.dialogoErro("Erro",error.getMessage(),3,ReceptionActivity.this,false);
            }
        });
        createBoWs.execute(dataModelRececao);

    }


    private void requerySuppliersOpenItems(final boolean showView)
    {
        SupplierOpenItemsWs supplierOpenItemsWs = new SupplierOpenItemsWs(ReceptionActivity.this);
        supplierOpenItemsWs.setOnSupplierOpenReception(new SupplierOpenItemsWs.OnSupplierOpenItems()
        {
            @Override
            public void onWsResponseSupplierOpenItems(ArrayList<DataModelBi> arrayListDataModelBi)
            {
                listDataModelSupplierItems = arrayListDataModelBi;

                if(listDataModelSupplierItems.size() == 0)
                {
                    dialogoInfo("Aviso", "Sem resultado",4.0, ReceptionActivity.this,false);
                    return;
                }

                adapterDataModelSuppliersItems = new ArrayAdapterDataModelSuppliersItems(ReceptionActivity.this, listDataModelSupplierItems);
                adapterDataModelSuppliersItems.notifyDataSetChanged();
                listView.setAdapter(adapterDataModelSuppliersItems);

                if(showView)
                {
                    viewsBackStackManager.setActiveView(BackStackViews.SUPPLIERS_OPEN_ITEMS);
                    viewsBackStackManager.addViewToBackStack(viewsBackStackManager.getActiveView());
                    Log.d("ActiveView", viewsBackStackManager.getActiveView());
                    Log.d("StackViews",viewsBackStackManager.getViewStack()+"");
                    showSupplierOpenItems();
                }
            }
        });
        supplierOpenItemsWs.execute(selItemSuppliersOpenReceptions.getNo());
    }

    private void showSupplierOpenItems()
    {
        constraintLayoutC.setVisibility(View.GONE);
        button.setVisibility(View.GONE);
        fragmentReception.setVisibility(View.GONE);
        fragmentVerConformidade.setVisibility(View.GONE);


        menuItemSearch.setVisible(false);


        menuItemRefresh.setVisible(true);
        constraintLayout1.setVisibility(View.VISIBLE);
        constraintLayout1a.setVisibility(View.VISIBLE);
        constraintLayout1b.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);
        constraintLayoutTitulo.setVisibility(View.VISIBLE);

        getSupportActionBar().setTitle(StringName.TITLE_SUPPLIERS_RECEPTION);
        getSupportActionBar().setSubtitle(StringName.SUTITLE_SUPPLIERS);

        txtconstraintLayoutTitulo.setText(StringName.SUPPLIERS_OPEN_ITEMS);

        textViewA1.setText(selItemSuppliersWithOpenOrders.getNome());
        textViewA2.setText("nº"+ selItemSuppliersWithOpenOrders.getNo());

        if(!selItemSuppliersOpenReceptions.getVossodoc().trim().isEmpty())
            textViewB1.setText("Recepção nº "+selItemSuppliersOpenReceptions.getVossodoc());
        else
            textViewB1.setText("Recepção nº N/A");

        textViewB2.setText(selItemSuppliersOpenReceptions.getDataobra().substring(0,10));
        adapterDataModelSuppliersItems.notifyDataSetChanged();
        listView.setAdapter(adapterDataModelSuppliersItems);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                selItemSuppliersOpenItems = adapterDataModelSuppliersItems.getItem(i);
                if(selItemSuppliersOpenItems.isUsalote())
                    alertDialogLote();
                else
                    showFragmentQtt();

            }
        });







    }

    private void alertDialogLote()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReceptionActivity.this);
        builder.setTitle("Lote do fornecedor");
        final EditText input = new EditText(ReceptionActivity.this);
        builder.setCancelable(false);
        builder.setView(input);
        input.setMaxLines(1);
        input.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        input.setHint("Introduza o lote");

        showKeyboard(ReceptionActivity.this);

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if(!input.getText().toString().trim().isEmpty() ||input.getText().toString().trim().equals("0"))
                {
                    lote = input.getText().toString().trim();
                    hidekeyboard(ReceptionActivity.this);
                    dialogInterface.dismiss();
                    showFragmentQtt();
                }
                else
                    showToast(ReceptionActivity.this,"Introduza o lote",3.0,28);

            }
        });
        builder.setNegativeButton("cancelar", new DialogInterface.OnClickListener()
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

    private void showFragmentQtt()
    {
        constraintLayoutTitulo.setVisibility(View.GONE);
        constraintLayout1.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        fragmentVerConformidade.setVisibility(View.GONE);
        menuItemRefresh.setVisible(false);
        button.setVisibility(View.GONE);

        fragmentReception.setVisibility(View.VISIBLE);


//        txtconstraintLayoutTitulo.setText(StringName.FRAGMENT_QTT);

        viewsBackStackManager.setActiveView(BackStackViews.FRAGMENT_QTT);
        viewsBackStackManager.addViewToBackStack(viewsBackStackManager.getActiveView());
        Log.d("ActiveView", viewsBackStackManager.getActiveView());
        Log.d("StackViews",viewsBackStackManager.getViewStack()+"");


        setDataModelEditActivity();


        FragmentQtt fragmentQtt = new FragmentQtt();
        fragmentQtt.setDataModelEditActivity(dataModelEditActivity);


        FragmentManager fragmentManager          = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction  = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentReception, fragmentQtt);
        fragmentTransaction.commit();

        fragmentQtt.setOnFragmentQtt(new FragmentQtt.OnFragmentQtt()
        {
            @Override
            public void onQtt(Double qtt, String unidade)
            {
                System.out.println("quantidade "+qtt);

//                registerSupplierReception(qtt, unidade);

//                showValidate(qtt, unidade);


            }
        });
    }

    private void showValidate(Double qtt, String unidade)
    {
        txtconstraintLayoutTitulo.setText(StringName.VALIDATE_PRODUCT);
        constraintLayoutTitulo.setVisibility(View.VISIBLE);
        constraintLayout1.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);

        fragmentVerConformidade.setVisibility(View.GONE);
        menuItemRefresh.setVisible(false);
        fragmentReception.setVisibility(View.GONE);



//        final DataModelBi dataModelBi = new DataModelBi();
//
//        dataModelBi.setArmazem(StringName.ARMAZEM);
//        dataModelBi.setReferencia(selItemSuppliersOpenItems.getRef().trim());
//        dataModelBi.setQtt(qtt);
//        dataModelBi.setBostamp(selItemSuppliersOpenReceptions.getBostamp().trim());
//        dataModelBi.setLobs(lote);
//        dataModelBi.setNo(selItemSuppliersOpenReceptions.getNo());
//        dataModelBi.setDesign(selItemSuppliersOpenItems.getDesign().trim());
//        dataModelBi.setUnidade(unidade);


        textViewA1.setText("["+selItemSuppliersOpenItems.getRef().trim()+"]- "+selItemSuppliersOpenItems.getDesign().trim());
        textViewB1.setText("Lote: "+lote);
        textViewB2.setText("Qtt: "+retiraDecimais(qtt)+" "+ unidade);

        viewsBackStackManager.setActiveView(BackStackViews.VALIDATE_PRODUCT);
        viewsBackStackManager.addViewToBackStack(viewsBackStackManager.getActiveView());
        Log.d("ActiveView", viewsBackStackManager.getActiveView());
        Log.d("StackViews",viewsBackStackManager.getViewStack()+"");


        myNotificationDataChanged();





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                if(arrayAdapterDataModelValidate.getItem(position).getTipo().toLowerCase().trim().equals(StringName.LOGICO.toLowerCase()))
                {
                     selValidate = arrayAdapterDataModelValidate.getItem(position);
                     alertDialogLogico();
                }

                if(arrayAdapterDataModelValidate.getItem(position).getTipo().toLowerCase().trim().equals(StringName.NUMERICO.toLowerCase()))
                {
                    selValidate = arrayAdapterDataModelValidate.getItem(position);
                    alertDialogNumerico();
                }

                if(arrayAdapterDataModelValidate.getItem(position).getTipo().toLowerCase().trim().equals(StringName.LIST.toLowerCase()))
                {
                    selValidate = arrayAdapterDataModelValidate.getItem(position);
                    alertDialogList();
                }

            }
        });

        if(viewsBackStackManager.getLastViewOnStack().equals(BackStackViews.VALIDATE_PRODUCT))
        {
            button.setText("Submeter");
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String checked = "";

                    for(int i = 0; i < arrayListObjModelValidate.size(); i++)
                    {
                       if(arrayListObjModelValidate.get(i).getObrigatorio().toLowerCase().trim().equals("sim")
                       && arrayListObjModelValidate.get(i).getChoosed().trim().isEmpty())
                       {
                           arrayListObjModelValidate.get(i).setAllChecked(true);
                           checked = "checked";
                       }

                    }
                    if(checked.isEmpty())
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ReceptionActivity.this);
                        builder.setTitle("Pretende Submeter Avaliação");
                        builder.setCancelable(false);

                        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {


                            }
                        });

                        builder.setNegativeButton("Não", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.dismiss();
                            }
                        });



                        final AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    }
                    else
                    {
                        showToast(ReceptionActivity.this,"Existe campos obrigatório a preencher",3.0, 16);
                        myNotificationDataChanged();

                    }


                }
            });


        }




//        FragmentVerificarConformidade fragmentVerificarConformidade  = new FragmentVerificarConformidade();
//        fragmentVerificarConformidade.setDataModelBi(dataModelBi);
//
//        FragmentManager fragmentManager          = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction  = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.fragmentVerConformidade, fragmentVerificarConformidade);
//        fragmentTransaction.commit();
//
//        fragmentVerificarConformidade.setOnFragmentVerificarConformidade(new FragmentVerificarConformidade.OnFragmentVerificarConformidade()
//        {
//            @Override
//            public void onVerificarConformidade(String visivel, String legivel)
//            {
//                dataModelBi.setVisivel(visivel);
//                dataModelBi.setLegivel(legivel);
//
//                registerSupplierReception(dataModelBi);
//
//            }
//        });

    }




    private void registerSupplierReception(Double qtt, String unidade)
    {
        final DataModelBi dataModelBi = new DataModelBi();

        dataModelBi.setArmazem(StringName.ARMAZEM);
        dataModelBi.setReferencia(selItemSuppliersOpenItems.getRef().trim());
        dataModelBi.setQtt(qtt);
        dataModelBi.setBostamp(selItemSuppliersOpenReceptions.getBostamp().trim());
        dataModelBi.setLobs(lote);
        dataModelBi.setNo(selItemSuppliersOpenReceptions.getNo());
        dataModelBi.setDesign(selItemSuppliersOpenItems.getDesign().trim());
        dataModelBi.setUnidade(unidade);

        RegisterSupplierReceptionWs registerSupplierReceptionWs = new RegisterSupplierReceptionWs(this);
        registerSupplierReceptionWs.setOnRegisterSupplierReceptionListener(new RegisterSupplierReceptionWs.OnRegisterSupplierReceptionListener()
        {
            @Override
            public void onResponse(DataModelWsResponse wsResponse)
            {
                if(wsResponse.getCodigo() == 0)
                    Dialogos.dialogoSucess("Sucesso", wsResponse.getDescricao(),3,ReceptionActivity.this,false);
                else
                    Dialogos.dialogoInfo("Informação", wsResponse.getDescricao(),3.0,ReceptionActivity.this,false);

                viewsBackStackManager.removeLastViewOnStack();

                requerySuppliersOpenItems(true);
                System.out.println(wsResponse.getDescricao());

            }
        });
        registerSupplierReceptionWs.execute(dataModelBi);

    }

    private void setDataModelEditActivity()
    {
        String[] qtt       = selItemSuppliersOpenItems.getQttpend().trim().split("\\.");
        String[] conv      = selItemSuppliersOpenItems.getU_qttconv().trim().split("\\.");
        String[] uni2Pend  = selItemSuppliersOpenItems.getUni2qttpend().trim().split("\\.");

        dataModelEditActivity.setTextView_1a("Design:");
        dataModelEditActivity.setTxt1b(selItemSuppliersOpenItems.getDesign().trim());

        dataModelEditActivity.setTextView_2a("Referência:");
        dataModelEditActivity.setTxt2b(selItemSuppliersOpenItems.getRef().trim());

        dataModelEditActivity.setTextView_3a("Lote:");
        if(!lote.isEmpty())
            dataModelEditActivity.setTxt3b(lote);

        dataModelEditActivity.setTextView_4a("Stock:");
        dataModelEditActivity.setTxt4b(qtt[0]);


        dataModelEditActivity.setTextView22a("Quantidade rececionada:");
        dataModelEditActivity.setTextView_21a("Editar quantidade");

        dataModelEditActivity.setTxt22b(uni2Pend[0]);// unidade 2
        dataModelEditActivity.setU_qttconv(conv[0]); // convertido
        dataModelEditActivity.setUnidade(selItemSuppliersOpenItems.getUnidade());
        dataModelEditActivity.setUn2(selItemSuppliersOpenItems.getUni2());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cab, menu);

        menuItemRefresh = menu.findItem(R.id.menu_item_refresh);
        menuItemOptions = menu.findItem(R.id.menu_item_list);
        menuItemSearch  = menu.findItem(R.id.menu_item_search);

        menuItemSearch.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReceptionActivity.this);
                builder.setTitle("Pesquisar");

                final EditText input = new EditText(ReceptionActivity.this);
                builder.setCancelable(false);
                builder.setView(input);
                input.setMaxLines(1);
                input.setHint("Introduza a pesquisa");

                showKeyboard(ReceptionActivity.this);

                builder.setPositiveButton("pesquisar", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if(input.getText().toString().trim().isEmpty() || input.getText().toString().trim().equals("0"))
                            showToast(ReceptionActivity.this,"Introduza a pesquisa",2.0,20);

                        else
                            getOnSearch(input.getText().toString().trim());

                        hidekeyboard(ReceptionActivity.this);
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("cancelar", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });
        menuItemRefresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {

                if(viewsBackStackManager.getLastViewOnStack().equals(BackStackViews.SUPPLIERS_WITH_OPEN_ORDERS))
                {
                    requerySuppliersWithOpenOrders(true);
                    return true;
                }

                if(viewsBackStackManager.getLastViewOnStack().equals(BackStackViews.SUPPLIERS_OPEN_RECEPTIONS))
                {
                    requerySupplierOpenReceptions(true);
                    return true;
                }

                if(viewsBackStackManager.getLastViewOnStack().equals(BackStackViews.SUPPLIERS_OPEN_ITEMS))
                {
                    requerySuppliersOpenItems(true);
                    return true;
                }

                return false;
            }
        });


        return true;
    }

    private void getOnSearch(String textSearch)
    {
        ArrayList<DataModelBo>  listDataModelSuppliersReceptionsSearch        = new ArrayList<>();
        ArrayList<DataModelBo>  listDataModelSuppliersWithOpenOrdersSearch    = new ArrayList<>();

        listDataModelSuppliersReceptionsSearch.clear();
        listDataModelSuppliersWithOpenOrdersSearch.clear();


        if(viewsBackStackManager.getLastViewOnStack().equals(BackStackViews.SUPPLIERS_WITH_OPEN_ORDERS))
        {
            for(DataModelBo modelBo : listDataModelSuppliersWithOpenOrders)
            {
                if(modelBo.getNome().toLowerCase().contains(textSearch.toLowerCase()))
                    listDataModelSuppliersWithOpenOrdersSearch.add(modelBo);
            }

            if(listDataModelSuppliersWithOpenOrdersSearch.size() == 0)
            {
                showToast(ReceptionActivity.this,"Sem resultado",2.0,20);
                return;
            }
            constraintLayoutC.setVisibility(View.VISIBLE);
            textViewC2.setVisibility(View.GONE);

            textViewC1.setText("Resultado de pesquisa: '"+textSearch+"'");
            adapterDataModelSuppliersWithOpenOrders = new ArrayAdapterDataModelSuppliersWithOpenOrders(ReceptionActivity.this, listDataModelSuppliersWithOpenOrdersSearch);
            adapterDataModelSuppliersWithOpenOrders.notifyDataSetChanged();
            listView.setAdapter(adapterDataModelSuppliersWithOpenOrders);
            return;
        }

    }




    public void alertDialogLogico()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Avaliação");

        if(!selValidate.getObservacao().trim().isEmpty())
            builder.setMessage(selValidate.getObservacao().trim());

        LayoutInflater inflater              = this.getLayoutInflater();
        View           view                  = inflater.inflate(R.layout.activity_validate, null);
        RadioGroup  radioGroup               = view.findViewById(R.id.radioGroup);
        final RadioButton radioButtonPos     = view.findViewById(R.id.radioButtonPos);
        final RadioButton  radioButtonNeg    = view.findViewById(R.id.radioButtonNeg);

        builder.setView(view);

        radioGroup.setVisibility(View.VISIBLE);


//        mySplit(dataModelValidate.get);


        radioButtonPos.setText(selValidate.getLogicYes().trim());
        radioButtonNeg.setText(selValidate.getLogicNo().trim());

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checked)
            {
                switch (checked)
                {
                    case R.id.radioButtonPos:
                        radioButtonPos.setChecked(true);
                        radioButtonNeg.setChecked(false);
                        selValidate.setChoosed(selValidate.getLogicYes().trim());
                        selValidate.setAllChecked(false);
                        break;

                    case R.id.radioButtonNeg:
                        radioButtonNeg.setChecked(true);
                        radioButtonPos.setChecked(false);
                        selValidate.setAllChecked(false);
                        selValidate.setChoosed(selValidate.getLogicNo().trim());
                        break;
                }

            }
        });

        builder.setPositiveButton("Submeter", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
                myNotificationDataChanged();
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
//
    public void alertDialogNumerico()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReceptionActivity.this);
        builder.setTitle("Avaliação");

        if(!selValidate.getObservacao().trim().isEmpty())
            builder.setMessage(selValidate.getObservacao().trim());

        builder.setCancelable(false);

        final EditText editText = new EditText(ReceptionActivity.this);

        if( selValidate.getDecimais() <= 0)
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        else
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        showKeyboard(ReceptionActivity.this);

        builder.setView(editText);

        builder.setPositiveButton("Submeter", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();

                String [] strValid =  editText.getText().toString().trim().split("\\.");

                if(editText.getText().toString().trim().isEmpty() || editText.getText().toString().trim().equals("0") || editText.getText().toString().trim().equals("."))
                {
                    showToast(ReceptionActivity.this,"Introduza um valor para avaliaçâo",2.0,16);
                    return;
                }

                if(strValid.length > 1)
                {
                    if(strValid[1].length() > selValidate.getDecimais())
                    {
                        Dialogos.dialogoInfo("Info", "Número de casa decimais superior",2.0,ReceptionActivity.this,false);
                        return;
                    }

                }

                if( Double.parseDouble(editText.getText().toString().trim()) < selValidate.getLimInf() ||
                        Double.parseDouble(editText.getText().toString().trim()) > selValidate.getLimSup())

                    showToast(ReceptionActivity.this,"Valor Superior ou inferior a limite permitido",2.0,16);
                else
                {
                    selValidate.setChoosed(editText.getText().toString().trim());
                    selValidate.setAllChecked(false);
                    myNotificationDataChanged();
                }


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

    public void alertDialogList()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReceptionActivity.this);
        builder.setTitle("Avaliação");

        if(!selValidate.getObservacao().trim().isEmpty())
            builder.setMessage(selValidate.getObservacao().trim());

        builder.setCancelable(false);

        LayoutInflater inflater              = this.getLayoutInflater();
        View           view                  = inflater.inflate(R.layout.activity_validate, null);
        ListView       listV                 = view.findViewById(R.id.listV);

        builder.setView(view);

        listV.setVisibility(View.VISIBLE);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,arrayValidateList);
        arrayAdapter.notifyDataSetChanged();
        listV.setAdapter(arrayAdapter);

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        listV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
           {
               selValidate.setAllChecked(false);
               selValidate.setChoosed(arrayValidateList.get(i).trim());
               myNotificationDataChanged();
               alertDialog.cancel();
           }
        });

    }

    private void myNotificationDataChanged()
    {
        arrayAdapterDataModelValidate = new ArrayAdapterDataModelValidate(ReceptionActivity.this, arrayListObjModelValidate);
        arrayAdapterDataModelValidate.notifyDataSetChanged();
        listView.setAdapter(arrayAdapterDataModelValidate);
    }







    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menuItemOptions.setVisible(false);
        menuItemSearch.setVisible(true);
        menuItemRefresh.setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    public void initialize()
    {
        listView                         = findViewById(R.id.lstView);
        txtconstraintLayoutTitulo        = findViewById(R.id.txtconstraintLayoutTitulo);
        textViewA1                       = findViewById(R.id.TextViewa1);
        textViewA2                       = findViewById(R.id.TextViewa2);
        textViewB1                       = findViewById(R.id.TextViewb1);
        textViewB2                       = findViewById(R.id.TextViewb2);
        textViewC1                       = findViewById(R.id.TextViewC1);
        textViewC2                       = findViewById(R.id.TextViewC2);
        constraintLayoutTitulo           = findViewById(R.id.constraintLayoutTitulo);
        constraintLayoutC                = findViewById(R.id.constraintLayoutC);
        constraintLayout1                = findViewById(R.id.constraintLayout1);
        constraintLayout1a               = findViewById(R.id.constraintLayout1a);
        constraintLayout1b               = findViewById(R.id.constraintLayout1b);
        toolbarReception                 = findViewById(R.id.toolbarReception);
        button = findViewById(R.id.button);
        fragmentReception                = findViewById(R.id.fragmentReception);
        fragmentVerConformidade          = findViewById(R.id.fragmentVerConformidade);

        txtconstraintLayoutTitulo.setText(StringName.SUPPLIERS_OPEN_RECEPTIONS);
        constraintLayout1.setVisibility(View.GONE);
        constraintLayoutC.setVisibility(View.GONE);
        button.setVisibility(View.GONE);
    }

    private void initializeToobar()
    {
        setSupportActionBar(toolbarReception);
        toolbarReception.setTitleTextColor(Color.WHITE);
        toolbarReception.setSubtitleTextColor(Color.WHITE);
        toolbarReception.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                myOnBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(StringName.TITLE_SUPPLIERS_RECEPTION);
        getSupportActionBar().setSubtitle(StringName.SUTITLE_SUPPLIERS_RECEPTION);

        menuItemRefresh                  = toolbarReception.findViewById(R.id.menu_item_refresh);
        menuItemOptions                  = toolbarReception.findViewById(R.id.menu_item_list);
        menuItemSearch                   = toolbarReception.findViewById(R.id.menu_item_search);
    }

    public void progressDialog()
    {
        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("Aguarde");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

    }

    public void initializeClasses()
    {
        viewsBackStackManager                = new ViewsBackStackManager();
        listDataModelSuppliersWithOpenOrders = new ArrayList<>();
        dataModelEditActivity                = new DataModelEditActivity();
        arrayListObjModelValidate            = new ArrayList<>();
        arrayValidateList                    = new ArrayList<>();
        arrayOpcoes                          = new ArrayList<>();

        arrayOpcoes.add(StringName.SEE_DOSSIER_LINE);


    }

    public void myOnBackPressed()
    {

        viewsBackStackManager.removeLastViewOnStack();

        if(viewsBackStackManager.getViewStack().size() == 0)
        {
            finish();
            return;
        }

        if(viewsBackStackManager.getLastViewOnStack().equals(BackStackViews.SUPPLIERS_OPEN_RECEPTIONS))
        {
            showSupplierOpenReception();
            return;
        }

        if(viewsBackStackManager.getLastViewOnStack().equals(BackStackViews.SUPPLIERS_WITH_OPEN_ORDERS))
        {
            showSuppliersWithOpenOrders();
            return;
        }

        if(viewsBackStackManager.getLastViewOnStack().equals(BackStackViews.SUPPLIERS_OPEN_ITEMS))
        {
            showSupplierOpenItems();
            return;
        }
        if(viewsBackStackManager.getLastViewOnStack().equals(BackStackViews.FRAGMENT_QTT))
        {
            if(selValidate != null)
            {
                for(int i = 0; i < arrayListObjModelValidate.size(); i++)
                {
                    arrayListObjModelValidate.get(i).setChoosed("");
                    arrayListObjModelValidate.get(i).setAllChecked(false);

                }
            }
            myNotificationDataChanged();
            showFragmentQtt();
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
        private final static String SUPPLIERS_WITH_OPEN_ORDERS = "Fornecedores com pedidos em aberto_1  "  ;
        private final static String SUPPLIERS_OPEN_RECEPTIONS  = "Receções em aberto do fornecedor_2"   ;
        private final static String SUPPLIERS_OPEN_DOSSIER     = "Linhas de dossier"   ;
        private final static String SUPPLIERS_OPEN_ITEMS       = "Itens em aberto de fornecedores_3"     ;
        private final static String FRAGMENT_QTT               = "fragment qtt_4"     ;
        private final static String VALIDATE_PRODUCT           = "Validar Produto_5"     ;

    }

    private class StringName
    {

        private final static String SUPPLIERS_WITH_OPEN_RECEPTIONS   = "Fornecedores com encomendas em aberto"   ;
        private final static String SUPPLIERS_OPEN_RECEPTIONS        = "Receções em aberto do fornecedor"   ;
        private final static String SUPPLIERS_OPEN_ITEMS             = "Itens em aberto do fornecedor"   ;
        private final static String FRAGMENT_QTT                     = "Introduzir a quantidade"   ;
        private final static String VALIDATE_PRODUCT                 = "Validar Produto"     ;

        private final static String LINHAS_DE_DOSSIER                = "Linhas de Dossier"     ;
        private final static String TITLE_SUPPLIERS_RECEPTION        = "Receção de encomendas"   ;
        private final static String SUTITLE_SUPPLIERS_RECEPTION      = "Receber"   ;
        private final static String SUTITLE_SUPPLIERS                = "(Fornecedores)"   ;
        private final static String FABRICA_BARCELOS                 = "BARCELOS"   ;
        private final static String FABRICA_GAIA                     = "GAIA"   ;

        private final static int    ARMAZEM                          = 1;


        private final static String    LOGICO                         = "L";
        private final static String    NUMERICO                       = "N";
        private final static String    LIST                           = "T";

        private final static String    SEE_DOSSIER_LINE               = "VER LINHAS DO DOSSIER";


    }
}









