package mx.com.satoritech.satorifinger.ui.utils;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import mx.com.satoritech.satorifinger.R;

public class Utils {

    public static void setupToolbarAsBackBtn(@NonNull Toolbar toolbar,
                                             @NonNull AppCompatActivity mActivity) {
        mActivity.setSupportActionBar(toolbar);

        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_left_arrow);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

}
