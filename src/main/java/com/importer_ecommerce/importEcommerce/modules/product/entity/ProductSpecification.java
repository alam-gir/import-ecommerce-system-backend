package com.importer_ecommerce.importEcommerce.modules.product.entity;

import com.importer_ecommerce.importEcommerce.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * ProductSpecification entity - Informational specs (Material composition, Care instructions, etc.)
 */
@Entity
@Table(name = "product_specifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSpecification extends AuditableEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name; // e.g., "Material", "Care Instructions", "Warranty"
    
    @Column(name = "value", nullable = false, columnDefinition = "TEXT")
    private String value; // e.g., "100% Cotton", "Machine washable", "1 Year"
}
