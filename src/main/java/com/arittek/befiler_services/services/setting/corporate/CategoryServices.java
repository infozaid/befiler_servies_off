package com.arittek.befiler_services.services.setting.corporate;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.settings.corporate.Category;

import java.util.List;

public interface CategoryServices {
    Category findOne(Integer categoryId)throws Exception;
    Category create(Category corporateCategory)throws Exception;
    List<Category> findAllByStatus(AppStatus appStatus)throws Exception;
    void delete(Category category);;
}

