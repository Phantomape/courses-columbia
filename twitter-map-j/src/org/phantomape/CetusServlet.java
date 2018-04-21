package org.phantomape;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

/**
 * Servlet implementation class CetusServlet
 */
public class CetusServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	public AmazonSQS sqs;
    public CetusServlet() throws ClientProtocolException, IOException {
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
        
        System.out.println("Receiving a message from Erinyes.\n");
        String ErinyesURL = "https://sqs.us-west-2.amazonaws.com/969782898675/Erinyes";
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(ErinyesURL);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        for (Message message : messages) {
            System.out.println("  Message");
            System.out.println("    MessageId:     " + message.getMessageId());
            System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
            System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
            System.out.println("    Body:          " + message.getBody());
            for (Entry<String, String> entry : message.getAttributes().entrySet()) {
                System.out.println("  Attribute");
                System.out.println("    Name:  " + entry.getKey());
                System.out.println("    Value: " + entry.getValue());
            }
            String text = message.getBody();
            String id = message.getMessageId();
            JSONObject json = new JSONObject();
			json.put("language", "en");
			json.put("id", String.valueOf(id));
			json.put("text", text);
			String orderJSONString = parseJSON(json);
			getSentiment(orderJSONString);
        }
        System.out.println();
        // Delete a message
        System.out.println("Deleting a message.\n");
        String messageReceiptHandle = messages.get(0).getReceiptHandle();
        //sqs.deleteMessage(new DeleteMessageRequest(ErinyesURL, messageReceiptHandle));
        
        
	
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	private static String parseJSON(JSONObject obj)
	{
		String head = "{\"documents\":[";
		String tail = "]}";
		String body = obj.toJSONString().substring(1, obj.toJSONString().length() - 1 );
		int idx_id = body.indexOf("\"id\"");
		int idx_text = body.indexOf("\"text\"");
		int idx_lang = body.indexOf("\"language\"");
		String id = body.substring(idx_id, idx_text - 1);
		String text = body.substring(idx_text, idx_lang - 1);
		String lang = body.substring(idx_lang);
		body = "{" + lang + "," + id + "," + text + "}";
		System.out.println(head + body + tail);
		return head + body + tail;
	}
	
	private static void getSentiment(String JSONString) throws ClientProtocolException, IOException
	{
		if(JSONString == null)
			return;
		String url = "https://westus.api.cognitive.microsoft.com/text/analytics/v2.0/sentiment";
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost httppost = new HttpPost(url);
		//	Set header
		httppost.setHeader("Content-type", "application/json");
		httppost.setHeader("Ocp-Apim-Subscription-Key", "511759357b824ccc8b8336b6ebba9ad7");
		httppost.setHeader("Accept", "application/json");
		
		StringEntity params =new StringEntity(JSONString);
		httppost.setEntity(params);
		HttpResponse response = client.execute(httppost);
		System.out.println("Response Code:"+response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null)
			result.append(line);
		int start = result.indexOf("score");
		int end = result.indexOf("id");
		String score = result.substring(start + 7, end - 2);
		System.out.println(score);
		
		
		System.out.println("result:"+result);
	}
	
}
