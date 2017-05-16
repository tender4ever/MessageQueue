package test;

import ibmmq.*;
import MQInterface.*;

public class IBMMQtest implements MQEventListener{

	//實作MQEventListener onMessage
	public void onMessage(String txtMessage){
		
		System.out.println(txtMessage);
	}
	
	//實作MQEventListener 
	public void systemMessage(String status){
		
		System.out.println(status);
	}
	
	//Receiver
	public void receiveAndCheckConnect(){
		
		IBMMQreceiver a = new IBMMQreceiver(this);
	}
	
	//Sender
	public void sender(String textMessage){
		
		IBMMQsender b = new IBMMQsender();
		b.connect();
		b.send(textMessage);
	}
}
