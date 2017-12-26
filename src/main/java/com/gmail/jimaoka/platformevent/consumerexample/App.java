package com.gmail.jimaoka.platformevent.consumerexample;

import com.gmail.jimaoka.platformevent.consumerexample.client.PlatformEventService;
import com.gmail.jimaoka.platformevent.consumerexample.oauth.OAuthResponse;
import com.gmail.jimaoka.platformevent.consumerexample.oauth.OAuthService;

public class App {
    public static void main( String[] args ){
    	OAuthService oauthService = new OAuthService();
    	try {
    		String jwtBeareToken = oauthService.getJWTBeareToken();
    		System.out.println("jwtBeareToken:" + jwtBeareToken);
    		OAuthResponse oauthResponse = oauthService.getOAuthResponse(jwtBeareToken);
    		System.out.println("access_token:" + oauthResponse.getAccess_token());
    		PlatformEventService pevService = 
    				new PlatformEventService(oauthResponse.getInstance_url(), oauthResponse.getAccess_token());
    		pevService.start();
		} catch (Exception e) {
			System.out.println("exception");
			e.printStackTrace();
		}
    }
}
