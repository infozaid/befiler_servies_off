package com.arittek.befiler_services.repository.payment.ibft;

import com.arittek.befiler_services.model.payment.ibft.IBFTRequestDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface IBFTRequestDocumentRepository extends JpaRepository<IBFTRequestDocument,Integer>, RevisionRepository<IBFTRequestDocument, Integer, Integer> {
}
