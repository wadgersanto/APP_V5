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
import gnotus.inoveplastika.Globals;
import gnotus.inoveplastika.Logistica.DataModelAlv;

import static com.android.volley.VolleyLog.TAG;

public class LerAlveoloWs {

    Context context;
    OnLerAlveoloListener mOnLerAlveoloListener;

    public LerAlveoloWs(Activity activity) {
        context = activity;
    }


    public void execute(int armazem,String zona, String alveolo) {

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LerAlveolo?").buildUpon()
                .appendQueryParameter("armazem",String.valueOf (armazem))
                .appendQueryParameter("zona",zona)
                .appendQueryParameter("alveolo",alveolo)
                .build();

        VolleyRequest volleyRequest = new VolleyRequest(context);

        volleyRequest.setOnVolleyResultListener(new VolleyRequest.OnVolleyResultListener() {
            @Override
            public void onSuccess(String response) {

                Gson gson = new Gson();
                ArrayList<DataModelAlv> arrayAlveolo = new ArrayList<DataModelAlv>();

                try {

                    arrayAlveolo = gson.fromJson(response,new TypeToken< ArrayList<DataModelAlv>>() {}.getType());

                } catch (JsonParseException exception) {
                    Log.e(TAG, "EXCEPTION_ON_JSON_PARSE");
                    exception.printStackTrace();

                } finally {

                    DataModelAlv alveolo = null;

                    if (arrayAlveolo.size() > 0) {
                        alveolo = arrayAlveolo.get(0);
                    }

                    mOnLerAlveoloListener.onSuccess(alveolo);

                }

            }

            @Override
            public void onError(VolleyError error) {
                mOnLerAlveoloListener.onError(error);
            }
        });

        volleyRequest.setDialogMessage("Ler alvéolo");
        volleyRequest.VolleyStringRequestGet(uri);

    }

    public interface OnLerAlveoloListener {
        void onSuccess(DataModelAlv dmAlveolo);
        void onError(VolleyError error);

    }

    public void setOnLerAlveoloListener(OnLerAlveoloListener mOnLerAlveoloListener) {
        this.mOnLerAlveoloListener = mOnLerAlveoloListener;
    }
}
