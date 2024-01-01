package com.arittek.befiler_services.fbr;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FbrActiveTaxpayerRepository extends CrudRepository<FbrActiveTaxpayer,Integer> {
    FbrActiveTaxpayer save(FbrActiveTaxpayer payerRegistered);
    FbrActiveTaxpayer findOneByRegistrationNo(String registrationNo);
}
