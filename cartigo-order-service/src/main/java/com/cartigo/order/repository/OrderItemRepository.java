package com.cartigo.order.repository;

import com.cartigo.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    @Query("""
        select case when count(oi) > 0 then true else false end
        from OrderItem oi
        join Order o on o.id = oi.orderId
        where o.userId = :userId
          and oi.productId = :productId
          and o.status = com.cartigo.order.entity.OrderStatus.DELIVERED
    """)
    boolean existsDeliveredPurchase(@Param("userId") Long userId,
                                   @Param("productId") Long productId);
}
