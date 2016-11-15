package org.phantomape;

import java.util.List;
import java.util.Map.Entry;

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

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AmazonSQS sqs = null;
		
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
        String ErinyesURL = "https://sqs.us-west-2.amazonaws.com/969782898675/Test";
        //String ErinyesURL = "https://sqs.us-west-2.amazonaws.com/969782898675/Erinyes";
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(ErinyesURL);
        int count = 0;
        while(count++ < 1000){
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        if(messages.size() == 0)
        	break;
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
        }
        System.out.println();
        // Delete a message
        System.out.println("Deleting a message.\n");
        String messageReceiptHandle = messages.get(0).getReceiptHandle();
        }
	}

}
