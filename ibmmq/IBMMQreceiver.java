package ibmmq;

import javax.jms.*;
import com.ibm.mq.*;
import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueueConnectionFactory;

import MQInterface.*;

public class IBMMQreceiver extends Thread implements MQReceiver{

	private String hostName = "localhost";				//hostName
	private String queueManager = "mq_app";				//��C�޲z�{��
	private String channelName = "mychannel";			//Channel Name
	private int port = 1414;							//port
	private int ccsid = 1381;							//ccsid
	private String queueName = "myqueue";				//Queue Name
	public static String connectionState = "NotReady"; 	//�s�u���A
	private MQQueueConnectionFactory cf = null;			//JMS ConnectionFactory
	private QueueConnection connection = null;			//JMS Connection
	private QueueSession session = null;				//JMS Session
	private Queue queue = null;							//JMS Queue
	private QueueReceiver receiver = null;				//JMS Receiver
	private MQEventListener listener;					//Event Listener
	
	//�غc�l
	public IBMMQreceiver(MQEventListener eventListener,String InputHostName,String InputQueueManager,
			String InputChannelName,int InputPort,int InputCcsid,String InputQueueName)
	{
		listener = eventListener;						//Event Listener
		this.hostName = InputHostName;					//�]�whostName
		this.queueManager = InputQueueManager;			//�]�wqueueManager
		this.channelName =  InputChannelName;			//�]�wChannelName
		this.port = InputPort;							//�]�wport
		this.ccsid = InputCcsid;						//�]�wccsid
		this.queueName = InputQueueName;				//�]�wqueueName
		
		connect();										//�s�u
		super.start();									//�}�lreceive��Thread 
		checkConnect.start();							//�}�lcheck Connect��Thread
	}
	//�غc�l
	public IBMMQreceiver(MQEventListener eventListener,String InputHostName,String InputChannelName,
			int InputPort,int InputCcsid,String InputQueueName)
	{
		listener = eventListener;						//Event Listener
		this.hostName = InputHostName;					//�]�whostName
		this.channelName =  InputChannelName;			//�]�wChannelName
		this.port = InputPort;							//�]�wport
		this.ccsid = InputCcsid;						//�]�wccsid
		this.queueName = InputQueueName;				//�]�wqueueName
		
		connect();										//�s�u
		super.start();									//�}�lreceive��Thread 
		checkConnect.start();							//�}�lcheck Connect��Thread
	}
	//�غc�l
	public IBMMQreceiver(MQEventListener eventListener,String InputHostName,String InputChannelName,
			int InputPort,String InputQueueName)
	{
		listener = eventListener;						//Event Listener
		this.hostName = InputHostName;					//�]�whostName
		this.channelName =  InputChannelName;			//�]�wChannelName
		this.port = InputPort;							//�]�wport
		this.queueName = InputQueueName;				//�]�wqueueName
			
		connect();										//�s�u
		super.start();									//�}�lreceive��Thread 
		checkConnect.start();							//�}�lcheck Connect��Thread
	}
	//�غc�l
	public IBMMQreceiver(MQEventListener eventListener,String InputHostName,int InputPort,
			String InputQueueName)
	{
		listener = eventListener;						//Event Listener
		this.hostName = InputHostName;					//�]�whostName
		this.port = InputPort;							//�]�wport
		this.queueName = InputQueueName;				//�]�wqueueName
			
		connect();										//�s�u
		super.start();									//�}�lreceive��Thread 
		checkConnect.start();							//�}�lcheck Connect��Thread
	}
	//�غc�l
	public IBMMQreceiver(MQEventListener eventListener,String InputHostName,String InputQueueName)
	{
		listener = eventListener;						//Event Listener
		this.hostName = InputHostName;					//�]�whostName
		this.queueName = InputQueueName;				//�]�wqueueName
		connect();										//�s�u
		super.start();									//�}�lreceive��Thread 
		checkConnect.start();							//�}�lcheck Connect��Thread
	}
	//�غc�l
	public IBMMQreceiver(MQEventListener eventListener,String InputHostName,int InputPort)
	{
		listener = eventListener;						//Event Listener
		this.hostName = InputHostName;					//�]�whostName
		this.port = InputPort;							//�]�wport
			
		connect();										//�s�u
		super.start();									//�}�lreceive��Thread 
		checkConnect.start();							//�}�lcheck Connect��Thread
	}
	//�غc�l
	public IBMMQreceiver(MQEventListener eventListener,String InputHostName)
	{
		listener = eventListener;						//Event Listener
		this.hostName = InputHostName;					//�]�whostName
				
		connect();										//�s�u
		super.start();									//�}�lreceive��Thread 
		checkConnect.start();							//�}�lcheck Connect��Thread
	}
	//�غc�l
	public IBMMQreceiver(MQEventListener eventListener)
	{
		listener = eventListener;						//Event Listener
				
		connect();										//�s�u
		super.start();									//�}�lreceive��Thread 
		checkConnect.start();							//�}�lcheck Connect��Thread
	}
	
	//�s�u
	public void connect(){
		try {
			cf = new MQQueueConnectionFactory();					//�]�wConnectionFactory
			cf.setHostName(hostName);
			cf.setQueueManager(queueManager);
			cf.setChannel(channelName);
			cf.setPort(port);
			cf.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
			cf.setCCSID(ccsid);
				
			connection = cf.createQueueConnection();				//�]�wConnection
			connection.start();
				
			session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);//�]�wSession
				
			queue = session.createQueue(queueName);					//�]�wQueue
				
			connectionState = "Ready";
			
			listener.systemMessage("receiver connect ok");
		} 
		catch (JMSException e) {
			e.printStackTrace();
			disconnect();//�_�u
		}
	}
	//�_�u
	public synchronized void disconnect(){
		try{
			connectionState = "NotReady";
					
			try{
				if (receiver != null){
					receiver.close();
					receiver = null;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
				
			try {
				if (session != null){
					session.close();
					session = null;
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
				
			try {
				if ( connection != null ){
					connection.close();
					connection = null;
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		catch(Exception ex2){
			ex2.printStackTrace();
		}
	}
	
	public void run(){
		while(true){
			try{
				if (connectionState == "Ready"){
				
					receiver = session.createReceiver(queue);
					Message message = receiver.receive();
					String txt = message.toString();
					
					if(listener != null){
						
						try{
							listener.onMessage(txt);
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}
				}
				else{
					disconnect();	
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
				disconnect();

			}
		}	
	}
	Thread checkConnect = new Thread(new Runnable(){
		public void run(){
			while (true)
	        {
	            if (connectionState == "NotReady")
	            {
	                connect();
	            }
	        }
			
		}	
	});
}

