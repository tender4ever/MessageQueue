package ibmmq;

import MQInterface.*;

public class InterfaceQueueHandler extends Thread implements MQEventListener{

	private MQReceiver receiver;
	
	public InterfaceQueueHandler(String hostName,String channelName,int port,String queueName){
		
		receiver = new IBMMQreceiver(this,hostName,channelName,port,queueName);
		
		super.start();
	}
	
	public InterfaceQueueHandler(){
		
		receiver = new IBMMQreceiver(this);
		
		super.start();
	}
	
	public void onMessage(String txtMessage){
		
		System.out.println(txtMessage + "¤w¦¬¨ì");
		
	}
	
	public void onConnect(String status){
		
		System.out.println(status);
	}
	
	public void run(){
		
		while(true){
			
			if(receiver.getConnectionStatus()=="NotReady"){
				receiver.connect();
			}
		}
	}

}
