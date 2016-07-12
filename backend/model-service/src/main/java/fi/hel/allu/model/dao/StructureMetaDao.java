package fi.hel.allu.model.dao;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.meta.AttributeMeta;
import fi.hel.allu.model.domain.meta.StructureMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QAttributeMeta.attributeMeta;
import static fi.hel.allu.QStructureMeta.structureMeta;

/**
 * Data access object for metadata structures.
 */
@Repository
public class StructureMetaDao {

  private static final Logger logger = LoggerFactory.getLogger(StructureMetaDao.class);

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<StructureMeta> structureMetaBean = bean(StructureMeta.class, structureMeta.all());
  final QBean<AttributeMeta> attributeMetaBean = bean(AttributeMeta.class, attributeMeta.all());

  /**
   * Returns the latest metadata related to given application type.
   *
   * @param   applicationType   Application type whose metadata is requested.
   * @return  the latest metadata related to given application type.
   */
  @Transactional
  public Optional<StructureMeta> findByApplicationType(String applicationType) {
    logger.debug("applicationType: {}", applicationType);
    SQLQuery<StructureMeta> sQuery = queryFactory
        .select(structureMetaBean)
        .from(structureMeta)
        .where((structureMeta.applicationType.eq(applicationType)))
        .groupBy(structureMeta.id, structureMeta.version)
        .having(structureMeta.version.eq(structureMeta.version.max()));

    logger.debug(String.format("Executing query \"%s\"", sQuery.getSQL().getSQL()));
    StructureMeta sMeta = sQuery.fetchOne();
    if (sMeta != null) {
      sMeta.setAttributes(resolveAttributes(sMeta));
    }
    return Optional.ofNullable(sMeta);
  }

  /**
   * Returns the metadata related to given application type with given version.
   *
   * @param   applicationType   Application type whose metadata is requested.
   * @return  the latest metadata related to given application type.
   */
  @Transactional
  public Optional<StructureMeta> findByApplicationType(
      String applicationType,
      int version) {
    logger.debug("applicationType: {}", applicationType);
    SQLQuery<StructureMeta> sQuery = queryFactory
        .select(structureMetaBean)
        .from(structureMeta)
        .where((structureMeta.applicationType.eq(applicationType)).and(structureMeta.version.eq(version)));

    logger.debug(String.format("Executing query \"%s\"", sQuery.getSQL().getSQL()));
    StructureMeta sMeta = sQuery.fetchOne();
    if (sMeta != null) {
      sMeta.setAttributes(resolveAttributes(sMeta));
    }
    return Optional.ofNullable(sMeta);
  }

  /**
   * Returns metadata with given id.
   *
   * @param   id    The id of metadata structure requested.
   * @return  metadata with given id.
   */
  @Transactional
  public Optional<StructureMeta> findByid(int id) {
    SQLQuery<StructureMeta> sQuery = queryFactory.select(structureMetaBean).from(structureMeta).where(structureMeta.id.eq(id));
    logger.debug(String.format("Executing query \"%s\"", sQuery.getSQL().getSQL()));
    StructureMeta sMeta = sQuery.fetchOne();
    if (sMeta != null) {
      sMeta.setAttributes(resolveAttributes(sMeta));
    }
    return Optional.ofNullable(sMeta);
  }

  /**
   * Resolve attributes of given structure.
   */
  private List<AttributeMeta> resolveAttributes(StructureMeta structureMeta) {
    SQLQuery<AttributeMeta> aQuery =
        queryFactory.select(attributeMetaBean).from(attributeMeta).where(attributeMeta.structure.eq(structureMeta.getId()));
    logger.debug(String.format("Executing query \"%s\"", aQuery.getSQL().getSQL()));
    List<AttributeMeta> aMetas = aQuery.fetch();

    if (aMetas == null) {
      aMetas = new ArrayList<>();
    } else {
      List<AttributeMeta> structureAttributes =
          aMetas.stream().filter(am -> am.getStructureAttribute() != null).collect(Collectors.toList());
      structureAttributes.forEach(am -> am.setStructureMeta(findByid(am.getStructureAttribute())
          .orElseThrow(() -> new NoSuchEntityException(
              "Attribute metadata with structure " + am.getName() + " is missing related structure metadata",
              am.getStructureAttribute().toString()))));
    }

    return aMetas;
  }
}
