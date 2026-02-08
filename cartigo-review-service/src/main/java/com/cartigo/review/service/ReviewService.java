package com.cartigo.review.service;

import com.cartigo.review.dto.ReviewCreateRequest;
import com.cartigo.review.dto.ReviewUpdateRequest;
import com.cartigo.review.entity.Review;
import com.cartigo.review.exception.BadRequestException;
import com.cartigo.review.exception.ResourceNotFoundException;
import com.cartigo.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PurchaseVerifier purchaseVerifier;

    public ReviewService(ReviewRepository reviewRepository, PurchaseVerifier purchaseVerifier) {
        this.reviewRepository = reviewRepository;
        this.purchaseVerifier = purchaseVerifier;
    }

    @Transactional
    public Review create(ReviewCreateRequest req) {
        if (!purchaseVerifier.hasPurchased(req.getUserId(), req.getProductId())) {
            throw new BadRequestException("Only customers who ordered this product can post a review");
        }

        reviewRepository.findByProductIdAndUserId(req.getProductId(), req.getUserId())
                .ifPresent(existing -> { throw new BadRequestException("You already reviewed this product"); });

        Review r = new Review();
        r.setProductId(req.getProductId());
        r.setUserId(req.getUserId());
        r.setRating(req.getRating());
        r.setComment(req.getComment());

        return reviewRepository.save(r);
    }

    public Review get(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + id));
    }

    public List<Review> listByProduct(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    @Transactional
    public Review update(Long reviewId, ReviewUpdateRequest req) {
        Review r = get(reviewId);

        if (!r.getUserId().equals(req.getUserId())) {
            throw new BadRequestException("You can update only your own review");
        }

        r.setRating(req.getRating());
        r.setComment(req.getComment());
        return reviewRepository.save(r);
    }

    @Transactional
    public void delete(Long reviewId, Long userId) {
        Review r = get(reviewId);
        if (!r.getUserId().equals(userId)) {
            throw new BadRequestException("You can delete only your own review");
        }
        reviewRepository.delete(r);
    }
}
