package com.arittek.befiler_services.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/marketing")
public class MarketingController {


    /*private UsersServices usersServices;
    private TaxformServices taxformServices;
    private AppStatusServices appStatusServices;
    private CorporateEmployeeServices corporateEmployeeServices;
    @Autowired
    MarketingController( UsersServices usersServices, TaxformServices taxformServices, AppStatusServices appStatusServices, CorporateEmployeeServices corporateEmployeeServices){
        this.usersServices=usersServices;
        this.taxformServices=taxformServices;
        this.appStatusServices=appStatusServices;
        this.corporateEmployeeServices=corporateEmployeeServices;
    }*/


  /*  //   SAVE
    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Status> save(@RequestBody MarketingBean marketingBean) throws Exception {
        if (marketingBean != null) {
            User user = usersServices.findOne(marketingBean.getUserId());
            Taxform taxform = taxformServices.findOne(marketingBean.getTaxformId());

            Marketing marketing1 = marketingServices.findByUserAndTaxform(user, taxform);
            if (marketing1 == null) {

                if (user != null && taxform != null) {
                    Marketing marketing = new Marketing();
                    marketing.setUser(user);
                    marketing.setTaxform(taxform);
                    marketing.setCurrentDate(CommonUtil.getCurrentTimestamp());
                    marketing.setStatus(appStatusServices.findOne(1));
                    marketingServices.save(marketing);
                    return new ResponseEntity<Status>(new Status(1, "Save successfully"), HttpStatus.OK);
                }
            }
            return new ResponseEntity<Status>(new Status(0, "already send!"), HttpStatus.OK);
        }
        return new ResponseEntity<Status>(new Status(0, "Incomplete data"), HttpStatus.OK);
    }*/

   /* // LIST OF TAXFORM IN INDIVIDUAL
    @RequestMapping(value = "/individual/texforms", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<StatusBean> getIndividualList() throws Exception {
        List<TaxformMinimalBean> taxformMinimalBeanList = new ArrayList<TaxformMinimalBean>();
        List<Marketing> getList = marketingServices.findAll();

        if (getList.size() > 0) {
            for (Marketing marketing : getList) {

                Taxform taxform = taxformServices.findOne(marketing.getTaxform().getId());
                if (taxform != null) {

                    User user = usersServices.findOne(taxform.getUser().getId());
                    if (user != null && user.getUserType() != null && user.getUserType().getId() == 1) { //individual employee usertype 1

                        TaxformMinimalBean taxformMinimalBean = new TaxformMinimalBean();
                        taxformMinimalBean.setTaxformId(taxform.getId());
                        taxformMinimalBean.setTaxYear(taxform.getYear());
                        taxformMinimalBean.setCnic(taxform.getCnic());
                        taxformMinimalBean.setNameAsPerCnic(taxform.getNameAsPerCnic());
                        taxformMinimalBean.setStatus(taxform.getStatus().getStatus());
                        taxformMinimalBean.setDateDifference(CommonUtil.daysBetweenTwoDates(taxform.getCurrentDate(),CommonUtil.getCurrentTimestamp())); //
                        taxformMinimalBeanList.add(taxformMinimalBean);

                        StatusBean statusBean = new StatusBean(1, "successfully");
                        statusBean.setResponse(taxformMinimalBeanList);
                        return new ResponseEntity<StatusBean>(statusBean, HttpStatus.OK);
                    } // user type
                } // taxform
            }   // for loop
        }
        return new ResponseEntity<StatusBean>(new StatusBean(0, "Empty list"), HttpStatus.OK);
    }*/

    //  LIST OF TAXFORM IN CORPORATE
    /*@RequestMapping(value = "/corporate/texforms", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<StatusBean> getCorporateTexform() throws Exception {
        List<TaxformMinimalBean> taxformMinimalBeanList = new ArrayList<TaxformMinimalBean>();
        List<Marketing> getList = marketingServices.findAll();

        if (getList.size() > 0) {
            for (Marketing marketing : getList) {
                Taxform taxform = taxformServices.findOne(marketing.getTaxform().getId());
                if (taxform != null) {
                    User user = usersServices.findOne(taxform.getUser().getId());
                    if (user != null && user.getUserType() != null && user.getUserType().getId() == 2) { // Corporate Employee usertype 2
                        TaxformMinimalBean taxformMinimalBean = new TaxformMinimalBean();
                        taxformMinimalBean.setTaxformId(taxform.getId());
                        taxformMinimalBean.setTaxYear(taxform.getYear());
                        taxformMinimalBean.setCnic(taxform.getCnic());
                        taxformMinimalBean.setNameAsPerCnic(taxform.getNameAsPerCnic());
                        taxformMinimalBean.setStatus(taxform.getStatus().getStatus());
                        taxformMinimalBean.setDateDifference(CommonUtil.daysBetweenTwoDates(taxform.getCurrentDate(),CommonUtil.getCurrentTimestamp()));
                        taxformMinimalBeanList.add(taxformMinimalBean);
                        StatusBean statusBean = new StatusBean(1, "successfully");
                        statusBean.setResponse(taxformMinimalBeanList);
                        return new ResponseEntity<StatusBean>(statusBean, HttpStatus.OK);
                    } // user type
                } // taxform
            }   // for loop
        }
        return new ResponseEntity<StatusBean>(new StatusBean(0, "Data not found!"), HttpStatus.OK);
    }*/
}
