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

public class GetInfoOfWs {

    Context context;
    OnGetInfoOfListener mOnGetInfoOfListener;
    private String dialogMessage = "Ler informação OF";

    public GetInfoOfWs(Activity activity) {
        context = activity;
    }


    public void execute(String referencia, String numof) {

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/GetInfoOf?").buildUpon()
                .appendQueryParameter("referencia",referencia)
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

                mOnGetInfoOfListener.onSuccess(infoOf);

            }

            @Override
            public void onError(VolleyError error) {
                mOnGetInfoOfListener.onError(error);
            }
        });

        volleyRequest.setDialogMessage(dialogMessage);
        volleyRequest.VolleyStringRequestGet(uri);

    }

    public interface OnGetInfoOfListener {
        void onSuccess(InfoOfDataModel mInfoOf);
        void onError(VolleyError error);
    }

    public void setOnGetInfoOfListener(OnGetInfoOfListener mOnGetInfoOfListener) {
        this.mOnGetInfoOfListener = mOnGetInfoOfListener;
    }
}
