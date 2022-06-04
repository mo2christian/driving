package com.driving.planning.config.database;

import com.driving.planning.student.reservation.Reservation;
import com.driving.planning.student.reservation.ReservationCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class PlanningCodecProvider implements CodecProvider {

    @Override
    public <T> Codec<T> get(Class<T> aClass, CodecRegistry codecRegistry) {
        if (aClass == Reservation.class){
            return (Codec<T>) new ReservationCodec();
        }
        return null;
    }

}
