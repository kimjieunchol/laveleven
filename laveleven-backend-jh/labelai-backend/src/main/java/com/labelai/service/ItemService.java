package com.labelai.service;

import com.labelai.dto.ItemDTO;
import com.labelai.entity.Item;
import com.labelai.entity.User;
import com.labelai.repository.ItemRepository;
import com.labelai.repository.UserRepository;
import com.labelai.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final PermissionService permissionService;
    
    /**
     * 아이템 목록 조회 (권한별 필터링)
     */
    public List<ItemDTO> getAllItems() {
        CustomUserDetails currentUser = permissionService.getCurrentUser();
        List<Item> items;
        
        if (permissionService.isSuperAdmin()) {
            // SUPER_ADMIN: 모든 아이템 조회
            items = itemRepository.findAll();
            log.info("SUPER_ADMIN이 모든 아이템 조회: count={}", items.size());
        } else if (permissionService.isAdmin()) {
            // ADMIN: 같은 부서 아이템만 조회
            items = itemRepository.findByDepartmentId(currentUser.getDepartmentId());
            log.info("ADMIN이 부서 아이템 조회: department={}, count={}", 
                currentUser.getDepartmentId(), items.size());
        } else {
            // USER: 자기가 생성한 아이템만 조회
            User user = userRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            items = itemRepository.findByCreatedBy(user);
            log.info("USER가 자신의 아이템 조회: userId={}, count={}", 
                currentUser.getUserId(), items.size());
        }
        
        return items.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 아이템 단건 조회
     */
    public ItemDTO getItemById(String id) {
        if (!permissionService.canViewItem(id)) {
            throw new RuntimeException("해당 아이템을 조회할 권한이 없습니다.");
        }
        
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("아이템을 찾을 수 없습니다."));
        
        log.info("아이템 조회 성공: itemId={}", id);
        return convertToDTO(item);
    }
    
    /**
     * 아이템 검색 (권한별 필터링)
     */
    public List<ItemDTO> searchItems(String keyword) {
        CustomUserDetails currentUser = permissionService.getCurrentUser();
        List<Item> items;
        
        if (permissionService.isSuperAdmin()) {
            // SUPER_ADMIN: 전체 검색
            items = itemRepository.searchByKeyword(keyword);
        } else if (permissionService.isAdmin()) {
            // ADMIN: 부서 내 검색
            items = itemRepository.searchByKeywordAndDepartment(
                keyword, currentUser.getDepartmentId());
        } else {
            // USER: 자기 아이템 내 검색
            User user = userRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            items = itemRepository.searchByKeywordAndCreatedBy(keyword, user);
        }
        
        log.info("아이템 검색: keyword={}, count={}", keyword, items.size());
        return items.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 아이템 생성 (모든 권한 가능!)
     */
    @Transactional
    public ItemDTO createItem(ItemDTO dto) {
        CustomUserDetails currentUser = permissionService.getCurrentUser();
        
        User creator = userRepository.findById(currentUser.getUserId())
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 부서 설정
        String departmentId = dto.getDepartment();
        
        // ADMIN과 USER는 자기 부서에만 생성 가능
        if (permissionService.isAdmin() || permissionService.isUser()) {
            departmentId = currentUser.getDepartmentId();
        }
        
        Item item = Item.builder()
            .itemName(dto.getName())
            .itemType(dto.getType())
            .departmentId(departmentId)
            .description(dto.getDescription())
            .createdBy(creator)
            .updatedBy(creator)
            .build();
        
        Item savedItem = itemRepository.save(item);
        log.info("아이템 생성 성공: itemId={}, name={}, createdBy={}", 
            savedItem.getId(), savedItem.getItemName(), creator.getUsername());
        
        return convertToDTO(savedItem);
    }
    
    /**
     * 아이템 수정 (권한 범위 내에서 모두 수정 가능!)
     */
    @Transactional
    public ItemDTO updateItem(String id, ItemDTO dto) {
        if (!permissionService.canEditItem(id)) {
            throw new RuntimeException("해당 아이템을 수정할 권한이 없습니다.");
        }
        
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("아이템을 찾을 수 없습니다."));
        
        CustomUserDetails currentUser = permissionService.getCurrentUser();
        User updater = userRepository.findById(currentUser.getUserId())
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        if (dto.getName() != null) item.setItemName(dto.getName());
        if (dto.getType() != null) item.setItemType(dto.getType());
        if (dto.getDescription() != null) item.setDescription(dto.getDescription());
        item.setUpdatedBy(updater);
        
        Item updatedItem = itemRepository.save(item);
        log.info("아이템 수정 성공: itemId={}, updatedBy={}", id, updater.getUsername());
        
        return convertToDTO(updatedItem);
    }
    
    /**
     * 아이템 삭제 (권한 범위 내에서 모두 삭제 가능!)
     */
    @Transactional
    public void deleteItem(String id) {
        if (!permissionService.canDeleteItem(id)) {
            throw new RuntimeException("해당 아이템을 삭제할 권한이 없습니다.");
        }
        
        CustomUserDetails currentUser = permissionService.getCurrentUser();
        itemRepository.deleteById(id);
        log.info("아이템 삭제 성공: itemId={}, deletedBy={}", id, currentUser.getUsername());
    }
    
    /**
     * Entity -> DTO 변환
     */
    private ItemDTO convertToDTO(Item item) {
        return ItemDTO.builder()
            .id(item.getId())
            .name(item.getItemName())
            .type(item.getItemType())
            .department(item.getDepartmentId())
            .version(item.getVersion())
            .description(item.getDescription())
            .createdBy(item.getCreatedBy().getUsername())
            .createdAt(item.getCreatedAt().toString())
            .build();
    }
}