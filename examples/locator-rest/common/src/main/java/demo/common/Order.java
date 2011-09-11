package demo.common;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Order")
public class Order {
	
	private String orderId;
	private String description;
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
