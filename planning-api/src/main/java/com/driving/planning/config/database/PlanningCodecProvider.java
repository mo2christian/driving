package com.driving.planning.config.database;

import com.driving.planning.common.hourly.Hourly;
import com.driving.planning.common.hourly.HourlyCodec;
import com.driving.planning.monitor.absent.Absent;
import com.driving.planning.monitor.absent.AbsentCodec;
import com.driving.planning.student.reservation.Reservation;
import com.driving.planning.student.reservation.ReservationCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class PlanningCodecProvider implements CodecProvider {

    @Override
    public <T> Codec<T> get(Class<T> aClass, CodecRegistry codecRegistry) {
        if (aClass == Hourly.class){
            return (Codec<T>) new HourlyCodec();
        }
        if (aClass == Reservation.class){
            return (Codec<T>) new ReservationCodec();
        }
        if (aClass == Absent.class){
            return (Codec<T>) new AbsentCodec();
        }
        return null;
    }

}
