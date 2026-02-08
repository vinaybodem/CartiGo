package com.cartigo.user.service;

import com.cartigo.user.dto.AdminCreateRequest;
import com.cartigo.user.entity.Admin;
import com.cartigo.user.entity.Role;
import com.cartigo.user.entity.User;
import com.cartigo.user.exception.BadRequestException;
import com.cartigo.user.exception.ResourceNotFoundException;
import com.cartigo.user.repository.AdminRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserService userService;

    public AdminService(AdminRepository adminRepository, UserService userService) {
        this.adminRepository = adminRepository;
        this.userService = userService;
    }

    public Admin createAdminProfile(Long userId, AdminCreateRequest req) {
        User user = userService.getUser(userId);
        userService.assertRole(user, Role.ADMIN);

        if (adminRepository.existsById(userId)) {
            throw new BadRequestException("Admin profile already exists for this user");
        }

        Admin a = new Admin();
        a.setUser(user);
        a.setAdminName(req.getAdminName());
        return adminRepository.save(a);
    }

    public Admin getAdmin(Long userId) {
        return adminRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found for userId " + userId));
    }
}
