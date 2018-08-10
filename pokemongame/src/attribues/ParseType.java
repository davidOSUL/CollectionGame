package attribues;
import attributeTypes.regularAttributes.*;
import attributeTypes.setAttributes.*;
import attributeTypes.enumAttributes.*;
import attributeTypes.listAttributes.*;
enum ParseType{
	INTEGER(IntegerAttribute.class), DOUBLE(DoubleAttribute.class), STRING(StringAttribute.class), POKEMONTYPE(PokemonTypeAttribute.class), EXPERIENCEGROUP(ExperienceGroupAttribute.class), BOOLEAN(BooleanAttribute.class), LISTSTRING(ListStringAttribute.class);
	private Class<? extends ReadableAttribute<?>> associatedClass;
	private ParseType(Class<? extends ReadableAttribute<?>> associatedClass) {
		this.associatedClass = associatedClass;
	}
	public Class<? extends ReadableAttribute<?>> getAssociatedClass() {
		return associatedClass;
	}
	
}
