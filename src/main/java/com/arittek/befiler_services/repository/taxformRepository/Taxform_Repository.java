package com.arittek.befiler_services.repository.taxformRepository;

import com.arittek.befiler_services.model.taxform.TaxformYears;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.taxform.Taxform_Status;
import com.arittek.befiler_services.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Taxform_Repository extends JpaRepository<Taxform, Integer>, RevisionRepository<Taxform, Integer, Integer> {

    Taxform findByUser(User user);
    Taxform findOneByTaxformYearAndUser(TaxformYears taxformYear, User user);

    List<Taxform> findAllByUser(User userId);
    List<Taxform> findAllByUserAndTaxformYearIn(User user, List<TaxformYears> taxformYearsList);
    List<Taxform> findAllByUserAndStatus(User user, Taxform_Status status);
    List<Taxform> findAllByStatus(Taxform_Status status);

    List<Taxform> findAllByStatusIn(List<Taxform_Status> taxform_statusList);
    List<Taxform> findAllByStatusNotIn(List<Taxform_Status> taxform_statusList);

  //  @Query(value = "SELECT s FROM taxform s WHERE s.taxform_status_id <   :7",nativeQuery=true)
    List<Taxform> findAllByStatusLessThan(Taxform_Status status);
}
