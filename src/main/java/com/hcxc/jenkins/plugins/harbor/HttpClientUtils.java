/*
 * Copyright 2007-2022 Home Credit Xinchi Consulting Co. Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2. *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hcxc.jenkins.plugins.harbor;

import java.io.IOException;
import java.net.SocketTimeoutException;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpClientUtils {

    public static Logger logger = Logger.getLogger(HttpClientUtils.class);

    public static int socketTimeOut = 20000;

    public static int connectionTimeOut = 20000;

    protected static String getResultByHttpGet(String requestUrl, String user, String password) {
        CloseableHttpClient httpclient = getNoopSSLClient(user, password);
        try {
            HttpGet httpGet = new HttpGet(requestUrl);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeOut)
                    .setConnectTimeout(connectionTimeOut).build();
            httpGet.setConfig(requestConfig);
            HttpResponse res = httpclient.execute(httpGet);
            HttpEntity entity = res.getEntity();
            return EntityUtils.toString(entity);
        } catch (ConnectTimeoutException e) {
            logger.error(" Network Connect Time Out ", e);
            return "";
        } catch (SocketTimeoutException e) {
            logger.error(" Network Socket Time Out ", e);
            return "";
        } catch (Exception e) {
            logger.error(" GetResult error: ", e);
            return "";
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                logger.error("Close CloseableHttpClient Error", e);
            }
        }
    }

    /**
     *
     * @param user
     * @param password
     * @return
     */
    private static CloseableHttpClient getNoopSSLClient(String user, String password) {
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();

        AuthScope authScope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT);
        UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(
                user, password);
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(authScope,
                usernamePasswordCredentials);
        clientBuilder.setDefaultCredentialsProvider(credentialsProvider);

        clientBuilder.setSSLSocketFactory(getNoopSSLConnectionSocketFactory());

        return clientBuilder.build();
    }

    private static SSLConnectionSocketFactory getNoopSSLConnectionSocketFactory() {
        SSLContext sslContext;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null,
//					new TrustStrategy() {
//						public boolean isTrusted(X509Certificate[] chain,
//								String authType) throws CertificateException {
//							return true;
//						}
//					}).build();
                    (chain, authType) -> true).build();

            // trust all hosts
            return new SSLConnectionSocketFactory(
                    sslContext, NoopHostnameVerifier.INSTANCE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
