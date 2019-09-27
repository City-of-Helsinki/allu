package fi.hel.allu.supervision.api.service;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.module.SimpleModule;
import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;
import fi.hel.allu.model.domain.Location;
import org.geolatte.geom.Geometry;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.PropertyAccessorFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fi.hel.allu.servicecore.domain.mapper.UpdatableProperty;

public abstract class ModelFieldUpdater {

  private ObjectMapper objectMapper;

  protected ModelFieldUpdater() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    SimpleModule module =
      new SimpleModule("CustomGeometrySerializer", new Version(1, 0, 0, null, null, null));
    module.addSerializer(Geometry.class, new GeometrySerializerProxy());
    module.addDeserializer(Geometry.class, new GeometryDeserializerProxy());
    objectMapper.registerModule(module);

    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  protected void updateObject(Map<String, Object> fields, Object targetObject) {
    if (fields.keySet().isEmpty()) {
      return;
    }
    try {
      validateFieldsUpdatable(fields.keySet(), targetObject);
      updateFields(fields, targetObject);
    } catch (IllegalArgumentException | IOException e) {
      throw new IllegalArgumentException("Failed to set value for field", e);
    }
  }

  private void validateFieldsUpdatable(Set<String> fields, Object targetObject) {
    fields.forEach(f -> validateIsUpdatable(getPropertyDescriptor(f, targetObject)));
  }

  protected PropertyDescriptor getPropertyDescriptor(String fieldName, Object targetObject) {
    try {
     return PropertyAccessorFactory.forBeanPropertyAccess(targetObject).getPropertyDescriptor(fieldName);
    } catch (InvalidPropertyException ex) {
      throw new IllegalArgumentException("Unknown field " + fieldName);
    }
  }

  private void validateIsUpdatable(PropertyDescriptor propertyDescriptor) {
    Method writeMethod = Optional.ofNullable(propertyDescriptor.getWriteMethod()).orElseThrow(() -> new IllegalArgumentException("Given field cannot be updated"));
    if (requireUpdatablePropertyAnnotation()) {
      Optional.ofNullable(writeMethod.getAnnotation(UpdatableProperty.class)).orElseThrow(() -> new IllegalArgumentException("Given field cannot be updated"));
    }
  }

  private void updateFields(Map<String, Object> fields, Object targetObject)
      throws JsonProcessingException, IOException {
    // the geolatte GeometryDeserializer does not support updating
    // via .readerForUpdating so we read and update the value manually
    if (fields.containsKey("geometry") && targetObject instanceof Location) {
      Location targetLocation = (Location) targetObject;
      Object geometryFields = fields.remove("geometry");
      JsonNode geometryNode = objectMapper.convertValue(geometryFields, JsonNode.class);
      ObjectReader geometryReader = objectMapper.readerFor(Geometry.class);
      Geometry geometry = geometryReader.readValue(geometryNode);
      targetLocation.setGeometry(geometry);
    }

    JsonNode node = objectMapper.convertValue(fields, JsonNode.class);
    ObjectReader readerForUpdating = objectMapper.readerForUpdating(targetObject);
    readerForUpdating.readValue(node);
  }

  protected boolean requireUpdatablePropertyAnnotation() {
    return true;
  }
}
