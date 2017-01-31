package database.services.undoRedo;

import java.util.LinkedList;
import database.services.ServiceRegistry;
import database.services.settings.ISettingsProvider;

public class BoundedStack<T> {
	private LinkedList<T> list;

	public BoundedStack() {
		list = new LinkedList<>();
	}

	public void clear() {
		list.clear();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public T peek() {
		if (list.size() > 0) {
			return list.getFirst();
		}
		return null;
	}

	public T pop() {
		if (list.size() > 0) {
			T value = list.getFirst();
			list.removeFirst();
			return value;
		}
		return null;
	}

	public void push(T value) {
		ISettingsProvider settingsProvider = ServiceRegistry.Instance().get(ISettingsProvider.class);
		if (list.size() >= settingsProvider.getInternalParameters().getRevertStackSize()) {
			list.removeLast();
		}
		list.addFirst(value);
	}
}