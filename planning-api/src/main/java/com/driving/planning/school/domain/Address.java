package com.driving.planning.school.domain;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;

public class Address {

    @BsonProperty("town")
    private String town;

    @BsonProperty("postal_code")
    private String postalCode;

    @BsonProperty("path")
    private String path;

    public Address(){
        //default constructor
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var address = (Address) o;
        return town.equals(address.town) && postalCode.equals(address.postalCode) && path.equals(address.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(town, postalCode, path);
    }
}
