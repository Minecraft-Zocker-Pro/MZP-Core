package minecraft.core.zocker.pro.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class Validator {

    /**
     * The constant PATTERN_INTEGER.
     */
    private final static Pattern PATTERN_INTEGER = Pattern.compile("-?\\d+");

    /**
     * The constant PATTERN_DECIMAL.
     */
    private final static Pattern PATTERN_DECIMAL = Pattern.compile("-?\\d+.\\d+");

    // ------------------------------------------------------------------------------------------------------------
    // Checking for validity and throwing errors if false or null
    // ------------------------------------------------------------------------------------------------------------

    /**
     * Check not null.
     *
     * @param toCheck the to check
     */
    public static void checkNotNull(Object toCheck) {
        if (toCheck == null)
            throw new InvalidValidationException();
    }

    /**
     * Check not null.
     *
     * @param toCheck      the to check
     * @param falseMessage the false message
     */
    public static void checkNotNull(Object toCheck, String falseMessage) {
        if (toCheck == null)
            throw new InvalidValidationException(falseMessage);
    }

    /**
     * Check not null.
     *
     * @param falseMessage the false message
     * @param toCheck      the to check
     */
    public static void checkNotNull(String falseMessage, Object... toCheck) {
        for (Object o : toCheck) if (o == null) throw new InvalidValidationException(falseMessage);
    }

    /**
     * Check not null.
     *
     * @param toCheck the to check
     */
    public static void checkNotNull(Object... toCheck) {
        for (Object o : toCheck) if (o == null) throw new InvalidValidationException();
    }


    /**
     * Check boolean.
     *
     * @param expression the expression
     */
    public static void checkBoolean(boolean expression) {
        if (!expression)
            throw new InvalidValidationException();
    }

    /**
     * Check boolean.
     *
     * @param expression   the expression
     * @param falseMessage the false message
     */
    public static void checkBoolean(boolean expression, String falseMessage) {
        if (!expression)
            throw new InvalidValidationException(falseMessage);
    }

    /**
     * Check not empty.
     *
     * @param collection the collection
     * @param message    the message
     */
    public static void checkNotEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.size() == 0)
            throw new IllegalArgumentException(message);
    }

    // ------------------------------------------------------------------------------------------------------------
    // Checking for true without throwing errors
    // ------------------------------------------------------------------------------------------------------------

    /**
     * Is integer boolean.
     *
     * @param raw the raw
     * @return the boolean
     */
    public static boolean isInteger(String raw) {
        return PATTERN_INTEGER.matcher(raw).find();
    }

    /**
     * Is decimal boolean.
     *
     * @param raw the raw
     * @return the boolean
     */
    public static boolean isDecimal(String raw) {
        return PATTERN_DECIMAL.matcher(raw).find();
    }

    /**
     * Is null or empty boolean.
     *
     * @param array the array
     * @return the boolean
     */
    public static boolean isNullOrEmpty(Object[] array) {
        for (final Object object : array)
            if (object instanceof String) {
                if (!((String) object).isEmpty())
                    return false;

            } else if (object != null)
                return false;

        return true;
    }

    /**
     * Is null or empty boolean.
     *
     * @param message the message
     * @return the boolean
     */
    public static boolean isNullOrEmpty(String message) {
        return message == null || message.isEmpty();
    }

    /**
     * Is in range boolean.
     *
     * @param value the value
     * @param min   the min
     * @param max   the max
     * @return the boolean
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /**
     * Is in range boolean.
     *
     * @param value the value
     * @param min   the min
     * @param max   the max
     * @return the boolean
     */
    public static boolean isInRange(long value, long min, long max) {
        return value >= min && value <= max;
    }

    // ------------------------------------------------------------------------------------------------------------
    // Equality checks
    // ------------------------------------------------------------------------------------------------------------

    /**
     * List equals boolean.
     *
     * @param <T>    the type parameter
     * @param first  the first
     * @param second the second
     * @return the boolean
     */
    public static <T> boolean listEquals(List<T> first, List<T> second) {
        if (first == null && second == null)
            return true;

        if (first == null)
            return false;

        if (second == null)
            return false;

        if (first.size() != second.size())
            return false;

        for (int i = 0; i < first.size(); i++) {
            final T f = first.get(i);
            final T s = second.get(i);

            if (f == null && s != null)
                return false;

            if (f != null && s == null)
                return false;
        }

        return true;
    }

    // ------------------------------------------------------------------------------------------------------------
    // Matching in lists
    // ------------------------------------------------------------------------------------------------------------

    /**
     * Is in list boolean.
     *
     * @param element the element
     * @param list    the list
     * @return the boolean
     */
    public static boolean isInList(String element, Iterable<String> list) {
        try {
            for (final String matched : list)
                if (normalizeEquals(element).equals(normalizeEquals(matched)))
                    return true;

        } catch (final ClassCastException ex) { // for example when YAML translates "yes" to "true" to boolean (!) (#wontfix)
        }

        return false;
    }

    /**
     * Is in list starts with boolean.
     *
     * @param element the element
     * @param list    the list
     * @return the boolean
     */
    public static boolean isInListStartsWith(String element, Iterable<String> list) {
        try {
            for (final String matched : list)
                if (normalizeEquals(element).startsWith(normalizeEquals(matched)))
                    return true;
        } catch (final ClassCastException ex) { // for example when YAML translates "yes" to "true" to boolean (!) (#wontfix)
        }

        return false;
    }

    /**
     * Is in list contains boolean.
     *
     * @param element the element
     * @param list    the list
     * @return the boolean
     */
    public static boolean isInListContains(String element, Iterable<String> list) {
        try {
            for (final String matched : list)
                if (normalizeEquals(element).contains(normalizeEquals(matched)))
                    return true;

        } catch (final ClassCastException ex) { // for example when YAML translates "yes" to "true" to boolean (!) (#wontfix)
        }

        return false;
    }

    /**
     * Is in array boolean.
     *
     * @param <T>   the type parameter
     * @param array the array
     * @param index the index
     * @return the boolean
     */
    public static <T> boolean isInArray(T[] array, Integer index) {
        return array.length - 1 >= index;
    }

    /**
     * Normalize equals string.
     *
     * @param message the message
     * @return the string
     */
    private static String normalizeEquals(String message) {
        if (message.startsWith("/"))
            message = message.substring(1);

        return message.toLowerCase();
    }

    /**
     * Stack trace to string string.
     *
     * @param exception the exception
     * @return the string
     */
    public static String stackTraceToString(Exception exception) {
        StringWriter errors = new StringWriter();
        exception.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }
}
