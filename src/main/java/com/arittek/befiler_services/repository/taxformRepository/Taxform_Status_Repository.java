package com.arittek.befiler_services.repository.taxformRepository;

import com.arittek.befiler_services.model.taxform.Taxform_Status;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Taxform_Status_Repository extends CrudRepository<Taxform_Status, Integer> {
    public Taxform_Status findByStatus(Integer id);

}