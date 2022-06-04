package com.driving.planning.account.domain;


import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonProperty;

@MongoEntity(collection = Account.COLLECTION_NAME)
public class Account {

    public static final String COLLECTION_NAME = "account";

    @BsonProperty("email")
    private String email;

    @BsonProperty("password")
    private String password;

    public Account() {
        //default constructor
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
