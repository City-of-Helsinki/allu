package fi.hel.allu.common.util;

import java.util.Collection;
import java.util.Map;

public class EmptyUtil {

    /**
     * Used to hide implicit public constructor of this class. This is util class and should not be initialized
     * in other classes.
     */
    private EmptyUtil() {
        throw new RuntimeException("Should not get here");
    }

    /**
     * Returns false if  list is null or the list is empty.
     * @param collection Collection of values that are checked
     * @return boolean
     * @param <T> generic value of checked collection
     */
    public static <T> boolean isNotEmpty(Collection<T> collection){
        return collection != null && !collection.isEmpty();
    }

    /**
     * Returns false if  map is null or the map is empty.
     * @param map map that will be checked is it null or empty
     * @return boolean
     * @param <T> generic value of checked map
     */
    public static <T,U> boolean isNotEmpty(Map<T, U> map){
        return map != null && !map.isEmpty();
    }
}