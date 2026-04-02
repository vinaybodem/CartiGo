package com.cartigo.user.service;

import com.cartigo.user.dto.CustomerCreateRequest;
import com.cartigo.user.entity.Customer;
import com.cartigo.user.entity.Role;
import com.cartigo.user.entity.User;
import com.cartigo.user.exception.BadRequestException;
import com.cartigo.user.exception.ResourceNotFoundException;
import com.cartigo.user.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomerService customerService;

    // ✅ 1. CREATE SUCCESS
    @Test
    void createCustomerProfile_shouldCreateSuccessfully() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setRole(Role.CUSTOMER);

        CustomerCreateRequest req = new CustomerCreateRequest();
        req.setPhoneNumber("9999999999");
        req.setAddressLine1("Address");
        req.setCity("City");
        req.setState("State");
        req.setPincode("500001");
        req.setCountry("India");

        when(userService.getUser(userId)).thenReturn(user);
        doNothing().when(userService).assertRole(user, Role.CUSTOMER);
        when(customerRepository.existsById(userId)).thenReturn(false);

        Customer saved = new Customer();
        saved.setId(userId);

        when(customerRepository.save(any())).thenReturn(saved);

        Customer result = customerService.createCustomerProfile(userId, req);

        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    // ❌ 2. PROFILE EXISTS
    @Test
    void createCustomerProfile_shouldThrow_whenAlreadyExists() {
        Long userId = 1L;

        User user = new User();
        user.setRole(Role.CUSTOMER);

        when(userService.getUser(userId)).thenReturn(user);
        doNothing().when(userService).assertRole(user, Role.CUSTOMER);
        when(customerRepository.existsById(userId)).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> customerService.createCustomerProfile(userId, new CustomerCreateRequest()));
    }

    // ❌ 3. NOT FOUND
    @Test
    void getCustomer_shouldThrow_whenNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> customerService.getCustomer(1L));
    }

    // ✅ 4. GET SUCCESS
    @Test
    void getCustomer_shouldReturnCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomer(1L);

        assertEquals(1L, result.getId());
    }

    // ✅ 5. UPDATE CUSTOMER
    @Test
    void updateCustomer_shouldUpdateFields() {
        Long userId = 1L;

        Customer customer = new Customer();
        customer.setId(userId);

        CustomerCreateRequest req = new CustomerCreateRequest();
        req.setCity("UpdatedCity");

        when(customerRepository.findById(userId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any())).thenReturn(customer);

        Customer result = customerService.updateCustomer(userId, req);

        assertEquals("UpdatedCity", result.getCity());
    }
}