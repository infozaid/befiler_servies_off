package com.arittek.befiler_services.services;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.Settings;
import com.arittek.befiler_services.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsServicesImpl implements SettingsServices {

    private SettingsRepository settingsRepository;

    @Autowired
    public SettingsServicesImpl(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    public Settings getActiveRecord() throws Exception {
        return settingsRepository.findOneByStatus(AppStatus.ACTIVE);
    }

    @Override
    public Settings saveOrUpdated(Settings settings) throws Exception {
        if(settings != null){
         return settingsRepository.save(settings);
        }
        return null;
    }


}
