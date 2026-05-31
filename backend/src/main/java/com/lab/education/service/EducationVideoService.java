package com.lab.education.service;

import com.lab.education.dto.EducationVideoCreateRequest;
import com.lab.education.dto.EducationVideoResponse;
import com.lab.education.entity.EducationVideo;
import com.lab.education.repository.EducationVideoRepository;
import com.lab.global.exception.ApiException;
import com.lab.global.file.FileUploadService;
import com.lab.global.security.AuthUtil;
import com.lab.user.entity.AppUser;
import com.lab.user.entity.UserRole;
import com.lab.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EducationVideoService {

    private final EducationVideoRepository repository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    public List<EducationVideoResponse> getVideos(String keyword, String educationType) {
        requireEducationViewer();

        return repository.findAll().stream()
                .filter(v -> educationType == null || educationType.isBlank() || educationType.equalsIgnoreCase(v.getEducationType()))
                .filter(v -> keyword == null || keyword.isBlank() || matchesKeyword(v, keyword))
                .map(EducationVideoResponse::summary)
                .toList();
    }

    public EducationVideoResponse getVideo(Long id) {
        requireEducationViewer();

        return EducationVideoResponse.from(repository.findById(id)
                .orElseThrow(() -> ApiException.notFound("안전교육 동영상을 찾을 수 없습니다.")));
    }

    @Transactional
    public EducationVideoResponse createVideo(EducationVideoCreateRequest r, MultipartFile video) {
        requireEducationManagerOrAdmin();

        if (repository.existsByTitle(r.getTitle())) {
            throw ApiException.badRequest("이미 등록된 교육 동영상 제목입니다.");
        }

        AppUser creator = userRepository.findById(AuthUtil.getCurrentUserId())
                .orElseThrow(() -> ApiException.notFound("사용자를 찾을 수 없습니다."));

        String filePath = fileUploadService.upload(video, "education-videos");

        EducationVideo v = EducationVideo.builder()
                .title(r.getTitle())
                .educationType(r.getEducationType())
                .filePath(filePath)
                .description(r.getDescription())
                .createdBy(creator)
                .build();

        return EducationVideoResponse.from(repository.save(v));
    }

    private void requireEducationViewer() {
        UserRole role = AuthUtil.getCurrentUserRole();

        if (role != UserRole.LAB_MEMBER
                && role != UserRole.EDUCATION_MANAGER
                && role != UserRole.ADMIN) {
            throw ApiException.forbidden("안전교육 조회 권한이 필요합니다.");
        }
    }

    private void requireEducationManagerOrAdmin() {
        UserRole role = AuthUtil.getCurrentUserRole();

        if (role != UserRole.EDUCATION_MANAGER && role != UserRole.ADMIN) {
            throw ApiException.forbidden("안전교육 담당자 권한이 필요합니다.");
        }
    }

    private boolean matchesKeyword(EducationVideo v, String keyword) {
        String k = keyword.toLowerCase();

        return contains(v.getTitle(), k)
                || contains(v.getEducationType(), k)
                || contains(v.getDescription(), k);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }
}