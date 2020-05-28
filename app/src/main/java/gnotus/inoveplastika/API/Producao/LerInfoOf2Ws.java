package gnotus.inoveplastika.API.Producao;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import gnotus.inoveplastika.API.VolleyRequest;
import gnotus.inoveplastika.Globals;
import gnotus.inoveplastika.Producao.InfoOfDataModel;

public class LerInfoOf2Ws {

    Context context;
    OnLerInfoOf2Listener mOnLerInfoOf2Listener;

    public LerInfoOf2Ws(Activity activity) {
        context = activity;
    }


    public void execute(String numof) {

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LerInfoOf2?").buildUpon()
                .appendQueryParameter("lote",numof)
                .build();

        VolleyRequest volleyRequest = new VolleyRequest(context);

        volleyRequest.setOnVolleyResultListener(new VolleyRequest.OnVolleyResultListener() {
            @Override
            public void onSuccess(String response) {

                Gson gson = new Gson();
                ArrayList<InfoOfDataModel>  arrayInfoOf;
                arrayInfoOf = gson.fromJson(response,new TypeToken<ArrayList<InfoOfDataModel>>() {}.getType());

                InfoOfDataModel infoOf = null;

                if (arrayInfoOf.size() > 0) {
                    infoOf = arrayInfoOf.get(0);
                }

                mOnLerInfoOf2Listener.onSuccess(infoOf);

            }

            @Override
            public void onError(VolleyError error) {

            }
        });

        volleyRequest.VolleyStringRequestGet(uri);

    }

    public interface OnLerInfoOf2Listener {
        void onSuccess(InfoOfDataModel mInfoOf);
    }

    public void setOnLerInfoOf2Listener(OnLerInfoOf2Listener mOnLerInfoOf2Listener) {
        this.mOnLerInfoOf2Listener = mOnLerInfoOf2Listener;
    }
}
