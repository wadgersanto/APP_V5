package gnotus.inoveplastika;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import static android.content.ContentValues.TAG;


public class EditTextBarCodeReader implements TextView.OnEditorActionListener, TextView.OnClickListener{

    private EditText mEditText;

    private OnGetScannedTextListener mScannedTextListener;

    private String oldEditTextInputValue = "";
    private String scannedText; // Texto que foi lido pelo scanner;

    private Activity mActivity;

    private OnTouchListener blockedOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    };

    private Boolean allowUserInputOnEditText = false;

    // se, a cada leitura do código de barras, é substituido o texto existente anteriormente na caixa de texto
    private Boolean replaceTextOnBarCodeReading = false;
    private Boolean clearEditTextAfterRead = false;

    public EditTextBarCodeReader(EditText editText, Activity activity){

        mActivity = activity;
        mEditText = editText;

        mEditText.setMaxLines(1);
        mEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mEditText.setOnEditorActionListener(this);
        mEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        // necessário que tenha implementado o Interface Onclick, caso contrário depois do onEditorAction perde o foco (p.ex no caso de um alert dialog
        mEditText.setOnClickListener(this);

        mEditText.setLongClickable(false);
        mEditText.setShowSoftInputOnFocus(false);


        // mEditText.setOnTouchListener(blockedOnTouchListener);

        try { mScannedTextListener = (OnGetScannedTextListener) activity; }
        catch (Exception e) {}


    }


    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

        Log.e(TAG, "onEditorAction: KeyEvent "+ keyEvent );
        Log.e(TAG, "onEditorAction: actionId "+ actionId );

        int mEditTextImeOption = getEditTextImeOptions();

        // If the event is a key-down event on the "enter" button
        if (actionId == EditorInfo.IME_ACTION_SEND ||actionId == mEditTextImeOption || ( keyEvent != null &&  keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER))
        {

            scannedText = mEditText.getText().toString();

            // if (allowUserInputOnEditText) return false;

            if (replaceTextOnBarCodeReading) {

                if (oldEditTextInputValue.equals("")) {
                    oldEditTextInputValue = mEditText.getText().toString();
                }
                else {
                    mEditText.setText(mEditText.getText().toString().substring(oldEditTextInputValue.length(),mEditText.getText().length()));
                    oldEditTextInputValue = mEditText.getText().toString();
                }

                mEditText.setSelection(mEditText.getText().toString().length());
            }

            if (mScannedTextListener != null) {
                mScannedTextListener.onGetScannedText(mEditText.getText().toString(), mEditText);
            }

            if (clearEditTextAfterRead) {
                mEditText.setText("");
                oldEditTextInputValue = "";
            }

            mEditText.requestFocus();
            mEditText.setSelection(mEditText.getText().toString().length());

            return true;
        }

        return false;

    }


    public void setOnGetScannedTextListener(@Nullable OnGetScannedTextListener onGetScannedTextListener) {
        mScannedTextListener = onGetScannedTextListener;
    }


    public int getEditTextImeOptions() {
        return mEditText.getImeOptions();
    }

    public void setOldEditTextInputValue(String oldEditTextInputValue) {
        this.oldEditTextInputValue = oldEditTextInputValue;
    }

    public void setReplaceTextOnBarCodeReading(Boolean replaceText) {
        replaceTextOnBarCodeReading = replaceText;
    }

    public void setOnTouchListenerIsBlocked(boolean blocked) {

        if (blocked) {
            mEditText.setOnTouchListener(blockedOnTouchListener);

        }
        else {
            mEditText.setOnTouchListener(null);
        }

    }


    public void setAllowUserInput(Boolean allowUserInput) {

        allowUserInputOnEditText = allowUserInput;

        if (allowUserInput) {

            mEditText.setLongClickable(true);
            mEditText.setShowSoftInputOnFocus(true);
            mEditText.setOnTouchListener(null);

        }

        else {

            mEditText.setLongClickable(false);
            mEditText.setShowSoftInputOnFocus(false);

        }

    };

    public String getScannedText() {
        return scannedText;
    }

    public Boolean getAllowUserInputOnEditText() {
        return allowUserInputOnEditText;
    }

    @Override
    public void onClick(View view) {

    }

    public interface OnGetScannedTextListener {
        void onGetScannedText(String scannedText, EditText editText);
    }

    public void setText(String text) {

        mEditText.setText(text);
        oldEditTextInputValue = text;
        mEditText.setSelection(mEditText.getText().toString().length());

    }



    public void setClearEditTextAfterRead(Boolean clearEditTextAfterRead) {
        this.clearEditTextAfterRead = clearEditTextAfterRead;
    }


}
