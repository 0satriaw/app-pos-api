package sawi.saas.pos.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sawi.saas.pos.dto.CategoryResponse;
import sawi.saas.pos.dto.RoleResponse;
import sawi.saas.pos.entity.Category;
import sawi.saas.pos.entity.Role;
import sawi.saas.pos.repository.RoleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private final RoleRepository roleRepository;

    @Transactional(readOnly = true )
    public List<RoleResponse> getAllRoles() {
        logger.info("Fetching all categories");
        List<Role> roles = roleRepository.findAll();

        return roles.stream().map(this::mapToRole).toList();
    }


    private RoleResponse mapToRole(Role role) {
        return new RoleResponse(
                role.getId().toString(),
                role.getName()
        );
    }
}
