package com.cartigo.user.repository;

import com.cartigo.user.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    boolean existsByGstNumber(String gstNumber);
}
