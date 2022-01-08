package com.driving.planning.monitor;

import com.driving.planning.common.hourly.Hourly;
import com.driving.planning.monitor.absent.Absent;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@RegisterForReflection
public class Monitor implements Serializable {

    public static final String COLLECTION_NAME = "monitor";

    @BsonProperty("_id")
    @BsonId
    private ObjectId id;

    @BsonProperty("first_name")
    private String firstName;

    @BsonProperty("last_name")
    private String lastName;

    @BsonProperty("phone_number")
    private String phoneNumber;

    @BsonProperty("work_days")
    private Set<Hourly> workDays;

    @BsonProperty("absents")
    private Set<Absent> absents;

    public Monitor(){
        workDays = new HashSet<>();
        absents = new HashSet<>();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<Hourly> getWorkDays() {
        return workDays;
    }

    public void setWorkDays(Set<Hourly> workDays) {
        if (workDays != null){
            this.workDays = workDays;
        }
    }

    public Set<Absent> getAbsents() {
        return absents;
    }

    public void setAbsents(Set<Absent> absents) {
        if (absents != null){
            this.absents = absents;
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
