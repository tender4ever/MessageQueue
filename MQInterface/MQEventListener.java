package MQInterface;

import java.util.*;

public interface MQEventListener extends EventListener{

	public void onMessage(String txtMessage);
	public void systemMessage(String status);
}
