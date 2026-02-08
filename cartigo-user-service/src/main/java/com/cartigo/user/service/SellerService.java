package com.cartigo.user.service;

import com.cartigo.user.dto.SellerCreateRequest;
import com.cartigo.user.entity.Role;
import com.cartigo.user.entity.Seller;
import com.cartigo.user.entity.User;
import com.cartigo.user.exception.BadRequestException;
import com.cartigo.user.exception.ResourceNotFoundException;
import com.cartigo.user.repository.SellerRepository;
import org.springframework.stereotype.Service;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserService userService;

    public SellerService(SellerRepository sellerRepository, UserService userService) {
        this.sellerRepository = sellerRepository;
        this.userService = userService;
    }

    public Seller createSellerProfile(Long userId, SellerCreateRequest req) {
        User user = userService.getUser(userId);
        userService.assertRole(user, Role.SELLER);

        if (sellerRepository.existsById(userId)) {
            throw new BadRequestException("Seller profile already exists for this user");
        }

        if (req.getGstNumber() != null && sellerRepository.existsByGstNumber(req.getGstNumber())) {
            throw new BadRequestException("GST number already exists");
        }

        Seller s = new Seller();
        s.setUser(user);
        s.setStoreName(req.getStoreName());
        s.setGstNumber(req.getGstNumber());
        s.setBusinessAddress(req.getBusinessAddress());
        return sellerRepository.save(s);
    }

    public Seller getSeller(Long userId) {
        return sellerRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found for userId " + userId));
    }

    public Seller updateSeller(Long userId, SellerCreateRequest req) {
        Seller s = getSeller(userId);

        if (req.getStoreName() != null) s.setStoreName(req.getStoreName());
        if (req.getGstNumber() != null) s.setGstNumber(req.getGstNumber());
        if (req.getBusinessAddress() != null) s.setBusinessAddress(req.getBusinessAddress());

        return sellerRepository.save(s);
    }

    // In real app, admin-service should call internally.
    public Seller setApproved(Long userId, boolean approved) {
        Seller s = getSeller(userId);
        s.setIsApproved(approved);
        return sellerRepository.save(s);
    }
}
