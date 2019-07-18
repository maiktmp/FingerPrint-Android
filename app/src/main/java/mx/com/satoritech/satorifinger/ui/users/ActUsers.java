package mx.com.satoritech.satorifinger.ui.users;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;

import mx.com.satoritech.satorifinger.R;
import mx.com.satoritech.satorifinger.databinding.ActUsersBinding;
import mx.com.satoritech.satorifinger.interactors.ClientInteractor;
import mx.com.satoritech.satorifinger.models.User;
import mx.com.satoritech.satorifinger.ui.finger.ActCheck;
import mx.com.satoritech.satorifinger.ui.finger.ActCreateFinger;

public class ActUsers extends AppCompatActivity {

    private ActUsersBinding vBind;
    private ClientInteractor clientInteractor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        vBind = DataBindingUtil.setContentView(this, R.layout.act_users);
        clientInteractor = ClientInteractor.getInstance();
        clientInteractor.getEmployees(this::setUpRecyclerView);
    }

    private void setUpRecyclerView(ArrayList<User> userList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        vBind.rvUsers.setLayoutManager(layoutManager);
        vBind.rvUsers.setItemAnimator(new DefaultItemAnimator());

        Adapter adapter = new Adapter(userList,
                this,
                user -> {
                    Intent i = new Intent(this, ActCheck.class);
                    i.putExtra(ActCheck.USER_ID, user.getId());
                    startActivity(i);
                },
                user -> {
                    Intent i = new Intent(this, ActCreateFinger.class);
                    i.putExtra(ActCreateFinger.USER_ID, user.getId());
                    startActivity(i);
                });

        vBind.rvUsers.setAdapter(adapter);
    }
}
