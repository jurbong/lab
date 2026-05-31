package com.lab.global.file;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file
    ) {
        String fileUrl = fileUploadService.upload(file);

        return ResponseEntity.ok(Map.of(
                "fileUrl", fileUrl
        ));
    }
}