package attributes;

public class TestAttributeManager {
	public static void main(final String[] args) {
		final AttributeManager manager = new AttributeManager();
		//manager.setDoOnGenerationForType(at -> System.out.println(at.getName() + "created yay"), ParseType.INTEGER);
		manager.generateAttribute("gph", "5");
		manager.generateAttribute("gpm");
		manager.setAttributeValue("gpm", 10, ParseType.INTEGER);
		final AttributeManager manager2 = new AttributeManager();
		manager2.generateAttribute("gph", "7");
		manager2.generateAttribute("gpm");
		manager2.setAttributeValue("gpm", 17, ParseType.INTEGER);
		//System.out.println(manager.getAttribute("gph", ParseType.INTEGER).getValue());
		System.out.println(manager.getAttributeAsString("gpm"));
		//System.out.println(manager2.getAttribute("gph", ParseType.INTEGER).getValue());
		System.out.println(manager2.getAttributeAsString("gpm"));
		
	}
	
}
