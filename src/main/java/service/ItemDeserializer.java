package service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.LongNode;
import model.MessageDTO;

import java.io.IOException;

public class ItemDeserializer extends StdDeserializer<MessageDTO> {

    public ItemDeserializer() {
        this(null);
    }

    public ItemDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public MessageDTO deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        Long id = (node.get("id")).asLong();
        String text = node.get("text").asText();
        String name = node.get("name").asText();
        Long time = ( node.get("dateTime")).asLong();

        return new MessageDTO(id,text,name,time);
    }
}