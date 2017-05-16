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
	private String connectionState = "NotReady";	//�s�u���A
	private QueueConnectionFactory cf = null;		//JMS ConnectionFactory
	private QueueConnection connection = null;		//JMS Connection
	private QueueSession session = null;			//JMS Session
	private AQQueueTable q_table = null;
	private Queue queue = null;						//JMS Queue(Destination)
	private QueueReceiver receiver = null;			//JMS Receiver
	private MQEventListener listener;				//Event Listener
	
	//�غc�l
	public OracleAQreceiver(MQEventListener eventListener,String hostName,String oracleSid,
			int port,String driver,String dbAccount,String dbPwd,String queueTable,String queueName)
	{
		listener = eventListener;						//Event Listener
		this.hostName = hostName;						//�]�whostName
		this.oracleSid = oracleSid;						//�]�woraclesid
		this.port = port;								//�]�wport
		this.driver = driver;							//�]�wdriver
		this.dbAccount = dbAccount;						//�]�woracleAccount
		this.dbPwd = dbPwd;								//�]�woraclePwd
		this.queueTable = queueTable;					//�]�wqueueTable
		this.queueName = queueName;						//�]�wqueueName
		connect();										//�s�u
		super.start();									//�}�lreceive��Thread 
		checkConnect.start();							//�}�lcheck Connect��Thread
	}
	//�غc�l
	public OracleAQreceiver(MQEventListener eventListener,String hostName,String oracleSid,
			int port,String dbAccount,String dbPwd,String queueTable,String queueName)
	{
		listener = eventListener;						//Event Listener
		this.hostName = hostName;						//�]�whostName
		this.oracleSid = oracleSid;						//�]�woraclesid
		this.port = port;								//�]�wport
		this.dbAccount = dbAccount;						//�]�woracleAccount
		this.dbPwd = dbPwd;								//�]�woraclePwd
		this.queueTable = queueTable;					//�]�wqueueTable
		this.queueName = queueName;						//�]�wqueueName
		connect();										//�s�u
		super.start();									//�}�lreceive��Thread 
		checkConnect.start();							//�}�lcheck Connect��Thread
	}
	//�غc�l
	public OracleAQreceiver(MQEventListener eventListener,String hostName,
			int port,String dbAccount,String dbPwd,String queueTable,String queueName)
	{
		listener = eventListener;						//Event Listener
		this.hostName = hostName;						//�]�whostName
		this.port = port;								//�]�wport
		this.dbAccount = dbAccount;						//�]�woracleAccount
		this.dbPwd = dbPwd;								//�]�woraclePwd
		this.queueTable = queueTable;					//�]�wqueueTable
		this.queueName = queueName;						//�]�wqueueName
		connect();										//�s�u
		super.start();									//�}�lreceive��Thread 
		checkConnect.start();							//�}�lcheck Connect��Thread
	}
	//�غc�l
	public OracleAQreceiver(MQEventListener eventListener,String hostName,int port,String queueName)
	{
		listener = eventListener;						//Event Listener
		this.hostName = hostName;						//�]�whostName
		this.port = port;								//�]�wport
		this.queueName = queueName;						//�]�wqueueName
		connect();										//�s�u
		super.start();									//�}�lreceive��Thread 
		checkConnect.start();							//�}�lcheck Connect��Thread
	}
	//�غc�l
	public OracleAQreceiver(MQEventListener eventListener,String hostName,String queueName)
	{
		listener = eventListener;						//Event Listener
		this.hostName = hostName;						//�]�whostName
		this.queueName = queueName;						//�]�wqueueName
		connect();										//�s�u
		super.start();									//�}�lreceive��Thread 
		checkConnect.start();							//�}�lcheck Connect��Thread
	}
	//�غc�l
	public OracleAQreceiver(MQEventListener eventListener,String hostName,int port)
	{
		listener = eventListener;						//Event Listener
		this.hostName = hostName;						//�]�whostName
		this.port = port;								//�]�wport
		connect();										//�s�u
		super.start();									//�}�lreceive��Thread 
		checkConnect.start();							//�}�lcheck Connect��Thread
	}
	//�غc�l
	public OracleAQreceiver(MQEventListener eventListener,String hostName)
	{
		listener = eventListener;						//Event Listener
		this.hostName = hostName;						//�]�whostName
		connect();										//�s�u
		super.start();									//�}�lreceive��Thread 
		checkConnect.start();							//�}�lcheck Connect��Thread
	}
	//�غc�l
	public OracleAQreceiver(MQEventListener eventListener){
		listener = eventListener;						//Event Listener
		connect();										//�s�u
		super.start();									//�}�lreceive��Thread 
		checkConnect.start();							//�}�lcheck Connect��Thread
	}
	
	//�s�u
	public void connect(){
		
		try{
			cf = AQjmsFactory.getQueueConnectionFactory(hostName, oracleSid, port, driver);	//ConnectionFactory
			connection = cf.createQueueConnection(dbAccount,dbPwd);							//Connection
			session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);		//Session
			connection.start();
			q_table = ((AQjmsSession)session).getQueueTable(dbAccount, queueTable);			//queueTable
			queue = ((AQjmsSession)session).getQueue(dbAccount, queueName);					//queue
			((AQjmsDestination)queue).start(session, true, true);
			connectionState = "Ready";
			listener.systemMessage("receiver connect ok");
		}
		catch(Exception e){
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
