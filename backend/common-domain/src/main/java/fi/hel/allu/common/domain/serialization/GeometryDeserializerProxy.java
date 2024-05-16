package fi.hel.allu.common.domain.serialization;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.geolatte.common.dataformats.json.jackson.GeometryDeserializer;
import org.geolatte.common.dataformats.json.jackson.JsonMapper;
import org.geolatte.geom.Geometry;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class GeometryDeserializerProxy extends JsonDeserializer<Geometry> {

  private final JsonMapper jsonMapper = new JsonMapper();
  private final GeometryDeserializer<Geometry> proxy = new GeometryDeserializer<>(jsonMapper, Geometry.class);

  @Override
  public Geometry deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    return proxy.deserialize(p, null);
  }

  private class JsonParserAdaptor extends JsonParser {

    private final JsonParser parser;

    public JsonParserAdaptor(JsonParser parser) {
      this.parser = parser;
    }

    @Override
    public <T> T readValueAs(Class<T> valueType) throws IOException {
      return parser.readValueAs(valueType);
    }

    // Other interface methods should not even be used:
    @Override
    public ObjectCodec getCodec() {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public void setCodec(ObjectCodec objectCodec) {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public Version version() {
      return null;
    }

    @Override
    public void close() throws IOException {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public JsonToken nextToken() throws IOException, JsonParseException {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public JsonToken nextValue() throws IOException {
      return null;
    }

    @Override
    public JsonParser skipChildren() throws IOException, JsonParseException {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public JsonToken getCurrentToken() {
      return null;
    }

    @Override
    public int getCurrentTokenId() {
      return 0;
    }

    @Override
    public boolean hasCurrentToken() {
      return false;
    }

    @Override
    public boolean hasTokenId(int i) {
      return false;
    }

    @Override
    public boolean hasToken(JsonToken jsonToken) {
      return false;
    }

    @Override
    public void clearCurrentToken() {

    }

    @Override
    public JsonToken getLastClearedToken() {
      return null;
    }

    @Override
    public void overrideCurrentName(String s) {

    }

    @Override
    public boolean isClosed() {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public String getCurrentName() throws IOException, JsonParseException {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public JsonStreamContext getParsingContext() {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public JsonLocation getTokenLocation() {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public JsonLocation getCurrentLocation() {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public String getText() throws IOException, JsonParseException {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public char[] getTextCharacters() throws IOException, JsonParseException {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public int getTextLength() throws IOException, JsonParseException {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public int getTextOffset() throws IOException, JsonParseException {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public boolean hasTextCharacters() {
      return false;
    }

    @Override
    public Number getNumberValue() throws IOException, JsonParseException {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public NumberType getNumberType() throws IOException, JsonParseException {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public int getIntValue() throws IOException, JsonParseException {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public long getLongValue() throws IOException, JsonParseException {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public BigInteger getBigIntegerValue() throws IOException, JsonParseException {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public float getFloatValue() throws IOException, JsonParseException {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public double getDoubleValue() throws IOException, JsonParseException {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public BigDecimal getDecimalValue() throws IOException, JsonParseException {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public byte[] getBinaryValue(Base64Variant b64variant) throws IOException, JsonParseException {
      throw new UnsupportedOperationException("Not implemented in the adapter class");
    }

    @Override
    public String getValueAsString(String s) throws IOException {
      return null;
    }

  }
}