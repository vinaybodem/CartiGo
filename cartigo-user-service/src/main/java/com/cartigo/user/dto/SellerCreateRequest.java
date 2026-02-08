package com.cartigo.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SellerCreateRequest {

    @NotBlank
    @Size(max = 150)
    private String storeName;

    @NotBlank
    @Size(max = 30)
    private String gstNumber;

    @NotBlank
    @Size(max = 1000)
    private String businessAddress;

    public SellerCreateRequest() {}

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }

    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
}
