package oracleaq;

import javax.jms.*;

import MQInterface.MQEventListener;
import MQInterface.MQReceiver;
import oracle.AQ.*;
import oracle.jms.*;

public class OracleAQreceiver extends Thread implements MQReceiver{

	private String hostName = "STKC.localdomain"; 	//hostName
	
	private String oracleSid = "orcl";				//oracle Sid
	
	private int port = 1521;						//port
	
	private String driver = "thin";					//ojdbc driver
	
	private String dbAccount = "aqjmsuser";			//DB Account
	
	private String dbPwd = "aqjmsuser";				//DB Pwd
	
	private String queueTable = "myQueueTable";		//Queue Table Name
	
	private String queueName = "userQueue";			//Queue Name
	
	private String connectionState = "NotReady";	//連線狀態
	
	private QueueConnectionFactory cf = null;		//JMS ConnectionFactory
	
	private QueueConnection connection = null;		//JMS Connection
	
	private QueueSession session = null;			//JMS Session
	
	private AQQueueTable q_table = null;
	
	private Queue queue = null;						//JMS Queue(Destination)
	
	private QueueReceiver receiver = null;			//JMS Receiver
	
	private MQEventListener listener;
	
	//建構子
	public OracleAQreceiver(MQEventListener eventListener,String hostName,String oracleSid,int port,String driver,
							String dbAccount,String dbPwd,String queueTable,String queueName){
		
		listener = eventListener;
		this.hostName = hostName;
		this.oracleSid = oracleSid;
		this.port = port;
		this.driver = driver;
		this.dbAccount = dbAccount;
		this.dbPwd = dbPwd;
		this.queueTable = queueTable;
		this.queueName = queueName;
		connect();//連線
		super.start();
	}
		
	//建構子
	public OracleAQreceiver(MQEventListener eventListener){
		listener = eventListener;
		connect();//連線
		super.start();
	}
	
	//連線
	public void connect(){
		
		try{
			cf = AQjmsFactory.getQueueConnectionFactory(hostName, oracleSid, port, driver);
			
			connection = cf.createQueueConnection(dbAccount,dbPwd);
			
			session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			
			connection.start();
			
			q_table = ((AQjmsSession)session).getQueueTable(dbAccount, queueTable);
			
			queue = ((AQjmsSession)session).getQueue(dbAccount, queueName);
			
			((AQjmsDestination)queue).start(session, true, true);
			
			connectionState = "Ready";
			
			listener.onConnect("receiver connect ok");
		}
		catch(Exception e){
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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		catch(Exception ex2){
			ex2.printStackTrace();
		}
	}
	
	public synchronized void receive(){
		
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
	public String getConnectionStatus(){
		return connectionState;
	}
	public void setConnectionStatus(String connectionState){
		this.connectionState = connectionState;
	}
}
