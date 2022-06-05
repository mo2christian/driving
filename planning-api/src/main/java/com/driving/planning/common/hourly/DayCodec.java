package com.driving.planning.common.hourly;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class DayCodec implements Codec<Day> {
    @Override
    public Day decode(BsonReader bsonReader, DecoderContext decoderContext) {
        return Day.parse(bsonReader.readString());
    }

    @Override
    public void encode(BsonWriter bsonWriter, Day day, EncoderContext encoderContext) {
        if (day != null){
            bsonWriter.writeString(day.getValue());
        }
    }

    @Override
    public Class<Day> getEncoderClass() {
        return Day.class;
    }
}
