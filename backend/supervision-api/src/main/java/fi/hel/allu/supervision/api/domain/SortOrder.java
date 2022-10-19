package fi.hel.allu.supervision.api.domain;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Sort.Direction;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Sort field and direction")
public class SortOrder<T> {

  @NotNull(message = "{sort.field}")
  T field;
  @NotNull(message = "{sort.direction}")
  Direction direction;

  @Schema(description = "Field to order for", required = true)
  public T getField() {
    return field;
  }

  public void setField(T field) {
    this.field = field;
  }

  @Schema(description = "Direction", required = true)
  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }
}
