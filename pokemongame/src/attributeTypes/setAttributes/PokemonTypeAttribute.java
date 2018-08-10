package attributeTypes.setAttributes;

import java.util.EnumSet;

import thingFramework.PokemonType;

public class PokemonTypeAttribute extends SetAttribute<PokemonType, EnumSet<PokemonType>> {
	public PokemonTypeAttribute() {}
	private PokemonTypeAttribute(final PokemonTypeAttribute attribute) {
		super(attribute);
	}
	@Override
	public PokemonTypeAttribute makeCopy() {
		return new PokemonTypeAttribute(this);
	}

}
