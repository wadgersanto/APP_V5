package gnotus.inoveplastika.API.Producao;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import gnotus.inoveplastika.API.VolleyRequest;
import gnotus.inoveplastika.DataModels.DataModelWsResponse;
import gnotus.inoveplastika.Globals;

import static android.content.ContentValues.TAG;

public class ActLocalConsumoOfWs {

    Context context;
    OnActLocalConsumoOfListener mOnActLocalConsumoOfListener;
    private String dialogMessage = "A atualizar localização de consumo";

    public ActLocalConsumoOfWs(Activity activity) {
        context = activity;
    }


    public void execute(String bistamp,String armazem, String zona, String alveolo) {

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/ActLocalConsumoOf?").buildUpon()

                .appendQueryParameter("bistamp",bistamp)
                .appendQueryParameter("armazem",armazem)
                .appendQueryParameter("zona",zona)
                .appendQueryParameter("alveolo",alveolo)

                .build();

        VolleyRequest volleyRequest = new VolleyRequest(context);

        volleyRequest.setOnVolleyResultListener(new VolleyRequest.OnVolleyResultListener() {
            @Override
            public void onSuccess(String response) {

                Gson gson = new Gson();
                DataModelWsResponse wsResponse = new DataModelWsResponse();

                try {
                    wsResponse = gson.fromJson(response,new TypeToken<DataModelWsResponse>() {}.getType());
                } catch (JsonParseException exception) {
                    Log.e(TAG, "EXCEPTION_ON_JSON_PARSE");
                    exception.printStackTrace();
                } finally {
                    mOnActLocalConsumoOfListener.onSuccess(wsResponse);
                }


            }

            @Override
            public void onError(VolleyError error) {

            }
        });

        volleyRequest.setDialogMessage(dialogMessage);
        volleyRequest.VolleyStringRequestGet(uri);

    }

    public interface OnActLocalConsumoOfListener {
        void onSuccess(DataModelWsResponse wsResponse);
        void onError(String error);
    }

    public void setmOnActLocalConsumoOfListener(OnActLocalConsumoOfListener mOnActLocalConsumoOfListener) {
        this.mOnActLocalConsumoOfListener = mOnActLocalConsumoOfListener;
    }
}
