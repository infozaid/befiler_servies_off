package com.arittek.befiler_services.repository.taxformRepository;

import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.taxform.Taxform_WelthStatement_BankAccountsOrInvestments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Taxform_WelthStatement_BankAccountsOrInvestments_Repository extends JpaRepository<Taxform_WelthStatement_BankAccountsOrInvestments, Integer>, RevisionRepository<Taxform_WelthStatement_BankAccountsOrInvestments, Integer, Integer> {
    List<Taxform_WelthStatement_BankAccountsOrInvestments> findAllByTaxform(Taxform taxform);

    void deleteAllByTaxformAndIdNotIn(Taxform taxform, List bankAccountsOrInvestmentUpdatedRecord);
}
