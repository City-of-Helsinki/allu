package fi.hel.allu.common.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Utility for handling resources.
 */
public class ResourceUtil {

  /**
   * Returns the contents of the given file in classpath as UTF-8 string.
   *
   * @param resource Resource whose contents should be returned.
   * @return Contents of the given resource as UTF-8 string or null if the
   *         resource doesn't exits.
   * @throws IOException
   */
  static public String readClassPathResource(String resource) throws IOException {
    // Get file from resources folder
    StringBuilder result = new StringBuilder();
    try (InputStream stream = ResourceUtil.class.getResourceAsStream(resource)) {
      if (stream == null) {
        return null;
      }
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
      String line;

      while ((line = reader.readLine()) != null) {
        result.append(line).append('\n');
      }
    }
    return result.toString();
  }

  static public byte[] readClassPathResourceAsBytes(String resource) throws IOException {
    try (InputStream stream = ResourceUtil.class.getResourceAsStream(resource)) {
      if (stream == null) {
        return null;
      }
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      int nRead;
      byte[] data = new byte[1024];
      while ((nRead = stream.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, nRead);
      }

      buffer.flush();
      return buffer.toByteArray();
    }
  }
}
