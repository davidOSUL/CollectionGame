package attributes;

import attributes.AttributeFactories.AttributeFactory;

public final class ParseType<T> {
	
	public static final ParseType<Integer> INTEGER = new ParseType<Integer>(ParseTypeEnum.INTEGER, AttributeFactories.getInstance().INTEGER_FACTORY);
	public static final ParseType<Double> DOUBLE = new ParseType<Double>(ParseTypeEnum.DOUBLE, AttributeFactories.getInstance().DOUBLE_FACTORY);
	public static final ParseType<String> STRING = new ParseType<String>(ParseTypeEnum.STRING, AttributeFactories.getInstance().STRING_FACTORY);
	private final ParseTypeEnum associatedEnum;
	private final AttributeFactory<T> associatedFactory;
	private ParseType(final ParseTypeEnum associatedEnum, final AttributeFactory<T> associatedFactory) {
		this.associatedEnum = associatedEnum;
		this.associatedFactory = associatedFactory;
	}
	AttributeFactory<T> getAssociatedFactory() {
		return associatedFactory;
	}
	ParseTypeEnum getAssociatedEnum() {
		return associatedEnum;
	}
	enum ParseTypeEnum {
		INTEGER, DOUBLE, STRING;
	}

}
