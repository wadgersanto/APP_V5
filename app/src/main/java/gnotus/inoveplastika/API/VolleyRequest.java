package gnotus.inoveplastika.API;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import gnotus.inoveplastika.Dialogos;
import gnotus.inoveplastika.Globals;

public class VolleyRequest {

    //Timeout defined to 1min (60*1000ms)
    private static final int requestTimeout = Globals.getInstance().getmVolleyTimeOut();
    //Number of retries defined to 0
    private static final int requestMaxNumRetries = 0 ;

    private boolean shoudCache = false;
    private boolean clearCache = true;
    private int dialogErrorTimeout = 4;
    private String dialogMessage = "";

    Context context;
    private ProgressDialog progDailog;

    OnVolleyResultListener onVolleyResultListener;

    public VolleyRequest(Context c) {
        this.context = c;
        progDailog = new ProgressDialog(context);
    }


    public void VolleyStringRequestGet(Uri uri) {

        RequestQueue queue = Volley.newRequestQueue(context);

        progDailog.setMessage(dialogMessage);
        progDailog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (progDailog != null) progDailog.dismiss();
                        onVolleyResultListener.onSuccess(response);

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (progDailog != null) progDailog.dismiss();
                Dialogos.dialogoErro("Ocorreu um erro no processamento do pedido",error.getMessage(),dialogErrorTimeout, (Activity) context,false);
                onVolleyResultListener.onError(error);
            }
        });


        // Add the request to the RequestQueue.
        System.out.println(uri.toString());

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(requestTimeout,
                requestMaxNumRetries, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        stringRequest.setShouldCache(shoudCache);
        if (clearCache) {
            queue.getCache().clear();
        }

        queue.add(stringRequest);

    }

    public void VolleyJsonObjectRequest(Uri uri, JSONObject jsonObject) {

        Log.d("VolleyRequest","jsonObject :"+jsonObject);


        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, uri.toString(), jsonObject,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    progDailog.dismiss();
                    onVolleyResultListener.onSuccess(response.toString());

                }
            }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                progDailog.dismiss();
                onVolleyResultListener.onError(error);

            }
        });

        progDailog.setMessage(dialogMessage);
        progDailog.show();

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(requestTimeout,
                requestMaxNumRetries, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        jsonObjectRequest.setShouldCache(shoudCache);
        if (clearCache) {
            queue.getCache().clear();
        }

        queue.add(jsonObjectRequest);

    }


    public interface OnVolleyResultListener {
        void onSuccess(String response);
        void onError(VolleyError error);
    }

    public void setOnVolleyResultListener(OnVolleyResultListener onVolleyResultListener) {
        this.onVolleyResultListener = onVolleyResultListener;
    }

    public static int getRequestTimeout() {
        return requestTimeout;
    }

    public static int getRequestMaxNumRetries() {
        return requestMaxNumRetries;
    }

    public boolean isShoudCache() {
        return shoudCache;
    }

    public void setShoudCache(boolean shoudCache) {
        this.shoudCache = shoudCache;
    }

    public boolean isClearCache() {
        return clearCache;
    }

    public void setClearCache(boolean clearCache) {
        this.clearCache = clearCache;
    }

    public void setDialogErrorTimeout(int dialogErrorTimeout) {
        this.dialogErrorTimeout = dialogErrorTimeout;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }
}
