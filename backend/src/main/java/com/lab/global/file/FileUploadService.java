package com.lab.global.file;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service
public class FileUploadService {

    private final String uploadRoot =
            System.getProperty("user.dir") + File.separator + "uploads";

    /**
     * 기본 업로드
     * /uploads/파일명 형태로 저장
     */
    public String upload(MultipartFile file) {
        return upload(file, "");
    }

    /**
     * 폴더별 업로드
     * 예)
     * upload(file, "labs")              -> /uploads/labs/파일명
     * upload(file, "inspection-forms")  -> /uploads/inspection-forms/파일명
     * upload(file, "education-videos")  -> /uploads/education-videos/파일명
     */
    public String upload(MultipartFile file, String folder) {
        try {
            if (file == null || file.isEmpty()) {
                return null;
            }

            String safeFolder = folder == null ? "" : folder.trim();

            String uploadDir = uploadRoot;
            if (!safeFolder.isEmpty()) {
                uploadDir += File.separator + safeFolder;
            }

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String savedFilename = UUID.randomUUID() + extension;
            File savedFile = new File(dir, savedFilename);

            file.transferTo(savedFile);

            if (safeFolder.isEmpty()) {
                return "/uploads/" + savedFilename;
            }

            return "/uploads/" + safeFolder + "/" + savedFilename;

        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }
}