/*
package com.arittek.befiler_services.config.scheduler;


import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.AssignType;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.Assign;
import com.arittek.befiler_services.model.notifications.DynamicNotification;
import com.arittek.befiler_services.model.notifications.Notification;
import com.arittek.befiler_services.model.ntn.FbrUserAccountInfo;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.taxform.Taxform_Status;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.services.*;
import com.arittek.befiler_services.services.notifications.DynamicNotificationServices;
import com.arittek.befiler_services.services.notifications.NotificationLinksService;
import com.arittek.befiler_services.services.notifications.NotificationServices;
import com.arittek.befiler_services.services.notifications.NotificationTypeService;
import com.arittek.befiler_services.services.ntn.FbrUserAccountInfoServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import com.arittek.befiler_services.util.email.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Scheduler {

    private UsersServices usersServices;
    private TaxformServices taxformServices;
    private SettingsServices settingsServices;
    private TaxformStatusServices taxformStatusServices;
    private AssignServices assignServices;
    private NotificationServices notificationServices;
    private DynamicNotificationServices dynamicNotificationServices;
    private NotificationLinksService notificationLinksService;
    private NotificationTypeService notificationTypeService;
    private FbrUserAccountInfoServices fbrUserAccountInfoServices;
    @Value("${befiler.url}")
    private String serverUrl;


    @Autowired
    public Scheduler(UsersServices usersServices, TaxformServices taxformServices, SettingsServices settingsServices, TaxformStatusServices taxformStatusServices, AssignServices assignServices, NotificationServices notificationServices, DynamicNotificationServices dynamicNotificationServices, NotificationLinksService notificationLinksService, NotificationTypeService notificationTypeService, FbrUserAccountInfoServices fbrUserAccountInfoServices) {
        this.usersServices = usersServices;
        this.taxformServices = taxformServices;
        this.settingsServices = settingsServices;
        this.taxformStatusServices = taxformStatusServices;
        this.assignServices = assignServices;
        this.notificationServices = notificationServices;
        this.dynamicNotificationServices = dynamicNotificationServices;
        this.notificationLinksService = notificationLinksService;
        this.notificationTypeService = notificationTypeService;
        this.fbrUserAccountInfoServices = fbrUserAccountInfoServices;
    }

    */
/*@Scheduled(fixedDelay = 3600000) // one hour 3600000
    public void autoAssignToMarketing() throws Exception {
        try {
            Logger4j.getLogger().info("Inside Auto Assign to Marketing ::" + CommonUtil.getCurrentTimestamp());
            Taxform_Status accountantTaxformStatus = taxformStatusServices.findOneByAccountantStatus();

            if (accountantTaxformStatus != null) {
                List<Taxform> taxformList = taxformServices.findAllByStatus(accountantTaxformStatus);
                Settings settings = settingsServices.getActiveRecord();

                if (settings != null) {
                    for (Taxform taxform : taxformList) {

                        if (taxform != null && taxform.getStatus() != null) {
                            if (CommonUtil.daysBetweenTwoDates(CommonUtil.getCurrentTimestamp(), taxform.getCurrentDate()) > settings.getDaysToSentMarketing()) { // it will come from setting

                                Assign taxformAssign = new Assign();

                                //TODO assign marketing user not taxform user
                                taxformAssign.setUser(taxform.getUser());
                                taxformAssign.setTaxform(taxform);
                                taxformAssign.setCurrentDate(CommonUtil.getCurrentTimestamp());
                                taxformAssign.setAppStatus(appStatusServices.findOneByActiveStatus());
                                MyPrint.println(settings.getDaysToSentMarketing() + "Days sent to marketing after one hour: " + CommonUtil.getCurrentTimestamp());
                                assignServices.create(taxformAssign);
                            }
                        }
                    }
                }
            }
        } catch (InterruptedException ex) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(ex).getMessage());
            Logger4j.getLogger().error("Exception : " , ex);
            throw new IllegalStateException(ex);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            throw new IllegalStateException(e);
        }
    }
*//*



    // Auto Send Payment Notification
    */
