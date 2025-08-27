package com.user.service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * User address entity for ecommerce platform
 * Supports multiple addresses per user with default address designation
 */
@Entity
@Getter
@Setter
@Table(name = "user_addresses", indexes = {
    @Index(name = "idx_address_user_id", columnList = "user_id"),
    @Index(name = "idx_address_default", columnList = "user_id, is_default")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends BaseVO {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotBlank(message = "Address line 1 is required")
    @Size(max = 255, message = "Address line 1 cannot exceed 255 characters")
    @Column(name = "address_line1", nullable = false, length = 255)
    private String addressLine1;
    
    @Size(max = 255, message = "Address line 2 cannot exceed 255 characters")
    @Column(name = "address_line2", length = 255)
    private String addressLine2;
    
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String city;
    
    @NotBlank(message = "State/Province is required")
    @Size(max = 100, message = "State/Province cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String state;
    
    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;
    
    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String country;
    
    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;
    
    @Size(max = 100, message = "Address label cannot exceed 100 characters")
    @Column(length = 100)
    private String label; // e.g., "Home", "Work", "Shipping"
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Business logic methods
    
    /**
     * Get full address as formatted string
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(addressLine1);
        
        if (addressLine2 != null && !addressLine2.trim().isEmpty()) {
            sb.append(", ").append(addressLine2);
        }
        
        sb.append(", ").append(city)
          .append(", ").append(state)
          .append(" ").append(postalCode)
          .append(", ").append(country);
        
        return sb.toString();
    }
    
    /**
     * Get short address (city, state, country)
     */
    public String getShortAddress() {
        return String.format("%s, %s, %s", city, state, country);
    }
    
    /**
     * Check if this is a shipping address
     */
    public boolean isShippingAddress() {
        return !isDefault || "Shipping".equalsIgnoreCase(label);
    }
    
    /**
     * Check if this is a billing address
     */
    public boolean isBillingAddress() {
        return isDefault || "Billing".equalsIgnoreCase(label);
    }
}
