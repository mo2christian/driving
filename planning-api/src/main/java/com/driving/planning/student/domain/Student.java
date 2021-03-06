package com.driving.planning.student.domain;

import com.driving.planning.student.reservation.Reservation;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.List;

@MongoEntity(collection = Student.COLLECTION_NAME)
public class Student implements Serializable {

    public static final String COLLECTION_NAME = "student";

    @BsonProperty("_id")
    @BsonId
    private ObjectId id;

    @BsonProperty("first_name")
    private String firstName;

    @BsonProperty("last_name")
    private String lastName;

    @BsonProperty("email")
    private String email;

    @BsonProperty("phone_number")
    private String phoneNumber;

    @BsonProperty("reservations")
    private List<Reservation> reservations;

    public Student(){
        //default constructor
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}
