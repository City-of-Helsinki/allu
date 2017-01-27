package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;

import fi.hel.allu.common.types.CommentType;

/*
 * Tells QueryDSL how to map CommentType enum to SQL
 */
public class StringToCommentType extends EnumAsObjectType<CommentType> {

  public StringToCommentType() {
    super(CommentType.class);
  }

}
