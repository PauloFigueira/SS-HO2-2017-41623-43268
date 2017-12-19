
public class Capability {

	String resource;
	String operation;
	String expireTime;
	String key;
	
	public Capability (String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
}

/*
public Capability (String resource,String operation,String expireTime,String key) {
	this.resource = resource;
	this.operation = operation;
	this.expireTime = expireTime;
	this.key = key;
}*/
