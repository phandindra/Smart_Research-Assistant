package com.research.assistant;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class ResearchService {
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public ResearchService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public String processContent(ResearchRequest request) {
        try {
            // Build the prompt
            String prompt = buildPrompt(request);

            // Construct API URL with key as query parameter
            String finalUrl = geminiApiUrl + "?key=" + geminiKey;

            // Correct request body format as per Gemini API specs
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    )
            );

            // Debugging: Print request details
            System.out.println("Request URL: " + finalUrl);
            System.out.println("Request Body: " + requestBody);

            // Send API request
            String response = webClient.post()
                    .uri(finalUrl)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Handle empty response
            if (response == null || response.isEmpty()) {
                return "Error: Received an empty response from the API.";
            }

            // Debugging: Print API Response
            System.out.println("API Response: " + response);

            // Extract and return response content
            return extractTextFromResponse(response);

        } catch (Exception e) {
            e.printStackTrace();  // Print stack trace for debugging
            return "Error: " + e.getMessage();
        }
    }

    private String extractTextFromResponse(String response) {
        try {
            System.out.println("Raw API Response: " + response); // Debugging line

            GeminiResponse geminiResponse = objectMapper.readValue(response, GeminiResponse.class);
            if (geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()) {
                GeminiResponse.Candidate firstCandidate = geminiResponse.getCandidates().get(0);
                if (firstCandidate.getContent() != null &&
                        firstCandidate.getContent().getParts() != null &&
                        !firstCandidate.getContent().getParts().isEmpty()) {
                    return firstCandidate.getContent().getParts().get(0).getText();
                }
            }
            return "No content found in response.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing response: " + e.getMessage();
        }
    }

    private String buildPrompt(ResearchRequest request) {
        StringBuilder prompt = new StringBuilder();
        switch (request.getOperation()) {
            case "summarize":
                prompt.append("Provide a clear and concise summary of the following text in a few sentences:\n\n");
                break;
            case "suggest":
                prompt.append("Based on the following content, suggest related topics and further reading. Format response with clear headings and bullet points:\n\n");
                break;
            default:
                throw new IllegalArgumentException("Unknown operation: " + request.getOperation());
        }

        prompt.append(request.getContent());
        return prompt.toString();
    }
}