/*@Scheduled(fixedDelay = 1000) // one minute 60000
    public void getTaxformByStatus() throws Exception {
        Taxform_Status fbrTaxformStatus = taxformStatusServices.findOneByFBRStatus();
        List<Taxform> taxformList = taxformServices.findAllByStatus(fbrTaxformStatus);

        String[] emails = {"info@befiler.com"};
        for (Taxform taxform : taxformList) {
            if (taxform.getUser() != null) {
                Notification notificationPersisted = notificationServices.findByToUserAndNotificationType(taxform.getUser().getId(), "3 Days FBR Reminder");
                if (notificationPersisted == null) {
                    if (CommonUtil.getTimeStampDiffGreaterThenThreeDays(taxform.getCurrentDate(), taxform.getCreationDate())) {
                       EmailSender.sendEmail("PAYMENT review remainder", "PAYMENT review remainder", emails);
                       EmailSender.sendEmail("PAYMENT review remainder", "FBR review remainder", taxform.getUser().getEmailAddress());

                        DynamicNotification dynamicNotification = dynamicNotificationServices.findByNotificationTypeAndStatus("3 Days FBR Reminder", 1);
                        Notification notification = new Notification();
                        if (dynamicNotification != null) {
                            if (dynamicNotification.getNotificationTitle() != null) {
                                notification.setNotificationTitle(dynamicNotification.getNotificationTitle());
                            }
                            if (dynamicNotification.getNotificationDescription() != null) {
                                notification.setNotificationDescription(dynamicNotification.getNotificationDescription());
                            }
                            if (dynamicNotification.getLink() != null) {
                                notification.setLink(dynamicNotification.getLink());
                            }
                            if (dynamicNotification.getNotificationType() != null) {
                                notification.setNotificationType(dynamicNotification.getNotificationType());
                            }
                            if (taxform.getUser().getId() != null) {
                                if (taxform.getUser().getId() != null) {
                                    notification.setToUser(taxform.getUser().getId());
                                }
                            }
                            notification.setCreateDate(CommonUtil.getCurrentTimestamp());
                            notification.setReadFlag(0);
                            notification.setArchiveFlag(0);

                            notificationServices.save(notification);
                        }
                    }
                }
            }
        }

        Taxform_Status paymentStatus = taxformStatusServices.findOneByPaymentStatus();
        taxformList = taxformServices.findAllByStatus(paymentStatus);
        for (Taxform taxform : taxformList) {
            if (taxform.getUser() != null) {
                Notification notificationPersisted = notificationServices.findByToUserAndNotificationType(taxform.getUser().getId(), "3 Days Payment Reminder");
                if (notificationPersisted == null) {
                    if (CommonUtil.getTimeStampDiffGreaterThenThreeDays(taxform.getCurrentDate(), taxform.getCreationDate())) {
                        EmailSender.sendEmail("PAYMENT review remainder", "PAYMENT review remainder", emails);
                        EmailSender.sendEmail("PAYMENT review remainder", "FBR review remainder", taxform.getUser().getEmailAddress());

                        DynamicNotification dynamicNotification = dynamicNotificationServices.findByNotificationTypeAndStatus("3 Days Payment Reminder", 1);
                        Notification notification = new Notification();
                        if (dynamicNotification != null) {
                            if (dynamicNotification.getNotificationTitle() != null) {
                                notification.setNotificationTitle(dynamicNotification.getNotificationTitle());
                            }
                            if (dynamicNotification.getNotificationDescription() != null) {
                                notification.setNotificationDescription(dynamicNotification.getNotificationDescription());
                            }
                            if (dynamicNotification.getLink() != null) {
                                notification.setLink(dynamicNotification.getLink());
                            }
                            if (dynamicNotification.getNotificationType() != null) {
                                notification.setNotificationType(dynamicNotification.getNotificationType());
                            }
                            if (taxform.getUser().getId() != null) {
                                if (taxform.getUser().getId() != null) {
                                    notification.setToUser(taxform.getUser().getId());
                                }
                            }
                            notification.setCreateDate(CommonUtil.getCurrentTimestamp());
                            notification.setReadFlag(0);
                            notification.setArchiveFlag(0);

                            notificationServices.save(notification);

                        }
                    }
                }
            }
        }
    }*//*



    //@Scheduled(fixedDelay = 3600000) // one hour 3600000
    */
