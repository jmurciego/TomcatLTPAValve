/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.tomcatltpa.authenticator;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.tomcatltpa.utils.LTPAUtils;

/**
 * Tomcat valve that intercepts all requests.
 * In case the request is not authenticated and LtpaToken2 cookie exists, the valve
 * uses the cookie to authenticate the user
 * <p>
 * @author Javier Murciego
 */

public class LTPASSOAuthenticatorValve extends ValveBase {

    private static final Log log = LogFactory.getLog(LTPASSOAuthenticatorValve.class);
    
    List<String> roles = new ArrayList<>();
    String ltpa3DESKey = null;
    String keyPassword = null;
    String dnPrefix = "uid";
    
    LTPAUtils ltpaUtils = null;
    
    public LTPASSOAuthenticatorValve() {
        super(true);
    }
    
    @Override
    public String getInfo() {
        return "org.tomcatltpa.authenticator.LTPASSOAuthenticatorValve/1.0";
    }
    
    public void setRoleList(String roleList){
        String [] rolesAsArray=roleList.split(",");
        for(int i=0;rolesAsArray!=null && i < rolesAsArray.length;i++){
            roles.add(rolesAsArray[i]);
        }
    }
    
    public void setLtpa3DESKey(String ltpa3DESKey){
        this.ltpa3DESKey=ltpa3DESKey;
    }
    
    public void setKeyPassword(String keyPassword){
        this.keyPassword=keyPassword;
    }
    
    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            Cookie[] cookies = request.getCookies();
            Cookie ltpa2Cookie = null;
            for(int i=0;cookies!=null && i<cookies.length;i++){
                if (cookies[i].getName().equals("LtpaToken2")){
                    ltpa2Cookie = cookies[i];
                    break;
                }
            }
            if(ltpa2Cookie == null){
                log.info("LtpaToken2 cookie not found");
            }else{
                try {
                    String tokenDecrypted = ltpaUtils.decrypt(ltpa2Cookie.getValue());
                    String userName = ltpaUtils.extractUserName(tokenDecrypted, dnPrefix);
                    Principal p = new GenericPrincipal(userName,null,roles);
                    request.setAuthType("LTPA");
                    request.setUserPrincipal(p);  
                } catch (GeneralSecurityException ex) {
                    log.error("Error obtaining username from ltpatoken2", ex);
                }
                
                
            }
            
        }else{
            log.info("User already authenticated " + principal);
        }
        getNext().invoke(request, response);
    }
    
    @Override
    protected synchronized void startInternal() throws LifecycleException {
        try {
            ltpaUtils= new LTPAUtils(ltpa3DESKey, keyPassword);
        } catch (GeneralSecurityException ex) {
            throw new LifecycleException(ex);
        }
        super.startInternal();
    }
    
}
