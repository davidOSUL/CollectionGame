package loaders;

import java.util.Arrays;

import thingFramework.ExperienceGroup;

public class TestParse {

	public static void main(String[] args) {
//		String s = "\"Hitmonlee\r\n" + 
//				"Hitmonchan\r\n" + 
//				"Hitmontop\"";
//		s = s.replace("\n", "").replace("\r", "").replace("\"", "");
//		String[] r = s.split("(?=\\p{Lu})");
//		System.out.println(Arrays.toString(r));
		System.out.println(RequiredXPLookup.getAmountOfXPToNextLevel(ExperienceGroup.FLUCTUATING, 500));
		System.out.println(RequiredXPLookup.getAmountOfXPToNextLevel(ExperienceGroup.FLUCTUATING, 9, 500));
		System.out.println(RequiredXPLookup.getAmountOfXPToNextLevelFromBase(ExperienceGroup.FLUCTUATING, 9));
		System.out.println(RequiredXPLookup.getLevel100XP(ExperienceGroup.FLUCTUATING));
		System.out.println(RequiredXPLookup.getLevelFromCurrentXP(ExperienceGroup.FLUCTUATING, 500));
		System.out.println(RequiredXPLookup.getMinXPAtLevel(ExperienceGroup.FLUCTUATING, 9));
		System.out.println(RequiredXPLookup.getMinXPAtLevelGivenXP(ExperienceGroup.FLUCTUATING, 500));
		
		System.out.println(RequiredXPLookup.getAmountOfXPToNextLevel(ExperienceGroup.FLUCTUATING, 500));
		System.out.println(RequiredXPLookup.getAmountOfXPToNextLevel(ExperienceGroup.FLUCTUATING, 100, 1640000));
		System.out.println(RequiredXPLookup.getAmountOfXPToNextLevelFromBase(ExperienceGroup.FLUCTUATING, 100));
		System.out.println(RequiredXPLookup.getLevel100XP(ExperienceGroup.FLUCTUATING));
		System.out.println(RequiredXPLookup.getLevelFromCurrentXP(ExperienceGroup.FLUCTUATING, 1640000));
		System.out.println(RequiredXPLookup.getMinXPAtLevel(ExperienceGroup.FLUCTUATING, 100));
		System.out.println(RequiredXPLookup.getMinXPAtLevelGivenXP(ExperienceGroup.FLUCTUATING, 1640000));
		
		System.out.println(RequiredXPLookup.getAmountOfXPToNextLevel(ExperienceGroup.FLUCTUATING, 2));
		System.out.println(RequiredXPLookup.getAmountOfXPToNextLevel(ExperienceGroup.FLUCTUATING, 1, 2));
		System.out.println(RequiredXPLookup.getAmountOfXPToNextLevelFromBase(ExperienceGroup.FLUCTUATING, 1));
		System.out.println(RequiredXPLookup.getLevel100XP(ExperienceGroup.FLUCTUATING));
		System.out.println(RequiredXPLookup.getLevelFromCurrentXP(ExperienceGroup.FLUCTUATING, 2));
		System.out.println(RequiredXPLookup.getMinXPAtLevel(ExperienceGroup.FLUCTUATING, 1));
		System.out.println(RequiredXPLookup.getMinXPAtLevelGivenXP(ExperienceGroup.FLUCTUATING, 2));


		

	}

}
