package mx.com.satoritech.satorifinger.models;

import com.google.gson.annotations.SerializedName;

public class User {
    private long id;
    @SerializedName("full_name")
    private String fullName;
    private Job job;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }
}
