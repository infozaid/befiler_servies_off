package com.arittek.befiler_services.services.setting.corporate;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.settings.corporate.Category;
import com.arittek.befiler_services.repository.setting.corporate.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServicesImpl implements CategoryServices {

    CategoryRepository corporateCategoryRepository;

    @Autowired
    public CategoryServicesImpl(CategoryRepository corporateCategoryRepository) {
        this.corporateCategoryRepository = corporateCategoryRepository;
    }

    @Override
    public Category create(Category corporateCategory) throws Exception {
        if(corporateCategory != null){
            return corporateCategoryRepository.save(corporateCategory);
        }
        return null;
    }

    @Override
    public List<Category> findAllByStatus(AppStatus appStatus) throws Exception {
        List<Category> list =corporateCategoryRepository.findAllByStatus(appStatus);
        if(list.size()>0){
            return list;
        }
        return null;
    }

    @Override
    public void delete(Category category) {
        if (category != null) {
            corporateCategoryRepository.delete(category);
        }
    }

    @Override
    public Category findOne(Integer categoryId) throws Exception {
        if(categoryId != null){
          return corporateCategoryRepository.findById(categoryId).orElse(null);
        }
        return null;
    }
}
