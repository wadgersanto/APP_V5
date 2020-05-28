package gnotus.inoveplastika.API.Phc;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import gnotus.inoveplastika.API.VolleyRequest;
import gnotus.inoveplastika.DataModels.DataModelBo;
import gnotus.inoveplastika.DataModels.DataModelWsResponse;
import gnotus.inoveplastika.Dialogos;
import gnotus.inoveplastika.Globals;

import static com.android.volley.VolleyLog.TAG;

public class CreateBoWs {

    Context context;
    OnCreateBoListener mOnCreateBoListener;
    private String dialogMessage = "A criar novo nº documento";

    public CreateBoWs(Activity activity) {
        context = activity;
    }


    public void execute(DataModelBo mBO) {

        // Uri uri = "http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/CreateBo?";

        Gson gson = new Gson();

        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(gson.toJson(mBO));
        }
        catch (JSONException e) {
            Dialogos.dialogoErro("Erro na conversão para Json da classe processarPa",e.getMessage(),6, (Activity) context,false);
            e.printStackTrace();
            return;
        }

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/CreateBo?").buildUpon().build();

        System.out.println(uri.toString());
        VolleyRequest volleyRequest = new VolleyRequest(context);
        volleyRequest.setDialogMessage(dialogMessage);

        volleyRequest.setOnVolleyResultListener(new VolleyRequest.OnVolleyResultListener() {
            @Override
            public void onSuccess(String response) {

                Gson gson = new Gson();
                DataModelWsResponse wsResponse = new DataModelWsResponse();

                try {

                    wsResponse = gson.fromJson(response,new TypeToken<DataModelWsResponse>() {}.getType());

                } catch (JsonParseException exception) {

                    Log.e(TAG, "EXCEPTION_ON_JSON_PARSE");
                    exception.printStackTrace();


                } finally {

                    mOnCreateBoListener.onSuccess(wsResponse);
                }

            }

            @Override
            public void onError(VolleyError error) {
                mOnCreateBoListener.onError(error);
            }
        });

        volleyRequest.VolleyJsonObjectRequest(uri,jsonObject);

    }

    public interface OnCreateBoListener {
        void onSuccess(DataModelWsResponse wsResponse);
        void onError(VolleyError error);
    }

    public void setOnCreateListener(OnCreateBoListener mOnCreateBoListener) {
        this.mOnCreateBoListener = mOnCreateBoListener;
    }
}
