package mx.com.satoritech.satorifinger.interactors;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;

import mx.com.satoritech.satorifinger.api.APIFingerprint;
import mx.com.satoritech.satorifinger.models.Fingerprint;
import mx.com.satoritech.satorifinger.models.GenericResponse;
import mx.com.satoritech.satorifinger.models.User;
import mx.com.satoritech.satorifinger.utils.callbacks.CBGeneric;

public class ClientInteractor {
    private static String TAG = "CLIENT_INTERACTOR";
    private static ClientInteractor instance;
    private APIFingerprint api;

    private ClientInteractor() {
        api = APIFingerprint.getInstance();
    }

    public static ClientInteractor getInstance() {
        return instance == null ? new ClientInteractor() : instance;
    }

    public void getEmployees(CBGeneric<ArrayList<User>> cb) {
        api.getEmployees(((success, employees) -> {
            if (!success) {
                cb.onResult(null);
            } else {
                cb.onResult(employees);
            }
        }));
    }

    public void createFingerPrint(Fingerprint fingerprint, CBGeneric<GenericResponse> cb) {
        api.createEmployeeFinger(fingerprint, (success, generic) -> {
            if (!success) {
                cb.onResult(null);
            } else {
                cb.onResult(generic);
            }
        });
    }
}
