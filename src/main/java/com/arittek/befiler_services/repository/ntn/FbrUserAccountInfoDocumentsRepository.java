package com.arittek.befiler_services.repository.ntn;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.ntn.FbrUserAccountInfoDocuments;
import com.arittek.befiler_services.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FbrUserAccountInfoDocumentsRepository extends JpaRepository<FbrUserAccountInfoDocuments, Integer>, RevisionRepository<FbrUserAccountInfoDocuments, Integer, Integer> {

    FbrUserAccountInfoDocuments findOneByIdAndAppStatus(Integer fbrUserAccountInfoDocumentId, AppStatus appStatus);


    List<FbrUserAccountInfoDocuments> findAllByUserAndAppStatus(User user, AppStatus status);
    List<FbrUserAccountInfoDocuments> findAllByUserAndDocumentTypeAndAppStatus(User user, String documentType, AppStatus status);
    List<FbrUserAccountInfoDocuments> findAllByUserAndIdNotIn(User user, List<Integer> activeRecordsList);
}
