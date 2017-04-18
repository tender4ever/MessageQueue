package test;

import ibmmq.*;
import MQInterface.*;

public class IBMMQtest {

	public static void main(String args[]){
		
		InterfaceQueueHandler a = new InterfaceQueueHandler();
		
		MQSender testsender = new IBMMQsender();
		
		testsender.send("test~test");
	}
}
