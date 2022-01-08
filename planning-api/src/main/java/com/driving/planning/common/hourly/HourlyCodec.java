package com.driving.planning.common.hourly;

import com.driving.planning.common.DatePattern;
import com.mongodb.MongoClientSettings;
import org.bson.BsonReader;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class HourlyCodec implements CollectibleCodec<Hourly> {

    private final Codec<Document> documentCodec;
    private final DateTimeFormatter formatter;

    public HourlyCodec(){
        documentCodec = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);
        formatter = DateTimeFormatter.ofPattern(DatePattern.TIME);
    }

    @Override
    public Hourly generateIdIfAbsentFromDocument(Hourly hourly) {
        return hourly;
    }

    @Override
    public boolean documentHasId(Hourly hourly) {
        return false;
    }

    @Override
    public BsonValue getDocumentId(Hourly hourly) {
        return null;
    }

    @Override
    public Hourly decode(BsonReader bsonReader, DecoderContext decoderContext) {
        var document = documentCodec.decode(bsonReader, decoderContext);
        var hourly = new Hourly();
        hourly.setBegin(LocalTime.parse(document.get("begin", String.class), formatter));
        hourly.setEnd(LocalTime.parse(document.get("end", String.class), formatter));
        hourly.setDay(Day.parse(document.get("day", String.class)));
        return hourly;
    }

    @Override
    public void encode(BsonWriter bsonWriter, Hourly hourly, EncoderContext encoderContext) {
        var doc = new Document();
        doc.put("begin", formatter.format(hourly.getBegin()));
        doc.put("end", formatter.format(hourly.getEnd()));
        doc.put("day", hourly.getDay().getValue());
        documentCodec.encode(bsonWriter, doc, encoderContext);
    }

    @Override
    public Class<Hourly> getEncoderClass() {
        return Hourly.class;
    }
}
