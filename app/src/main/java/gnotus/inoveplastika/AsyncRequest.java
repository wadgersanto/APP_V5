package gnotus.inoveplastika;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;


public class AsyncRequest extends AsyncTask<String, Integer, String> {

    OnAsyncRequestComplete caller;
    Context context;
    int op;
    String dialogMessage = "";


    private ProgressDialog progDailog;
    private boolean STATUS_FLAG = false;


    public AsyncRequest(Activity a, int op) {

        caller = (OnAsyncRequestComplete) a;
        context = a;
        this.op = op;

        dialogMessage = context.getResources().getString(R.string.progressdialog_mensagem);

        progDailog = new ProgressDialog(context);

    }


    public void onPreExecute() {

        super.onPreExecute();
        System.out.println("onPreExecute");

        progDailog.setMessage(dialogMessage);
        progDailog.setIndeterminate(true);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(false);
        progDailog.show();


    }

    public String doInBackground(String... urls) {

        if (new CheckNetwork(context).isNetworkAvailable()) {
            String address = urls[0].toString();
            STATUS_FLAG = false;

            return get(address);

        } else {

            STATUS_FLAG = true;
            return null;

        }

    }

    public void onProgressUpdate(Integer... progress) {
    }


    public void onPostExecute(String response) {
        caller.asyncResponse(response, op);

        ImageView image = new ImageView(context);
        image.setImageResource(R.mipmap.ic_notok);


        if (STATUS_FLAG) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setTitle(context.getResources().getString(R.string.alert_erro_sem_internet));
            builder1.setMessage(context.getResources().getString(R.string.alert_mensagem_sem_internet));
            builder1.setCancelable(true);

            builder1.setView(image);

            final AlertDialog alert11 = builder1.create();
            alert11.show();

            new CountDownTimer(2000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onFinish() {
                    // TODO Auto-generated method stub

                    alert11.dismiss();
                }
            }.start();
        }

        if (progDailog.isShowing()) {
            progDailog.dismiss();
        }
        //progDailog.dismiss();
    }

    protected void onCancelled(String response) {

        caller.asyncResponse(response, op);
    }

    private String get(String address) {

        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(address);

        System.out.println("Resposta:" + jsonStr);

        return jsonStr;
    }

    public void setOnAsyncRequestComplete(OnAsyncRequestComplete newOnAsyncRequestComplete) {

        caller = newOnAsyncRequestComplete;

    }

    // Interface to be implemented by calling activity
    public interface OnAsyncRequestComplete {

        void asyncResponse(String response, int op);

    }


    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }
}