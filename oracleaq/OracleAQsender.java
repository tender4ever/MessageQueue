package oracleaq;

import javax.jms.*;

import MQInterface.MQSender;
import oracle.AQ.*;
import oracle.jms.*;

public class OracleAQsender extends Thread implements MQSender{

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
	
	private QueueSender sender = null;				//JMS Sender
	
	private TextMessage message = null;				//JMS Message
	
	//建構子
	public OracleAQsender(String hostName,String oracleSid,int port,String driver,String dbAccount,String dbPwd,String queueTable,String queueName){
		this.hostName = hostName;
		this.oracleSid = oracleSid;
		this.port = port;
		this.driver = driver;
		this.dbAccount = dbAccount;
		this.dbPwd = dbPwd;
		this.queueTable = queueTable;
		this.queueName = queueName;
		
		connect();//連線
	}
	
	//建構子
	public OracleAQsender(){
		connect();//連線
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
		}
		catch(Exception e){
			e.printStackTrace();
			disconnect(); //斷線
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
				//message.setText(InputText);
			
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
