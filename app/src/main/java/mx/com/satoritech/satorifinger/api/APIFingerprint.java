package mx.com.satoritech.satorifinger.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import mx.com.satoritech.satorifinger.api.serializers.BooleanDeserializer;
import mx.com.satoritech.satorifinger.api.serializers.BooleanSerializer;
import mx.com.satoritech.satorifinger.api.serializers.DateDeserializaer;
import mx.com.satoritech.satorifinger.api.serializers.DateSerializer;
import mx.com.satoritech.satorifinger.models.Fingerprint;
import mx.com.satoritech.satorifinger.models.GenericResponse;
import mx.com.satoritech.satorifinger.models.User;
import mx.com.satoritech.satorifinger.utils.callbacks.CBGeneric;
import mx.com.satoritech.satorifinger.utils.callbacks.CBSuccess;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.PUT;

public class APIFingerprint {
    private static final String TAG = "API_FINGERPRINT";
    private static APIFingerprint instance;
    private FingerService fingerService;


    public static APIFingerprint getInstance() {
        return instance == null ? new APIFingerprint() : instance;
    }

    private APIFingerprint() {
        //Request log interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Boolean.class, new BooleanSerializer())
                .registerTypeAdapter(Boolean.class, new BooleanDeserializer())
                .registerTypeAdapter(boolean.class, new BooleanSerializer())
                .registerTypeAdapter(boolean.class, new BooleanDeserializer())
                .registerTypeAdapter(Date.class, new DateSerializer())
                .registerTypeAdapter(Date.class, new DateDeserializaer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.serverPath)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClientBuilder.build())
                .build();

        fingerService = retrofit.create(FingerService.class);
    }

    public void getEmployees(CBSuccess<ArrayList<User>> cb) {
        doRequest(
                "Get all employees",
                fingerService.getEmployees(),
                cb
        );
    }

    public void createEmployeeFinger(Fingerprint fingerprint, CBSuccess<GenericResponse> cb) {
        doRequest(
                "Create fingerprint employee",
                fingerService.fingerPrintCreate(fingerprint),
                cb
        );
    }


    @SuppressWarnings("unchecked")
    private void doRequest(final String operation, Call call, final CBSuccess cb) {
        call.enqueue(new Callback() {
            @SuppressWarnings("NullableProblems")
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    cb.onResponse(true, response.body());
                } else {
                    handleUnsuccessful(operation, cb);
                }
            }

            @SuppressWarnings("NullableProblems")
            @Override
            public void onFailure(Call call, Throwable t) {
                handleFailure(operation, t, cb);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void handleUnsuccessful(String operation, CBSuccess callback) {
        Log.w(TAG, operation + " was unsuccessful");
        callback.onResponse(false, null);
    }

    @SuppressWarnings("unchecked")
    private void handleFailure(String operation, Throwable t,
                               CBSuccess callback) {
        Log.e(TAG, operation + " has failed");
        Log.e(TAG, "Message is: " + t.getMessage());
        callback.onResponse(false, null);
    }

}
