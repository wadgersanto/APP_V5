package gnotus.inoveplastika.PickingBoxes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import gnotus.inoveplastika.API.Logistica.PickingsWithUnreadBoxesLineWs;
import gnotus.inoveplastika.API.Logistica.PickingsWithUnreadBoxesWs;
import gnotus.inoveplastika.DataModels.DataModelBo;
import gnotus.inoveplastika.Logistica.DataModelPickingLine;
import gnotus.inoveplastika.R;
import gnotus.inoveplastika.UserFunc;
import gnotus.inoveplastika.ViewsBackStackManager;

import static gnotus.inoveplastika.Dialogos.dialogoInfo;
import static gnotus.inoveplastika.Dialogos.showToast;


public class PickingBoxesActivity extends AppCompatActivity {


    private android.support.v7.widget.Toolbar toolbarPickingBoxs;

    private ListView listView;

    private TextView txtconstraintLayoutTitulo, textViewA1, textViewA2, textViewB1, textViewB2,textViewC1,textViewC2;
    private ConstraintLayout constraintLayoutTitulo, constraintLayout1, constraintLayout1a, constraintLayout1b, constraintLayoutC;

    private ProgressDialog      progressDialog;

    private ViewsBackStackManager viewsBackStackManager;

    private FrameLayout fragmentBoxesAvailableForPicking;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;


    private MenuItem menuItemRefresh = null, menuItemOptions = null, menuItemSearch = null;

    private DataModelBo selItemWithUnreadBoxes;
    private DataModelPickingLine selItemWithUnreadBoxesLine;



    private String TITLE_ASSIGN_PICKING_BOX     = "Atribuir Caixa Picking"               ;
    private String TITLE_ORDER_LIST             = "Listas de Caixas"                     ;
    private String SUBTITLE_ORDER_LIST          = "Atribuir Caixa em falta"              ;
    private String SUBTITLE_MISSING_BOX         = "Caixas em Falta"                      ;
    private String SUBTITLE_BOXES_AVAILABLE     = "Caixas Disponíveis"                   ;

    private String UNREAD_BOXES                 = "Caixas não Lidas"                     ;
    private String UNREAD_BOXES_LINES           = "Linhas de Caixas não Lidas"           ;
    private String BOXES_AVAILABLE_FOR_PICKING  = "Caixas Disponíveis para Leitura"      ;



    /****************************************ArrayAdapter******************************************/
    private ArrayAdapterDataModelPicking          adapterDataModelPicking;
    private ArrayAdapterDataModelPickingLines     adapterDataModelPickingLines;
    /****************************************ArrayAdapter******************************************/


    /***************************************ArrayList**********************************************/
    private ArrayList<DataModelBo>          listDataModelPicking            = new ArrayList();
    private ArrayList<DataModelPickingLine> listDataModelPickingLines       = new ArrayList();


