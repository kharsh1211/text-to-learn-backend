package com.texttolearn.backend.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;

@Service
public class YoutubeService {
    @Value("${youtube.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String searchVideo(String query) {
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&q="
                + URLEncoder.encode(query, StandardCharsets.UTF_8)
                + "&type=video&key=" + apiKey;

        try {
            JsonNode root = new ObjectMapper().readTree(restTemplate.getForObject(url, String.class));
            String videoId = root.path("items").get(0).path("id").path("videoId").asText();

            // Construct the full URL before returning
            return "https://www.youtube.com/watch?v=" + videoId;
        } catch (Exception e) {
            // Fallback search link if the specific API call fails
            return "https://www.youtube.com/results?search_query=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
        }
    }
}
