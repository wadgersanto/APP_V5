package gnotus.inoveplastika;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;




public class EditTextOps {

    public static String getEditTextStr(android.widget.EditText editText) {
        return editText.getText().toString();
    }


    public static boolean isEditTextEmpty(android.widget.EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

    public static void clearEditText(EditText editText) {

        editText.setText("");

    }

    public static void setEditTextHint(EditText editText, String hint) {

        editText.setHint(hint);

    }

    public static void defineTextView(TextView view, String value, String value2, String valueIfNull)
    {
        if(value2==null)
        {
            value2="";
        }

        if(value==null || value.isEmpty())
        {
            view.setText(valueIfNull);
        }
        else
        {
            view.setText(String.valueOf(value)+" "+ String.valueOf(value2));
        }
    }

    public static String concatStringsWithSpace(String string1, String string2){
        return string1+" "+string2;
    }

    public static boolean defineTextView(String value, String value2)
    {
        if(value==null || value.isEmpty())
        {
            if (value2 == null)return false;
        }
        return true;
    }
    public static boolean defineTextView(String value, Object value2)
    {
        if(value==null|| value.isEmpty())
        {
            if (value2 == null)return false;
        }
        return true;
    }

    public static boolean checkEditTextIsEmpty(EditText editText)
    {
        if(editText.getText().toString().trim().isEmpty() || editText.getText().toString().trim().equals("0"))
            return true;
        return false;
    }


    public static void hideKeyboard(Activity activity, EditText view)
    {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboard(Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static void showKeyboard(Activity activity)
    {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
//            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }


}
