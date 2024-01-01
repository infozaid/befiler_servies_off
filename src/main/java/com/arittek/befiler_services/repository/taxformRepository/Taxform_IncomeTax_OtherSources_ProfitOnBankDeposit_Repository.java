package com.arittek.befiler_services.repository.taxformRepository;

import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.taxform.Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit_Repository extends JpaRepository<Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit, Integer>, RevisionRepository<Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit, Integer, Integer> {
    List<Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit> findAllByTaxform(Taxform id);

    void deleteAllByTaxformAndIdNotIn(Taxform taxform, List bankDepositUpdatedRecords);

}
