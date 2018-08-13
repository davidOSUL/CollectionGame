package attributes;

import attributes.AttributeFactories.AttributeFactory;

public final class ParseType<T> {
	
	public static final ParseType<Integer> INTEGER = new ParseType<Integer>(ParseTypeEnum.INTEGER);
	public static final ParseType<Double> DOUBLE = new ParseType<Double>(ParseTypeEnum.DOUBLE);
	public static final ParseType<String> STRING = new ParseType<String>(ParseTypeEnum.STRING);
	private final ParseTypeEnum associatedEnum;
	private AttributeFactory<T> associatedFactory;
	private ParseType(final ParseTypeEnum associatedEnum) {
		this.associatedEnum = associatedEnum;
	}
	AttributeFactory<T> getAssociatedFactory() {
		return associatedFactory;
	}
	void setAssociatedFactory(final AttributeFactory<T> associatedFactory) {
		this.associatedFactory = associatedFactory;
	}
	ParseTypeEnum getAssociatedEnum() {
		return associatedEnum;
	}

}
