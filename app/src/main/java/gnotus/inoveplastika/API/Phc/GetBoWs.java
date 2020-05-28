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
import gnotus.inoveplastika.Globals;

import static com.android.volley.VolleyLog.TAG;

public class GetBoWs {

    Context context;
    OnGetBoListener mOnGetBoListener;

    private ProgressDialog progDailog;

    private String dialogMessage = "A ler dossier";

    public GetBoWs(Activity a) {

        context = a;
        progDailog = new ProgressDialog(context);

    }

    public void execute(String bostamp) {

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/GetBo?").buildUpon()
                .appendQueryParameter("bostamp",bostamp)
                .build();

        VolleyRequest volleyRequest = new VolleyRequest(context);
        volleyRequest.setOnVolleyResultListener(new VolleyRequest.OnVolleyResultListener() {
            @Override
            public void onSuccess(String response) {

                Gson gson = new Gson();

                DataModelBo bo = null;
                ArrayList<DataModelBo> arrayBo = new ArrayList<>();

                try {

                    arrayBo = gson.fromJson(response,new TypeToken<ArrayList<DataModelBo>>() {}.getType());

                } catch (JsonParseException exception) {

                    Log.e(TAG, "EXCEPTION_ON_JSON_PARSE");
                    exception.printStackTrace();

                } finally {

                    if (arrayBo.size() > 0) bo = arrayBo.get(0);
                    mOnGetBoListener.onSuccess(bo);
                }

            }

            @Override
            public void onError(VolleyError error) {
                mOnGetBoListener.onError(error);
            }
        });

        volleyRequest.setDialogMessage(dialogMessage);
        volleyRequest.VolleyStringRequestGet(uri);

    }


    public interface OnGetBoListener {
        void onSuccess(DataModelBo bo);
        void onError(VolleyError error);
    }

    public void setOnGetBoListener(OnGetBoListener mOnGetBoListener) {
        this.mOnGetBoListener = mOnGetBoListener;
    }
}
