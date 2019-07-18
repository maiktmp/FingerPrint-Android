package mx.com.satoritech.satorifinger.ui;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import mx.com.satoritech.satorifinger.R;


/**
 * Created by TEA 2 on 3/20/2018.
 */
@SuppressWarnings("unused")
public class Dialogs {

    public static void alert(Context mContext,
                             @StringRes int title,
                             @StringRes int message) {
        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    public static void alert(Context mContext,
                             @StringRes int title,
                             String message,
                             @StringRes int okBtnText,
                             AlertDialog.OnClickListener onOk) {
        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(okBtnText, onOk)
                .show();
    }

    static void alert(Context mContext, @StringRes int title,
                      String message) {
        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    public static void alert(Context mContext, @StringRes int message) {
        new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    public static void alert(Context mContext, @StringRes int message,
                             AlertDialog.OnClickListener onOk) {
        new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setPositiveButton(R.string.ok, onOk)
                .show();
    }

    public static void alert(Context mContext, String message) {
        new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    public static void alert(Context mContext,
                             String message,
                             @StringRes int okBtnText,
                             AlertDialog.OnClickListener onOk) {
        new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .setPositiveButton(okBtnText, onOk)
                .show();
    }

    public static void alert(Context mContext,
                             String message,
                             AlertDialog.OnDismissListener onDismissListener) {
        new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .setOnDismissListener(onDismissListener)
                .show();
    }


    public static void alert(Context mContext,
                             String title,
                             String message,
                             AlertDialog.OnClickListener onOk) {
        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, onOk)
                .show();
    }

    public static void confirm(Context mContext, @StringRes int message,
                               @StringRes int okBtnText,
                               @StringRes int cancelBtnText,
                               AlertDialog.OnClickListener onOk,
                               AlertDialog.OnClickListener onCancel) {
        new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setPositiveButton(okBtnText, onOk)
                .setNegativeButton(cancelBtnText, onCancel)
                .show();
    }

    public static android.app.AlertDialog progress(Context mContext,
                                                   @StringRes int message) {
        ProgressDialog pDialog = new ProgressDialog(mContext);
        pDialog.setTitle(null);
        pDialog.setMessage(mContext.getString(message));
        pDialog.setCancelable(false);
        pDialog.show();

        return pDialog;
    }
}
