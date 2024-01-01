package com.arittek.befiler_services.services;

import com.arittek.befiler_services.model.Settings;

public interface SettingsServices {

    Settings getActiveRecord()throws Exception;

    Settings saveOrUpdated(Settings settings)throws Exception;
}
