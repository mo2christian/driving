package com.driving.planning.monitor.domain;

import com.driving.planning.common.hourly.Hourly;
import com.driving.planning.monitor.absence.Absence;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@MongoEntity(collection = Monitor.COLLECTION_NAME)
public class Monitor implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private List<Absence> absences;

    public Monitor(){
        workDays = new HashSet<>();
        absences = new ArrayList<>();
    }

    public List<Absence> getAbsences() {
        return absences;
    }

    public void setAbsences(List<Absence> absences) {
        this.absences = absences;
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

    public Set<Hourly> getWorkDays() {
        return workDays;
    }

    public void setWorkDays(Set<Hourly> workDays) {
        if (workDays != null){
            this.workDays = workDays;
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
