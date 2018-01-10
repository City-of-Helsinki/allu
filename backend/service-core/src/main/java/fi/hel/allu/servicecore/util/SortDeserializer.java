package fi.hel.allu.servicecore.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Custom deserializer for Spring Data's Sort
 */
public class SortDeserializer extends JsonDeserializer<Sort> {

  @Override
  public Sort deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    List<Sort.Order> orders = new ArrayList<>();
    ObjectCodec codec = p.getCodec();
    ArrayNode arrayNode = codec.<ArrayNode> readTree(p);
    for (JsonNode node : arrayNode) {
      String property = Optional.ofNullable(node.get("property")).map(n -> n.textValue()).orElse(null);
      Sort.Direction direction = Optional.ofNullable(node.get("direction"))
          .map(n -> Sort.Direction.fromStringOrNull(n.textValue())).orElse(null);
      Sort.Order order = new Sort.Order(direction, property);
      orders.add(order);
    }
    return new Sort(orders);
  }
}
