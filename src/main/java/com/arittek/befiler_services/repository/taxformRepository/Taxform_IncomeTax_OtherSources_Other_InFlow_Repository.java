package com.arittek.befiler_services.repository.taxformRepository;

import com.arittek.befiler_services.model.taxform.TaxForm_IncomeTax_OtherSources_OtherInflow;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.taxform.Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Taxform_IncomeTax_OtherSources_Other_InFlow_Repository extends JpaRepository<TaxForm_IncomeTax_OtherSources_OtherInflow, Integer>, RevisionRepository<TaxForm_IncomeTax_OtherSources_OtherInflow, Integer, Integer> {

    void deleteAllByTaxformAndIdNotIn(Taxform taxform, List otherInflowsUpdatedRecords);
}
