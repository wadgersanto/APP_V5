package gnotus.inoveplastika.API.Producao;

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
import gnotus.inoveplastika.DataModels.DataModelBi;
import gnotus.inoveplastika.Globals;

import static com.android.volley.VolleyLog.TAG;

public class DevolveLinhasInvOfWs {

    Context context;
    OnDevolveLinhasInvOfListener mOnDevolveLinhasInvOfListener;
    private String dialogMessage = "A obter linhas de inventário";

    public DevolveLinhasInvOfWs(Activity activity) {
        context = activity;
    }


    public void execute(String bostamp, Boolean recalcLinhas) {

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/DevolveLinhasInvOf?").buildUpon()
                .appendQueryParameter("bostamp", bostamp)
                .appendQueryParameter("recalcLinhas", Boolean.toString(recalcLinhas))
                .build();

        VolleyRequest volleyRequest = new VolleyRequest(context);

        volleyRequest.setOnVolleyResultListener(new VolleyRequest.OnVolleyResultListener() {
            @Override
            public void onSuccess(String response) {

                ArrayList<DataModelBi> linhasInvOf = new ArrayList<DataModelBi>();

                try {

                    Gson gson = new Gson();

                    linhasInvOf = gson.fromJson(response,new TypeToken<ArrayList<DataModelBi>>() {}.getType());

                } catch (JsonParseException exception) {

                    Log.e(TAG, "EXCEPTION_ON_JSON_PARSE");
                    exception.printStackTrace();

                } finally {

                    mOnDevolveLinhasInvOfListener.onSuccess(linhasInvOf);

                }


            }

            @Override
            public void onError(VolleyError error) {

            }
        });

        volleyRequest.setDialogMessage(dialogMessage);
        volleyRequest.VolleyStringRequestGet(uri);

    }

    public interface OnDevolveLinhasInvOfListener {

        void onSuccess(ArrayList<DataModelBi> linhasInvOf);
        void onError(String error);

    }

    public void setmOnDevolveLinhasInvOfListener(OnDevolveLinhasInvOfListener mOnDevolveLinhasInvOfListener) {
        this.mOnDevolveLinhasInvOfListener = mOnDevolveLinhasInvOfListener;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }
}
