package attributes;

import java.util.List;
import java.util.Map;

public class AttributeMap<R> {
	private Map<Class<?>, Type<R, ?>> map;

	public <Q> void put(final Class<Q> clazz, final Type<R, Q> input) {
		map.put(clazz, input);
	}
	@SuppressWarnings("unchecked")
	public <Q> Type<R, Q> get(final Class<Q> clazz) {
		return  (Type<R, Q>) map.get(clazz);
	}
	public interface Type<R, Q> {
		public R getInstance();
	}
	public class AttributeList<Q> implements Type<List<Attribute<Q>>, Q> {
		private List<Attribute<Q>> list;
		@Override
		public List<Attribute<Q>> getInstance() {
			return list;
		}
		
	}
}
