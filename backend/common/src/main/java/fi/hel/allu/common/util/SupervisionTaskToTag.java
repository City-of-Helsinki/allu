package fi.hel.allu.common.util;

import java.util.*;

import org.apache.commons.lang3.tuple.Pair;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;

import static fi.hel.allu.common.domain.types.ApplicationTagType.*;
import static fi.hel.allu.common.domain.types.SupervisionTaskStatusType.*;
import static fi.hel.allu.common.domain.types.SupervisionTaskType.*;

/**
 * Helper class for mapping supervision tasks to application tags
 */
public class SupervisionTaskToTag {
  private static final Map<Pair<SupervisionTaskType, SupervisionTaskStatusType>, ApplicationTagType> taskToTag = createMapping();

  public static Optional<ApplicationTagType> getBy(SupervisionTaskType type, SupervisionTaskStatusType status) {
    return Optional.ofNullable(taskToTag.get(Pair.of(type, status)));
  }

  /**
   * Method for getting application tag types which need to be removed when task of given type is deleted
   * @param taskType SupervisionTaskType which is deleted
   * @return List of application tag types which should be removed from application
   */
  public static List<ApplicationTagType> onTaskDeleteRemoveTags(SupervisionTaskType taskType) {
    switch (taskType) {
      case PRELIMINARY_SUPERVISION:
        return Arrays.asList(PRELIMINARY_SUPERVISION_REQUESTED, PRELIMINARY_SUPERVISION_REJECTED, PRELIMINARY_SUPERVISION_DONE);
      case SUPERVISION:
        return Arrays.asList(SUPERVISION_REQUESTED, SUPERVISION_REJECTED, SUPERVISION_DONE);
      default:
        return Collections.emptyList();
    }

  }

  private static Map<Pair<SupervisionTaskType, SupervisionTaskStatusType>, ApplicationTagType> createMapping() {
    Map<Pair<SupervisionTaskType, SupervisionTaskStatusType>, ApplicationTagType> map = new HashMap<>();
    map.put(Pair.of(PRELIMINARY_SUPERVISION, OPEN), PRELIMINARY_SUPERVISION_REQUESTED);
    map.put(Pair.of(PRELIMINARY_SUPERVISION, REJECTED), PRELIMINARY_SUPERVISION_REJECTED);
    map.put(Pair.of(PRELIMINARY_SUPERVISION, APPROVED), PRELIMINARY_SUPERVISION_DONE);

    map.put(Pair.of(SUPERVISION, OPEN), SUPERVISION_REQUESTED);
    map.put(Pair.of(SUPERVISION, REJECTED), SUPERVISION_REJECTED);
    map.put(Pair.of(SUPERVISION, APPROVED), SUPERVISION_DONE);

    return map;
  }
}
