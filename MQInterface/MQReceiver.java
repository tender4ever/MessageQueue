package MQInterface;

public interface MQReceiver {

	public void connect();
	public void disconnect();
	public String getConnectionStatus();
	public void setConnectionStatus(String connectionState);
}
