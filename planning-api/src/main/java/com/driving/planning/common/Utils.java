package com.driving.planning.common;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;

import java.time.LocalDate;
import java.time.LocalTime;

public class Utils {

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean between(LocalDate begin, LocalDate value, LocalDate end){
        return (begin.isBefore(value) || begin.equals(value)) && (value.isBefore(end) || value.isEqual(end));
    }

    public static boolean between(LocalTime begin, LocalTime value, LocalTime end){
        return (begin.isBefore(value) || begin.equals(value)) && value.isBefore(end);
    }

    public static TransactionOptions transactionOptions(){
        return TransactionOptions.builder()
                .readPreference(ReadPreference.primary())
                .readConcern(ReadConcern.LOCAL)
                .writeConcern(WriteConcern.MAJORITY)
                .build();
    }

}
