package fi.hel.allu.supervision.api.service;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    Optional.ofNullable(writeMethod.getAnnotation(UpdatableProperty.class)).orElseThrow(() -> new IllegalArgumentException("Given field cannot be updated"));
  }

  private void updateFields(Map<String, Object> fields, Object targetObject)
      throws JsonProcessingException, IOException {
    JsonNode node = objectMapper.convertValue(fields, JsonNode.class);
    ObjectReader readerForUpdating = objectMapper.readerForUpdating(targetObject);
    readerForUpdating.readValue(node);
  }
}
