package gnotus.inoveplastika.API.Logistica;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import gnotus.inoveplastika.API.VolleyRequest;
import gnotus.inoveplastika.DataModels.DataModelWsResponse;
import gnotus.inoveplastika.Globals;
import gnotus.inoveplastika.Logistica.DataModelAlv;

import static com.android.volley.VolleyLog.TAG;

public class BatchUnloadWs {

    Context context;
    OnBatchUnloadListener mOnBatchUnloadListener;
    private String dialogMessage = "A processar descarga";


    public BatchUnloadWs(Activity activity) {
        context = activity;
    }


    public void execute(String bostamp, int operador, int armazem,String zona, String alveolo) {

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/batchUnload?").buildUpon()
                .appendQueryParameter("bostamp",bostamp)
                .appendQueryParameter("operador",String.valueOf (operador))
                .appendQueryParameter("armazem",String.valueOf (armazem))
                .appendQueryParameter("zona",zona)
                .appendQueryParameter("alveolo",alveolo)
                .build();

        VolleyRequest volleyRequest = new VolleyRequest(context);
        volleyRequest.setDialogMessage(dialogMessage);

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

                    mOnBatchUnloadListener.onSuccess(wsResponse);
                }

            }

            @Override
            public void onError(VolleyError error) {
                mOnBatchUnloadListener.onError(error);
            }
        });

        volleyRequest.VolleyStringRequestGet(uri);

    }

    public interface OnBatchUnloadListener {
        void onSuccess(DataModelWsResponse wsResponse);
        void onError(VolleyError error);

    }

    public void setOnBatchUnloadListener(OnBatchUnloadListener mOnBatchUnloadListener) {
        this.mOnBatchUnloadListener = mOnBatchUnloadListener;
    }
}
