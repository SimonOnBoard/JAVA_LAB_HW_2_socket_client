package model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import service.ItemDeserializer;

@JsonDeserialize(using = ItemDeserializer.class)
public class MessageDTO {
    private Long id;
    private String text;
    private String name;
    private Long dateTime;


    public MessageDTO(Long id, String text, String name, Long dateTime) {
        this.id = id;
        this.text = text;
        this.name = name;
        this.dateTime = dateTime;
    }

    public MessageDTO() {
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
