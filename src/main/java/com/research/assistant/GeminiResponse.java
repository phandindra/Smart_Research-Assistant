package com.research.assistant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiResponse {
    private List<Candidate> candidates;

    // ✅ Explicit Getter
    public List<Candidate> getCandidates() {
        return candidates;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidate {
        private Content content;

        // ✅ Explicit Getter
        public Content getContent() {
            return content;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        private List<Part> parts;

        // ✅ Explicit Getter
        public List<Part> getParts() {
            return parts;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Part {
        private String text;

        // ✅ Explicit Getter
        public String getText() {
            return text;
        }
    }
}
