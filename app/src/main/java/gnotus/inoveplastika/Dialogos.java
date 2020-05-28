package gnotus.inoveplastika;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Alexandre on 09/12/2017.
 */



public class Dialogos {

    public int msgboxYesNoResultado;

    public static void dialogoInfo(String title, String subtitle, Double tempo, final Activity activity, final boolean finishActivity) {

        tempo = tempo*1000;

        ImageView image_info = new ImageView(activity);

        image_info.setImageResource(R.mipmap.ic_info);

        AlertDialog.Builder builderInfo = new AlertDialog.Builder(activity);

        builderInfo.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface d, int arg1) {
                d.dismiss();
            };
        });

        builderInfo.setTitle(title);
        builderInfo.setMessage(subtitle);
        builderInfo.setCancelable(true);

        builderInfo.setTitle(title);
        builderInfo.setMessage(subtitle);
        builderInfo.setCancelable(false);

        builderInfo.setView(image_info);

        final AlertDialog alertInfo = builderInfo.create();
        alertInfo.show();

        if (tempo > 0) {
            new CountDownTimer(tempo.intValue(), 500) {

                @Override
                public void onTick(long millisUntilFinished) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onFinish() {
                    // TODO Auto-generated method stub

                    alertInfo.dismiss();
                    if (finishActivity){
                        activity.finish();
                    }
                }
            }.start();
        }

    }

    public static void dialogoErro(String title, String subtitle, int tempo, final Activity activity, final boolean finishActivity) {

        ImageView image_ok = new ImageView(activity);
        ImageView image = new ImageView(activity);

        image.setImageResource(R.mipmap.ic_notok);
        image_ok.setImageResource(R.mipmap.ic_ok);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);


        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface d, int arg1) {
                d.dismiss();
            };
        });

        builder1.setTitle(title);
        builder1.setMessage(subtitle);
        builder1.setCancelable(true);


        builder1.setView(image);

        final AlertDialog alertErro = builder1.create();
        alertErro.show();

        if (tempo > 0) {
            new CountDownTimer(tempo*1000, 500) {

                @Override
                public void onTick(long millisUntilFinished) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onFinish() {
                    // TODO Auto-generated method stub

                    alertErro.dismiss();
                    if (finishActivity){
                        activity.finish();
                    }
                }
            }.start();
        }

    }

    public static void dialogoSucess(String title, String subtitle, int tempo, final Activity activity, final boolean finishActivity) {

        ImageView image_ok = new ImageView(activity);

        image_ok.setImageResource(R.mipmap.ic_ok);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);


        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface d, int arg1) {
                d.dismiss();
            };
        });

        builder1.setTitle(title);
        builder1.setMessage(subtitle);
        builder1.setCancelable(true);


        builder1.setView(image_ok);

        final AlertDialog alertErro = builder1.create();
        alertErro.show();

        if (tempo > 0) {
            new CountDownTimer(tempo*1000, 500) {

                @Override
                public void onTick(long millisUntilFinished) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onFinish() {
                    // TODO Auto-generated method stub

                    alertErro.dismiss();
                    if (finishActivity){
                        activity.finish();
                    }
                }
            }.start();
        }

    }

    public static int msgboxYesNo (String title, final String Subtitle, final String positivButtonText, String negativeButtonText,
                                   final Context context){

        final int mResultado[]= new int[1];

        AlertDialog.Builder builder  = new AlertDialog.Builder(context);
        builder.setMessage("Deseja finalizar o inventário?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {

                mResultado[1] = 1;
            }
        });

        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog myDialog  = builder.create();
        myDialog.show();

        return  mResultado[1];

    }

    public static String retiraDecimais(Double numero){

        String strnumero;

        strnumero = String.format("%.3f",numero).replace(",",".");

        boolean processa = true;

        while (strnumero.indexOf(".") >= 0 && ( strnumero.substring(strnumero.length()-1).equals("0") || strnumero.substring(strnumero.length()-1).equals(".") )) {
            strnumero  = strnumero.substring(0,strnumero.length()-1);
        }

        return strnumero;

    }

//    public static String[] mySplit(String s)
//    {
//       return String [] ;
//    }

    public static void showToast(Context context,CharSequence text,Double duration,int textSize) {

        duration = duration * 1000;

        final Toast mToast              = Toast.makeText(context,text,Toast.LENGTH_LONG);
        ViewGroup group          = (ViewGroup) mToast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(textSize);

        // Set the countdown to display the toast
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(duration.intValue(), 1000 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                // mToast.show();
            }
            public void onFinish() {
                mToast.cancel();
            }
        };

        // Show the toast and starts the countdown
        mToast.show();

        if(duration > 0) toastCountDown.start();

    }

}