/*@Scheduled(fixedDelay = 600000) // 10 minutes
    public void autoAssignToLawyer() throws Exception {
        try {
            Logger4j.getLogger().info("===============INSIDE AUTO ASSIGN TO LAWYER===============" + CommonUtil.getCurrentTimestamp());
            Logger4j.getLogger().info("TIMESTAMP ::: " + CommonUtil.getCurrentTimestamp());

            List<User> lawyers = usersServices.findAllActiveLawyers();
            if (lawyers != null && lawyers.size() > 0) {

                Taxform_Status accountantTaxformStatus = taxformStatusServices.findOneByAccountantStatus();
                List<Taxform> taxformList = taxformServices.findAllByStatus(accountantTaxformStatus);

                if (taxformList != null && taxformList.size() > 0) {

                    for (Taxform taxform : taxformList) {
                        Assign taxformAlreadyAssigned = assignServices.checkIfTaxformIsAssignedToLawyer(taxform);
                        if (taxformAlreadyAssigned == null) {
                            Logger4j.getLogger().info("TAXFORM IS NOT ASSIGNED ::: " + taxform.getId());

                            HashMap<Integer, Integer> taxformsAssignedToLawyers = new HashMap<>();
                            for (User lawyer : lawyers) {
                                List<Assign> taxformsAssignedToLawyer = assignServices.findAllActiveTaxformsAssignedToLawyer(lawyer);
                                if (taxformsAssignedToLawyer != null && taxformsAssignedToLawyer.size() > 0) {
                                    if (taxformsAssignedToLawyer.size() < 3) {
                                        taxformsAssignedToLawyers.put(lawyer.getId(), taxformsAssignedToLawyer.size());
                                    }
                                } else {
                                    taxformsAssignedToLawyers.put(lawyer.getId(), 0);
                                }
                            }
                            taxformsAssignedToLawyers = CommonUtil.sortHashMapByValues(taxformsAssignedToLawyers);

                            if (taxformsAssignedToLawyers != null && taxformsAssignedToLawyers.size() > 0) {
                                Map.Entry<Integer, Integer> entry = taxformsAssignedToLawyers.entrySet().iterator().next();
                                if (entry != null) {
                                    User lawyer = usersServices.findOneByIdAndStatus(entry.getKey(), UserStatus.ACTIVE);
                                    if (lawyer != null) {
                                        Assign taxformAssign = new Assign();
                                        taxformAssign.setAssignType(AssignType.TAXFORM);
                                        taxformAssign.setTaxform(taxform);
                                        taxformAssign.setCurrentDate(CommonUtil.getCurrentTimestamp());
                                        taxformAssign.setAppStatus(AppStatus.ACTIVE);
                                        taxformAssign.setUser(lawyer);
                                        assignServices.create(taxformAssign);

                                        taxform.setStatus(taxformStatusServices.findOneByLawyerNewStatus());
                                        taxform.setCurrentDate(CommonUtil.getCurrentTimestamp());

                                        taxformServices.updateTaxform(taxform);

                                        Logger4j.getLogger().info("TAXFORM ASSIGNED SUCCESSFULLY ::: TAXFORM::" + taxform.getId() + "#LAWYER::" + taxformAssign.getUser().getEmailAddress());
                                    }
                                }
                            }
                        } else {
                            Logger4j.getLogger().info("TAXFORM IS ALREADY ASSIGNED ::: TAXFORM::" + taxform.getId() + "#LAWYER::" + taxformAlreadyAssigned.getUser().getEmailAddress());
                        }
                    }
                } else {
                    Logger4j.getLogger().info("ALL TAXFORMS ARE ALREADY ASSIGNED ::: " + CommonUtil.getCurrentTimestamp());
                }

                List<FbrUserAccountInfo> fbrUserAccountInfoList = fbrUserAccountInfoServices.findAllByAccountantStatus();
                if (fbrUserAccountInfoList != null && fbrUserAccountInfoList.size() > 0) {
                    for (FbrUserAccountInfo fbrUserAccountInfo : fbrUserAccountInfoList) {
                        Assign ntnAlreadyAssigned = assignServices.checkIfFbrUserAccountInfoIsAssignedToLawyer(fbrUserAccountInfo);
                        if (ntnAlreadyAssigned == null) {
                            Logger4j.getLogger().info("NTN IS NOT ASSIGNED ::: " + fbrUserAccountInfo.getId());

                            HashMap<Integer, Integer> ntnAssignedToLawyers = new HashMap<>();
                            for (User lawyer : lawyers) {
                                List<Assign> taxformsAssignedToLawyer = assignServices.findAllActiveTaxformsAssignedToLawyer(lawyer);
                                if (taxformsAssignedToLawyer != null && taxformsAssignedToLawyer.size() > 0) {
                                    if (taxformsAssignedToLawyer.size() < 3) {
                                        ntnAssignedToLawyers.put(lawyer.getId(), taxformsAssignedToLawyer.size());
                                    }
                                } else {
                                    ntnAssignedToLawyers.put(lawyer.getId(), 0);
                                }
                            }
                            ntnAssignedToLawyers = CommonUtil.sortHashMapByValues(ntnAssignedToLawyers);

                            if (ntnAssignedToLawyers != null && ntnAssignedToLawyers.size() > 0) {
                                Map.Entry<Integer, Integer> entry = ntnAssignedToLawyers.entrySet().iterator().next();
                                if (entry != null) {
                                    User lawyer = usersServices.findOneByIdAndStatus(entry.getKey(), UserStatus.ACTIVE);
                                    if (lawyer != null) {
                                        Assign ntnAssign = new Assign();
                                        ntnAssign.setAssignType(AssignType.NTN);
                                        ntnAssign.setFbrUserAccountInfo(fbrUserAccountInfo);
                                        ntnAssign.setCurrentDate(CommonUtil.getCurrentTimestamp());
                                        ntnAssign.setAppStatus(AppStatus.ACTIVE);
                                        ntnAssign.setUser(lawyer);
                                        assignServices.create(ntnAssign);

                                        fbrUserAccountInfoServices.updateFbrUserAccountInfoToLawyerAssignStatus(fbrUserAccountInfo);

                                        Logger4j.getLogger().info("NTN ASSIGNED SUCCESSFULLY ::: NTN::" + fbrUserAccountInfo.getId() + "#LAWYER::" + ntnAssign.getUser().getEmailAddress());
                                    }
                                }
                            }
                        } else {
                            Logger4j.getLogger().info("NTN IS ALREADY ASSIGNED ::: NTN::" + fbrUserAccountInfo.getId() + "#LAWYER::" + ntnAlreadyAssigned.getUser().getEmailAddress());
                        }
                    }
                } else {
                    Logger4j.getLogger().info("ALL NTN ARE ALREADY ASSIGNED ::: " + CommonUtil.getCurrentTimestamp());
                }
            } else {
                Logger4j.getLogger().info("LAWYERS ARE NOT CREATED OR ALL LAWYERS ARE DISABLE/DELETED ::: " + CommonUtil.getCurrentTimestamp());
            }
        } catch (InterruptedException ex) {
            Logger4j.getLogger().error("AUTO ASSIGN TO LAWYERS RUN INTO AN ERROR ::: ", ex);
            throw new IllegalStateException(ex);
        }
    }*//*


}
*/
