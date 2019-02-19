package fi.hel.allu.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

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
  private static final String ID = "id";
  private ObjectMapper mapper;

  public ObjectComparer() {
    mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  /**
   * Adds a mixin to mapper.
   * Can be used to modify field serialization to ignore some fields for example
   * @param target target class which is modified
   * @param mixinSource mixin which is used to modify target
   */
  public void addMixin(Class<?> target, Class<?> mixinSource) {
    this.mapper.addMixIn(target, mixinSource);
  }

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
    JsonNode source;
    JsonNode target;
    try {
      String sourceJson = mapper.writeValueAsString(oldObject);
      String targetJson = mapper.writeValueAsString(newObject);
      source = mapper.readTree(sourceJson);
      target = mapper.readTree(targetJson);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    List<Difference> diff = new ArrayList<>();
    doCompare("", source, target, diff);
    return diff;
  }

  /*
   * The workhorse of the comparison. Recursively compare source and target
   * objects and store their differences into diff.
   */
  private void doCompare(String prefix, JsonNode source, JsonNode target, List<Difference> diff) {
    if (isSimple(source) && isSimple(target)) {
      compareSimple(prefix, source, target, diff);
    } else if (isObject(source) && isObject(target)) {
      compareObject(prefix, source, target, diff);
    } else if (isArray(source) && isArray(target)) {
      compareArray(prefix, source, target, diff);
    } else {
      markChanged(prefix, "", "", diff);
    }
  }

  /*
   * Does the given node count as a null node?
   */
  private boolean isNull(JsonNode node) {
    return node == null || node.isNull();
  }

  /*
   * Does the given node count as a simple value node?
   */
  private boolean isSimple(JsonNode node) {
    return isNull(node) || node.isValueNode();
  }

  /*
   * Does the given node count as an object node?
   */
  private boolean isObject(JsonNode node) {
    return isNull(node) || node.isObject();
  }

  /*
   * Does the given node count as an array node?
   */
  private boolean isArray(JsonNode node) {
    return isNull(node) || node.isArray();
  }

  /*
   * Comparison for a simple node. If both are null nodes, nothing has changed.
   * If only one is a null node, key was added or removed. Otherwise, compare
   * values to see if something changed.
   */
  private void compareSimple(String keyName, JsonNode source, JsonNode target, List<Difference> diff) {
    if (isNull(source) && isNull(target)) {
      return;
    }
    if (isNull(source)) {
      markAdded(keyName, target.asText(), diff);
    } else if (isNull(target)) {
      markRemoved(keyName, source.asText(), diff);
    } else {
      String sourceText = source.asText();
      String targetText = target.asText();
      if (!sourceText.equals(targetText)) {
        markChanged(keyName, sourceText, targetText, diff);
      }
    }
  }

  /*
   * Comparison for object nodes. If both are null or empty, nothing has
   * changed. If only on is a null node, the whole object was added or removed.
   * Otherwise, compare values for every key.
   */
  private void compareObject(String keyName, JsonNode source, JsonNode target, List<Difference> diff) {
    if ((isNull(source) || source.size() == 0) && (isNull(target) || target.size() == 0)) {
      return;
    }
    if (isNull(source) || source.size() == 0) {
      wasAdded(keyName, target, diff);
    } else if (isNull(target) || target.size() == 0) {
      wasRemoved(keyName, source, diff);
    } else {
      Map<String, JsonNode> sourceMap = new HashMap<>();
      Map<String, JsonNode> targetMap = new HashMap<>();
      source.fields().forEachRemaining(entry -> sourceMap.put(entry.getKey(), entry.getValue()));
      target.fields().forEachRemaining(entry -> targetMap.put(entry.getKey(), entry.getValue()));
      compareMaps(keyName, sourceMap, targetMap, diff);
    }
  }

  /*
   * Comparison for key-value maps.
   */
  private void compareMaps(String keyName, Map<String, JsonNode> source, Map<String, JsonNode> target,
      List<Difference> diff) {
    source.forEach((key, srcNode) -> {
      JsonNode targetNode = target.get(key);
      if (targetNode != null) {
        doCompare(keyName + SLASH + key, srcNode, targetNode, diff);
        target.remove(key);
      } else {
        wasRemoved(keyName + SLASH + key, srcNode, diff);
      }
    } );
    // Anything still left in the target was not in source, so it was added:;
    target.forEach((key, targetNode) -> wasAdded(keyName + SLASH + key, targetNode, diff));
  }

  /*
   * Comparison for arrays.
   */
  private void compareArray(String keyName, JsonNode source, JsonNode target, List<Difference> diff) {
    if ((isNull(source) || source.size() == 0) && (isNull(target) || target.size() == 0)) {
      return; // both are empty or null
    }
    if (isNull(source) || source.size() == 0) {
      wasAdded(keyName, target, diff);
    }
    else if (isNull(target) || target.size() == 0) {
      wasRemoved(keyName, source, diff);
    }
    else if (source.get(0).has(ID) && target.get(0).has(ID)) {
      // compare by id
      Map<String, JsonNode> sourceMap = new HashMap<>();
      Map<String, JsonNode> targetMap = new HashMap<>();
      source.forEach(e -> safePut(sourceMap, e.get(ID).asText(), e));
      target.forEach(e -> safePut(targetMap, e.get(ID).asText(), e));
      compareMaps(keyName, sourceMap, targetMap, diff);
    } else {
      // compare by index
      int numCommon = Math.min(source.size(), target.size());
      for (int i = 0; i < numCommon; ++i) {
        doCompare(keyName + SLASH + i, source.get(i), target.get(i), diff);
      }
      // Only one of for loops gets executed:
      for (int i = numCommon; i < source.size(); ++i) {
        wasRemoved(keyName + SLASH + i, source.get(i), diff);
      }
      for (int i = numCommon; i < target.size(); ++i) {
        wasAdded(keyName + SLASH + i, target.get(i), diff);
      }
    }
  }

  /*
   * Add (key,value) to map but throw if key was already there
   */
  <K, V> void safePut(Map<K, V> map, K key, V value) {
    if (map.put(key, value) != null) {
      throw new IllegalArgumentException("The key " + key.toString() + " was added twice!");
    }
  }

  /*
   * Special foreach for JSON arrays. If array elements have ID, gives it to
   * consumer. Otherwise gives just element's index.
   */
  private void arrayForeach(JsonNode array, BiConsumer<String, JsonNode> consumer) {
    if (array.size() == 0)
      return;
    if (array.get(0).has(ID)) {
      array.elements().forEachRemaining(e -> consumer.accept(e.get(ID).asText(), e));
    } else {
      for (int i = 0; i < array.size(); ++i) {
        consumer.accept(String.valueOf(i), array.get(i));
      }
    }
  }

  /*
   * Recursively handle the addition of a JSON node.
   */
  private void wasAdded(String prefix, JsonNode node, List<Difference> diff) {
    if (isNull(node))
      return;
    if (node.isValueNode()) {
      markAdded(prefix, node.asText(), diff);
    } else if (node.isObject()) {
      node.fields().forEachRemaining(entry -> wasAdded(prefix + SLASH + entry.getKey(), entry.getValue(), diff));
    } else if (node.isArray()) {
      arrayForeach(node, (key, value) -> wasAdded(prefix + SLASH + key, value, diff));
    }
  }

  /*
   * Recursively handle the removal of a JSON node.
   */
  private void wasRemoved(String prefix, JsonNode node, List<Difference> diff) {
    if (isNull(node))
      return;
    if (node.isValueNode()) {
      markRemoved(prefix, node.asText(), diff);
    } else if (node.isObject()) {
      node.fields().forEachRemaining(entry -> wasRemoved(prefix + SLASH + entry.getKey(), entry.getValue(), diff));
    } else if (node.isArray()) {
      arrayForeach(node, (key, value) -> wasRemoved(prefix + SLASH + key, value, diff));
    }
  }

  /*
   * Mark the given key as changed
   */
  private void markChanged(String key, String oldValue, String newValue, List<Difference> diff) {
    diff.add(new Difference(key, oldValue, newValue));
  }

  /*
   * Mark the given key as removed
   */
  private void markRemoved(String key, String oldValue, List<Difference> diff) {
    if (!oldValue.isEmpty()) {
      diff.add(new Difference(key, oldValue, ""));
    }
  }

  /*
   * Mark the given key as added
   */
  private void markAdded(String key, String newValue, List<Difference> diff) {
    if (!newValue.isEmpty()) {
      diff.add(new Difference(key, "", newValue));
    }
  }

}
