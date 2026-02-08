package com.cartigo.returns.repository;

import com.cartigo.returns.entity.ReturnRequest;
import com.cartigo.returns.entity.ReturnStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {

    List<ReturnRequest> findByUserId(Long userId);

    List<ReturnRequest> findByStatus(ReturnStatus status);

    Optional<ReturnRequest> findByOrderIdAndProductId(Long orderId, Long productId);
}
