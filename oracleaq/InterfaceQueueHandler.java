package oracleaq;

import MQInterface.*;

public class InterfaceQueueHandler extends Thread implements MQEventListener{

	private MQReceiver receiver;
	
	//建構子
	public InterfaceQueueHandler(String hostName,String oracleSid,int port,String driver,
								String dbAccount,String dbPwd,String queueTable,String queueName){
			
		receiver = new OracleAQreceiver(this,hostName,oracleSid,port,driver,dbAccount,dbPwd,queueTable,queueName);
			
		super.start();
	}
	
	//建構子
	public InterfaceQueueHandler(){
		
		receiver = new OracleAQreceiver(this);
		
		super.start();
	}
	
	public void onMessage(String txtMessage){
		
		System.out.println(txtMessage + "已收到");
		
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
