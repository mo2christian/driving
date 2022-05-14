package com.driving.planning.event.domain;

import com.mongodb.MongoClientSettings;
import org.bson.BsonReader;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class EventTypeCodec implements CollectibleCodec<EventType> {

    private final Codec<Document> documentCodec;

    public EventTypeCodec(){
        documentCodec = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);
    }

    @Override
    public EventType generateIdIfAbsentFromDocument(EventType eventType) {
        return eventType;
    }

    @Override
    public boolean documentHasId(EventType eventType) {
        return false;
    }

    @Override
    public BsonValue getDocumentId(EventType eventType) {
        return null;
    }

    @Override
    public EventType decode(BsonReader bsonReader, DecoderContext decoderContext) {
        var document = documentCodec.decode(bsonReader, decoderContext);
        return EventType.valueOf(document.getString("value"));
    }

    @Override
    public void encode(BsonWriter bsonWriter, EventType eventType, EncoderContext encoderContext) {
        var doc = new Document();
        doc.put("value", eventType.name());
        documentCodec.encode(bsonWriter, doc, encoderContext);
    }

    @Override
    public Class<EventType> getEncoderClass() {
        return EventType.class;
    }
}
