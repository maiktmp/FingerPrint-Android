package mx.com.satoritech.satorifinger.api;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import mx.com.satoritech.satorifinger.models.Fingerprint;
import mx.com.satoritech.satorifinger.models.GenericResponse;
import mx.com.satoritech.satorifinger.models.Registry;
import mx.com.satoritech.satorifinger.models.RegistryType;
import mx.com.satoritech.satorifinger.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FingerService {
    @NonNull
    @GET(APIConstants.wsPath + "employees")
    Call<List<User>> getEmployees();

    @NonNull
    @GET(APIConstants.wsPath + "employee/{userId}/fingerprints")
    Call<List<Fingerprint>> getEmployeeFingerPrint(@Path("userId") long userId);

    @NonNull
    @GET(APIConstants.wsPath + "registry_types")
    Call<List<RegistryType>> getRegistryTypes();

    @NonNull
    @POST(APIConstants.wsPath + "fingerprints/create")
    Call<GenericResponse> fingerPrintCreate(@Body @NonNull Fingerprint fingerprint);

    @NonNull
    @POST(APIConstants.wsPath + "registries/create")
    Call<GenericResponse> check(@Body @NonNull Registry registry);


}
