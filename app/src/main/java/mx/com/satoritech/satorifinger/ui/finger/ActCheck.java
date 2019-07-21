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

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;
import androidx.databinding.DataBindingUtil;

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
        String[] registryTypesArray = new String[registryTypes.size() + 1];

        registryTypesArray[0] = "Seleccione un tipo de registro";

        for (int i = 0; i < registryTypes.size(); i++) {
            registryTypesArray[i + 1] = registryTypes.get(i).getName();
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.dropdown_registry_type,
                        registryTypesArray);

        vBind.spRegistryTypes.setAdapter(adapter);
        vBind.spRegistryTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                registryTypeId = id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                                ImageViewCompat.setImageTintList(
                                        vBind.ivFinger,
                                        ColorStateList.valueOf(getResources().getColor(R.color.red)));
                            } else {
                                vBind.tvMessages.setText("Huella reconocida");
                                ImageViewCompat.setImageTintList(
                                        vBind.ivFinger,
                                        ColorStateList.valueOf(getResources().getColor(R.color.green)));
                                Registry registry = new Registry();
                                registry.setFkIdEmployee(userId);
                                registry.setFkIdRegistryType(2);
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
                        } else {
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
