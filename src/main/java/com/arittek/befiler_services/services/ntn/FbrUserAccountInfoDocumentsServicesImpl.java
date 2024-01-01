package com.arittek.befiler_services.services.ntn;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.ntn.FbrUserAccountInfoDocuments;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.repository.ntn.FbrUserAccountInfoDocumentsRepository;
import com.arittek.befiler_services.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FbrUserAccountInfoDocumentsServicesImpl implements FbrUserAccountInfoDocumentsServices{

    private FbrUserAccountInfoDocumentsRepository fbrUserAccountInfoDocumentsRepository;

    @Autowired
    FbrUserAccountInfoDocumentsServicesImpl(FbrUserAccountInfoDocumentsRepository fbrUserAccountInfoDocumentsRepository) {
        this.fbrUserAccountInfoDocumentsRepository = fbrUserAccountInfoDocumentsRepository;
    }

    @Override
    public FbrUserAccountInfoDocuments save(FbrUserAccountInfoDocuments fbrUserAccountInfoDocuments) throws Exception {
        if (fbrUserAccountInfoDocuments != null) {
            return fbrUserAccountInfoDocumentsRepository.save(fbrUserAccountInfoDocuments);
        }
        return null;
    }

    @Override
    public Boolean saveDocumentsByUserAndDocumentType(List<FbrUserAccountInfoDocuments> fbrUserAccountInfoDocumentsList, User user, String documentType) throws Exception {
        if (fbrUserAccountInfoDocumentsList != null && fbrUserAccountInfoDocumentsList.size() > 0 && user != null && documentType != null && !documentType.isEmpty()) {

            List<FbrUserAccountInfoDocuments> fbrUserAccountInfoDocumentsList1 = findAllActiveRecordsByUserAndDocumentType(user, documentType);
            if (fbrUserAccountInfoDocumentsList1 != null) {
                for (FbrUserAccountInfoDocuments fbrUserAccountInfoDocuments : fbrUserAccountInfoDocumentsList1) {
                    if (fbrUserAccountInfoDocuments != null) {
                        /*fbrUserAccountInfoDocuments.setStatus(appStatusServices.findOneByDeletedStatus());*/
                        fbrUserAccountInfoDocuments.setAppStatus(AppStatus.DELETED);

                        fbrUserAccountInfoDocumentsRepository.save(fbrUserAccountInfoDocuments);
                    }
                }
            }

            for (FbrUserAccountInfoDocuments fbrUserAccountInfoDocuments : fbrUserAccountInfoDocumentsList) {
                fbrUserAccountInfoDocumentsRepository.save(fbrUserAccountInfoDocuments);
            }
        }
        return null;
    }

    @Override
    public Boolean deleteAllByUserNotIn(User user, List<Integer> activeRecordsList) throws Exception {
        if (user != null && activeRecordsList != null && activeRecordsList.size() > 0) {
            List<FbrUserAccountInfoDocuments> fbrUserAccountInfoDocumentsDeleteList = fbrUserAccountInfoDocumentsRepository.findAllByUserAndIdNotIn(user, activeRecordsList);
            if (fbrUserAccountInfoDocumentsDeleteList != null && fbrUserAccountInfoDocumentsDeleteList.size() > 0) {
                for (FbrUserAccountInfoDocuments fbrUserAccountInfoDocuments : fbrUserAccountInfoDocumentsDeleteList) {
                    /*fbrUserAccountInfoDocuments.setStatus(appStatusServices.findOneByDeletedStatus());*/
                    fbrUserAccountInfoDocuments.setAppStatus(AppStatus.DELETED);

                    fbrUserAccountInfoDocumentsRepository.save(fbrUserAccountInfoDocuments);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public FbrUserAccountInfoDocuments findOneActiveRecordById(Integer fbrUserAccountInfoDocumentId) throws Exception {
        /*AppStatus status = appStatusServices.findOneByActiveStatus();*/
        if (fbrUserAccountInfoDocumentId != null/* && status != null*/) {
            /*return fbrUserAccountInfoDocumentsRepository.findOneByIdAndStatus(fbrUserAccountInfoDocumentId, appStatusServices.findOneByActiveStatus());*/
            return fbrUserAccountInfoDocumentsRepository.findOneByIdAndAppStatus(fbrUserAccountInfoDocumentId, AppStatus.ACTIVE);
        }
        return null;
    }

    @Override
    public List<FbrUserAccountInfoDocuments> findAllActiveRecordsByUser(User user) throws Exception {
        if (user != null) {
            /*return fbrUserAccountInfoDocumentsRepository.findAllByUserAndStatus(user, appStatusServices.findOneByActiveStatus());*/
            return fbrUserAccountInfoDocumentsRepository.findAllByUserAndAppStatus(user, AppStatus.ACTIVE);
        }
        return null;
    }

    @Override
    public List<FbrUserAccountInfoDocuments> findAllActiveRecordsByUserAndDocumentType(User user, String documentType) throws Exception {
        if (user != null && documentType != null && !documentType.isEmpty()) {
            /*return fbrUserAccountInfoDocumentsRepository.findAllByUserAndDocumentTypeAndStatus(user, documentType, appStatusServices.findOneByActiveStatus());*/
            return fbrUserAccountInfoDocumentsRepository.findAllByUserAndDocumentTypeAndAppStatus(user, documentType, AppStatus.ACTIVE);
        }
        return null;
    }
}
