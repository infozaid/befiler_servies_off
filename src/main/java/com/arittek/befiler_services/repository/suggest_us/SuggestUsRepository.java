package com.arittek.befiler_services.repository.suggest_us;

import com.arittek.befiler_services.model.suggest_us.SuggestUs;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestUsRepository extends CrudRepository<SuggestUs,Integer>{

}
