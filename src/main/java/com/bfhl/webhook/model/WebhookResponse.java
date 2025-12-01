package com.bfhl.webhook.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebhookResponse {
    @JsonProperty("webhook")
    private String webhookUrl;
    
    private String accessToken;

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
