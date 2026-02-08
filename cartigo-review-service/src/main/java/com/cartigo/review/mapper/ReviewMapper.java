package com.cartigo.review.mapper;

import com.cartigo.review.dto.ReviewResponse;
import com.cartigo.review.entity.Review;

public class ReviewMapper {

    private ReviewMapper() {}

    public static ReviewResponse toResponse(Review r) {
        ReviewResponse out = new ReviewResponse();
        out.setId(r.getId());
        out.setProductId(r.getProductId());
        out.setUserId(r.getUserId());
        out.setRating(r.getRating());
        out.setComment(r.getComment());
        out.setCreatedAt(r.getCreatedAt());
        return out;
    }
}
