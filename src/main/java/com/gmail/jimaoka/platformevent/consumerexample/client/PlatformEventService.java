package com.gmail.jimaoka.platformevent.consumerexample.client;

import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.LongPollingTransport;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class PlatformEventService {
	private static final String COMETD_ENDPOINT = "/cometd/41.0";
	private static final String CHANNEL = "/event/DemoOrderEvent__e";

	private String instanceUrl;
	private String accessToken;
	private HttpClient httpClient;
	private BayeuxClient client;
	
	public PlatformEventService(String instanceUrl, String accessToken){
		this.instanceUrl = instanceUrl;
		this.accessToken = accessToken;
	}
	
	public void start() throws Exception{
		SslContextFactory sslContextFactory = new SslContextFactory();
		httpClient = new HttpClient(sslContextFactory);
		httpClient.start();
		
		LongPollingTransport httpTransport = new LongPollingTransport(null, httpClient){
			@Override
			protected void customize(Request request) {
				request.header("Authorization", "Beare " + accessToken);
			}
		};
		
		client = new BayeuxClient(instanceUrl + COMETD_ENDPOINT, httpTransport);
		client.handshake((channel, message) -> {
			if(message.isSuccessful()){
				System.out.println(message.toString());
			}
		});

		client.getChannel(CHANNEL).subscribe((channel, message) -> {
			System.out.println(message.toString());
		});
	}
	
	public void stop() throws Exception{
		if(client != null){
			client.disconnect();
		}
		if(httpClient != null){
			httpClient.stop();
		}
	}
	
}
