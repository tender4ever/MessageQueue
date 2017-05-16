package openmq;

import com.sun.messaging.ConnectionFactory;

import MQInterface.MQEventListener;
import MQInterface.MQReceiver;

import com.sun.messaging.ConnectionConfiguration;

import java.util.Enumeration;

import javax.jms.*;

public class OpenMQreceiver extends Thread implements MQReceiver{

	private String hostName = "127.0.0.1";			//hostName
	private int port = 7676; 						//port
	private String queueName = "Queue";				//Queue Name
	private String connectionState = "NotReady";	//�s�u���A
	private ConnectionFactory cf = null;			//JMS ConnectionFactory
	private Connection connection = null;			//JMS Connection
	private Session session = null;					//JMS Session
	private Destination destination = null;			//JMS Destination
	private Queue queue = null;						//JMS Queue
	private MessageConsumer receiver = null;		//JMS Sender
	private TextMessage message = null;				//JMS Message
	private MQEventListener listener;				//Event Listener
	
	//�غc�l
	public OpenMQreceiver(MQEventListener eventListener,String hostName,int port,String queueName){
		
		listener = eventListener;						//Event Listener
		this.hostName = hostName;						//�]�whostName
		this.port = port;								//�]�wport
		this.queueName = queueName;						//�]�wqueueName
		
		connect();										//�s�u
		super.start();									//�}�lreceive��Thread 
		checkConnect.start();							//�}�lcheck Connect��Thread
	}
	//�غc�l
	public OpenMQreceiver(MQEventListener eventListener,String hostName,String queueName){
		
		listener = eventListener;						//Event Listener
		this.hostName = hostName;						//�]�whostName
		this.queueName = queueName;						//�]�wqueueName
		
		connect();										//�s�u
		super.start();									//�}�lreceive��Thread 
		checkConnect.start();							//�}�lcheck Connect��Thread
	}
	//�غc�l
	public OpenMQreceiver(MQEventListener eventListener,String hostName,int port){
		
		listener = eventListener;						//Event Listener
		this.hostName = hostName;						//�]�whostName
		this.port = port;								//�]�wport
		
		connect();										//�s�u
		super.start();									//�}�lreceive��Thread 
		checkConnect.start();							//�}�lcheck Connect��Thread
	}
	//�غc�l
	public OpenMQreceiver(MQEventListener eventListener){
		listener = eventListener;
		connect();										//�s�u
		super.start();									//�}�lreceive��Thread 
		checkConnect.start();							//�}�lcheck Connect��Thread
	}
		
	//�s�u
	public void connect(){
			
		try {
			cf.setProperty(ConnectionConfiguration.imqAddressList,hostName + ":" + String.valueOf(port));
			cf.setProperty(ConnectionConfiguration.imqReconnectEnabled, "true");
				
			connection = cf.createConnection("admin","admin");
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				
			destination = new com.sun.messaging.Queue(queueName);
				
			queue = session.createQueue(queueName);
				
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
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		catch(Exception ex2){
			ex2.printStackTrace();
		}
	}
		
	public void run(){
		while(true)	{
			try{
				if (connectionState == "Ready"){
				
					receiver = session.createConsumer(destination);
				
					QueueBrowser browser = session.createBrowser((Queue)destination);
				
					Enumeration aEnumeration = browser.getEnumeration();
					
					while(aEnumeration.hasMoreElements()){
						aEnumeration.nextElement();
						receiver.receive();
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
