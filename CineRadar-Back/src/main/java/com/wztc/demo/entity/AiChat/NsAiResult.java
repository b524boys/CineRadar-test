package com.wztc.demo.entity.AiChat;

import java.util.List;

public class NsAiResult {

    private String id;
    private List<NsAiResultChoices> choices;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<NsAiResultChoices> getChoices() {return choices;}

    public void setChoices(List<NsAiResultChoices> choices) {this.choices = choices;}
}
