package mx.com.satoritech.satorifinger.models;

import com.google.gson.annotations.SerializedName;

public class Fingerprint {

    private String value;
    @SerializedName("fk_id_employee")
    private long fkIdEmployee;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getFkIdEmployee() {
        return fkIdEmployee;
    }

    public void setFkIdEmployee(long fkIdEmployee) {
        this.fkIdEmployee = fkIdEmployee;
    }
}
