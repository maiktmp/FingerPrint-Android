package mx.com.satoritech.satorifinger.ui.finger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fpreader.fpdevice.Constants;
import com.fpreader.fpdevice.UsbReader;

import java.util.ArrayList;
import java.util.Base64;

import mx.com.satoritech.satorifinger.R;
import mx.com.satoritech.satorifinger.databinding.ActCheckBinding;
import mx.com.satoritech.satorifinger.interactors.ClientInteractor;
import mx.com.satoritech.satorifinger.models.Fingerprint;
import mx.com.satoritech.satorifinger.models.Registry;
import mx.com.satoritech.satorifinger.models.RegistryType;
import mx.com.satoritech.satorifinger.ui.Dialogs;
import mx.com.satoritech.satorifinger.ui.utils.Utils;

public class ActCheck extends AppCompatActivity {
    public static final String USER_ID = "userId";
    private long userId;
    long registryTypeId = 0;
    private ClientInteractor clientInteractor;
    private ActCheckBinding vBind;
    private Context mContext = this;
    UsbReader fingerPrint;
    boolean isWorking = false;
    boolean fingerReadingOk = false;

    byte refdata[] = new byte[512];
    int refsize[] = new int[1];
    byte matdata[] = new byte[512];
    int matsize[] = new int[1];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getLongExtra(USER_ID, 0);
        clientInteractor = ClientInteractor.getInstance();
        vBind = DataBindingUtil.setContentView(this, R.layout.act_check);
        showProgressBar();
        setUpToolbar();
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.O) {
            Dialogs.alert(this, R.string.message_api, (dialog, s) -> finish());
        }
        clientInteractor.getRegistryTypes(this::setUpRegistryTypes);
        clientInteractor.getFingersprintEmployee(userId, fingersList -> {
            hideProgressBar();
            setUpFingerPrint(fingersList);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setUpRegistryTypes(ArrayList<RegistryType> registryTypes) {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        vBind.rvRegistryTypes.setLayoutManager(layoutManager);
        vBind.rvRegistryTypes.setItemAnimator(new DefaultItemAnimator());
        Adapter adapter = new Adapter(
                registryTypes,
                registryType -> {
                    this.registryTypeId = registryType.getId();
                    if (fingerReadingOk) createRegistry();
                },
                this);
        vBind.rvRegistryTypes.setAdapter(adapter);
    }

    public void readFinger() {
        if (isWorking) return;
        fingerPrint.GenerateTemplate();
        isWorking = true;
        ImageViewCompat.setImageTintList(
                vBind.ivFinger,
                ColorStateList.valueOf(getResources().getColor(R.color.black))
        );
    }

    public void setUpFingerPrint(ArrayList<Fingerprint> fingerprints) {
        @SuppressLint("HandlerLeak") Handler handler = new Handler() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constants.FPM_DEVICE: {
                        switch (msg.arg1) {
                            case Constants.DEV_ATTACHED:
                                break;
                            case Constants.DEV_OK:
                                isWorking = false;
                                vBind.tvMessages.setText("Dispositivo conectado");
                                readFinger();
                                break;
                            default:
                                vBind.tvMessages.setText("Error al conectar con el dispositivo");
                                break;
                        }
                    }
                    break;
                    case Constants.FPM_GENCHAR: {
                        if (msg.arg1 == 1) {
                            /**
                             * refdata = user finder data
                             * mdata =  current data finger read
                             */
                            boolean fingerSuccess = false;
                            for (Fingerprint fingerprint : fingerprints) {
                                refdata = Base64.getDecoder().decode(fingerprint.getValue());
                                fingerPrint.GetTemplateByGen(matdata, matsize);
                                int mret = fingerPrint.MatchTemplate(refdata, matdata);
                                if (mret >= 70) {
                                    fingerSuccess = true;
                                    break;
                                }
                            }
                            if (!fingerSuccess) {
                                isWorking = false;
                                vBind.tvMessages.setText("Huella no reconocida");
                                ImageViewCompat.setImageTintList(
                                        vBind.ivFinger,
                                        ColorStateList.valueOf(getResources().getColor(R.color.red)));
                                readFinger();
                            } else {
                                vBind.tvMessages.setText("Huella reconocida");
                                ImageViewCompat.setImageTintList(
                                        vBind.ivFinger,
                                        ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                fingerReadingOk = true;
                                if (registryTypeId == 0) {
                                    Toast.makeText(mContext,
                                            "Seleccione un tipo de registro",
                                            Toast.LENGTH_SHORT).show();
                                    break;
                                }
                                createRegistry();
                            }
                        } else {
                            vBind.tvMessages.setText("Huella no reconocida");
                            ImageViewCompat.setImageTintList(
                                    vBind.ivFinger,
                                    ColorStateList.valueOf(getResources().getColor(R.color.red)));
                            readFinger();
                        }
                        isWorking = false;
                    }
                    break;
                    case Constants.FPM_PLACE:
                        vBind.tvMessages.setText("Apoye su huella sobre el sensor.");
                        break;
                    case Constants.FPM_LIFT:
                        vBind.tvMessages.setText("Retire su huella de el sensor");
                        break;
                    case Constants.FPM_TIMEOUT:
                        vBind.tvMessages.setText("Time Out");
                        isWorking = false;
                        break;
                }
            }
        };

        fingerPrint = new UsbReader();
        fingerPrint.InitMatch();
        fingerPrint.SetContextHandler(this, handler);

        if (fingerPrint.OpenDevice() == 0) {
            vBind.tvMessages.setText("Lector conectado");
            readFinger();
        } else {
            fingerPrint.requestPermission();
            vBind.tvMessages.setText("Esperando lector");
        }

    }

    private void createRegistry() {
        Registry registry = new Registry();
        registry.setFkIdEmployee(userId);
        registry.setFkIdRegistryType(registryTypeId);
        clientInteractor.createRegistry(registry, genericResponse -> {
            if (genericResponse == null) {
                Dialogs.alert(mContext,
                        "Ocurrio un error al crear el registro",
                        dialog -> readFinger());
            } else {
                Dialogs.alert(mContext,
                        "Registro creado satisfactoriamente",
                        dialog -> finish());
            }
        });
    }

    private void setUpToolbar() {
        Utils.setupToolbarAsBackBtn(vBind.toolbar, this);
    }

    private void showProgressBar() {
        vBind.ivFinger.setVisibility(View.GONE);
        vBind.progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        vBind.ivFinger.setVisibility(View.VISIBLE);
        vBind.progressBar.setVisibility(View.GONE);
    }
}
