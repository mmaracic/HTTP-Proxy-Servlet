package hr.igea.httpproxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.params.HttpParams;
import org.mitre.dsmiley.httpproxy.ProxyServlet;

public class IgeaProxyServlet extends ProxyServlet {

	private Log log = LogFactory.getLog(this.getClass());
	
	// Get the proxy parameters
	private String proxyHost = System.getProperty("http.proxyHost");
	private String proxyPort = System.getProperty("http.proxyPort");
	private String proxyUser = System.getProperty("http.proxyUser");
	private String proxyPassword = System.getProperty("http.proxyPassword");

	@Override
	@SuppressWarnings({"deprecation"})
	protected HttpClient createHttpClient(HttpParams hcParams) {

		HttpClientBuilder hcBuilder = HttpClients.custom();

	    // Set HTTP proxy, if specified in system properties
	    if(proxyHost != null && proxyHost.length() > 0) {
	        int port = 80;
	        if(proxyPort != null && proxyPort.length() > 0) {
	            port = Integer.parseInt(proxyPort);
	        }
	        HttpHost proxy = new HttpHost(proxyHost, port, "http");
	        
	        if (proxyUser != null && proxyUser.length() > 0 && proxyPassword != null && proxyPassword.length() > 0) {
	        	Credentials credentials = new NTCredentials(proxyUser, proxyPassword, "", "");
	        	CredentialsProvider credentialProvider = new BasicCredentialsProvider();
	        	
	        	credentialProvider.setCredentials(new AuthScope(proxyHost, port), credentials);
	        	hcBuilder.setDefaultCredentialsProvider(credentialProvider);
	        }
	        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
	        hcBuilder.setRoutePlanner(routePlanner);
	    }

	    CloseableHttpClient httpClient = hcBuilder.build();

	    return httpClient;
	}
}
