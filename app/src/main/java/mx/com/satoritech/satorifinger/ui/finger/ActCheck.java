package mx.com.satoritech.satorifinger.ui.finger;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import mx.com.satoritech.satorifinger.R;
import mx.com.satoritech.satorifinger.databinding.ActCheckBinding;
import mx.com.satoritech.satorifinger.ui.utils.Utils;

public class ActCheck extends AppCompatActivity {
    public static final String USER_ID = "userId";
    private ActCheckBinding vBind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vBind = DataBindingUtil.setContentView(this, R.layout.act_check);
        setUpToolbar();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setUpToolbar() {
        Utils.setupToolbarAsBackBtn(vBind.toolbar, this);
    }
}
