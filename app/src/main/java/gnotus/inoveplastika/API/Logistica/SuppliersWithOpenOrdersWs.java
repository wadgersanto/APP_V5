package gnotus.inoveplastika.API.Logistica;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import gnotus.inoveplastika.DataModels.DataModelBo;
import gnotus.inoveplastika.Dialogos;
import gnotus.inoveplastika.Globals;

public class SuppliersWithOpenOrdersWs {

    Context context;
    private ProgressDialog progDailog;

    private OnSuppliersWithOpenOrders onSuppliersWithOpenOrders;

    public SuppliersWithOpenOrdersWs(Activity activity) {
        context = activity;
        progDailog = new ProgressDialog(context);
    }

    public void execute(String fabrica) {

        progDailog.show();

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/SuppliersWithOpenOrders?plant="+fabrica).buildUpon()
                .build();

        System.out.println("-> "+uri);

        RequestQueue requestQueue   = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(), new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                progDailog.dismiss();

                ArrayList<DataModelBo> supplierOpenOrders;
                Gson gson           = new Gson();
                supplierOpenOrders  = gson.fromJson(response,new TypeToken<ArrayList<DataModelBo>>() {}.getType());
                onSuppliersWithOpenOrders.onWsResponseSuppliersWithOpenOrders(supplierOpenOrders);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                progDailog.dismiss();
                Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage(),4, (Activity) context,false);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),
                0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    public void setOnSuppliersWithOpenOrders(OnSuppliersWithOpenOrders suppliersWithOpenOrders) {
        this.onSuppliersWithOpenOrders = suppliersWithOpenOrders;
    }

    public interface OnSuppliersWithOpenOrders {
        void onWsResponseSuppliersWithOpenOrders(ArrayList<DataModelBo> arrayListDataModelBO);
    }




}
