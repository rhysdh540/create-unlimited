package dev.rdh.createunlimited;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class Reflection {
	private static final Unsafe U = getUnsafe();

	private static Unsafe getUnsafe() {
		try {
			for(Field field : Unsafe.class.getDeclaredFields()) {
				if(field.getType() == Unsafe.class) {
					field.setAccessible(true);
					return (Unsafe) field.get(null);
				}
			}
			throw new RuntimeException("Unsafe not found");
		} catch (IllegalAccessException e) {
			throw unchecked(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getField(final Field field, final Object instance) {
		try {
			Object base;
			long offset;
			if (instance == null) {
				base = U.staticFieldBase(field);
				offset = U.staticFieldOffset(field);
			} else {
				base = instance;
				offset = U.objectFieldOffset(field);
			}

			return (T) U.getObject(base, offset);
		} catch (Exception e) {
			throw unchecked(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Throwable> RuntimeException unchecked(Throwable t) throws T {
		throw (T) t;
	}
}
