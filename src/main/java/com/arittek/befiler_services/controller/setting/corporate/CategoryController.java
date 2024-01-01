package com.arittek.befiler_services.controller.setting.corporate;

import com.arittek.befiler_services.beans.CorporateCategoryBean;
import com.arittek.befiler_services.beans.Status;
import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.settings.corporate.Category;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.services.setting.corporate.CategoryServices;
import com.arittek.befiler_services.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/corporate/category")
public class CategoryController {

    UsersServices usersServices;
    CategoryServices corporateCategoryServices;

    @Autowired
    public CategoryController(UsersServices usersServices, CategoryServices corporateCategoryServices) {
        this.usersServices = usersServices;
        this.corporateCategoryServices = corporateCategoryServices;
    }

    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Status> save(@RequestBody CorporateCategoryBean corporateCategoryBean) throws Exception {
        if (corporateCategoryBean != null) {
            /*User authorizer = usersServices.findOneByIdAndStatus(corporateCategoryBean.getAuthorizerId(), usersServices.findUserStatusById(1));*/
            User authorizer = usersServices.findOneByIdAndStatus(corporateCategoryBean.getAuthorizerId(), UserStatus.ACTIVE);
            if (authorizer != null) {

                Category corporateCategory = new Category();
                corporateCategory.setCategory(corporateCategoryBean.getCategory());
                corporateCategory.setStatus(AppStatus.ACTIVE);
                corporateCategory.setDiscription(corporateCategoryBean.getDiscription());
                corporateCategoryServices.create(corporateCategory);
                return new ResponseEntity<Status>(new Status(1, "Data save Successfully"), HttpStatus.OK);
            }
            return new ResponseEntity<Status>(new Status(0, "User not found!"), HttpStatus.OK);
        }

        return new ResponseEntity<Status>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }


    @RequestMapping(produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<StatusBean> getAll() throws Exception {

        List<CorporateCategoryBean> list1 = new ArrayList<>();
        List<Category> list = (List<Category>) corporateCategoryServices.findAllByStatus(AppStatus.ACTIVE);
        if (list.size() > 0) {
             for (Category corporateCategory : list) {

                CorporateCategoryBean bean = new CorporateCategoryBean();
                bean.setId(corporateCategory.getId());
                bean.setAppStatus(corporateCategory.getStatus().getId());
                bean.setCategory(corporateCategory.getCategory());
                bean.setDiscription(corporateCategory.getDiscription());
                list1.add(bean);
            }
            StatusBean statusBean = new StatusBean(1,"successfully");
            statusBean.setResponse(list1);
            return new ResponseEntity<StatusBean>(statusBean, HttpStatus.OK);
        }
        return new ResponseEntity<StatusBean>(new StatusBean(0,"data not found"), HttpStatus.OK);
    }

    @RequestMapping(produces = "application/json", method = RequestMethod.DELETE)
    public StatusBean delete(@RequestBody CorporateCategoryBean corporateCategoryBean) throws Exception {
        if (corporateCategoryBean != null && corporateCategoryBean.getId() != null) {
            Category category = corporateCategoryServices.findOne(corporateCategoryBean.getId());
            if (category != null) {
                corporateCategoryServices.delete(category);
                return new StatusBean(1, "delete successfully");
            }
        }
        return new StatusBean(0, "failed");
    }
}