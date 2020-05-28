package gnotus.inoveplastika.API.Logistica;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import gnotus.inoveplastika.DataModels.DataModelBi;
import gnotus.inoveplastika.DataModels.DataModelWsResponse;
import gnotus.inoveplastika.Dialogos;
import gnotus.inoveplastika.Globals;

public class RegisterSupplierReceptionWs {

    Context context;
    private ProgressDialog progDailog;
    OnRegisterSupplierReceptionListener onRegisterSupplierReceptionListener;

    public RegisterSupplierReceptionWs(Activity activity) {
        context = activity;
        progDailog = new ProgressDialog(context);
    }


    public void execute(DataModelBi reception) {

        Gson gson = new Gson();

        JSONObject jsonObjectBi = null;
        try
        {
            if(jsonObjectBi == null)
            {
                jsonObjectBi = new JSONObject(gson.toJson(reception));

                System.out.println(jsonObjectBi.toString());
            }

        }catch (Exception e)
        {
            Dialogos.dialogoErro("Erro na conversão para Json da classe submitReception",e.getMessage(),6, (Activity)context,false);
            e.printStackTrace();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        progDailog.show();

        String url = "http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/RegisterSupplierReception";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObjectBi, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                progDailog.dismiss();

                try
                {

                    Gson gson = new Gson();
                    DataModelWsResponse wsResponse;
                    wsResponse = gson.fromJson(response.toString(),new TypeToken<DataModelWsResponse>() {}.getType());
                    onRegisterSupplierReceptionListener.onResponse(wsResponse);

                } catch (Throwable t) {
                    Dialogos.dialogoErro("Erro na conversão para Json",t.toString(),6,(Activity)context,false);
                }

            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        System.out.println("Erro no pedido "+ error.getLocalizedMessage());
                        progDailog.dismiss();
                    }

                });

        System.out.println(url);

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setShouldCache(false);
        queue.getCache().clear();
        queue.add(jsonObjectRequest);

    }

    public interface OnRegisterSupplierReceptionListener {
        void onResponse(DataModelWsResponse wsResponse);
    }

    public void setOnRegisterSupplierReceptionListener(OnRegisterSupplierReceptionListener onRegisterSupplierReceptionListener) {
        this.onRegisterSupplierReceptionListener = onRegisterSupplierReceptionListener;
    }
}
