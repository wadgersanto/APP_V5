package gnotus.inoveplastika.API.Phc;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import gnotus.inoveplastika.API.VolleyRequest;
import gnotus.inoveplastika.DataModels.DataModelWsResponse;
import gnotus.inoveplastika.Globals;

public class DeleteBoWs {

    Context context;
    OnDeleteBoListener mOnDeleteBoListener;

    private String dialogMessage = "A eliminar dossier";

    public DeleteBoWs(Activity activity) {
        context = activity;
    }


    public void execute(String bostamp,Boolean onlyIfNoLines) {

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/DeleteBo?").buildUpon()
                .appendQueryParameter("bostamp",bostamp)
                .appendQueryParameter("onlyifnolines",String.valueOf(onlyIfNoLines))
                .build();


        VolleyRequest volleyRequest = new VolleyRequest(context);

        volleyRequest.setOnVolleyResultListener(new VolleyRequest.OnVolleyResultListener() {
            @Override
            public void onSuccess(String response) {

                Gson gson = new Gson();
                DataModelWsResponse wsResponse;
                wsResponse = gson.fromJson(response,new TypeToken<DataModelWsResponse>() {}.getType());
                mOnDeleteBoListener.onSuccess(wsResponse);

            }

            @Override
            public void onError(VolleyError error) {

                mOnDeleteBoListener.onError(error);
            }
        });

        volleyRequest.setDialogMessage(dialogMessage);
        volleyRequest.VolleyStringRequestGet(uri);

    }

    public interface OnDeleteBoListener {
        void onSuccess(DataModelWsResponse wsResponse);
        void onError(VolleyError error);
    }

    public void setOnDeleteBoListener(OnDeleteBoListener mOnDeleteBoListener) {
        this.mOnDeleteBoListener = mOnDeleteBoListener;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }
}
