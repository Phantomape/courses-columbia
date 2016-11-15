package org.phantomape;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import org.json.simple.JSONObject;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;

/**
 * Servlet implementation class VelaServlet
 */
public class VelaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	public AmazonSQS sqs;
    public VelaServlet() {
        super();
        // TODO Auto-generated constructor stub
        System.out.println("--Reading credential information...");
        AWSCredentials credentials = null;
        try{
        	//credentials = new PropertiesCredentials(
			//		MyServlet.class.getResourceAsStream("AwsCredentials.properties"));
        	credentials = new ProfileCredentialsProvider().getCredentials();
        	sqs = new AmazonSQSClient(credentials);
        	Region region = Region.getRegion(Regions.US_WEST_2);
        	sqs.setRegion(region);
        } catch (Exception e){
        	e.printStackTrace();
        }
        System.out.println("--Read credential information.");
        
//    	Get twitter info
        twitter4j.conf.ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
    	.setOAuthConsumerKey("uuchxIS2TFhIwAniWsnlq0Pwv")
        .setOAuthConsumerSecret("I9uQ7S2iKRFiFsbW7v1pLc2QETn74QasyVwDZ02jnnXTcsVDYG")
        .setOAuthAccessToken("785668193465401348-FakTCROba7Icmsf87d0MJOA9YyiTnZk")
        .setOAuthAccessTokenSecret("UZpgs53Qd1CNJgQ9OqeCyLbp7b2mgIbpZWrRn1GR68lW1");
		
		String[] keywordsArray = {"pretty", "girl","basketball","Trump","gun","marvel","physics","Chinese"};
		//		Implementing StatusListner
		StatusListener listener = new StatusListener() {
			@Override
	    	public void onStatus(Status status) {
				try{
					//	Get info within english speaking country
					if(status.getGeoLocation() != null && status.getLang().equalsIgnoreCase("en")){
						String createAt = status.getCreatedAt().toString();
						String text = status.getText();
						GeoLocation loc = status.getGeoLocation();
						long idStr = status.getId();
						double latitude = loc.getLatitude();
						double longitude = loc.getLongitude();
						//	Create JSON object
						JSONObject json = new JSONObject();
						json.put("text", text);
						json.put("lat", String.valueOf(latitude));
						json.put("lon", String.valueOf(longitude));
						String index = json.toString();
						System.out.println(index);
						//	Upload tweets to elasticsearch
						if(text != null){
							//String url = "http://search-twitter-1-kf5qeriqw5iu6uasbyv6dmwfbq.us-west-2.es.amazonaws.com/user/profile";
							String url = "http://search-twitter-1-kf5qeriqw5iu6uasbyv6dmwfbq.us-west-2.es.amazonaws.com/user2/profile";
							HttpClient client = (HttpClient) HttpClientBuilder.create().build();
							HttpPost put = new HttpPost(url);
							put.setHeader("Content-type", "application/json");
							StringEntity params =new StringEntity(json.toString());
							put.setEntity(params);
							HttpResponse response = client.execute(put);
							System.out.println("Response Code:"+response.getStatusLine().getStatusCode());
	
							BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
							StringBuffer result = new StringBuffer();
							String line = "";
							while ((line = rd.readLine()) != null) {
								result.append(line);
							}
							System.out.println("result:"+result);
						}
						//	Send tweets to SQS
			            System.out.println("Sending a message to Erinyes.\n");
			            String ErinyesURL = "https://sqs.us-west-2.amazonaws.com/969782898675/Erinyes";
			            sqs.sendMessage(new SendMessageRequest(ErinyesURL, text));
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
	        }
			
			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
	        }
			
			@Override
	        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
	        }
			
			@Override
	        public void onException(Exception ex){
				ex.printStackTrace();
	        }
			
			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
			}
			@Override
			public void onStallWarning(StallWarning warning) {
				System.out.println("Got stall warning:" + warning);
			}
		};
		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		twitterStream.addListener(listener);
		FilterQuery filter = new FilterQuery();
	    filter.track(keywordsArray);
	    twitterStream.filter(filter);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String title = "Getting form data";
		String keyword = request.getParameter("keyword");
		String docType = "<!DOCTYPE html> \n";
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		out.println(docType +
			    "<html>\n" +
			    "<head><title>" + title + "</title></head>\n" +
			    "<body bgcolor=\"#f0f0f0\">\n" +
			    "<h1 align=\"center\">" + title + "</h1>\n" +
			    "<ul>\n" +
			    "  <li><b>Keyword</b>£º"
			    + keyword + "\n" +
			    "</ul>\n" +
			    "</body></html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
