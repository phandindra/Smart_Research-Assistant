package com.research.assistant;

import lombok.Data;

@Data
public class ResearchRequest {
    private String content;
    private String operation;

    public String getContent() {
        return content;
    }

    public String getOperation() {
        return operation;
    }
}
