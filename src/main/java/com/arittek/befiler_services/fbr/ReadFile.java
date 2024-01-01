package com.arittek.befiler_services.fbr;


import com.arittek.befiler_services.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;

public class ReadFile implements Runnable {
    @Autowired
    private FbrActiveTaxpayerRepository repository;
    private String filePath;
    BufferedReader br = null;
    InputStream is = null;

    FbrActiveTaxpayer payerRegistered = null;

    public ReadFile(FbrActiveTaxpayerRepository repository, String filePath) {
        this.repository = repository;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        try {
            is = new FileInputStream(new File(filePath));
            br = new BufferedReader(new InputStreamReader(is));

            Path path = Paths.get(filePath);
            long lineCount = Files.lines(path).count();
            System.out.println("Path: "+path+"\tCount Lines:::::::" + lineCount);
            br.readLine(); // remove first Line
            long count = 0;
            while (count < lineCount) {

                payerRegistered = new FbrActiveTaxpayer();
                StringTokenizer st = new StringTokenizer(br.readLine(), ",");
                Boolean check = true;

                for (int i = 0; check; i++) {
                    if (i == 0) {
//                        System.out.println("S.NO: " + st.nextToken());
                        payerRegistered.setSrNo(st.nextToken());
                    } else if (i == 1) {
                        payerRegistered.setRegistrationNo(st.nextToken());
//                          System.out.println("Rr.No: " + st.nextToken());
                    } else if (i == 2) {
                        payerRegistered.setName(st.nextToken());
//                       System.out.println("Name: " + st.nextToken());
                    } else if (i == 3) {
                        payerRegistered.setBusinessName(st.nextToken());
//                       System.out.println("B.Name: " + st.nextToken());
                    }
                    if (!st.hasMoreTokens()) {
                        check = false;
                    }
                }
                payerRegistered.setCurrDate(CommonUtil.getCurrentTimestamp());
                repository.save(payerRegistered);

                System.out.println("--------------------------");
                count++;

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (is != null)
                    is.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
