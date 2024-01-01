package com.arittek.befiler_services.repository.setting.corporate;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.settings.corporate.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>, RevisionRepository<Category, Integer, Integer> {
    List<Category> findAllByStatus(AppStatus appStatus)throws  Exception;
    void delete(Category category);;

}
