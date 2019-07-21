package mx.com.satoritech.satorifinger.models;

import com.google.gson.annotations.SerializedName;

public class Registry {
    @SerializedName("fk_id_registry_type")
    private  long fkIdRegistryType;
    @SerializedName("fk_id_employee")
    private  long fkIdEmployee;

    public long getFkIdRegistryType() {
        return fkIdRegistryType;
    }

    public void setFkIdRegistryType(long fkIdRegistryType) {
        this.fkIdRegistryType = fkIdRegistryType;
    }

    public long getFkIdEmployee() {
        return fkIdEmployee;
    }

    public void setFkIdEmployee(long fkIdEmployee) {
        this.fkIdEmployee = fkIdEmployee;
    }
}
