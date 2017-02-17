package fi.hel.allu.ui.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.*;

/**
 * Class for comparing two objects.
 */
public class ObjectComparer {

  /**
   * Represents a single difference between two objects
   */
  public class Difference {
    public String keyName;
    public String oldValue;
    public String newValue;

    public Difference(String keyName, String oldValue, String newValue) {
      this.keyName = keyName;
      this.oldValue = oldValue;
      this.newValue = newValue;
    }
  }

  private static final String SLASH = "/";

  /**
   * Compare two objects and return their differences
   *
   * @param oldObject
   *          the "old" data
   * @param newObject
   *          the "new" data
   * @return list of changes
   */
  public List<Difference> compare(Object oldObject, Object newObject) {
    SortedMap<String, String> oldContents = flatten(oldObject);
    SortedMap<String, String> newContents = flatten(newObject);
    return compareContents(oldContents, newContents);
  }

  /*
   * Compare the contents of two sorted maps, return a list of differences.
   * Works by going through the maps in one pass, so executes in O(n). Looks
   * more complicated than is, thanks to Java iterators that don't have separate
   * "read" and "advance" operations.
   */
  private List<Difference> compareContents(SortedMap<String, String> oldContents,
      SortedMap<String, String> newContents) {
    List<Difference> difference = new ArrayList<>();

    Iterator<Map.Entry<String, String>> iterOld = oldContents.entrySet().iterator();
    Iterator<Map.Entry<String, String>> iterNew = newContents.entrySet().iterator();

    if (iterOld.hasNext() && iterNew.hasNext()) {
      Map.Entry<String, String> entryOld = iterOld.next();
      Map.Entry<String, String> entryNew = iterNew.next();
      // Loop until either map ends:
      while (true) {
        if (entryOld.getKey().compareTo(entryNew.getKey()) == 0) {
          // Same key. Did the value change?
          if (!entryOld.getValue().equals(entryNew.getValue())) {
            difference.add(new Difference(entryOld.getKey(), entryOld.getValue(), entryNew.getValue()));
          }
          if (!iterOld.hasNext() || !iterNew.hasNext())
            break;
          entryOld = iterOld.next();
          entryNew = iterNew.next();
        } else if (entryOld.getKey().compareTo(entryNew.getKey()) < 0) {
          // Key missing from newContents -> removed.
          difference.add(new Difference(entryOld.getKey(), entryOld.getValue(), null));
          if (!iterOld.hasNext())
            break;
          entryOld = iterOld.next();
        } else {
          // Key missing from oldContents -> added
          difference.add(new Difference(entryNew.getKey(), null, entryNew.getValue()));
          if (!iterNew.hasNext())
            break;
          entryNew = iterNew.next();
        }
      }
    }
    // End of oldContents or newContents was reached. Handle the possible tails:
    while (iterOld.hasNext()) {
      Map.Entry<String, String> entryOld = iterOld.next();
      difference.add(new Difference(entryOld.getKey(), entryOld.getValue(), null));
    }
    while (iterNew.hasNext()) {
      Map.Entry<String, String> entryNew = iterNew.next();
      difference.add(new Difference(entryNew.getKey(), null, entryNew.getValue()));
    }
    return difference;
  }

  /*
   * Flatten an object into (a sorted list of) keys and values.
   */
  private SortedMap<String, String> flatten(Object object) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    JsonNode rootNode = mapper.valueToTree(object);
    return flattenRecurse(mapper, rootNode, SLASH, new TreeMap<String, String>());
  }

  /*
   * Recursively flatten an object.
   */
  private SortedMap<String, String> flattenRecurse(ObjectMapper mapper, JsonNode node, String prefix,
      SortedMap<String, String> results) {
    for (Iterator<Map.Entry<String, JsonNode>> iter = node.fields(); iter.hasNext();) {
      Map.Entry<String, JsonNode> field = iter.next();
      String name = prefix + field.getKey();
      JsonNode value = field.getValue();
      // Special case: handle empty arrays as null nodes => avoids extra history
      // lines
      if (value.isArray()) {
        ArrayNode array = (ArrayNode) value;
        if (array.size() == 0) {
          value = NullNode.getInstance();
        }
      }
      // Similar special case for empty strings
      if (value.isTextual()) {
        TextNode text = (TextNode) value;
        if (text.textValue().isEmpty()) {
          value = NullNode.getInstance();
        }
      }
      if (value.isObject()) {
        flattenRecurse(mapper, value, name + SLASH, results);
      } else {
        results.put(name, stringify(mapper, value));
      }
    }
    return results;
  }

  private String stringify(ObjectMapper mapper, JsonNode value) {
    if (value.isContainerNode())
      return value.toString();
    try {
      return mapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("writeValueAsString failed", e);
    }
  }
}
