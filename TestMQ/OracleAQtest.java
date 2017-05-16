package test;

import oracleaq.*;
import MQInterface.*;

public class OracleAQtest implements MQEventListener{

	//��@MQEventListener onMessage
	public void onMessage(String txtMessage){
		
		System.out.println(txtMessage);
	}
	
	//��@MQEventListener 
	public void systemMessage(String status){
		
		System.out.println(status);
	}
	
	//Receiver
	public void receiveAndCheckConnect(){
		
		OracleAQreceiver a = new OracleAQreceiver(this);
	}
	
	//Sender
	public void sender(String textMessage){
		
		OracleAQsender b = new OracleAQsender();
		b.connect();
		b.send(textMessage);
	}
}
