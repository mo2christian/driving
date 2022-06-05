package com.driving.planning.config.database;

import com.driving.planning.common.hourly.Day;
import com.driving.planning.common.hourly.DayCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class PlanningCodecProvider implements CodecProvider {
    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry codecRegistry) {
        if (clazz == Day.class) {
            return (Codec<T>) new DayCodec();
        }
        return null;
    }
}
