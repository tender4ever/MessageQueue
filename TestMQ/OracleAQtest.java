package test;

import oracleaq.*;
import MQInterface.*;

public class OracleAQtest {

	public static void main(String args[]){
		
		InterfaceQueueHandler a = new InterfaceQueueHandler();
		
		MQSender testsender = new OracleAQsender();
		
		testsender.send("test~test");
	}
}
