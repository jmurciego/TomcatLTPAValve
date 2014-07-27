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

package org.tomcatltpa.utils;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.StringTokenizer;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 * Utility class for the processing the LTPA Tokens, based on the information
 * provided on the official pages and on hundreds of examples found on the
 * internet
 * <p>
 * @author Javier Murciego
 */

public class LTPAUtils {
    
    
    private static final String DES_DECRIPTING_ALGORITHM = "DESede/ECB/PKCS5Padding";
    private static final String AES_DECRIPTING_ALGORITHM_ZERO_PADDING = "AES/CBC/NoPadding";
    
    private byte[] secretKey = null;
    
    public LTPAUtils(String publicKey,String password) throws GeneralSecurityException {
        this.secretKey = getSecretKey(publicKey, password);
    }

    private byte[] getSecretKey(String ltpa3DESKey, String ltpaPassword) throws GeneralSecurityException {
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(ltpaPassword.getBytes());
        byte[] hash3DES = new byte[24];
        System.arraycopy(md.digest(), 0, hash3DES, 0, 20);
        Arrays.fill(hash3DES, 20, 24, (byte) 0);
        Cipher cipher = Cipher.getInstance(DES_DECRIPTING_ALGORITHM);
        KeySpec keySpec = new DESedeKeySpec(hash3DES);
        Key secretKey2 = SecretKeyFactory.getInstance("DESede").generateSecret(keySpec);
        cipher.init(Cipher.DECRYPT_MODE, secretKey2);
        byte[] secret = cipher.doFinal(Base64.decodeBase64(ltpa3DESKey.getBytes()));
        return secret;
    }

    public String decrypt(String ltpaToken) throws GeneralSecurityException {
        byte[] tokenBytes = Base64.decodeBase64(ltpaToken.getBytes());
        SecretKey sKey = new SecretKeySpec(secretKey, 0, 16, "AES");
        Cipher cipher = Cipher.getInstance(AES_DECRIPTING_ALGORITHM_ZERO_PADDING);
        IvParameterSpec ivs16 = new IvParameterSpec(Arrays.copyOf(secretKey, 16));
        cipher.init(Cipher.DECRYPT_MODE, sKey, ivs16);
        return new String(cipher.doFinal(tokenBytes));
    }
    
    public boolean verifySignature(String signature){
        throw new UnsupportedOperationException("Not supported yet.");
    }


    private String extractUserName(String token,String dnPrefix){
        String dn = null;
        String userName = null;
        StringTokenizer st = new StringTokenizer(token, "%");
        String userInfo = st.nextToken();
        String expires = st.nextToken();
        Date d = new Date(Long.parseLong(expires));
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss z");
        int beginIndex = userInfo.indexOf(dnPrefix+"=");
        if( beginIndex !=-1 ){
            dn = userInfo.substring(beginIndex);
            userName = dn.substring( (dnPrefix+"=").length(),dn.indexOf(","));
        }else{
            throw new LTPATokenException("Invalid ltpaToken: dn with prefix " + dnPrefix +" not found. LTPA token value is " + userInfo);
        }
        return userName;
        
    }
    
    
}
