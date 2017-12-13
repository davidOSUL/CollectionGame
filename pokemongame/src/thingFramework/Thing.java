package thingFramework;

import java.util.Map;

public abstract class Thing {
	private String name;
	private String image;
	private Map<Attribute, Object> attributes; //will map from valid attribute to value
	public Thing(String name, String image, Map<Attribute, Object> attributes) {
		if (!vallidateAttributes(attributes))
			throw new Error("INVALID ATTRIBUTE FOR: " + name);
		this.attributes = attributes;
		this.name = name ;
		this.image = image;
	}
	public boolean containsAttribute(Attribute at) {
		return attributes.containsKey(at);
	}
	public Object getAttributeValue(Attribute at) {
		return attributes.get(at);
	}
	abstract boolean vallidateAttributes(Map<Attribute, Object> attributes);
	
}
