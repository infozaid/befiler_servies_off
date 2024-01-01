package com.arittek.befiler_services.controller;

import com.arittek.befiler_services.beans.SettingsBean;
import com.arittek.befiler_services.beans.Status;
import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.Settings;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.services.SettingsServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/settings")
public class SettingsController {

    private UsersServices usersServices;
    private SettingsServices settingsServices;

    @Autowired
    SettingsController(UsersServices usersServices, SettingsServices settingsServices) {
        this.usersServices = usersServices;
        this.settingsServices = settingsServices;
    }

    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<Status> saveOrUpdateSettings(@RequestBody SettingsBean settingsBean) throws Exception {
        try {
            if (settingsBean != null && settingsBean.getAuthorizerId() != null && settingsBean.getDaysToSentMarketing() != null && settingsBean.getMaxLoginAttemptsCount() != null) {

                /*User authorizer = usersServices.findOneByIdAndStatus(settingsBean.getAuthorizerId(), usersServices.findUserStatusById(1));*/
                User authorizer = usersServices.findOneByIdAndStatus(settingsBean.getAuthorizerId(), UserStatus.ACTIVE);
                if (authorizer == null) {
                    return new ResponseEntity<>(new Status(0, "Session expired"), HttpStatus.OK);
                }

                Settings settings = settingsServices.getActiveRecord();
                if (settings != null) {
                    settings.setStatus(AppStatus.DE_ACTIVE);
                    settingsServices.saveOrUpdated(settings);
                }
                settings = new Settings();
                settings.setAuthorizer(authorizer);
                settings.setCurrentDate(CommonUtil.getCurrentTimestamp());
                settings.setStatus(AppStatus.ACTIVE);
                settings.setDaysToSentMarketing(settingsBean.getDaysToSentMarketing());
                settings.setMaxLoginAttempts(settingsBean.getMaxLoginAttemptsCount());
                settingsServices.saveOrUpdated(settings);

                return new ResponseEntity<>(new Status(1, "Record updated successfully."), HttpStatus.OK);
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete data."), HttpStatus.OK);
    }

    @RequestMapping(produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<StatusBean> getSettings() throws Exception {
        try {
            Settings settings = settingsServices.getActiveRecord();
            if (settings != null) {

                SettingsBean settingsBean = new SettingsBean();

                if (settings.getDaysToSentMarketing() != null)
                    settingsBean.setDaysToSentMarketing(settings.getDaysToSentMarketing());
                if (settings.getMaxLoginAttempts() != null)
                    settingsBean.setMaxLoginAttemptsCount(settings.getMaxLoginAttempts());

                List<SettingsBean> settingsBeanList = new ArrayList<>();
                settingsBeanList.add(settingsBean);

                StatusBean statusBean = new StatusBean(1, "Successfull");
                statusBean.setResponse(settingsBeanList);
                return new ResponseEntity<>(statusBean, HttpStatus.OK);
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
        }
        return new ResponseEntity<>(new StatusBean(0, "No record found"), HttpStatus.OK);

    }


}
