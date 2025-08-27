package com.importer_ecommerce.importEcommerce.email.repository;

import com.importer_ecommerce.importEcommerce.email.entity.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {
    
    Optional<EmailOtp> findByEmailAndOtpAndPurposeAndUsedFalse(String email, String otp, EmailOtp.OtpPurpose purpose);
    
    @Modifying
    @Transactional
    @Query("UPDATE EmailOtp e SET e.used = true WHERE e.email = ?1 AND e.purpose = ?2")
    void markAllAsUsed(String email, EmailOtp.OtpPurpose purpose);
    
    @Modifying
    @Transactional
    void deleteByEmailAndPurpose(String email, EmailOtp.OtpPurpose purpose);
}
