package mx.com.satoritech.satorifinger.utils.callbacks;

public interface CBSuccess<T> {
    void onResponse(boolean success, T result);
}

