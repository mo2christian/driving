package com.driving.planning.student.otp;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.io.Serializable;
import java.time.LocalDateTime;

@MongoEntity(collection = OTP.COLLECTION_NAME)
public class OTP implements Serializable {

    public static final String COLLECTION_NAME = "otp";

    @BsonProperty("student_id")
    private String studentId;

    @BsonProperty("content")
    private String content;

    @BsonProperty("created_date")
    private LocalDateTime createdDate;

    public OTP() {
        createdDate = LocalDateTime.now();
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
