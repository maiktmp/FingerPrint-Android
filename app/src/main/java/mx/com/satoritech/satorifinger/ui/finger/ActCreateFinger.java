package mx.com.satoritech.satorifinger.ui.finger;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;
import androidx.databinding.DataBindingUtil;

import com.fpreader.fpdevice.Constants;
import com.fpreader.fpdevice.UsbReader;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Base64;

import mx.com.satoritech.satorifinger.R;
import mx.com.satoritech.satorifinger.databinding.ActCreateFingerBinding;
import mx.com.satoritech.satorifinger.interactors.ClientInteractor;
import mx.com.satoritech.satorifinger.models.Fingerprint;
import mx.com.satoritech.satorifinger.ui.Dialogs;
import mx.com.satoritech.satorifinger.ui.utils.Utils;

public class ActCreateFinger extends AppCompatActivity {
    private ActCreateFingerBinding vBind;
    public static final String USER_ID = "USER_ID";
    private long userId;
    private ClientInteractor clientInteractor;
    UsbReader fingerPrint;
    boolean isWorking = false;

    byte refdata[] = new byte[512];
    int refsize[] = new int[1];
    byte matdata[] = new byte[512];
    int matsize[] = new int[1];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vBind = DataBindingUtil.setContentView(this, R.layout.act_create_finger);
        clientInteractor = ClientInteractor.getInstance();
        userId = getIntent().getLongExtra(USER_ID, 0);
        vBind.btnCreateFingerprint.setOnClickListener(this::createFingerPrint);
        setUpToolbar();
        setUpFingerPrint();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void setUpFingerPrint() {

        @SuppressLint("HandlerLeak") Handler handler = new Handler() {
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
                    case Constants.FPM_PLACE:
                        vBind.tvMessages.setText("Apoye su huella sobre el sensor.");
                        break;
                    case Constants.FPM_LIFT:
                        vBind.tvMessages.setText("Retire su huella de el sensor");
                        break;
                    case Constants.FPM_ENRFPT: {
                        if (msg.arg1 == 1) {
                            vBind.tvMessages.setText("Huella capturada");
                            ImageViewCompat.setImageTintList(
                                    vBind.ivFinger,
                                    ColorStateList.valueOf(getResources().getColor(R.color.green))
                            );
                            fingerPrint.GetTemplateByEnl(refdata, refsize);
                            vBind.btnCreateFingerprint.setEnabled(true);
                        } else {
                            vBind.tvMessages.setText("Error al capturar la huella");
                            ImageViewCompat.setImageTintList(
                                    vBind.ivFinger,
                                    ColorStateList.valueOf(getResources().getColor(R.color.red))
                            );
                            setUpFingerPrint();
                            isWorking = false;
                        }
                        isWorking = false;
                    }
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

    public void readFinger() {
        if (isWorking) return;
        fingerPrint.EnrolTemplate();
        isWorking = true;
        ImageViewCompat.setImageTintList(
                vBind.ivFinger,
                ColorStateList.valueOf(getResources().getColor(R.color.black))
        );
    }

    private void createFingerPrint(View v) {
        Fingerprint fingerprint = new Fingerprint();
        String value = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            value = Base64.getEncoder().encodeToString(refdata);
//            byte[] decode = Base64.getDecoder().decode(value.getBytes());
            fingerprint.setValue(value);
            fingerprint.setFkIdEmployee(userId);
            clientInteractor.createFingerPrint(fingerprint, genericResponse -> {
                if (genericResponse.isSuccess()) {
                    Dialogs.alert(this, "Huella registrada satisfactoriamente", dialog -> finish());
                } else {
                    Dialogs.alert(this, "Ocurrio un error al capturar la huella, itente de nuevo");
                }
            });
        } else {
            Dialogs.alert(this, R.string.message_api, (dialog, s) -> finish());
        }
    }

    private void setUpToolbar() {
        Utils.setupToolbarAsBackBtn(vBind.toolbar, this);
    }
}
