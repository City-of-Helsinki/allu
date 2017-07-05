package fi.hel.allu.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Utility for handling resources.
 */
public class ResourceUtil {

  /**
   * Returns the contents of the given file in classpath as UTF-8 string.
   *
   * @param resource  Resource whose contents should be returned.
   * @return  Contents of the given resource as UTF-8 string.
   * @throws IOException
   */
  static public String readClassPathResource(String resource) throws IOException {
    // Get file from resources folder
    StringBuilder result = new StringBuilder();
    try (InputStream stream = ResourceUtil.class.getResourceAsStream(resource)) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
      String line;

      while ((line = reader.readLine()) != null) {
        result.append(line).append('\n');
      }
    }
    return result.toString();
  }
}
