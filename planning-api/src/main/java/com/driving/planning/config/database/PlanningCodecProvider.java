package com.driving.planning.config.database;

import com.driving.planning.common.hourly.Hourly;
import com.driving.planning.common.hourly.HourlyCodec;
import com.driving.planning.event.domain.EventType;
import com.driving.planning.event.domain.EventTypeCodec;
import com.driving.planning.monitor.absent.AbsentCodec;
import com.driving.planning.monitor.absent.AbsentRequest;
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
        if (aClass == AbsentRequest.class){
            return (Codec<T>) new AbsentCodec();
        }
        if (aClass == EventType.class){
            return (Codec<T>) new EventTypeCodec();
        }
        return null;
    }

}
