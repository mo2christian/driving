package com.driving.planning.student;

import com.driving.planning.common.constraint.Email;
import com.driving.planning.common.constraint.PhoneNumber;
import com.driving.planning.student.reservation.Reservation;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@RegisterForReflection
public class StudentDto implements Serializable {

    public static final long serialVersionUID = 1L;

    private String id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @PhoneNumber
    private String phoneNumber;

    @Email
    private String email;

    private Set<Reservation> reservations;

    public StudentDto() {
        reservations = new HashSet<>();
    }

    public void addReservation(Reservation reservation){
        reservations.add(reservation);
    }

    public void removeReservation(Reservation reservation){
        reservations.remove(reservation);
    }

    public Set<Reservation> getReservations() {
        return Set.copyOf(reservations);
    }

    public void setReservations(Set<Reservation> reservations) {
        this.reservations = new HashSet<>(reservations);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
