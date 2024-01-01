package com.arittek.befiler_services.services.ntn;

import com.arittek.befiler_services.model.ntn.FbrUserAccountInfoDocuments;
import com.arittek.befiler_services.model.user.User;

import java.util.List;

public interface FbrUserAccountInfoDocumentsServices {

    FbrUserAccountInfoDocuments save(FbrUserAccountInfoDocuments fbrUserAccountInfoDocuments) throws Exception;
    Boolean saveDocumentsByUserAndDocumentType(List<FbrUserAccountInfoDocuments> fbrUserAccountInfoDocumentsList, User user, String documentType) throws Exception;

    Boolean deleteAllByUserNotIn(User user, List<Integer> activeRecordsList) throws Exception;

    FbrUserAccountInfoDocuments findOneActiveRecordById(Integer fbrUserAccountInfoDocumentId) throws Exception;

    List<FbrUserAccountInfoDocuments> findAllActiveRecordsByUser(User user) throws Exception;
    List<FbrUserAccountInfoDocuments> findAllActiveRecordsByUserAndDocumentType(User user, String documentType) throws Exception;
}
