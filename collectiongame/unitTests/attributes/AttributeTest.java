package attributes;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import thingFramework.CreatureTypeSet;
import thingFramework.ExperienceGroup;
class AttributeTest {

	@Test
	void testAttributeParseTypeOfT() {
		final Attribute<Integer> at = new Attribute<Integer>(ParseType.INTEGER);
		at.setValue(5);
		final Attribute<String> atSt = new Attribute<String>(ParseType.STRING);
		atSt.setValue("hello");

	}

	static void checkAttributesFunctionallyEqual(final Attribute at1, final Attribute at2) {
		assertTrue(at1.getValue().equals(at2.getValue()));
		assertTrue(at1.getDisplayRank() == at2.getDisplayRank());
		assertTrue(at1.getDisplayString().equals(at2.getDisplayString()));
		assertTrue(at1.getExtraDescription().equals(at2.getExtraDescription()));
		assertTrue(at1.getName().equals(at2.getName()));
		assertTrue(at1.hashCode() != at2.hashCode());
		assertTrue(at1.shouldDisplay() == at2.shouldDisplay());
		assertTrue(at1.toString().equals(at2.toString()));
	}
	@Test
	void testAttributeAttributeOfT() {
		final Attribute<Integer> at = new Attribute<Integer>(ParseType.INTEGER);
		at.setValue(5);
		at.setIsPositiveFunction(x -> x > 5);
		assertFalse(at.isPositive());
		at.setExtraDescription("test");
		final AttributeCharacteristicSet acs = new AttributeCharacteristicSet();
		acs.addValue(AttributeCharacteristic.CREATUREONLY);
		at.setAttributeCharacteristicSet(acs);
		final Attribute<Integer> atCopy = new Attribute<>(at);
		assertTrue(atCopy != at);
		assertTrue(atCopy.hasCharacteristic(AttributeCharacteristic.CREATUREONLY));
		assertTrue(atCopy.isPositive() == at.isPositive());
		assertTrue(atCopy.valEqualsParse("5") && at.valEqualsParse("5"));
		checkAttributesFunctionallyEqual(at, atCopy);
		
	}

	@Test
	void testSetValue() {
		final Attribute<Integer> at = new Attribute<>(ParseType.INTEGER);
		at.setValue(5);
		assertTrue(at.getValue() == 5);
	}

	@Test
	void testSetValueParse() {
		final Attribute<Integer> atInt = new Attribute<>(ParseType.INTEGER);
		atInt.setValueParse("5");
		assertTrue(atInt.getValue() == 5);
		
		final Attribute<String> atString = new Attribute<>(ParseType.STRING);
		atString.setValueParse("5");
		assertTrue(atString.getValue().equals("5"));
		
		final Attribute<List<?>> atList = new Attribute<>(ParseType.LIST);
		atList.setValueParse("[1,2,3]");
		final ArrayList<String> testCompare = new ArrayList<String>();
		testCompare.add("1"); testCompare.add("2"); testCompare.add("3"); 
		assertTrue(atList.getValue().equals(testCompare));
		atList.setValueParse("7");
		testCompare.clear(); testCompare.add("7");
		assertTrue(atList.getValue().equals(testCompare));
		
		final Attribute<Boolean> atBool = new Attribute<>(ParseType.BOOLEAN);
		final Attribute<CreatureTypeSet> atCT = new Attribute<>(ParseType.CREATURE_TYPES);
		final Attribute<ExperienceGroup> atEG = new Attribute<>(ParseType.EXPERIENCE_GROUP);
		final Attribute<Double> atDouble = new Attribute<>(ParseType.DOUBLE);



	}

	@Test
	void testGetValue() {
		fail("Not yet implemented");
	}

	@Test
	void testSetDefaultValue() {
		fail("Not yet implemented");
	}

	@Test
	void testSetAttributeTypeSet() {
		fail("Not yet implemented");
	}

	@Test
	void testMakeCopy() {
		fail("Not yet implemented");
	}

	@Test
	void testGetName() {
		fail("Not yet implemented");
	}

	@Test
	void testSetName() {
		fail("Not yet implemented");
	}

	@Test
	void testToString() {
		fail("Not yet implemented");
	}

	@Test
	void testIsPositive() {
		fail("Not yet implemented");
	}

	@Test
	void testHasCharacteristic() {
		fail("Not yet implemented");
	}

	@Test
	void testSetIsPositiveFunction() {
		fail("Not yet implemented");
	}

	@Test
	void testGetExtraDescription() {
		fail("Not yet implemented");
	}

	@Test
	void testSetExtraDescription() {
		fail("Not yet implemented");
	}

	@Test
	void testValEqualsParse() {
		fail("Not yet implemented");
	}

	@Test
	void testSetValueToDefault() {
		fail("Not yet implemented");
	}

	@Test
	void testSetParseType() {
		fail("Not yet implemented");
	}

}
