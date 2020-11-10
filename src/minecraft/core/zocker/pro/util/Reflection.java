package minecraft.core.zocker.pro.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Reflection {

	/**
	 * Sets raw value.
	 *
	 * @param field  the field
	 * @param object the object
	 * @param value  the value
	 */
	public static void setRawValue(Field field, Object object, Object value) {
		try {
			field.set(object, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static boolean set(Class<?> sourceClass, Object instance, String fieldName, Object value) {
		try {
			Field field = sourceClass.getDeclaredField(fieldName);
			boolean accessible = field.isAccessible();

			//field.setAccessible(true);
			Field modifiersField = Field.class.getDeclaredField("modifiers");

			int modifiers = modifiersField.getModifiers();
			boolean isFinal = (modifiers & Modifier.FINAL) == Modifier.FINAL;

			if (!accessible) {
				field.setAccessible(true);
			}
			if (isFinal) {
				modifiersField.setAccessible(true);
				modifiersField.setInt(field, modifiers & ~Modifier.FINAL);
			}
			try {
				field.set(instance, value);
			} finally {
				if (isFinal) {
					modifiersField.setInt(field, modifiers | Modifier.FINAL);
				}
				if (!accessible) {
					field.setAccessible(false);
				}
			}

			return true;
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
			ex.printStackTrace();
		}

		return false;
	}
}
