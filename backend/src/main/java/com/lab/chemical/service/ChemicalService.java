package com.lab.chemical.service;

import com.lab.chemical.dto.ChemicalCreateRequest;
import com.lab.chemical.dto.ChemicalResponse;
import com.lab.chemical.entity.Chemical;
import com.lab.chemical.repository.ChemicalRepository;
import com.lab.global.exception.ApiException;
import com.lab.global.security.AuthUtil;
import com.lab.laboratory.entity.Laboratory;
import com.lab.laboratory.service.LaboratoryService;
import com.lab.user.entity.AppUser;
import com.lab.user.entity.UserRole;
import com.lab.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChemicalService {
    private final ChemicalRepository chemicalRepository;
    private final LaboratoryService laboratoryService;
    private final UserRepository userRepository;

    public List<ChemicalResponse> getChemicals(String keyword, String riskLevel, Long labId, Long departmentId, String storageLocation) {
        requireApprovedViewer();
        return chemicalRepository.findAll().stream()
                .filter(c -> riskLevel == null || riskLevel.isBlank() || riskLevel.equalsIgnoreCase(c.getRiskLevel()))
                .filter(c -> labId == null || (c.getLaboratory() != null && c.getLaboratory().getId().equals(labId)))
                .filter(c -> departmentId == null || (c.getLaboratory() != null && c.getLaboratory().getDepartment() != null && c.getLaboratory().getDepartment().getId().equals(departmentId)))
                .filter(c -> storageLocation == null || storageLocation.isBlank() || contains(c.getStorageLocation(), storageLocation.toLowerCase()))
                .filter(c -> keyword == null || keyword.isBlank() || matchesKeyword(c, keyword))
                .map(ChemicalResponse::from)
                .toList();
    }

    public ChemicalResponse getChemical(Long id) {
        requireApprovedViewer();
        return ChemicalResponse.from(findChemical(id));
    }

    @Transactional
    public ChemicalResponse createChemical(ChemicalCreateRequest r) {
        if (!AuthUtil.isSystemAdmin(AuthUtil.getCurrentUserRole())) {
            throw ApiException.forbidden("화학물질 등록은 시스템 관리자만 가능합니다.");
        }

        Long uid = AuthUtil.getCurrentUserId();
        Laboratory lab = laboratoryService.findLaboratory(r.getLabId());
        AppUser creator = userRepository.findById(uid)
                .orElseThrow(() -> ApiException.notFound("사용자를 찾을 수 없습니다."));

        Chemical c = Chemical.builder()
                .chemicalName(r.getChemicalName())
                .quantity(r.getQuantity())
                .unit(r.getUnit())
                .riskLevel(r.getRiskLevel())
                .storageLocation(r.getStorageLocation())
                .laboratory(lab)
                .createdBy(creator)
                .build();
        return ChemicalResponse.from(chemicalRepository.save(c));
    }

    private Chemical findChemical(Long id) {
        return chemicalRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("화학물질을 찾을 수 없습니다."));
    }

    private void requireApprovedViewer() {
        UserRole role = AuthUtil.getCurrentUserRole();
        if (role != UserRole.ADMIN && role != UserRole.GROUP_MANAGER && role != UserRole.SAFETY_MANAGER
                && role != UserRole.EDUCATION_MANAGER && role != UserRole.LAB_MEMBER) {
            throw ApiException.forbidden("화학물질 조회 권한이 필요합니다.");
        }
    }

    private boolean matchesKeyword(Chemical c, String keyword) {
        String k = keyword.toLowerCase();
        return contains(c.getChemicalName(), k)
                || contains(c.getRiskLevel(), k)
                || contains(c.getStorageLocation(), k)
                || (c.getLaboratory() != null && contains(c.getLaboratory().getLabName(), k))
                || (c.getLaboratory() != null && contains(c.getLaboratory().getManagerName(), k))
                || (c.getLaboratory() != null && c.getLaboratory().getDepartment() != null && contains(c.getLaboratory().getDepartment().getName(), k));
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }
}
