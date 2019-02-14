package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;

import fi.hel.allu.common.domain.types.SurfaceHardness;

/*
 * Tells QueryDSL how to map enum type to SQL
 */
public class StringToSurfaceHardness extends EnumAsObjectType<SurfaceHardness> {

  public StringToSurfaceHardness() {
    super(SurfaceHardness.class);
  }
}
