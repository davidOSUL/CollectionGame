package attributes;

public class TestAttributeManager {
	public static void main(final String[] args) {
		final AttributeManager manager = new AttributeManager();
		manager.generateAttribute("gph", "5");
		manager.generateAttribute("gpm");
		manager.setAttributeValue("gpm", 10, ParseType.INTEGER);
		System.out.println(manager.getAttributeValue("gph", ParseType.INTEGER));
		System.out.println(manager.getAttributeAsString("gpm"));
		
	}
	
}
