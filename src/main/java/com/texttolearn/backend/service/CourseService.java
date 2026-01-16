package com.texttolearn.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.texttolearn.backend.model.Course;
import com.texttolearn.backend.model.Lesson;
import com.texttolearn.backend.model.Module;
import com.texttolearn.backend.repository.CourseRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.UUID;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;
    private final ChatClient chatClient;
    private final YoutubeService youtubeService;

    public CourseService(CourseRepository courseRepository,
                         ObjectMapper objectMapper,
                         ChatClient.Builder builder,
                         YoutubeService youtubeService) {
        this.courseRepository = courseRepository;
        this.objectMapper = objectMapper;
        this.chatClient = builder.build();
        this.youtubeService = youtubeService;
    }

    public Course generateCourseFromTopic(String topic) {
        try {
            // --- STEP 1: GENERATE SKELETON WITH MODULE VIDEO QUERIES ---
            String skeletonPrompt = """
                    Act as a Master Educator. Create a structural course outline about %s.

                    REQUIREMENTS:
                    1. Return ONLY a JSON object.
                    2. Structure:
                       {
                         "title": "...",
                         "description": "...",
                         "modules": [
                            {
                              "title": "...",
                              "moduleVideoQuery": "A specific YouTube search query for this module",
                              "moduleVideoUrl": "",
                              "learning_objectives": ["..."],
                              "lessons": [{"title": "..."}]
                            }
                         ]
                       }
                    3. Crucial: Each module MUST have a 'moduleVideoQuery' relevant ONLY to that module's specific focus.
                    4. Each lesson in the 'modules' should ONLY have a 'title' for now.

                    CRITICAL: No markdown. No conversational text. Return ONLY raw JSON.
                    """.formatted(topic);

            String skeletonJson = callAiAndClean(skeletonPrompt);
            Course course = objectMapper.readValue(skeletonJson, Course.class);

            // --- STEP 2: ITERATIVE MODULE FILLING + VIDEO FETCHING ---
            for (Module module : course.getModules()) {
                
                // 2a. Fetch the real YouTube URL for this specific module
                if (module.getModuleVideoQuery() != null && !module.getModuleVideoQuery().isEmpty()) {
                    System.out.println("Searching video for module: " + module.getTitle());
                    
                    // We combine the module query with the main topic for better accuracy
                    String fullSearchQuery = module.getModuleVideoQuery() + " " + topic;
                    String realUrl = youtubeService.searchVideo(fullSearchQuery);
                    module.setModuleVideoUrl(realUrl);
                    
                    System.out.println("Result: " + realUrl);
                }

                // 2b. Populate technical details for lessons
                String modulePrompt = """
                        Populate technical details for module: '%s' in the course '%s'.
                        FOR EACH LESSON:
                        1. "title": descriptive title.
                        2. "detailed_content": 300-word deep technical explanation.
                        3. "practical_exercise": hands-on task.
                        (Note: Do NOT provide video_urls here.)

                        Return ONLY a JSON array of these lesson objects.
                        """.formatted(module.getTitle(), course.getTitle());

                String lessonsJson = callAiAndClean(modulePrompt);

                List<Lesson> detailedLessons = objectMapper.readValue(lessonsJson,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Lesson.class));

                module.setLessons(detailedLessons);
            }

            return saveCourse(course);

        } catch (Exception e) {
            System.err.println("Generation Error: " + e.getMessage());
            throw new RuntimeException("AI Generation Failed: " + e.getMessage());
        }
    }

    private String callAiAndClean(String prompt) {
        String rawResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        if (rawResponse == null)
            throw new RuntimeException("AI returned empty response");

        String cleaned = rawResponse.trim();
        cleaned = cleaned.replace("\\ ", "\\\\ ");

        if (cleaned.contains("```")) {
            int firstIdx = cleaned.indexOf("```");
            int lastIdx = cleaned.lastIndexOf("```");

            if (cleaned.substring(firstIdx, Math.min(firstIdx + 10, cleaned.length())).contains("json")) {
                cleaned = cleaned.substring(firstIdx + 7, lastIdx);
            } else {
                cleaned = cleaned.substring(firstIdx + 3, lastIdx);
            }
        }

        int startIdx = Math.min(
                cleaned.indexOf("{") == -1 ? Integer.MAX_VALUE : cleaned.indexOf("{"),
                cleaned.indexOf("[") == -1 ? Integer.MAX_VALUE : cleaned.indexOf("["));
        int endIdx = Math.max(cleaned.lastIndexOf("}"), cleaned.lastIndexOf("]"));

        if (startIdx != Integer.MAX_VALUE && endIdx != -1) {
            cleaned = cleaned.substring(startIdx, endIdx + 1);
        }

        return cleaned.trim();
    }

    public Course saveCourse(Course course) {
        if (course.getModules() != null) {
            course.getModules().forEach(module -> {
                if (module.getId() == null) {
                    module.setId(UUID.randomUUID().toString());
                }
                if (module.getLessons() != null) {
                    module.getLessons().forEach(lesson -> {
                        if (lesson.getId() == null) {
                            lesson.setId(UUID.randomUUID().toString());
                        }
                    });
                }
            });
        }
        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Course getCourseById(String id) {
        return courseRepository.findById(id).orElse(null);
    }
}