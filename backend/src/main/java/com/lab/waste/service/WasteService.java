package com.lab.waste.service;

import com.lab.global.exception.ApiException;
import com.lab.laboratory.entity.Laboratory;
import com.lab.laboratory.service.LaboratoryService;
import com.lab.user.entity.AppUser;
import com.lab.user.entity.UserRole;
import com.lab.user.repository.UserRepository;
import com.lab.waste.dto.WasteCreateRequest;
import com.lab.waste.dto.WasteResponse;
import com.lab.waste.entity.Waste;
import com.lab.waste.repository.WasteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WasteService {
    private final WasteRepository wasteRepository;
    private final LaboratoryService laboratoryService;
    private final UserRepository userRepository;

    public List<WasteResponse> getWastes(String keyword, String wasteType, String hazardLevel, Long labId, Long departmentId, String unit) {
        AppUser currentUser = laboratoryService.getCurrentUser();
        Set<Long> accessibleLabIds = accessibleLabIds(currentUser);

        return wasteRepository.findAll().stream()
                .filter(w -> canAccessWaste(currentUser, accessibleLabIds, w))
                .filter(w -> wasteType == null || wasteType.isBlank() || contains(w.getWasteType(), wasteType.toLowerCase()))
                .filter(w -> hazardLevel == null || hazardLevel.isBlank() || hazardLevel.equalsIgnoreCase(w.getHazardLevel()))
                .filter(w -> unit == null || unit.isBlank() || unit.equalsIgnoreCase(w.getUnit()))
                .filter(w -> labId == null || (w.getLaboratory() != null && w.getLaboratory().getId().equals(labId)))
                .filter(w -> departmentId == null || (w.getLaboratory() != null && w.getLaboratory().getDepartment() != null && w.getLaboratory().getDepartment().getId().equals(departmentId)))
                .filter(w -> keyword == null || keyword.isBlank() || matchesKeyword(w, keyword))
                .map(WasteResponse::from)
                .toList();
    }

    public WasteResponse getWaste(Long id) {
        Waste waste = findWaste(id);
        AppUser currentUser = laboratoryService.getCurrentUser();
        if (!canAccessWaste(currentUser, accessibleLabIds(currentUser), waste)) {
            throw ApiException.forbidden("해당 폐기물을 조회할 권한이 없습니다.");
        }
        return WasteResponse.from(waste);
    }

    @Transactional
    public WasteResponse createWaste(WasteCreateRequest r) {
        AppUser currentUser = laboratoryService.getCurrentUser();
        Laboratory lab = laboratoryService.findLaboratory(r.getLabId());

        if (currentUser.getRole() != UserRole.ADMIN && !laboratoryService.isLabMember(r.getLabId(), currentUser.getId())) {
            throw ApiException.forbidden("해당 연구실에 소속된 사용자만 폐기물을 등록할 수 있습니다.");
        }

        Waste w = Waste.builder()
                .wasteType(r.getWasteType())
                .quantity(r.getQuantity())
                .unit(r.getUnit())
                .generatedDate(r.getGeneratedDate())
                .storageLocation(r.getStorageLocation())
                .hazardLevel(r.getHazardLevel())
                .laboratory(lab)
                .handler(currentUser)
                .build();
        return WasteResponse.from(wasteRepository.save(w));
    }

    private Waste findWaste(Long id) {
        return wasteRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("폐기물을 찾을 수 없습니다."));
    }

    private Set<Long> accessibleLabIds(AppUser user) {
        return laboratoryService.getAccessibleLaboratories(user).stream()
                .map(Laboratory::getId)
                .collect(Collectors.toSet());
    }

    private boolean canAccessWaste(AppUser user, Set<Long> accessibleLabIds, Waste waste) {
        if (user.getRole() == UserRole.ADMIN) return true;
        return waste.getLaboratory() != null && accessibleLabIds.contains(waste.getLaboratory().getId());
    }

    private boolean matchesKeyword(Waste w, String keyword) {
        String k = keyword.toLowerCase();
        return contains(w.getWasteType(), k)
                || contains(w.getHazardLevel(), k)
                || contains(w.getStorageLocation(), k)
                || contains(w.getUnit(), k)
                || (w.getLaboratory() != null && contains(w.getLaboratory().getLabName(), k))
                || (w.getLaboratory() != null && contains(w.getLaboratory().getManagerName(), k))
                || (w.getHandler() != null && contains(w.getHandler().getName(), k));
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }
}
