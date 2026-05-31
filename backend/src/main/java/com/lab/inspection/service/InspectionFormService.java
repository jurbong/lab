package com.lab.inspection.service;

import com.lab.global.exception.ApiException;
import com.lab.global.file.FileUploadService;
import com.lab.global.security.AuthUtil;
import com.lab.inspection.dto.InspectionFormCreateRequest;
import com.lab.inspection.dto.InspectionFormResponse;
import com.lab.inspection.entity.InspectionForm;
import com.lab.inspection.repository.InspectionFormRepository;
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
public class InspectionFormService {

    private final InspectionFormRepository repository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    public List<InspectionFormResponse> getForms(String keyword, String inspectionType) {
        requireSafetyManagerOrAdmin();

        return repository.findAll().stream()
                .filter(f -> inspectionType == null || inspectionType.isBlank() || inspectionType.equalsIgnoreCase(f.getInspectionType()))
                .filter(f -> keyword == null || keyword.isBlank() || matchesKeyword(f, keyword))
                .map(InspectionFormResponse::summary)
                .toList();
    }

    public InspectionFormResponse getForm(Long id) {
        requireSafetyManagerOrAdmin();

        return InspectionFormResponse.from(repository.findById(id)
                .orElseThrow(() -> ApiException.notFound("점검 양식을 찾을 수 없습니다.")));
    }

    @Transactional
    public InspectionFormResponse createForm(InspectionFormCreateRequest r, MultipartFile file) {
        requireSafetyManagerOrAdmin();

        if (repository.existsByFormName(r.getFormName())) {
            throw ApiException.badRequest("이미 등록된 점검 양식명입니다.");
        }

        AppUser creator = userRepository.findById(AuthUtil.getCurrentUserId())
                .orElseThrow(() -> ApiException.notFound("사용자를 찾을 수 없습니다."));

        String filePath = fileUploadService.upload(file, "inspection-forms");

        InspectionForm f = InspectionForm.builder()
                .formName(r.getFormName())
                .inspectionType(r.getInspectionType())
                .filePath(filePath)
                .description(r.getDescription())
                .createdBy(creator)
                .build();

        return InspectionFormResponse.from(repository.save(f));
    }

    private void requireSafetyManagerOrAdmin() {
        UserRole role = AuthUtil.getCurrentUserRole();

        if (role != UserRole.SAFETY_MANAGER && role != UserRole.ADMIN) {
            throw ApiException.forbidden("연구실 안전관리 담당자 권한이 필요합니다.");
        }
    }

    private boolean matchesKeyword(InspectionForm f, String keyword) {
        String k = keyword.toLowerCase();

        return contains(f.getFormName(), k)
                || contains(f.getInspectionType(), k)
                || contains(f.getDescription(), k);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }
}