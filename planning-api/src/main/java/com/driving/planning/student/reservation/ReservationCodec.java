package com.driving.planning.student.reservation;

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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ReservationCodec implements CollectibleCodec<Reservation> {

    private final Codec<Document> documentCodec;
    private final DateTimeFormatter timeFormatter;
    private final DateTimeFormatter dayFormatter;

    public ReservationCodec(){
        documentCodec = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);
        timeFormatter = DateTimeFormatter.ofPattern(DatePattern.TIME);
        dayFormatter = DateTimeFormatter.ofPattern(DatePattern.DATE);
    }

    @Override
    public Reservation generateIdIfAbsentFromDocument(Reservation reservation) {
        return null;
    }

    @Override
    public boolean documentHasId(Reservation reservation) {
        return false;
    }

    @Override
    public BsonValue getDocumentId(Reservation reservation) {
        return null;
    }

    @Override
    public Reservation decode(BsonReader bsonReader, DecoderContext decoderContext) {
        var document = documentCodec.decode(bsonReader, decoderContext);
        var reservation = new Reservation();
        reservation.setBegin(LocalTime.parse(document.get("begin", String.class), timeFormatter));
        reservation.setEnd(LocalTime.parse(document.get("end", String.class), timeFormatter));
        reservation.setDate(LocalDate.parse(document.get("date", String.class), dayFormatter));
        return reservation;
    }

    @Override
    public void encode(BsonWriter bsonWriter, Reservation reservation, EncoderContext encoderContext) {
        var doc = new Document();
        doc.put("begin", timeFormatter.format(reservation.getBegin()));
        doc.put("end", timeFormatter.format(reservation.getEnd()));
        doc.put("date", dayFormatter.format(reservation.getDate()));
        documentCodec.encode(bsonWriter, doc, encoderContext);
    }

    @Override
    public Class<Reservation> getEncoderClass() {
        return null;
    }
}
