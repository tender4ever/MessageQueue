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
		
	private String connectionState = "NotReady";	//連線狀態
		
	private ConnectionFactory cf = null;			//JMS ConnectionFactory
		
	private Connection connection = null;			//JMS Connection
		
	private Session session = null;					//JMS Session
		
	private Destination destination = null;			//JMS Destination
		
	private Queue queue = null;						//JMS Queue
		
	private MessageConsumer receiver = null;		//JMS Sender
		
	private TextMessage message = null;				//JMS Message
	
	private MQEventListener listener;
	
	//建構子
	public OpenMQreceiver(MQEventListener eventListener,String hostName,int port,String queueName){
		
		listener = eventListener;
		this.hostName = hostName;
		this.port = port;
		this.queueName = queueName;
		
		connect();
		super.start();
	}
	//建構子
	public OpenMQreceiver(MQEventListener eventListener){
		listener = eventListener;
		connect();
		super.start();
	}
		
	//連線
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
		} 
		catch (JMSException e) {
			e.printStackTrace();
			disconnect();
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
	public String getConnectionStatus(){
		return connectionState;
	}
	public void setConnectionStatus(String connectionState){
		this.connectionState = connectionState;
	}
}
