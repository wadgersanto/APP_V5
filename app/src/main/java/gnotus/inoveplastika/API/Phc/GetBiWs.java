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
import gnotus.inoveplastika.DataModels.DataModelBi;
import gnotus.inoveplastika.Globals;

import static com.android.volley.VolleyLog.TAG;

public class GetBiWs {

    Context context;
    OnGetBiListener mOnGetBiListener;

    private ProgressDialog progDailog;

    private String dialogMessage = "A ler linhas de dossier";

    public GetBiWs(Activity a) {

        context = a;
        progDailog = new ProgressDialog(context);

    }

    public void execute(String bostamp, String bistamp) {

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/GetBi?").buildUpon()
                .appendQueryParameter("bostamp",bostamp)
                .appendQueryParameter("bistamp",bistamp)
                .build();

        VolleyRequest volleyRequest = new VolleyRequest(context);
        volleyRequest.setOnVolleyResultListener(new VolleyRequest.OnVolleyResultListener() {
            @Override
            public void onSuccess(String response) {

                Gson gson = new Gson();

                ArrayList<DataModelBi> arrayBi = new ArrayList<>();

                try {

                    arrayBi = gson.fromJson(response,new TypeToken<ArrayList<DataModelBi>>() {}.getType());

                } catch (JsonParseException exception) {

                    Log.e(TAG, "EXCEPTION_ON_JSON_PARSE");
                    exception.printStackTrace();

                } finally {


                    mOnGetBiListener.onSuccess(arrayBi);
                }

            }

            @Override
            public void onError(VolleyError error) {
                mOnGetBiListener.onError(error);
            }
        });

        volleyRequest.setDialogMessage(dialogMessage);
        volleyRequest.VolleyStringRequestGet(uri);

    }


    public interface OnGetBiListener {
        void onSuccess(ArrayList<DataModelBi> arrayBi);
        void onError(VolleyError error);
    }

    public void setOnGetBiListener(OnGetBiListener mOnGetBiListener) {
        this.mOnGetBiListener = mOnGetBiListener;
    }
}
