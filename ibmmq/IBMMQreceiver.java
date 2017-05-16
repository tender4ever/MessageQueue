package ibmmq;

import javax.jms.*;
import com.ibm.mq.*;
import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueueConnectionFactory;

import MQInterface.*;

public class IBMMQreceiver extends Thread implements MQReceiver{

	private String hostName = "localhost";				//hostName
	private String queueManager = "mq_app";				//佇列管理程式
	private String channelName = "mychannel";			//Channel Name
	private int port = 1414;							//port
	private int ccsid = 1381;							//ccsid
	private String queueName = "myqueue";				//Queue Name
	public static String connectionState = "NotReady"; 	//連線狀態
	private MQQueueConnectionFactory cf = null;			//JMS ConnectionFactory
	private QueueConnection connection = null;			//JMS Connection
	private QueueSession session = null;				//JMS Session
	private Queue queue = null;							//JMS Queue
	private QueueReceiver receiver = null;				//JMS Receiver
	private MQEventListener listener;					//Event Listener
	
	//建構子
	public IBMMQreceiver(MQEventListener eventListener,String InputHostName,String InputQueueManager,
			String InputChannelName,int InputPort,int InputCcsid,String InputQueueName)
	{
		listener = eventListener;						//Event Listener
		this.hostName = InputHostName;					//設定hostName
		this.queueManager = InputQueueManager;			//設定queueManager
		this.channelName =  InputChannelName;			//設定ChannelName
		this.port = InputPort;							//設定port
		this.ccsid = InputCcsid;						//設定ccsid
		this.queueName = InputQueueName;				//設定queueName
		
		connect();										//連線
		super.start();									//開始receive的Thread 
		checkConnect.start();							//開始check Connect的Thread
	}
	//建構子
	public IBMMQreceiver(MQEventListener eventListener,String InputHostName,String InputChannelName,
			int InputPort,int InputCcsid,String InputQueueName)
	{
		listener = eventListener;						//Event Listener
		this.hostName = InputHostName;					//設定hostName
		this.channelName =  InputChannelName;			//設定ChannelName
		this.port = InputPort;							//設定port
		this.ccsid = InputCcsid;						//設定ccsid
		this.queueName = InputQueueName;				//設定queueName
		
		connect();										//連線
		super.start();									//開始receive的Thread 
		checkConnect.start();							//開始check Connect的Thread
	}
	//建構子
	public IBMMQreceiver(MQEventListener eventListener,String InputHostName,String InputChannelName,
			int InputPort,String InputQueueName)
	{
		listener = eventListener;						//Event Listener
		this.hostName = InputHostName;					//設定hostName
		this.channelName =  InputChannelName;			//設定ChannelName
		this.port = InputPort;							//設定port
		this.queueName = InputQueueName;				//設定queueName
			
		connect();										//連線
		super.start();									//開始receive的Thread 
		checkConnect.start();							//開始check Connect的Thread
	}
	//建構子
	public IBMMQreceiver(MQEventListener eventListener,String InputHostName,int InputPort,
			String InputQueueName)
	{
		listener = eventListener;						//Event Listener
		this.hostName = InputHostName;					//設定hostName
		this.port = InputPort;							//設定port
		this.queueName = InputQueueName;				//設定queueName
			
		connect();										//連線
		super.start();									//開始receive的Thread 
		checkConnect.start();							//開始check Connect的Thread
	}
	//建構子
	public IBMMQreceiver(MQEventListener eventListener,String InputHostName,String InputQueueName)
	{
		listener = eventListener;						//Event Listener
		this.hostName = InputHostName;					//設定hostName
		this.queueName = InputQueueName;				//設定queueName
		connect();										//連線
		super.start();									//開始receive的Thread 
		checkConnect.start();							//開始check Connect的Thread
	}
	//建構子
	public IBMMQreceiver(MQEventListener eventListener,String InputHostName,int InputPort)
	{
		listener = eventListener;						//Event Listener
		this.hostName = InputHostName;					//設定hostName
		this.port = InputPort;							//設定port
			
		connect();										//連線
		super.start();									//開始receive的Thread 
		checkConnect.start();							//開始check Connect的Thread
	}
	//建構子
	public IBMMQreceiver(MQEventListener eventListener,String InputHostName)
	{
		listener = eventListener;						//Event Listener
		this.hostName = InputHostName;					//設定hostName
				
		connect();										//連線
		super.start();									//開始receive的Thread 
		checkConnect.start();							//開始check Connect的Thread
	}
	//建構子
	public IBMMQreceiver(MQEventListener eventListener)
	{
		listener = eventListener;						//Event Listener
				
		connect();										//連線
		super.start();									//開始receive的Thread 
		checkConnect.start();							//開始check Connect的Thread
	}
	
	//連線
	public void connect(){
		try {
			cf = new MQQueueConnectionFactory();					//設定ConnectionFactory
			cf.setHostName(hostName);
			cf.setQueueManager(queueManager);
			cf.setChannel(channelName);
			cf.setPort(port);
			cf.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
			cf.setCCSID(ccsid);
				
			connection = cf.createQueueConnection();				//設定Connection
			connection.start();
				
			session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);//設定Session
				
			queue = session.createQueue(queueName);					//設定Queue
				
			connectionState = "Ready";
			
			listener.systemMessage("receiver connect ok");
		} 
		catch (JMSException e) {
			e.printStackTrace();
			disconnect();//斷線
		}
	}
	//斷線
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

