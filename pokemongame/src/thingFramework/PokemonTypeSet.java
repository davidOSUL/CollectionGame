package thingFramework;

import java.util.EnumSet;

import gameutils.EnumSetHolder;

public class PokemonTypeSet extends EnumSetHolder<PokemonType>{
	private EnumSet<PokemonType> types;
	public PokemonTypeSet() {};
	public PokemonTypeSet(final String...types) {
		for (final String type : types) {
			if(type.isEmpty())
				continue;
			addValue(PokemonType.valueOf(type.trim().toUpperCase()));
		}
	}
	private PokemonTypeSet(final PokemonTypeSet set) {
		super(set);
	}
	@Override
	public PokemonTypeSet makeCopy() {
		return new PokemonTypeSet(this);
	}

	@Override
	protected Class<PokemonType> getEnumClass() {
		return PokemonType.class;
	}


	
}

