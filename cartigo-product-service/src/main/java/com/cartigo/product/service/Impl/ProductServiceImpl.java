package com.cartigo.product.service.Impl;

import com.cartigo.product.client.CategoryClient;
import com.cartigo.product.client.SellerClient;
import com.cartigo.product.dto.*;
import com.cartigo.product.entity.Product;
import com.cartigo.product.entity.ProductStatus;
import com.cartigo.product.exception.BadRequestException;
import com.cartigo.product.exception.ResourceNotFoundException;
import com.cartigo.product.repository.ProductRepository;
import com.cartigo.product.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryClient categoryClient;
    private final SellerClient sellerClient;

    public ProductServiceImpl(ProductRepository productRepository, CategoryClient categoryClient, SellerClient sellerClient) {
        this.productRepository = productRepository;
        this.categoryClient = categoryClient;
        this.sellerClient = sellerClient;
    }

    @Override
    public Product createProduct(ProductCreateRequest request) {

        SellerDto seller = sellerClient.getSellerById(request.getSellerId());

        if(seller == null) throw new RuntimeException("Invalid Seller");
        if(!categoryClient.isCategoryValid(request.getCategoryId())) throw new RuntimeException("Invalid Category");
        if (productRepository.existsBySku(request.getSku())) {
            throw new BadRequestException("SKU already exists");
        }
        Product p = new Product();
        p.setName(request.getName());
        p.setDescription(request.getDescription());
        p.setPrice(request.getPrice());
        p.setDiscountPrice(request.getDiscountPrice());
        p.setBrand(request.getBrand());
        p.setSku(request.getSku());
        p.setImage(request.getImage());
        p.setCategoryId(request.getCategoryId());
        p.setSellerId(request.getSellerId());
        p.setStatus(ProductStatus.ACTIVE);
        return productRepository.save(p);
    }

    @Override
    public Product updateProduct(Long id, ProductUpdateRequest req) {
        Product p = getProductById(id);
        if (req.getName() != null) p.setName(req.getName());
        if (req.getDescription() != null) p.setDescription(req.getDescription());
        if (req.getPrice() != null) p.setPrice(req.getPrice());
        if (req.getDiscountPrice() != null) p.setDiscountPrice(req.getDiscountPrice());
        if (req.getBrand() != null) p.setBrand(req.getBrand());
        return productRepository.save(p);
    }

    @Override
    public Product getProductById(Long id) {
        Product p = productRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Product not found: " + id));
        if(p.getStatus() != ProductStatus.ACTIVE){
            throw new ResourceNotFoundException("Product not found: " + id);
        }
        return p;
    }

    @Override
    public List<Product> listActiveProducts() {
        return productRepository.findByStatus(ProductStatus.ACTIVE);
    }
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsBySeller(Long sellerId) {

        return productRepository.findBySellerId(sellerId);
    }

    @Override
    public List<Product> getProductsByCategoryId(String categoryName) {
        Long categoryId = categoryClient.getCategroyId(categoryName);
        List<Product> p = productRepository.findByCategoryId(categoryId);
        if(p.isEmpty()) throw new ResourceNotFoundException("Products Not Found with Category");
        return p;
    }

    @Override
    public List<Product> listByBrand(String brand) {
        List<Product> p =productRepository.findByBrandIgnoreCaseAndStatus(brand, ProductStatus.ACTIVE);
        if(p.isEmpty()) throw new ResourceNotFoundException("Product Not Found with this brand") ;
        return p;
    }
    @Override
    public List<Product> searchProductsByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Override
    public ResponseToInvetory getProductIdAndSellerId(Long id) {
        Product product = getProductById(id);
        return new ResponseToInvetory(product.getId(), product.getSellerId());
    }

    @Override
    public void deleteProducts(Long id) {
       productRepository.delete(getProductById(id));
    }

    @Override
    public Product setStatus(Long id, ProductStatus status) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));

        p.setStatus(status);
        return productRepository.save(p);
    }
}
