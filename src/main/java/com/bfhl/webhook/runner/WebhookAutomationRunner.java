package com.bfhl.webhook.runner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bfhl.webhook.model.FinalSubmission;
import com.bfhl.webhook.model.InitialRequest;
import com.bfhl.webhook.model.WebhookResponse;

@Component
public class WebhookAutomationRunner implements ApplicationRunner {

    @Value("${user.name}")
    private String userName;

    @Value("${user.regNo}")
    private String regNo;

    @Value("${user.email}")
    private String email;

    @Value("${api.initial.url}")
    private String initialApiUrl;

    @Value("${sql.odd.solution}")
    private String oddSqlSolution;

    @Value("${sql.even.solution}")
    private String evenSqlSolution;

    private final RestTemplate restTemplate;

    public WebhookAutomationRunner() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("=== Starting Webhook Automation Flow ===\n");

        try {
            System.out.println("Step 1: Sending initial request...");
            WebhookResponse webhookResponse = sendInitialRequest();
            System.out.println("✓ Received webhook URL and access token\n");

            System.out.println("Step 2: Analyzing registration number...");
            String selectedSql = selectSqlBasedOnRegNo();
            System.out.println("✓ SQL query selected\n");

            System.out.println("Step 3: Submitting final query...");
            sendFinalSubmission(webhookResponse.getWebhookUrl(), 
                              webhookResponse.getAccessToken(), 
                              selectedSql);
            System.out.println("✓ Final submission completed successfully!\n");

            System.out.println("=== Automation Flow Completed Successfully ===");

        } catch (Exception e) {
            System.err.println("✗ Error during automation flow: " + e.getMessage());
            throw e;
        }
    }

    private WebhookResponse sendInitialRequest() {
        try {
            InitialRequest request = new InitialRequest(userName, regNo, email);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            
            HttpEntity<InitialRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                initialApiUrl,
                HttpMethod.POST,
                entity,
                String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                return mapper.readValue(response.getBody(), WebhookResponse.class);
            } else {
                throw new RuntimeException("Initial request failed with status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to send initial request: " + e.getMessage(), e);
        }
    }

    private String selectSqlBasedOnRegNo() {
        String lastTwoDigits = regNo.substring(regNo.length() - 2);
        int number = Integer.parseInt(lastTwoDigits);

        if (number % 2 == 0) {
            return evenSqlSolution;
        } else {
            return oddSqlSolution;
        }
    }

    private void sendFinalSubmission(String webhookUrl, String accessToken, String sqlQuery) {
        try {
            FinalSubmission submission = new FinalSubmission(sqlQuery);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", accessToken);
            
            HttpEntity<FinalSubmission> entity = new HttpEntity<>(submission, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                webhookUrl,
                HttpMethod.POST,
                entity,
                String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Final submission failed with status: " + response.getStatusCode());
            }

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            throw new RuntimeException("HTTP Error " + e.getStatusCode() + ": " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send final submission: " + e.getMessage(), e);
        }
    }
}
