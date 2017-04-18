package openmq;

import com.sun.messaging.ConnectionFactory;

import MQInterface.MQSender;

import com.sun.messaging.ConnectionConfiguration;
import javax.jms.*;

public class OpenMQsender extends Thread implements MQSender{

	private String hostName = "127.0.0.1";				//hostName
	
	private int port = 7676; 							//port

	private String queueName = "Queue";					//Queue Name
	
	private String connectionState = "NotReady";		//連線狀態
	
	private ConnectionFactory cf = null;				//JMS ConnectionFactory
	
	private Connection connection = null;				//JMS Connection
	
	private Session session = null;						//JMS Session
	
	private Queue queue = null;							//JMS Queue
	
	private MessageProducer sender = null;				//JMS Sender
	
	private TextMessage message = null;					//JMS Message
	
	//建構子
	public OpenMQsender(String hostName,int port,String queueName){
		this.hostName = hostName;
		this.port = port;
		this.queueName = queueName;
		
		connect();
	}
	//建構子
		public OpenMQsender(){
			
			connect();
		}
		
	//連線
	public void connect(){
		try {
			cf.setProperty(ConnectionConfiguration.imqAddressList,hostName + ":" + String.valueOf(port));
			cf.setProperty(ConnectionConfiguration.imqReconnectEnabled, "true");
			
			connection = cf.createConnection("admin","admin");
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
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
				if (sender != null){
					sender.close();
					sender = null;
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
	
	public synchronized void send(String InputText){
		
		try{
			if (connectionState == "Ready"){
				message.setText(InputText);
			
				sender = session.createProducer(queue);
			
				sender.send(message);
				
				disconnect();
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
	public String getConnectionStatus(){
		return connectionState;
	}
	public void setConnectionStatus(String connectionState){
		this.connectionState = connectionState;
	}
}
