package com.lab.laboratory.service;

import com.lab.department.entity.Department;
import com.lab.department.repository.DepartmentRepository;
import com.lab.global.exception.ApiException;
import com.lab.global.file.FileUploadService;
import com.lab.global.security.AuthUtil;
import com.lab.laboratory.dto.LaboratoryCreateRequest;
import com.lab.laboratory.dto.LaboratoryMemberResponse;
import com.lab.laboratory.dto.LaboratoryOptionResponse;
import com.lab.laboratory.dto.LaboratoryResponse;
import com.lab.laboratory.entity.LabMemberRole;
import com.lab.laboratory.entity.Laboratory;
import com.lab.laboratory.entity.LaboratoryMember;
import com.lab.laboratory.repository.LaboratoryMemberRepository;
import com.lab.laboratory.repository.LaboratoryRepository;
import com.lab.user.entity.AppUser;
import com.lab.user.entity.UserRole;
import com.lab.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LaboratoryService {

    private final LaboratoryRepository laboratoryRepository;
    private final LaboratoryMemberRepository laboratoryMemberRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final FileUploadService fileUploadService;

    public List<LaboratoryResponse> getLaboratories(String keyword, Long departmentId, String labType) {
        return laboratoryRepository.findAll().stream()
                .filter(l -> departmentId == null || (l.getDepartment() != null && l.getDepartment().getId().equals(departmentId)))
                .filter(l -> labType == null || labType.isBlank() || labType.equalsIgnoreCase(l.getLabType()))
                .filter(l -> keyword == null || keyword.isBlank() || matchesKeyword(l, keyword))
                .map(LaboratoryResponse::from)
                .toList();
    }

    public List<LaboratoryResponse> getMyLaboratories() {
        AppUser currentUser = getCurrentUser();
        Set<Long> seen = new HashSet<>();

        return laboratoryMemberRepository.findByUserId(currentUser.getId()).stream()
                .map(LaboratoryMember::getLaboratory)
                .filter(l -> seen.add(l.getId()))
                .map(LaboratoryResponse::from)
                .toList();
    }

    public List<LaboratoryOptionResponse> getLaboratoryOptions(String keyword, Long departmentId) {
        return laboratoryRepository.findAll().stream()
                .filter(l -> departmentId == null || (l.getDepartment() != null && l.getDepartment().getId().equals(departmentId)))
                .filter(l -> keyword == null || keyword.isBlank() || matchesKeyword(l, keyword))
                .map(LaboratoryOptionResponse::from)
                .toList();
    }

    public LaboratoryResponse getLaboratory(Long id) {
        Laboratory lab = findLaboratory(id);

        List<LaboratoryMemberResponse> members = laboratoryMemberRepository.findByLaboratoryId(id).stream()
                .map(LaboratoryMemberResponse::from)
                .toList();

        return LaboratoryResponse.from(lab, members);
    }

    @Transactional
    public LaboratoryResponse createLaboratory(LaboratoryCreateRequest r, MultipartFile image) {
        AppUser creator = getCurrentUser();

        if (!canCreateLaboratory(creator)) {
            throw ApiException.forbidden("연구실 등록 권한이 필요합니다.");
        }

        if (laboratoryRepository.existsByLabName(r.getLabName())) {
            throw ApiException.badRequest("이미 등록된 연구실명입니다.");
        }

        Department department = resolveDepartment(r.getDepartmentId(), r.getDepartment());

        AppUser manager = null;
        String managerName = r.getManagerName();

        if (r.getManagerId() != null) {
            manager = findUser(r.getManagerId());
            managerName = manager.getName();
        }

        String imageUrl = fileUploadService.upload(image, "labs");

        Laboratory lab = Laboratory.builder()
                .labName(r.getLabName())
                .department(department)
                .managerName(managerName)
                .location(r.getLocation())
                .labType(r.getLabType())
                .imageUrl(imageUrl)
                .createdBy(creator)
                .build();

        Laboratory saved = laboratoryRepository.save(lab);

        Set<Long> memberIds = new HashSet<>();

        if (r.getMemberIds() != null) {
            memberIds.addAll(r.getMemberIds());
        }

        if (manager != null) {
            memberIds.add(manager.getId());
        }

        if (memberIds.isEmpty()) {
            memberIds.add(creator.getId());
        }

        for (Long memberId : memberIds) {
            AppUser member = findUser(memberId);
            saveMember(saved, member);
        }

        return LaboratoryResponse.from(saved);
    }

    public Laboratory findLaboratory(Long id) {
        return laboratoryRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("연구실을 찾을 수 없습니다."));
    }

    public boolean isLabMember(Long labId, Long userId) {
        return laboratoryMemberRepository.existsByLaboratoryIdAndUserId(labId, userId);
    }

    public void checkLabAccess(Laboratory lab, AppUser user) {
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }

        if (user.getRole() == UserRole.GROUP_MANAGER
                && user.getDepartment() != null
                && lab.getDepartment() != null
                && user.getDepartment().getId().equals(lab.getDepartment().getId())) {
            return;
        }

        if (laboratoryMemberRepository.existsByLaboratoryIdAndUserId(lab.getId(), user.getId())) {
            return;
        }

        throw ApiException.forbidden("해당 연구실 정보를 조회할 권한이 없습니다.");
    }

    public List<Laboratory> getAccessibleLaboratories(AppUser user) {
        if (user.getRole() == UserRole.ADMIN) {
            return laboratoryRepository.findAll();
        }

        if (user.getRole() == UserRole.GROUP_MANAGER && user.getDepartment() != null) {
            return laboratoryRepository.findAll().stream()
                    .filter(l -> l.getDepartment() != null && l.getDepartment().getId().equals(user.getDepartment().getId()))
                    .toList();
        }

        Set<Long> seen = new HashSet<>();

        return laboratoryMemberRepository.findByUserId(user.getId()).stream()
                .map(LaboratoryMember::getLaboratory)
                .filter(l -> seen.add(l.getId()))
                .toList();
    }

    public AppUser getCurrentUser() {
        return userRepository.findById(AuthUtil.getCurrentUserId())
                .orElseThrow(() -> ApiException.notFound("사용자를 찾을 수 없습니다."));
    }

    private boolean canCreateLaboratory(AppUser user) {
        return user.getRole() == UserRole.ADMIN
                || user.getRole() == UserRole.GROUP_MANAGER
                || user.getRole() == UserRole.LAB_MEMBER;
    }

    private AppUser findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> ApiException.badRequest("선택한 사용자를 찾을 수 없습니다."));
    }

    private void saveMember(Laboratory lab, AppUser user) {
        if (!laboratoryMemberRepository.existsByLaboratoryIdAndUserId(lab.getId(), user.getId())) {
            laboratoryMemberRepository.save(
                    LaboratoryMember.builder()
                            .laboratory(lab)
                            .user(user)
                            .memberRole(LabMemberRole.MEMBER)
                            .build()
            );
        }
    }

    private Department resolveDepartment(Long departmentId, String departmentName) {
        if (departmentId != null) {
            return departmentRepository.findById(departmentId)
                    .orElseThrow(() -> ApiException.badRequest("선택한 학과를 찾을 수 없습니다."));
        }

        if (departmentName != null && !departmentName.isBlank()) {
            return departmentRepository.findByName(departmentName)
                    .orElseThrow(() -> ApiException.badRequest("선택한 학과를 찾을 수 없습니다."));
        }

        return null;
    }

    private boolean matchesKeyword(Laboratory l, String keyword) {
        String k = keyword.toLowerCase();

        return contains(l.getLabName(), k)
                || contains(l.getManagerName(), k)
                || contains(l.getLocation(), k)
                || contains(l.getLabType(), k)
                || (l.getDepartment() != null
                && (contains(l.getDepartment().getName(), k)
                || contains(l.getDepartment().getDisplayName(), k)));
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }
}