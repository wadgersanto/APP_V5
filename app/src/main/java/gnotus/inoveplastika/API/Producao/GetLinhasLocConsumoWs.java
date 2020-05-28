package gnotus.inoveplastika.API.Producao;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import gnotus.inoveplastika.API.VolleyRequest;
import gnotus.inoveplastika.DataModels.DataModelBi;
import gnotus.inoveplastika.Dialogos;
import gnotus.inoveplastika.Globals;

public class GetLinhasLocConsumoWs {

    Context context;
    OnGetLinhasLocConsumoListener mOnGetLinhasLocConsumoListener;
    private String dialogMessage = "A listar consumos da OF";

    public GetLinhasLocConsumoWs(Activity activity) {
        context = activity;
    }


    public void execute(String ref, String lote, int armazem, String zona, String alveolo,  Boolean resetLocal) {


        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/GetLinhasLocConsumo?").buildUpon()

                .appendQueryParameter("referencia",ref)
                .appendQueryParameter("numof",lote)
                .appendQueryParameter("armazem",String.valueOf(armazem))
                .appendQueryParameter("zona",zona)
                .appendQueryParameter("alveolo",alveolo)
                .appendQueryParameter("resetLocal",String.valueOf(resetLocal))
                .build();


        VolleyRequest volleyRequest = new VolleyRequest(context);

        volleyRequest.setOnVolleyResultListener(new VolleyRequest.OnVolleyResultListener() {
            @Override
            public void onSuccess(String response) {

                ArrayList<DataModelBi> linhasConsumoOf = new ArrayList<DataModelBi>();

                try {

                    Gson gson = new Gson();

                    linhasConsumoOf = gson.fromJson(response,new TypeToken<ArrayList<DataModelBi>>() {}.getType());

                } catch (JsonParseException exception) {

                    Dialogos.dialogoErro("Erro no serviço GetLinhasLocConsumo",exception.getMessage(),5, (Activity) context,false);

                    mOnGetLinhasLocConsumoListener.onError(exception.getMessage());

                } finally {

                    mOnGetLinhasLocConsumoListener.onSuccess(linhasConsumoOf);

                }


            }

            @Override
            public void onError(VolleyError error) {
                mOnGetLinhasLocConsumoListener.onError(error.getMessage());
            }
        });

        volleyRequest.setDialogMessage(dialogMessage);
        volleyRequest.VolleyStringRequestGet(uri);

    }

    public interface OnGetLinhasLocConsumoListener {

        void onSuccess(ArrayList<DataModelBi> linhasConsumosOF);
        void onError(String error);

    }

    public void setOnGetLinhasLocConsumoListener(OnGetLinhasLocConsumoListener mOnGetLinhasLocConsumoListener) {
        this.mOnGetLinhasLocConsumoListener = mOnGetLinhasLocConsumoListener;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }
}
