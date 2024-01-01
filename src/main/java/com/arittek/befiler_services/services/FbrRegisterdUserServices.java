package com.arittek.befiler_services.services;

import com.arittek.befiler_services.model.FbrRegisteredUser;

public interface FbrRegisterdUserServices {

     public FbrRegisteredUser createFbrRegistredUser(FbrRegisteredUser fbrRegisteredUser)throws Exception;
     public FbrRegisteredUser update(FbrRegisteredUser fbrRegistredUser);

}
