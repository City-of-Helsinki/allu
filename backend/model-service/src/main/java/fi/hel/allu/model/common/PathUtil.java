package fi.hel.allu.model.common;

import com.querydsl.core.types.Path;

public class PathUtil {
  /**
   * Converts given path to period separated string as table.field
   * @param path contains path specification for table field
   * @return table.field as string
   */
  public static String pathNameWithParent(Path path) {
    Path parent = path.getMetadata().getParent();
    return String.join(".", pathName(parent), pathName(path));
  }

  /**
   * Converts given path to string as table field
   * @param path contains path specification for table field
   * @return table field as string
   */
  public static String pathName(Path path) {
    return path.getMetadata().getName();
  }
}
