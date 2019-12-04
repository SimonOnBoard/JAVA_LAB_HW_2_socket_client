package clients;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.MessageDTO;

import java.io.IOException;
import java.util.*;

public class JsonTransfer {
    private ObjectMapper objectMapper;

    public JsonTransfer() {
        this.objectMapper = new ObjectMapper();
    }

    private static Map<String, Object> map = null;

    public String readMessage() {
        Map<String, String> map1 = objectMapper.convertValue(map.get("payload"), new TypeReference<Map<String, String>>() {
        });
        return map1.get("message");
    }

    public String getHeader() {
        return (String) map.get("header");
    }



    public void loadMessageMap(String response) {
        try {
                map = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public String prepareJsonObject(String header, Map<String, String> params) {
        Map<String, Object> data = new HashMap<>();
        data.put("header", header);
        data.put("payload", params);
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<MessageDTO> getListMessages(String response) {
        try {
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
            objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            JsonNode rootNode = objectMapper.readTree(response);
//            List<MessageDTO> myObjects =
//                    objectMapper.convertValue(object.toString(),  new TypeReference<List<MessageDTO>>(){});
            List<MessageDTO> myObjects = Arrays.asList(objectMapper.treeToValue(rootNode.get("data"), MessageDTO[].class));
            return myObjects;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
