/**
 * 
 */
package MQInterface;

/**
 * @author Administrator
 *
 */
public interface MQSender {

	public void connect();
	public void disconnect();
	public void send(String InputText);
}
