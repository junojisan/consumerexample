package com.gmail.jimaoka.platformevent.consumerexample.oauth;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.MessageFormat;
import java.util.Base64;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OAuthService {
	private static final String END_POINT = "https://login.salesforce.com/services/oauth2/token";
	private static final String AUDIENCE = "https://login.salesforce.com";
	private static final String CLIENT_ID = "{YOUR_CLIENT_ID}";
	private static final String GRANT_TYPE = "urn:ietf:params:oauth:grant-type:jwt-bearer";

	public String getJWTBeareToken() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException, InvalidKeyException, SignatureException{
		String header = "{\"alg\":\"RS256\"}";
	    String claimTemplate = "'{'\"iss\": \"{0}\", \"sub\": \"{1}\", \"aud\": \"{2}\", \"exp\": \"{3}\"'}'";

	    StringBuffer token = new StringBuffer();
	    token.append(Base64.getUrlEncoder().encodeToString(header.getBytes("UTF-8")));
	    token.append(".");

	    String[] claimArray = new String[4];
	    claimArray[0] = CLIENT_ID;
	    claimArray[1] = "{YOUR_USER_ID}";
	    claimArray[2] = AUDIENCE;
	    claimArray[3] = Long.toString((System.currentTimeMillis() / 1000) + 300);

	    MessageFormat claims = new MessageFormat(claimTemplate);
	    String payload = claims.format(claimArray);
	    token.append(Base64.getUrlEncoder().encodeToString(payload.getBytes("UTF-8")));

	    KeyStore keystore = KeyStore.getInstance("JKS");
	    keystore.load(new FileInputStream("./keystore/keystore.jks"), "storepassword".toCharArray());
	    PrivateKey privateKey = (PrivateKey)keystore.getKey("keystore", "keypassword".toCharArray());

	    Signature signature = Signature.getInstance("SHA256withRSA");
	    signature.initSign(privateKey);
	    signature.update(token.toString().getBytes("UTF-8"));
	    String signedPayload = Base64.getUrlEncoder().encodeToString(signature.sign());

	    token.append(".");
	    token.append(signedPayload);
	    return token.toString();
	}

	public OAuthResponse getOAuthResponse(String jwtBeareToken) throws Exception {
		SslContextFactory sslContextFactory = new SslContextFactory();
		HttpClient httpClient = new HttpClient(sslContextFactory);
		httpClient.start();
		ContentResponse response = httpClient.POST(END_POINT)
				.param("grant_type", GRANT_TYPE)
				.param("assertion", jwtBeareToken)
				.send();
		httpClient.stop();

		ObjectMapper objectMapper = new ObjectMapper();
		OAuthResponse oauthResponse =
				objectMapper.readValue(response.getContentAsString(), OAuthResponse.class);
		return oauthResponse;
	}

	public OAuthResponse getOAuthResponse() throws  Exception {
		return getOAuthResponse(getJWTBeareToken());
	}
}
