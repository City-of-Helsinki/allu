package fi.hel.allu.supervision.api.domain;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Sort.Direction;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Sort field and direction")
public class SortOrder<T> {

  @NotNull(message = "{sort.field}")
  T field;
  @NotNull(message = "{sort.direction}")
  Direction direction;

  @ApiModelProperty(value = "Field to order for", required = true)
  public T getField() {
    return field;
  }

  public void setField(T field) {
    this.field = field;
  }

  @ApiModelProperty(value = "Direction", required = true)
  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }
}
