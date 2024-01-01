package com.arittek.befiler_services.security;

import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.user.role_permission.Role;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import com.arittek.befiler_services.util.MacAddress;
import com.arittek.befiler_services.util.MainLogger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JwtAuthorizationTokenFilter extends OncePerRequestFilter {

    private UserDetailsService userDetailsService;
    private JwtTokenUtil jwtTokenUtil;
    private String tokenHeader;
    private UsersServices usersServices;
    private ServletContext servletContext;

    public JwtAuthorizationTokenFilter(UserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil, String tokenHeader, UsersServices usersServices, ServletContext servletContext) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.tokenHeader = tokenHeader;
        this.usersServices = usersServices;
        this.servletContext = servletContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {

            MainLogger.getInstance().getLogger().info("TEMP :: IP:::"+request.getRemoteAddr()+"##MAC:::"+ MacAddress.getMac(request.getRemoteAddr())
                    +"##DATE:::"+ CommonUtil.getCurrentTimestamp().toString()+"##SERVICE:::"+request.getRequestURI()+"##METHOD:::"+request.getMethod());

            if(request.getRequestURI().contains(".png") ||
                    request.getRequestURI().contains(".jpg") ||
                    request.getRequestURI().contains(".pdf") ||
                    request.getRequestURI().contains(".jpeg") ||
                    request.getRequestURI().contains(".svg") ||
                    request.getRequestURI().contains(".png")) {

                chain.doFilter(request, response);
            }

            String url = request.getRequestURI().replace(servletContext.getContextPath() + "/", "").replaceAll("(([/]+)([0-9])+)","");
            Logger4j.getLogger().info("OriginalUrl: " + request.getRequestURI());
            Logger4j.getLogger().info("Url: " +request.getMethod()+"::"+ url);
            ArrayList<String> skipUrls = new ArrayList<>();
            skipUrls.add("auth");
            skipUrls.add("fbr/verification");
            skipUrls.add("verifyPin");
            skipUrls.add("users/registration");
            skipUrls.add("users/createPassword");
            skipUrls.add("users/forgotPassword");
            skipUrls.add("api/customer/registration");
            skipUrls.add("fbr/taxpayer");
            skipUrls.add("keenu/response");
            skipUrls.add("easypaisa/confirm");
            skipUrls.add("suggestUs");
            skipUrls.add("easypaisa/ipn");
            skipUrls.add("book");

            skipUrls.add("fbr/taxpayer/getByRegistrationNo");

            if (skipUrls.contains(url)) {
                /*Logger4j.getLogger().info("UserName: "+request.getParameter("username"));
                Logger4j.getLogger().info("Password: "+request.getParameter("password"));*/
                Logger4j.getLogger().info("DoFilter: "+Boolean.TRUE );
                chain.doFilter(request, response);
            }

            final String requestHeader = request.getHeader(this.tokenHeader);
            Logger4j.getLogger().info("Requeset Header :::: " + requestHeader);
            String username = null;
            String authToken = null;
            if (requestHeader != null && !requestHeader.equals("Basic")) {
                authToken = requestHeader;
                try {
                    username = jwtTokenUtil.getUsernameFromToken(authToken);
                } catch (IllegalArgumentException e) {
                    Logger4j.getLogger().error("an error occured during getting username from token", e);
                }
            } else {
                Logger4j.getLogger().warn("couldn't find token in request");
            }

            Logger4j.getLogger().info("checking authentication for user " + username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                MainLogger.getInstance().getLogger().info("IP:::"+request.getRemoteAddr()+"##MAC:::"+ MacAddress.getMac(request.getRemoteAddr())
                        +"##DATE:::"+ CommonUtil.getCurrentTimestamp().toString()+"##User:::"+username+"##SERVICE:::"+request.getRequestURI()+"##METHOD:::"+request.getMethod());
                // It is not h necessary to load the use details from the database. You could also store the information
                // in the token and read it from it. It's up to you ;)
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // For simple validation it is completely sufficient to just check the token integrity. You don't have to call
                // the database compellingly. Again it's up to you ;)
//                if (jwtTokenUtil.validateToken(authToken, userDetails)) {
//                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    Logger4j.getLogger().info("authenticated user " + username + ", setting security context");
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                }

                User user = usersServices.findOneByEmailAddress(username);
                if (user.getStatus() == UserStatus.DEACTIVE) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User Not Found OR User is deactivated");

                }
                Logger4j.getLogger().info("============================================");
                Logger4j.getLogger().info("UserName: " + username);
                Logger4j.getLogger().info("Status: " + user.getStatus());
                Logger4j.getLogger().info("Url: " +request.getMethod()+"::"+ url);
                if (!skipUrls.contains(url)) {
//                    String[] urlSpiltter = url.split("/");
//                    url = urlSpiltter[0] + "/" + urlSpiltter[1];
                    if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                        for (Role role : user.getRoles()) {

                            if (role.getPermissionList() != null && !role.getPermissionList().isEmpty()) {
                                if (role.getPermissionsNameList().contains(request.getMethod()+"::"+url)) {
                                    Logger4j.getLogger().info("Authorize: " + Boolean.TRUE);
                                    Logger4j.getLogger().info("============================================\n");
                                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                    SecurityContextHolder.getContext().setAuthentication(authentication);
                                    if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                                        Logger4j.getLogger().info("Valid Token");
                                        chain.doFilter(request, response);
                                        break;
                                    }
                                } else {
                                    Logger4j.getLogger().info("Authorize:NOT " + Boolean.FALSE);
                                    Logger4j.getLogger().info("============================================\n");
                                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                                }
                            } else {
                                Logger4j.getLogger().info("Authorize:PERMISSION " + Boolean.FALSE);
                                Logger4j.getLogger().info("============================================\n");
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                            }
                        }
                    } else {

                        Logger4j.getLogger().info("Authorize:ROLE " + Boolean.FALSE);
                        Logger4j.getLogger().info("============================================\n");
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    }
                }else{
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    Logger4j.getLogger().info("Authorize:URL " + Boolean.FALSE);
                    Logger4j.getLogger().info("============================================\n");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                }
            }
//            chain.doFilter(request, response);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());

        }
    }
}
