package com.arittek.befiler_services.controller;/*
package com.arittek.controller;

import com.arittek.beans.FbrUserAccountInfoBean;
import com.arittek.beans.FbrUserAccountInfoDocumentsBean;
import com.arittek.beans.StatusBean;
import com.arittek.util.AntiVirusUtil;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

@Controller
@RequestMapping(value = "/virusScan")
public class UploadController {

    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "F://temp//";

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/upload") // //new annotation since 4.3
    public ResponseEntity<StatusBean> singleFileUpload(@RequestBody() FbrUserAccountInfoBean fbrUserAccountInfoBean) {
        ArrayList<FbrUserAccountInfoDocumentsBean> statusBeans = new ArrayList<>();
        for (FbrUserAccountInfoDocumentsBean infoDocumentsBean : fbrUserAccountInfoBean.getVirusScanList()) {
            byte[] base64 = Base64.decodeBase64(infoDocumentsBean.getBase64());
            try {
                boolean isVirusFound = AntiVirusUtil.scanFile(base64, infoDocumentsBean.getFilename());
                FbrUserAccountInfoDocumentsBean bean = new FbrUserAccountInfoDocumentsBean();
                bean.setFilename(infoDocumentsBean.getFilename());
                bean.setStatus(isVirusFound);
                statusBeans.add(bean);

                System.out.println("VirusFileName: "+infoDocumentsBean.getFilename());
                System.out.println("VirusFileSize: "+infoDocumentsBean.getFilesize());
                System.out.println("VirusFileType: "+infoDocumentsBean.getFiletype());
                System.out.println("VirusFileStatus: "+bean.isStatus());
                String fileUrl= "D:\\UPLOADED_FOLDER\\" + infoDocumentsBean.getFilename();
                byte[] imageByte= Base64.decodeBase64(infoDocumentsBean.getBase64());
                BufferedOutputStream imageOutFile = new BufferedOutputStream(new FileOutputStream(fileUrl));
                imageOutFile.write(imageByte);
                imageOutFile.close();

            } catch (IOException e) {
                return new ResponseEntity<>(new StatusBean(0,e.getMessage()), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(new StatusBean(0,e.getMessage()), HttpStatus.OK);
            }
        }
        StatusBean  statusBean = new StatusBean(1,"SUCCESS");
        statusBean.getResponse().addAll(statusBeans);
        return new ResponseEntity<>(statusBean, HttpStatus.OK);
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

}
*/
