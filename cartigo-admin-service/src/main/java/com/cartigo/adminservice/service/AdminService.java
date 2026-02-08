package com.cartigo.adminservice.service;

import com.cartigo.adminservice.client.AuditClient;
import com.cartigo.adminservice.client.ProductClient;
import com.cartigo.adminservice.client.ReturnClient;
import com.cartigo.adminservice.client.UserClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final ProductClient productClient;
    private final UserClient userClient;
    private final ReturnClient returnClient;
    private final AuditClient auditClient;

    public AdminService(ProductClient productClient,
                        UserClient userClient,
                        ReturnClient returnClient,
                        AuditClient auditClient) {
        this.productClient = productClient;
        this.userClient = userClient;
        this.returnClient = returnClient;
        this.auditClient = auditClient;
    }

    public List<Map<String, Object>> listProducts(String q, Long sellerId) {
        return productClient.list(q, sellerId).getData();
    }

    public Map<String, Object> createProduct(Map<String, Object> p, Long adminId) {
        Map<String, Object> created = productClient.create(p).getData();
        audit("CREATE_PRODUCT", adminId, created);
        return created;
    }

    public Map<String, Object> updateProduct(Long id, Map<String, Object> p, Long adminId) {
        Map<String, Object> updated = productClient.update(id, p).getData();
        audit("UPDATE_PRODUCT", adminId, updated);
        return updated;
    }

    public void deleteProduct(Long id, Long adminId) {
        productClient.delete(id);
        Map<String, Object> payload = new HashMap<>();
        payload.put("productId", id);
        audit("DELETE_PRODUCT", adminId, payload);
    }

    public Map<String, Object> getUser(Long id) {
        return userClient.get(id).getData();
    }

    public Map<String, Object> approveReturn(Long returnId, Long adminId) {
        Map<String, Object> body = Map.of("adminId", adminId);
        Map<String, Object> res = returnClient.approve(returnId, body).getData();
        audit("APPROVE_RETURN", adminId, res);
        return res;
    }

    public Map<String, Object> rejectReturn(Long returnId, Long adminId) {
        Map<String, Object> body = Map.of("adminId", adminId);
        Map<String, Object> res = returnClient.reject(returnId, body).getData();
        audit("REJECT_RETURN", adminId, res);
        return res;
    }

    private void audit(String action, Long actorId, Map<String, Object> data) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("action", action);
            payload.put("actorId", actorId);
            payload.put("data", data);
            auditClient.log(payload);
        } catch (Exception ignored) {}
    }
}
