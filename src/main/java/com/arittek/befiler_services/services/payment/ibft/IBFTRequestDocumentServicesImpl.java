package com.arittek.befiler_services.services.payment.ibft;

import com.arittek.befiler_services.model.payment.ibft.IBFTRequestDocument;
import com.arittek.befiler_services.repository.payment.ibft.IBFTRequestDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IBFTRequestDocumentServicesImpl implements IBFTRequestDocumentServices {

    private IBFTRequestDocumentRepository ibftRequestDocumentRepository;

    @Autowired
    public IBFTRequestDocumentServicesImpl(IBFTRequestDocumentRepository ibftRequestDocumentRepository) {
	this.ibftRequestDocumentRepository = ibftRequestDocumentRepository;
    }

    @Override
    public IBFTRequestDocument saveOrUpdate(IBFTRequestDocument ibftRequestDocument) throws Exception {
        if (ibftRequestDocument != null)
            return ibftRequestDocumentRepository.save(ibftRequestDocument);
	return null;
    }
}