    private ArrayList<DataModelBo>          listDataModelPickingBOSearch    = new ArrayList();
    /***************************************ArrayList**********************************************/


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picking_boxes);

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
        toolbarPickingBoxs               = findViewById(R.id.toolbarPickingBoxs);
        fragmentBoxesAvailableForPicking = findViewById(R.id.fragmentBoxesAvailableForPicking);


        viewsBackStackManager = new ViewsBackStackManager();


        setSupportActionBar(toolbarPickingBoxs);
        toolbarPickingBoxs.setTitleTextColor(Color.WHITE);
        toolbarPickingBoxs.setSubtitleTextColor(Color.WHITE);
        toolbarPickingBoxs.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        menuItemRefresh = toolbarPickingBoxs.findViewById(R.id.menu_item_refresh);
        menuItemOptions = toolbarPickingBoxs.findViewById(R.id.menu_item_list);
        menuItemSearch  = toolbarPickingBoxs.findViewById(R.id.menu_item_search);


        progressDialog  = new ProgressDialog(PickingBoxesActivity.this);
        progressDialog.setMessage("Aguarde");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);


        txtconstraintLayoutTitulo.setText(UNREAD_BOXES);
        getSupportActionBar().setTitle(TITLE_ASSIGN_PICKING_BOX);
        getSupportActionBar().setSubtitle(SUBTITLE_ORDER_LIST);
        constraintLayout1.setVisibility(View.GONE);
        constraintLayoutC.setVisibility(View.GONE);

        start();
    }//fim onCreate

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
            public boolean onMenuItemClick(MenuItem item)
            {
                if(viewsBackStackManager.getLastViewOnStack().equals(BackStackViews.PICKINGS_WITH_UNREAD_BOXES))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PickingBoxesActivity.this);
                    builder.setTitle("Pesquisar");

                    final EditText input = new EditText(PickingBoxesActivity.this);
                    builder.setCancelable(false);
                    builder.setView(input);
                    input.setMaxLines(1);
                    input.setHint("Introduza a pesquisa");

                    showKeyboard(PickingBoxesActivity.this);

                    builder.setPositiveButton("pesquisar", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            if(input.getText().toString().trim().isEmpty() || input.getText().toString().trim().equals("0"))
                                showToast(PickingBoxesActivity.this,"Introduza a pesquisa",2.0,20);

                            else
                                getOnSearch(input.getText().toString().trim());

                            hidekeyboard(PickingBoxesActivity.this);
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
                }
                return true;
            }
        });

        menuItemRefresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                if(viewsBackStackManager.getLastViewOnStack().equals(BackStackViews.PICKINGS_WITH_UNREAD_BOXES))
                {
                    requeryPickingsWithUnreadBoxes(false);
                    return true;
                }

                if(viewsBackStackManager.getLastViewOnStack().equals(BackStackViews.PICKINGS_WITH_UNREAD_BOXES_LINES))
                {
                    requeryPickingsWithUnreadBoxesLine(false);
                    return true;
                }


                Toast toast = Toast.makeText(getApplicationContext(),"Sem nenhuma activity",Toast.LENGTH_SHORT);
                ViewGroup group = (ViewGroup) toast.getView();
                TextView messageTextView = (TextView) group.getChildAt(0);
                messageTextView.setTextSize(20);
                toast.show();
                return true;
            }
        });


        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menuItemOptions.setVisible(false);
        menuItemSearch.setVisible(true);
        menuItemRefresh.setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    private void start()
    {
      requeryPickingsWithUnreadBoxes(true);
    }

    private void requeryPickingsWithUnreadBoxes(final boolean showView)
    {
        constraintLayoutC.setVisibility(View.GONE);
        PickingsWithUnreadBoxesWs pickingsWithUnreadBoxesWs = new PickingsWithUnreadBoxesWs(PickingBoxesActivity.this);
        pickingsWithUnreadBoxesWs.setOnPickingsWithUnreadBoxesListener(new PickingsWithUnreadBoxesWs.OnPickingsWithUnreadBoxes() {
            @Override
            public void onWsResponsePickings(ArrayList<DataModelBo> arrayListDataModelBO)
            {
                listDataModelPicking = arrayListDataModelBO;

                if(listDataModelPicking.size() == 0)
                {
                    dialogoInfo("Aviso", "Sem resultado",4.0, PickingBoxesActivity.this,false);
                    return;
                }

                adapterDataModelPicking   = new ArrayAdapterDataModelPicking(PickingBoxesActivity.this, listDataModelPicking);
                adapterDataModelPicking.notifyDataSetChanged();
                listView.setAdapter(adapterDataModelPicking);

                if(showView)
                {
                    viewsBackStackManager.setActiveView(BackStackViews.PICKINGS_WITH_UNREAD_BOXES);
                    viewsBackStackManager.addViewToBackStack(BackStackViews.PICKINGS_WITH_UNREAD_BOXES);
                    Log.d("ActiveView", BackStackViews.PICKINGS_WITH_UNREAD_BOXES);
                    Log.d("StackViews",viewsBackStackManager.getViewStack()+"");


                    showPickingWithUnreadBoxes();
                }


            }
        });
        pickingsWithUnreadBoxesWs.execute();
    }


    private void getOnSearch(String textSearch)
    {
        listDataModelPickingBOSearch.clear();
       for(DataModelBo modelBO : listDataModelPicking)
       {
           if(modelBO.getNome().toLowerCase().contains(textSearch.toLowerCase()))
           {
               listDataModelPickingBOSearch.add(modelBO);
           }
       }
       if(listDataModelPickingBOSearch.size() == 0)
       {
           showToast(PickingBoxesActivity.this,"Sem resultado",2.0,20);
           return;
       }
        constraintLayoutC.setVisibility(View.VISIBLE);
        textViewC2.setVisibility(View.GONE);

        textViewC1.setText("Resultado de pesquisa: '"+textSearch+"'");
        adapterDataModelPicking   = new ArrayAdapterDataModelPicking(PickingBoxesActivity.this, listDataModelPickingBOSearch);
        adapterDataModelPicking.notifyDataSetChanged();
        listView.setAdapter(adapterDataModelPicking);

    }

    public void showPickingWithUnreadBoxes()
    {

        menuItemRefresh.setVisible(true);
        menuItemSearch.setVisible(true);

        txtconstraintLayoutTitulo.setText(UNREAD_BOXES);
        getSupportActionBar().setTitle(TITLE_ASSIGN_PICKING_BOX);
        getSupportActionBar().setSubtitle(SUBTITLE_ORDER_LIST);


        constraintLayoutTitulo.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);
        textViewC2.setVisibility(View.VISIBLE);

        constraintLayout1.setVisibility(View.GONE);
        constraintLayoutC.setVisibility(View.GONE);
        fragmentBoxesAvailableForPicking.setVisibility(View.GONE);


        listView.setAdapter(adapterDataModelPicking);
        adapterDataModelPicking.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                selItemWithUnreadBoxes = adapterDataModelPicking.getItem(i); // estamos pegar o bostamp do item Clicado

                requeryPickingsWithUnreadBoxesLine(true);



            }
        });

    }

    public void showFragmentBoxesAvailableForPicking()
    {

        menuItemRefresh.setVisible(false);
        menuItemSearch.setVisible(false);
        getSupportActionBar().setSubtitle(SUBTITLE_BOXES_AVAILABLE);

        txtconstraintLayoutTitulo.setText(BOXES_AVAILABLE_FOR_PICKING);

        constraintLayoutC.setVisibility(View.VISIBLE);
        fragmentBoxesAvailableForPicking.setVisibility(View.VISIBLE);

        listView.setVisibility(View.INVISIBLE);

        textViewC1.setText("Lote: "+selItemWithUnreadBoxesLine.getLote());
        textViewC2.setText("Referência: "+ selItemWithUnreadBoxesLine.getRef());



        DataModelPickingLine itemDataModelPickingLine = new DataModelPickingLine();

        itemDataModelPickingLine.setLote(selItemWithUnreadBoxesLine.getLote());
        itemDataModelPickingLine.setRef(selItemWithUnreadBoxesLine.getRef());
        itemDataModelPickingLine.setNrboxes_pend(Double.valueOf(UserFunc.retiraZerosDireita(selItemWithUnreadBoxesLine.getNrboxes_pend())));

        FragmentReadBoxesAvailable fragmentReadBoxesAvailable = new FragmentReadBoxesAvailable();
        fragmentReadBoxesAvailable.setDataModelPickingLine(itemDataModelPickingLine);
        fragmentReadBoxesAvailable.setOnSelectItemListener(new FragmentReadBoxesAvailable.onSelectItemListener() {
            @Override
            public void onSelectedItem() {

            }
        });

        fragmentManager     = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentBoxesAvailableForPicking, fragmentReadBoxesAvailable);
        fragmentTransaction.commit();

    }


    public void showPickingWithUnreadBoxesLine()
    {
        menuItemRefresh.setVisible(true);
        menuItemSearch.setVisible(false);

        hidekeyboard(PickingBoxesActivity.this);

        getSupportActionBar().setTitle(TITLE_ASSIGN_PICKING_BOX);
        getSupportActionBar().setSubtitle(SUBTITLE_MISSING_BOX);

        txtconstraintLayoutTitulo.setText(UNREAD_BOXES_LINES);

        constraintLayout1.setVisibility(View.VISIBLE);
        constraintLayoutTitulo.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);

        constraintLayoutC.setVisibility(View.GONE);
        textViewA2.setVisibility(View.GONE);
        textViewB2.setVisibility(View.GONE);
        fragmentBoxesAvailableForPicking.setVisibility(View.GONE);


        textViewA1.setText("["+selItemWithUnreadBoxes.getNome().trim()+"]  nº "+selItemWithUnreadBoxes.getObrano());
        textViewB1.setText(selItemWithUnreadBoxes.getDataobra().substring(0, 10));

        listView.setAdapter(adapterDataModelPickingLines);
        adapterDataModelPickingLines.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                selItemWithUnreadBoxesLine = adapterDataModelPickingLines.getItem(i);

                viewsBackStackManager.setActiveView(BackStackViews.FRAGMENT_BOXES_AVAILABLE_FOR_PICKING);
                viewsBackStackManager.addViewToBackStack(BackStackViews.FRAGMENT_BOXES_AVAILABLE_FOR_PICKING);
                Log.d("ActiveView", BackStackViews.FRAGMENT_BOXES_AVAILABLE_FOR_PICKING);
                Log.d("StackViews",viewsBackStackManager.getViewStack()+"");
                showFragmentBoxesAvailableForPicking();


            }
        });

    }


    private void requeryPickingsWithUnreadBoxesLine(final boolean showView)
    {

        PickingsWithUnreadBoxesLineWs pickingsWithUnreadBoxesLineWs = new PickingsWithUnreadBoxesLineWs(PickingBoxesActivity.this);

        pickingsWithUnreadBoxesLineWs.setOnPickingsWithUnreadBoxesLineListener(new PickingsWithUnreadBoxesLineWs.OnPickingsWithUnreadBoxesLine() {
            @Override
            public void onWsResponsePickingsLine(ArrayList<DataModelPickingLine> DataModelPickingLine)
            {
                listDataModelPickingLines = DataModelPickingLine;

                if(listDataModelPickingLines.size() == 0)
                {
                    dialogoInfo("Aviso", "Sem resultado",4.0, PickingBoxesActivity.this,false);
                    return;
                }

                adapterDataModelPickingLines   = new ArrayAdapterDataModelPickingLines(PickingBoxesActivity.this, listDataModelPickingLines);
                adapterDataModelPickingLines.notifyDataSetChanged();
                listView.setAdapter(adapterDataModelPickingLines);

                if(showView)
                {
                    viewsBackStackManager.setActiveView(BackStackViews.PICKINGS_WITH_UNREAD_BOXES_LINES);
                    viewsBackStackManager.addViewToBackStack(BackStackViews.PICKINGS_WITH_UNREAD_BOXES_LINES);

                    Log.d("ActiveView", BackStackViews.PICKINGS_WITH_UNREAD_BOXES_LINES);
                    Log.d("StackViews",viewsBackStackManager.getViewStack()+"");
                    showPickingWithUnreadBoxesLine();
                }

            }
        });

        pickingsWithUnreadBoxesLineWs.execute(selItemWithUnreadBoxes.getBostamp().trim());
    }


    public void onBackPressed()
    {

        viewsBackStackManager.removeLastViewOnStack(); // removemos a ultima view do nosso arrayList

        if(viewsBackStackManager.getViewStack().size() == 0)
        {
            finish();
            return;
        }

        if(viewsBackStackManager.getLastViewOnStack().equals(BackStackViews.PICKINGS_WITH_UNREAD_BOXES))
        {
            showPickingWithUnreadBoxes();
            return;
        }

        if(viewsBackStackManager.getLastViewOnStack().equals(BackStackViews.PICKINGS_WITH_UNREAD_BOXES_LINES))
        {
            showPickingWithUnreadBoxesLine();
            return;
        }

        if(viewsBackStackManager.getLastViewOnStack().equals(BackStackViews.FRAGMENT_BOXES_AVAILABLE_FOR_PICKING))
        {
            showFragmentBoxesAvailableForPicking();
            return;
        }

        Toast toast = Toast.makeText(getApplicationContext(),"Sem nenhuma activity",Toast.LENGTH_SHORT);
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(20);
        toast.show();

    }

    private class BackStackViews
    {
        private final static String PICKINGS_WITH_UNREAD_BOXES           = "PICKINGS_WITH_UNREAD_BOXES_1"           ;
        private final static String PICKINGS_WITH_UNREAD_BOXES_LINES     = "PICKINGS_WITH_UNREAD_BOXES_LINES_2"     ;
        private final static String FRAGMENT_BOXES_AVAILABLE_FOR_PICKING = "FRAGMENT_BOXES_AVAILABLE_FOR_PICKING_3" ;

    }

    public static void showKeyboard(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
    public static void hidekeyboard(Activity activity)
    {
        activity.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

}//fiom class










