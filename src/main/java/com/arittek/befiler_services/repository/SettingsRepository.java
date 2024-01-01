package com.arittek.befiler_services.repository;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.Settings;
import org.springframework.data.repository.CrudRepository;

public interface SettingsRepository extends CrudRepository<Settings,Integer> {

    Settings findOneByStatus(AppStatus appStatus);

}
