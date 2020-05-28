package gnotus.inoveplastika.API.Phc;

import android.app.Activity;
import android.app.ProgressDialog;
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
import gnotus.inoveplastika.DataModels.DataModelSe;
import gnotus.inoveplastika.Globals;

import static com.android.volley.VolleyLog.TAG;

public class GetSeWs {

    Context context;
    OnGetSeListener mOnGetSeListener;

    private ProgressDialog progDailog;

    private String dialogMessage = "A ler informação de lote";

    public GetSeWs(Activity a) {

        context = a;
        progDailog = new ProgressDialog(context);

    }

    public void execute(String ref, String lote) {

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/GetSe?").buildUpon()
                .appendQueryParameter("referencia",ref)
                .appendQueryParameter("lote",lote)
                .build();

        VolleyRequest volleyRequest = new VolleyRequest(context);
        volleyRequest.setOnVolleyResultListener(new VolleyRequest.OnVolleyResultListener() {
            @Override
            public void onSuccess(String response) {

                Gson gson = new Gson();

                // quando o serviço retorna um DataTable é sempre necessário receber um array e transformar em objecto

                DataModelSe se = null;
                ArrayList<DataModelSe> arraySe = new ArrayList<>();


                if (response.equals(Globals.WSRESPONSEVAZIO)) {
                    mOnGetSeListener.onSuccess(se);
                }

                try {

                    arraySe = gson.fromJson(response,new TypeToken<ArrayList<DataModelSe>>() {}.getType());

                } catch (JsonParseException exception) {

                    Log.e(TAG, "EXCEPTION_ON_JSON_PARSE");
                    exception.printStackTrace();

                } finally {
                    if (arraySe.size() > 0) se = arraySe.get(0);
                    mOnGetSeListener.onSuccess(se);
                }

            }

            @Override
            public void onError(VolleyError error) {
                mOnGetSeListener.onError(error);
            }
        });

        volleyRequest.setDialogMessage(dialogMessage);
        volleyRequest.VolleyStringRequestGet(uri);

    }


    public interface OnGetSeListener {
        void onSuccess(DataModelSe se);
        void onError(VolleyError error);
    }

    public void setOnGetSeListener(OnGetSeListener mOnGetSeListener) {
        this.mOnGetSeListener = mOnGetSeListener;
    }
}
