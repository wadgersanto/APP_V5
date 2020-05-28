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
import gnotus.inoveplastika.DataModels.DataModelBo;
import gnotus.inoveplastika.Globals;

import static com.android.volley.VolleyLog.TAG;

public class GetPdaLoadsWs {

    Context context;
    OnGetPdaLoadsListener mOnGetPdaLoadsListener;

    public GetPdaLoadsWs(Activity activity) {
        context = activity;
    }


    public void execute(int operador,int requestType) {

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/GetPdaLoads?").buildUpon()
                .appendQueryParameter("operador",String.valueOf (operador))
                .appendQueryParameter("requestType",String.valueOf (requestType))
                .build();

        VolleyRequest volleyRequest = new VolleyRequest(context);

        volleyRequest.setOnVolleyResultListener(new VolleyRequest.OnVolleyResultListener() {
            @Override
            public void onSuccess(String response) {

                Gson gson = new Gson();
                ArrayList<DataModelBo> pdaLoads = new ArrayList<>();

                try {

                    pdaLoads = gson.fromJson(response,new TypeToken< ArrayList<DataModelBo>>() {}.getType());

                } catch (JsonParseException exception) {

                    mOnGetPdaLoadsListener.onError(exception.getLocalizedMessage());
                    Log.e(TAG, "EXCEPTION_ON_JSON_PARSE");
                    exception.printStackTrace();

                } finally {

                    mOnGetPdaLoadsListener.onSuccess(pdaLoads);

                }

            }

            @Override
            public void onError(VolleyError error) {
                mOnGetPdaLoadsListener.onError(error.getLocalizedMessage());
            }
        });

        volleyRequest.setDialogMessage("Ler alvéolo");
        volleyRequest.VolleyStringRequestGet(uri);

    }

    public interface OnGetPdaLoadsListener {
        void onSuccess(ArrayList<DataModelBo> pdaLoads);
        void onError(String error);

    }

    public void setOnLerAlveoloListener(OnGetPdaLoadsListener mOnGetPdaLoadsListener) {
        this.mOnGetPdaLoadsListener = mOnGetPdaLoadsListener;
    }

    public static class ReqType
    {
        public  final int REQTYPE_TRANSF  = 1;
        public  final int REQTYPE_PICKING = 2;
    }

}
