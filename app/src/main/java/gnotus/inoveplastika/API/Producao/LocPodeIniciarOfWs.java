package gnotus.inoveplastika.API.Producao;

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

import gnotus.inoveplastika.API.VolleyRequest;
import gnotus.inoveplastika.DataModels.DataModelWsResponse;
import gnotus.inoveplastika.Dialogos;
import gnotus.inoveplastika.Globals;

public class LocPodeIniciarOfWs {

    Context context;

    OnLocPodeIniciarOfListener mOnLocPodeIniciarOfListener;
    private String dialogMessage = "A verificar se pode iniciar OF na localização...";

    public LocPodeIniciarOfWs(Activity activity) {
        context = activity;
    }


    public void execute(String numof,String pArmazem, String pZona, String pAlveolo) {

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LocPodeIniciarOf?").buildUpon()
                .appendQueryParameter("numof",numof)
                .appendQueryParameter("armazem",String.valueOf (pArmazem))
                .appendQueryParameter("zona",pZona)
                .appendQueryParameter("alveolo",pAlveolo)
                .build();


        VolleyRequest volleyRequest = new VolleyRequest(context);
        volleyRequest.setOnVolleyResultListener(new VolleyRequest.OnVolleyResultListener() {
            @Override
            public void onSuccess(String response) {

                Gson gson = new Gson();

                DataModelWsResponse wsResponse;

                wsResponse = gson.fromJson(response,new TypeToken<DataModelWsResponse>() {}.getType());

                mOnLocPodeIniciarOfListener.onSuccess(wsResponse);

            }

            @Override
            public void onError(VolleyError error) {
                mOnLocPodeIniciarOfListener.onError(error);
            }
        });

        volleyRequest.setDialogMessage(dialogMessage);
        volleyRequest.VolleyStringRequestGet(uri);


    }

    public interface OnLocPodeIniciarOfListener {
        void onSuccess(DataModelWsResponse wsResponse);
        void onError(VolleyError error);
    }

    public void setOnRegisterSupplierReceptionListener(OnLocPodeIniciarOfListener onLocPodeIniciarOfListener) {
        this.mOnLocPodeIniciarOfListener = onLocPodeIniciarOfListener;
    }


}
