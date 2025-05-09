package com.wztc.demo.entity.AiChat;

import java.util.List;

public class AiResult {
    //private Integer code;
    //private String message;
    //private String sid;
    private String id;
    //private Long created;
    private List<AiResultChoices> choices;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AiResultChoices> getChoices() {
        return choices;
    }

    public void setChoices(List<AiResultChoices> choices) {
        this.choices = choices;
    }
}
