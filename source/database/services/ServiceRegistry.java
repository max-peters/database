package database.services;

import java.util.HashMap;
import java.util.Map;

public class ServiceRegistry {
	private Map<Class<?>, Object>	map;
	private static ServiceRegistry	instance;

	private ServiceRegistry() {
		map = new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> type) {
		return (T) map.get(type);
	}

	public void register(Class<?> type, Object service) {
		map.put(type, service);
	}

	public static ServiceRegistry Instance() {
		if (instance == null) {
			instance = new ServiceRegistry();
		}
		return instance;
	}
}
