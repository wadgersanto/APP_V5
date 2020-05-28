package gnotus.inoveplastika.API.Phc;

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
import gnotus.inoveplastika.DataModels.DataModelST;
import gnotus.inoveplastika.Globals;
import gnotus.inoveplastika.Logistica.DataModelSzz;

import static com.android.volley.VolleyLog.TAG;

public class GetStWs {

    Context context;
    OnLerStListener mOnLerStListener;
    private String dialogMessage = "A ler informação do artigo";

    public GetStWs(Activity activity) {
        context = activity;
    }


    public void execute(String referencia) {

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/GetST?").buildUpon()
                .appendQueryParameter("referencia",referencia)
                .build();

        VolleyRequest volleyRequest = new VolleyRequest(context);

        volleyRequest.setOnVolleyResultListener(new VolleyRequest.OnVolleyResultListener() {
            @Override
            public void onSuccess(String response) {

                Gson gson = new Gson();

                ArrayList<DataModelST> arraySt = new ArrayList<DataModelST>();

                try {

                    arraySt = gson.fromJson(response,new TypeToken< ArrayList<DataModelST>>() {}.getType());

                } catch (JsonParseException exception) {

                    Log.e(TAG, "EXCEPTION_ON_JSON_PARSE");
                    exception.printStackTrace();


                } finally {

                    mOnLerStListener.onSuccess(arraySt);
                }

            }

            @Override
            public void onError(VolleyError error) {
                mOnLerStListener.onError(error);
            }
        });

        volleyRequest.setDialogMessage(dialogMessage);
        volleyRequest.VolleyStringRequestGet(uri);

    }

    public interface OnLerStListener {
        void onSuccess(ArrayList<DataModelST> arraySt);
        void onError(VolleyError error);

    }

    public void setOnLerStListener(OnLerStListener mOnLerStListener) {
        this.mOnLerStListener = mOnLerStListener;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }
}
