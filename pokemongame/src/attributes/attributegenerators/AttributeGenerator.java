package attributes.attributegenerators;

import thingFramework.Item;
import thingFramework.Pokemon;

public interface AttributeGenerator {
	public void addAttributes(Pokemon p);
	public void addAttributes(Item i);
}
