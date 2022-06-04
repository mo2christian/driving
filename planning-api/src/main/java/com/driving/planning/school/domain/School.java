package com.driving.planning.school.domain;

import com.driving.planning.common.hourly.Hourly;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;
import java.util.Set;

@MongoEntity(collection = School.COLLECTION_NAME, database = "base")
public class School {

    public static final String COLLECTION_NAME = "school";

    @BsonProperty("_id")
    @BsonId
    private String pseudo;

    @BsonProperty("name")
    private String name;

    @BsonProperty("phone_number")
    private String phoneNumber;

    @BsonProperty("address")
    private Address address;

    @BsonProperty("work_days")
    private Set<Hourly> workDays;

    public School() {
        //default constructor
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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
        this.workDays = workDays;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var school = (School) o;
        return pseudo.equals(school.pseudo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pseudo);
    }
}
