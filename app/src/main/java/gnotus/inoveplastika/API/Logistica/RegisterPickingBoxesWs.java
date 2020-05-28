package gnotus.inoveplastika.API.Logistica;

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

import java.util.List;

import gnotus.inoveplastika.API.VolleyRequest;
import gnotus.inoveplastika.DataModels.DataModelWsResponse;
import gnotus.inoveplastika.Globals;
import gnotus.inoveplastika.Producao.DataModelCxof;

import static com.android.volley.VolleyLog.TAG;

public class RegisterPickingBoxesWs {

    Context context;
    OnRegisterPickingBoxesListener mOnRegisterPickingBoxesListener;
    private String dialogMessage = "A registar caixas";


    public RegisterPickingBoxesWs(Activity activity) {
        context = activity;
    }


    public void execute(List<DataModelCxof> boxes) {

        Gson gson = new Gson();

        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(gson.toJson(boxes));
            System.out.println(jsonObject);
        }
        catch (JSONException exception) {

            mOnRegisterPickingBoxesListener.onError(exception.getLocalizedMessage());
            exception.printStackTrace();
            return;
        }


        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/RegisterPickingBoxes?").buildUpon().build();

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

                    mOnRegisterPickingBoxesListener.onError(exception.getMessage());
                    Log.e(TAG, "EXCEPTION_ON_JSON_PARSE");
                    exception.printStackTrace();


                } finally {

                    mOnRegisterPickingBoxesListener.onSuccess(wsResponse);
                }

            }

            @Override
            public void onError(VolleyError error) {
                mOnRegisterPickingBoxesListener.onError(error.getMessage());
            }
        });
        volleyRequest.VolleyJsonObjectRequest(uri,jsonObject);

    }

    public interface OnRegisterPickingBoxesListener {
        void onSuccess(DataModelWsResponse wsResponse);
        void onError(String error);

    }

    public void setOnRegisterPickingBoxesListener(OnRegisterPickingBoxesListener mOnRegisterPickingBoxesListener) {
        this.mOnRegisterPickingBoxesListener = mOnRegisterPickingBoxesListener;
    }
}
