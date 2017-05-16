package test;

import openmq.*;
import MQInterface.*;

public class OpenMQtest implements MQEventListener{

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
		
		OpenMQreceiver a = new OpenMQreceiver(this);
	}
	
	//Sender
	public void sender(String textMessage){
		
		OpenMQsender b = new OpenMQsender();
		b.connect();
		b.send(textMessage);
	}
}
