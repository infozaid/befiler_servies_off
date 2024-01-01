package com.arittek.befiler_services.controller.user;

import com.arittek.befiler_services.beans.MinimalUserBean;
import com.arittek.befiler_services.beans.Status;
import com.arittek.befiler_services.beans.UserExperienceLevelBean;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/user/experience")
public class UserExperienceLevelController {

    private final UsersServices usersServices;

    @Autowired
    public UserExperienceLevelController(UsersServices usersServices) {
        this.usersServices = usersServices;
    }

    @SuppressWarnings("unchecked")
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(produces = "application/json", method = {RequestMethod.POST})
    public ResponseEntity<Status> saveUserExperienceLevel(@RequestBody MinimalUserBean minimalUserBean) throws Exception {
        try {

            User user = null;
            if (minimalUserBean.getUserId() != null) {
                user = usersServices.findOneByIdAndStatus(minimalUserBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new Status(0,"Session Expired."), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new Status(0,"Session Expired."), HttpStatus.OK);
            }

            if (minimalUserBean.getExperienceLevel() == null) {
                return new ResponseEntity<>(new Status(0,"Please select Experience Level."), HttpStatus.OK);
            }

            user.setExperienceLevel(minimalUserBean.getExperienceLevel());
            usersServices.update(user);

            return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new Status(0, "Exception"), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<UserExperienceLevelBean> getUserExperienceLevel(@PathVariable("userId") Integer userId) throws Exception {
        try {

            User user = null;
            if (userId != null) {
                user = usersServices.findOneByIdAndStatus(userId, UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new UserExperienceLevelBean(0,"Session Expired."), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new UserExperienceLevelBean(0,"Session Expired."), HttpStatus.OK);
            }

            UserExperienceLevelBean experienceLevelBean = new UserExperienceLevelBean(1, "Successfull");
            if (user.getExperienceLevel() != null)
                experienceLevelBean.setExperienceLevel(user.getExperienceLevel());

            return new ResponseEntity<>(experienceLevelBean, HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new UserExperienceLevelBean(0, "Exception"), HttpStatus.OK);
        }

    }

}
