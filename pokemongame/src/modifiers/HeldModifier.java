package modifiers;

import interfaces.SerializableConsumer;
import interfaces.SerializablePredicate;
import thingFramework.Thing;

public class HeldModifier<T> extends Modifier<T> {
	private Thing creator;

	public HeldModifier (final SerializableConsumer<T> modification,  final SerializableConsumer<T> reverseModification) {
		super(modification, reverseModification);
	}
	public HeldModifier (final long lifeInMillis, final SerializableConsumer<T> modification,  final SerializableConsumer<T> reverseModification) {
		super(lifeInMillis, modification, reverseModification);
	}
	public HeldModifier (final SerializablePredicate<T> shouldModify, final SerializableConsumer<T> modification,  final SerializableConsumer<T> reverseModification) {
		super(shouldModify, modification, reverseModification);
	}
	public HeldModifier (final long lifeInMillis, final SerializablePredicate<T> shouldModify, final SerializableConsumer<T> modification, final SerializableConsumer<T> reverseModification) {
		super(lifeInMillis, shouldModify, modification, reverseModification);
	}
	public void setCreator(final Thing thing) {
		this.creator = thing;
	}
	public Thing getCreator() {
		return creator;
	}

}
