package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

public interface CommentInterface {

    ZonedDateTime getCreateTime();
    String getText();
}