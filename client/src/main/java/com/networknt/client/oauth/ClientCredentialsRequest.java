/*
 * Copyright (c) 2016 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.networknt.client.oauth;

import com.networknt.client.Http2Client;
import com.networknt.common.DecryptUtil;
import com.networknt.common.SecretConstants;
import com.networknt.config.Config;

import java.util.List;
import java.util.Map;

/**
 * load default values from client.yml for client credentials grant, overwrite by setters
 * in case you want to change it at runtime.
 *
 * Note that client_secret is loaded from secret.yml instead of client.yml and the assumption
 * is that there is only one client shared by both authorization code grant and client credentials
 * grant.
 *
 * @author Steve Hu
 */
public class ClientCredentialsRequest extends TokenRequest {
    static Map<String, Object> secret = DecryptUtil.decryptMap((Map<String, Object>)Config.getInstance().getJsonMapConfig(Http2Client.CONFIG_SECRET));


    public ClientCredentialsRequest() {
        setGrantType(CLIENT_CREDENTIALS);
        Map<String, Object> clientConfig = Config.getInstance().getJsonMapConfig(Http2Client.CONFIG_NAME);
        // client_secret is in secret.yml instead of client.yml
        if(clientConfig != null) {
            Map<String, Object> oauthConfig = (Map<String, Object>)clientConfig.get(OAUTH);
            if(oauthConfig != null) {
                Map<String, Object> tokenConfig = (Map<String, Object>)oauthConfig.get(TOKEN);
                if(tokenConfig != null) {
                    setServerUrl((String)tokenConfig.get(SERVER_URL));
                    Object object = tokenConfig.get(ENABLE_HTTP2);
                    setEnableHttp2(object != null && (Boolean) object);
                    Map<String, Object> ccConfig = (Map<String, Object>) tokenConfig.get(CLIENT_CREDENTIALS);
                    if(ccConfig != null) {
                        setClientId((String)ccConfig.get(CLIENT_ID));
                        setClientSecret((String)secret.get(SecretConstants.CLIENT_CREDENTIALS_CLIENT_SECRET));
                        setUri((String)ccConfig.get(URI));
                        setScope((List<String>)ccConfig.get(SCOPE));
                    }
                }
            }
        }
    }
}
