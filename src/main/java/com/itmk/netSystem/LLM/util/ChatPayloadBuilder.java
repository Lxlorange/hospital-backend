package com.itmk.netSystem.LLM.util;

public class ChatPayloadBuilder {
    private final String model;
    private final String systemPrompt;
    private final String userMessage;
    private final double temperature;

    public ChatPayloadBuilder(String model, String systemPrompt, String userMessage, double temperature) {
        this.model = model;
        this.systemPrompt = systemPrompt;
        this.userMessage = userMessage;
        this.temperature = temperature;
    }

    public String build() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"model\": \"").append(escape(model)).append("\",");
        sb.append("\"messages\": [");
        sb.append("{\"role\": \"system\", \"content\": \"")
                .append(escape(systemPrompt)).append("\"},");
        sb.append("{\"role\": \"user\", \"content\": \"")
                .append(escape(userMessage)).append("\"}");
        sb.append("],");
        sb.append("\"temperature\": ").append(temperature);
        sb.append("}");
        return sb.toString();
    }

    private String escape(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}