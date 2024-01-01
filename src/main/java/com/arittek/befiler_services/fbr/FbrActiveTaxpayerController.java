package com.arittek.befiler_services.fbr;


import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.beans.UserDetailBean;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.services.user.UsersServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/fbr/taxpayer")
public class FbrActiveTaxpayerController {


    private FbrActiveTaxpayerRepository repository;
    private UsersServices usersServices;

    @Autowired
    FbrActiveTaxpayerController(FbrActiveTaxpayerRepository repository, UsersServices usersServices) {
        this.usersServices = usersServices;
        this.repository = repository;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<StatusBean> getRegistrationNoDetail() throws IOException {

        try{
            File f = new File("C:\\example");
            int count = f.listFiles().length;
            if(count == 0){
                System.out.println("There is no any File available here "+f.getPath());
                return new ResponseEntity<>(new StatusBean(0, "There is no any File available here ."), HttpStatus.OK);
            }
            for(File file: f.listFiles()){
                new Thread(new ReadFile(repository,file.getPath())).start();
            }

        }catch (Exception e){e.printStackTrace();}

        return new ResponseEntity<>(new StatusBean(1, "success"), HttpStatus.OK);
    }

    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getByUserId(@RequestBody UserDetailBean detailBean) {
        if (detailBean.getUserId() != null) {
            try {
                User user = usersServices.findOneByIdAndStatus(detailBean.getUserId(), UserStatus.ACTIVE);
                if (user != null) {

                    FbrActiveTaxpayer fbrActiveTaxpayer = repository.findOneByRegistrationNo(user.getCnicNo());
                    if (fbrActiveTaxpayer != null) {
                        FbrActiveTaxpayerBean registeredBean = new FbrActiveTaxpayerBean();
                        registeredBean.setRegistrationNo(fbrActiveTaxpayer.getRegistrationNo());
                        registeredBean.setName(fbrActiveTaxpayer.getName());
                        registeredBean.setBusinessName(fbrActiveTaxpayer.getBusinessName());

                        StatusBean bean = new StatusBean(1,"success");
                        List<FbrActiveTaxpayerBean> registeredBeans = new ArrayList<>();
                        registeredBeans.add(registeredBean);
                        bean.setResponse(registeredBeans);

                        return new ResponseEntity<>(bean, HttpStatus.OK);
                    }
                    return new ResponseEntity<>(new StatusBean(0, "No record Exits"), HttpStatus.OK);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity<>(new StatusBean(0, "Invalid user."), HttpStatus.OK);
    }

/*    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<StatusBean> getRegistrationNoDetail() throws IOException {
        try {
            String path = "D://2018827981843494ATL_IT.xlsx";
            InputStream is = new FileInputStream(new File(path));

            Workbook workbook = StreamingReader.builder()
                    .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                    .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                    .open(is);

            long countRow = 0;
            for (Sheet sheet : workbook) {
                System.out.println(sheet.getSheetName());
                for (Row r : sheet) {
                    FbrActiveTaxpayer payerRegistered = new FbrActiveTaxpayer();
                    payerRegistered.setRegistrationNo(r.getCell(1).getStringCellValue());
                    payerRegistered.setName(r.getCell(2).getStringCellValue());
                    payerRegistered.setBusinessName(r.getCell(3).getStringCellValue());
                    payerRegistered.setCurrDate(CommonUtil.getCurrentTimestamp());
                    repository.save(payerRegistered);
                    System.out.println(countRow++);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new StatusBean(1, "success"), HttpStatus.OK);
    }
*/
}
