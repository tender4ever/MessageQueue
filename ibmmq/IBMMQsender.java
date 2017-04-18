package ibmmq;

import javax.jms.*;
import com.ibm.mq.*;
import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueueConnectionFactory;

import MQInterface.*;

public class IBMMQsender extends Thread implements MQSender {

	private String hostName = "localhost";			//hostName
	
	private String queueManager = "mq_app";			//佇列管理程式
	
	private String channelName = "mychannel";			//Channel Name
	
	private int port = 1414;						//port
	
	private int ccsid = 1381;						//ccsid
	
	private String queueName = "myqueue";			//Queue Name
	
	private String connectionState = "NotReady"; 	//連線狀態
	
	private MQQueueConnectionFactory cf = null;		//JMS ConnectionFactory
	
	private QueueConnection connection = null;		//JMS Connection
	
	private QueueSession session = null;			//JMS Session
	
	private Queue queue = null;						//JMS Queue(Destination)
	
	private QueueSender sender = null;				//JMS Sender
	
	private TextMessage message = null;				//JMS Message
	
	//建構子
	public IBMMQsender(String InputHostName,String InputChannelName,int InputPort,String InputQueueName){
		this.hostName = InputHostName;
		this.channelName =  InputChannelName;
		this.port = InputPort;
		this.queueName = InputQueueName;
		
		connect();//連線
	}
		
	//建構子
	public IBMMQsender(){
		connect();//連線
	}
	//連線
	public void connect(){
		try {
			cf = new MQQueueConnectionFactory();
			cf.setHostName(hostName);
			cf.setQueueManager(queueManager);
			cf.setChannel(channelName);
			cf.setPort(port);
			cf.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
			cf.setCCSID(ccsid);
				
			connection = cf.createQueueConnection();
			connection.start();
				
			session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
				
			queue = session.createQueue(queueName);
				
			connectionState = "Ready";
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

				message = session.createTextMessage(InputText);
			
				sender = session.createSender(queue);
			
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
