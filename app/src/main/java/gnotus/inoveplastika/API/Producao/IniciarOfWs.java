package gnotus.inoveplastika.API.Producao;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import gnotus.inoveplastika.API.VolleyRequest;
import gnotus.inoveplastika.DataModels.DataModelWsResponse;
import gnotus.inoveplastika.Globals;

public class IniciarOfWs {

    Context context;
    OnIniciarOfListener mOnIniciarOfListener;
    private String dialogMessage = "A iniciar OF";

    public IniciarOfWs(Activity activity) {
        context = activity;
    }


    public void execute(String ref,String lote, String armazem, String zona, String alveolo, String operador) {

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/IniciarOf?").buildUpon()
                .appendQueryParameter("referencia",ref)
                .appendQueryParameter("ordfab",lote)
                .appendQueryParameter("armazem",armazem)
                .appendQueryParameter("zona",zona)
                .appendQueryParameter("alveolo",alveolo)
                .appendQueryParameter("user",operador)
                .build();

        VolleyRequest volleyRequest = new VolleyRequest(context);

        volleyRequest.setOnVolleyResultListener(new VolleyRequest.OnVolleyResultListener() {
            @Override
            public void onSuccess(String response) {

                Gson gson = new Gson();
                DataModelWsResponse wsResponse;
                wsResponse = gson.fromJson(response,new TypeToken<DataModelWsResponse>() {}.getType());
                mOnIniciarOfListener.onSuccess(wsResponse);

            }

            @Override
            public void onError(VolleyError error) {

            }
        });

        volleyRequest.setDialogMessage(dialogMessage);
        volleyRequest.VolleyStringRequestGet(uri);

    }

    public interface OnIniciarOfListener {
        void onSuccess(DataModelWsResponse wsResponse);
    }

    public void setmOnIniciarOfListener(OnIniciarOfListener mOnIniciarOfListener) {
        this.mOnIniciarOfListener = mOnIniciarOfListener;
    }
}
