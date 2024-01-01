package com.arittek.befiler_services.services;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.user.Corporate;
import com.arittek.befiler_services.model.user.CorporateEmployee;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.repository.corporateRepository.CorporateEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CorporateEmployeeServicesImpl implements CorporateEmployeeServices {

    CorporateEmployeeRepository corporateEmployeeRepository;

    @Autowired
    CorporateEmployeeServicesImpl(CorporateEmployeeRepository corporateEmployeeRepository){
        this.corporateEmployeeRepository=corporateEmployeeRepository;
    }


    @Override
    public CorporateEmployee saveOrUpdate(CorporateEmployee corporateEmployee) throws Exception {
        if (corporateEmployee != null) {
            return corporateEmployeeRepository.save(corporateEmployee);
        }
        return null;
    }

    @Override
    public CorporateEmployee findCorporateEmployeeByCnicNo(String cnic) throws Exception {
        if(cnic != null){
            CorporateEmployee corporateEmployee = corporateEmployeeRepository.findCorporateEmployeeByCnicNo(cnic);
            if(corporateEmployee != null){
                return corporateEmployee;
            }
        }
        return null;
    }

    @Override
    public CorporateEmployee create(CorporateEmployee corporateEmployee)throws Exception {
        if(corporateEmployee != null){
            return corporateEmployeeRepository.save(corporateEmployee);
        }
        return null;
    }

    @Override
    public CorporateEmployee findOne(Integer corporateEmployeeId)throws Exception {
        if(corporateEmployeeId != null){
            return corporateEmployeeRepository.findById(corporateEmployeeId).orElse(null);
        }
        return null;
    }

    @Override
    public CorporateEmployee findByIdAndStatus(Integer id,AppStatus appStatus)throws Exception {
       return corporateEmployeeRepository.findByIdAndStatus(id,appStatus);
    }

    @Override
    public void delete(CorporateEmployee corporateEmployee) throws Exception {
        if(corporateEmployee.getId() != null){
            corporateEmployeeRepository.delete(corporateEmployee);
        }
    }

    @Override
    public List<CorporateEmployee> findAllByUser(User user) throws Exception {
        if(user != null){
           return corporateEmployeeRepository.findAllByUser(user);
        }
        return null;
    }

    @Override
    public List<CorporateEmployee> findAllByCorporate(Corporate corporate) throws Exception {
        if(corporate != null){
            return corporateEmployeeRepository.findAllByCorporate(corporate);
        }
        return null;
    }

    @Override
    public List<CorporateEmployee> findAllByCorporateAndStatus(Corporate corporate, AppStatus status) throws Exception {
        if (corporate != null && status != null) {
            return corporateEmployeeRepository.findAllByCorporateAndStatus(corporate, status);
        }
        return null;
    }
}
