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
import gnotus.inoveplastika.Logistica.DataModelSzz;

import static com.android.volley.VolleyLog.TAG;

public class LerZonaWs {

    Context context;
    OnLerZonaListener mOnLerZonaListener;

    public LerZonaWs(Activity activity) {
        context = activity;
    }


    public void execute(int armazem,String zona) {

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LerZona?").buildUpon()
                .appendQueryParameter("armazem",String.valueOf (armazem))
                .appendQueryParameter("zona",zona)
                .build();

        VolleyRequest volleyRequest = new VolleyRequest(context);

        volleyRequest.setOnVolleyResultListener(new VolleyRequest.OnVolleyResultListener() {
            @Override
            public void onSuccess(String response) {

                Gson gson = new Gson();
                ArrayList<DataModelSzz> arrayZona = new ArrayList<DataModelSzz>();

                try {

                    arrayZona = gson.fromJson(response,new TypeToken< ArrayList<DataModelSzz>>() {}.getType());

                } catch (JsonParseException exception) {
                    Log.e(TAG, "EXCEPTION_ON_JSON_PARSE");
                    exception.printStackTrace();

                } finally {

                    DataModelSzz zona = null;

                    if (arrayZona.size() > 0) {
                        zona = arrayZona.get(0);
                    }

                    mOnLerZonaListener.onSuccess(zona);

                }

            }

            @Override
            public void onError(VolleyError error) {
                mOnLerZonaListener.onError(error);
            }
        });

        volleyRequest.VolleyStringRequestGet(uri);

    }

    public interface OnLerZonaListener {
        void onSuccess(DataModelSzz dmZona);
        void onError(VolleyError error);

    }

    public void setOnLerZonaListener(OnLerZonaListener mOnLerZonaListener) {
        this.mOnLerZonaListener = mOnLerZonaListener;
    }
}
