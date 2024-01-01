package com.arittek.befiler_services.repository.active_taxpayer;

import com.arittek.befiler_services.model.active_taxpayer.ActiveTaxpayerInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActiveTaxpayerInquiryRepository extends JpaRepository<ActiveTaxpayerInquiry, Integer> {

    ActiveTaxpayerInquiry findOneByRegistrationNo(String registrationNo);
}
