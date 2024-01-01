package com.arittek.befiler_services.repository.taxformRepository;

import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.taxform.Taxform_TaxDeductedCollected_BankingTransaction;
import com.arittek.befiler_services.model.taxform.Taxform_TaxDeductedCollected_WithholdTaxVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Taxform_TaxDeductedCollected_BankingTransaction_Repository extends JpaRepository<Taxform_TaxDeductedCollected_BankingTransaction, Integer>, RevisionRepository<Taxform_TaxDeductedCollected_BankingTransaction, Integer, Integer> {
    List<Taxform_TaxDeductedCollected_BankingTransaction> findAllByTaxform(Taxform taxform);

    void deleteAllByTaxformAndIdNotIn(Taxform taxform, List deductedCollectedBankingTransactionUpdatedRecord);
}
