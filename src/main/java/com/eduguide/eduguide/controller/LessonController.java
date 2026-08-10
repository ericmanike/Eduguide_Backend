package com.eduguide.eduguide.controller;

import com.eduguide.eduguide.model.Lesson;
import com.eduguide.eduguide.repository.LessonRepository;
import com.eduguide.eduguide.repository.ModuleRepository;
import com.eduguide.eduguide.service.VideoStorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final VideoStorageService videoStorageService;

    public LessonController(LessonRepository lessonRepository, ModuleRepository moduleRepository, VideoStorageService videoStorageService) {
        this.lessonRepository = lessonRepository;
        this.moduleRepository = moduleRepository;
        this.videoStorageService = videoStorageService;
    }

    @GetMapping
    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lesson> getLesson(@PathVariable UUID id) {
        return lessonRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<Lesson>> getLessonsByModule(@PathVariable UUID moduleId) {
        if (!moduleRepository.existsById(moduleId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(lessonRepository.findByModuleIdOrderBySequenceOrderAsc(moduleId));
    }

    @PostMapping("/module/{moduleId}")
    public ResponseEntity<?> createLesson(@PathVariable UUID moduleId, @RequestBody Lesson lesson) {
        return moduleRepository.findById(moduleId)
                .map(module -> {
                    if (lesson.getTitle() == null || lesson.getTitle().isBlank()
                            || lesson.getVideoUrl() == null || lesson.getVideoUrl().isBlank()
                            || lesson.getSequenceOrder() == null) {
                        return ResponseEntity.badRequest().body("Error: title, videoUrl, and sequenceOrder are required!");
                    }
                    lesson.setId(null);
                    lesson.setModule(module);
                    return ResponseEntity.ok(lessonRepository.save(lesson));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLesson(@PathVariable UUID id, @RequestBody Lesson request) {
        return lessonRepository.findById(id)
                .map(lesson -> {
                    lesson.setTitle(request.getTitle());
                    lesson.setVideoUrl(request.getVideoUrl());
                    lesson.setDurationMinutes(request.getDurationMinutes());
                    lesson.setSequenceOrder(request.getSequenceOrder());
                    lesson.setSummary(request.getSummary());
                    lesson.setResourcesUrl(request.getResourcesUrl());
                    return ResponseEntity.ok(lessonRepository.save(lesson));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable UUID id) {
        if (!lessonRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lessonRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{lessonId}/upload-video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadVideo(@PathVariable UUID lessonId, @RequestParam("video") MultipartFile file) {
        return lessonRepository.findById(lessonId)
                .map(lesson -> {
                    try {
                        if (file.isEmpty()) {
                            return ResponseEntity.badRequest().body("Error: Video file is required!");
                        }

                        // Validate file type
                        String contentType = file.getContentType();
                        if (contentType == null || !contentType.startsWith("video/")) {
                            return ResponseEntity.badRequest().body("Error: File must be a video!");
                        }

                        // Upload video to S3
                        String videoKey = videoStorageService.uploadVideo(file, lessonId);
                        String videoUrl = videoStorageService.getPublicUrl(videoKey);

                        // Update lesson with video URL
                        lesson.setVideoUrl(videoUrl);
                        lessonRepository.save(lesson);

                        Map<String, String> response = new HashMap<>();
                        response.put("message", "Video uploaded successfully");
                        response.put("videoUrl", videoUrl);
                        response.put("videoKey", videoKey);

                        return ResponseEntity.ok(response);
                    } catch (IOException e) {
                        return ResponseEntity.internalServerError().body("Error uploading video: " + e.getMessage());
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{lessonId}/video-url")
    public ResponseEntity<?> getSignedVideoUrl(@PathVariable UUID lessonId,
                                               @RequestParam(defaultValue = "3600") int expiresIn) {
        return lessonRepository.findById(lessonId)
                .map(lesson -> {
                    if (lesson.getVideoUrl() == null || lesson.getVideoUrl().isBlank()) {
                        return ResponseEntity.badRequest().body("Error: Lesson has no video!");
                    }

                    // Extract key from URL or use videoUrl as key
                    String videoKey = lesson.getVideoUrl();
                    if (videoKey.contains("/")) {
                        videoKey = videoKey.substring(videoKey.lastIndexOf('/') + 1);
                    }

                    String signedUrl = videoStorageService.getSignedUrl(videoKey, expiresIn);

                    Map<String, String> response = new HashMap<>();
                    response.put("signedUrl", signedUrl);
                    response.put("expiresIn", String.valueOf(expiresIn));

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
