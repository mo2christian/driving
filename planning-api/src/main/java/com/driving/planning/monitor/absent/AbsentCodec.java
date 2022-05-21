package com.driving.planning.monitor.absent;

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
import java.time.format.DateTimeFormatter;

public class AbsentCodec implements CollectibleCodec<Absent> {

    private final Codec<Document> documentCodec;
    private final DateTimeFormatter dayFormatter;

    public AbsentCodec(){
        documentCodec = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);
        dayFormatter = DateTimeFormatter.ofPattern(DatePattern.DATE_TIME);
    }

    @Override
    public Absent generateIdIfAbsentFromDocument(Absent absent) {
        return null;
    }

    @Override
    public boolean documentHasId(Absent absent) {
        return false;
    }

    @Override
    public BsonValue getDocumentId(Absent absent) {
        return null;
    }

    @Override
    public Absent decode(BsonReader bsonReader, DecoderContext decoderContext) {
        var document = documentCodec.decode(bsonReader, decoderContext);
        var absent = new Absent();
        absent.setStart(LocalDate.parse(document.get("start", String.class), dayFormatter));
        absent.setEnd(LocalDate.parse(document.get("end", String.class), dayFormatter));
        return absent;
    }

    @Override
    public void encode(BsonWriter bsonWriter, Absent absent, EncoderContext encoderContext) {
        var doc = new Document();
        doc.put("start", dayFormatter.format(absent.getStart()));
        doc.put("end", dayFormatter.format(absent.getEnd()));
        documentCodec.encode(bsonWriter, doc, encoderContext);
    }

    @Override
    public Class<Absent> getEncoderClass() {
        return null;
    }
}
