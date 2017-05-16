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
	private String connectionState = "NotReady";	//�s�u���A
	private QueueConnectionFactory cf = null;		//JMS ConnectionFactory
	private QueueConnection connection = null;		//JMS Connection
	private QueueSession session = null;			//JMS Session
	private AQQueueTable q_table = null;
	private Queue queue = null;						//JMS Queue(Destination)
	private QueueSender sender = null;				//JMS Sender
	private TextMessage message = null;				//JMS Message
	
	//�غc�l
	public OracleAQsender(String hostName,String oracleSid,int port,String driver,
			String dbAccount,String dbPwd,String queueTable,String queueName)
	{
		this.hostName = hostName;					//�]�whostName
		this.oracleSid = oracleSid;					//�]�woracleSid
		this.port = port;							//�]�wport
		this.driver = driver;						//�]�wdriver
		this.dbAccount = dbAccount;					//�]�woracleAccount
		this.dbPwd = dbPwd;							//�]�woraclePwd
		this.queueTable = queueTable;				//�]�wqueueTable
		this.queueName = queueName;					//�]�wqueueName
		connect();									//�s�u
	}
	//�غc�l
	public OracleAQsender(String hostName,String oracleSid,int port,
			String dbAccount,String dbPwd,String queueTable,String queueName)
	{
		this.hostName = hostName;					//�]�whostName
		this.oracleSid = oracleSid;					//�]�woracleSid
		this.port = port;							//�]�wport
		this.dbAccount = dbAccount;					//�]�woracleAccount
		this.dbPwd = dbPwd;							//�]�woraclePwd
		this.queueTable = queueTable;				//�]�wqueueTable
		this.queueName = queueName;					//�]�wqueueName
		connect();									//�s�u
	}
	//�غc�l
	public OracleAQsender(String hostName,int port,
			String dbAccount,String dbPwd,String queueTable,String queueName)
	{
		this.hostName = hostName;					//�]�whostName
		this.port = port;							//�]�wport
		this.dbAccount = dbAccount;					//�]�woracleAccount
		this.dbPwd = dbPwd;							//�]�woraclePwd
		this.queueTable = queueTable;				//�]�wqueueTable
		this.queueName = queueName;					//�]�wqueueName
		connect();									//�s�u
	}
	//�غc�l
	public OracleAQsender(String hostName,int port,String queueName)
	{
		this.hostName = hostName;					//�]�whostName
		this.port = port;							//�]�wport
		this.queueName = queueName;					//�]�wqueueName
		connect();									//�s�u
	}
	//�غc�l
	public OracleAQsender(String hostName,String queueName)
	{
		this.hostName = hostName;					//�]�whostName
		this.queueName = queueName;					//�]�wqueueName
		connect();									//�s�u
	}
	//�غc�l
	public OracleAQsender(String hostName,int port)
	{
		this.hostName = hostName;					//�]�whostName
		this.port = port;							//�]�wport
		connect();									//�s�u
	}
	//�غc�l
	public OracleAQsender(String hostName)
	{
		this.hostName = hostName;					//�]�whostName
		connect();									//�s�u
	}
	//�غc�l
	public OracleAQsender(){
		connect();//�s�u
	}
	
	//�s�u
	public void connect(){
		
		try{
			cf = AQjmsFactory.getQueueConnectionFactory(hostName, oracleSid, port, driver);//ConnectionFactory
			connection = cf.createQueueConnection(dbAccount,dbPwd);						   //Connection
			session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);	   //Session
			connection.start();
			q_table = ((AQjmsSession)session).getQueueTable(dbAccount, queueTable);		   //QueueTable
			queue = ((AQjmsSession)session).getQueue(dbAccount, queueName);				   //queue
			((AQjmsDestination)queue).start(session, true, true);
			connectionState = "Ready";
		}
		catch(Exception e){
			e.printStackTrace();
			disconnect(); //�_�u
		}
	}
	
	//�_�u
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
}
