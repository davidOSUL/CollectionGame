package gui;

import java.io.IOException;

import guiutils.GuiUtils;
import thingFramework.Thing;

@Deprecated
public class ThingSpace extends GameSpace {
	private Thing t;
	public ThingSpace(Thing t) throws IOException {
		super(GuiUtils.readImage(t.getImage()));
	}
	public void setThing(Thing t) {
		this.t = t;
	}
	public Thing getThing() {
		return t;
	}
}
