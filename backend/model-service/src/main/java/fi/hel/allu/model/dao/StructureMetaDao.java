package fi.hel.allu.model.dao;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.meta.AttributeDataType;
import fi.hel.allu.model.domain.meta.AttributeMeta;
import fi.hel.allu.model.domain.meta.StructureMeta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
   * Parse the complete metadata tree for an application type. Recurses into
   * each STRUCTURE type attribute it can find and puts all their attributes
   * into the result set, too. All found attributes are returned as a flat list,
   * so to prevent name clashes, the attribute names consist also of the path
   * into the attribute, for example "/applicant/postalAddress/streetAddress".
   *
   * The recursion can be guided by giving a set of path overrides that can used
   * to, for example, recurse into "EVENT" while handling the attribute
   * "/extension", instead of simply going into the statically defined
   * "ApplicationExtension" type.
   *
   *
   * @param applicationType
   *          The name of the application type to parse
   * @param version
   *          Metadata version to use
   * @param pathOverrides
   *          Mapping of attributePath -> typeName to override the static types
   * @return Structure meta information
   */
  @Transactional
  public Optional<StructureMeta> findCompleteByApplicationType(String applicationType, int version,
      Map<String, String> typeOverrides) {
    StructureMeta meta = queryFactory.select(structureMetaBean).from(structureMeta)
        .where(structureMeta.applicationType.eq(applicationType).and(structureMeta.version.eq(version))).fetchOne();
    if (meta==null) {
      return Optional.empty();
    }
    List<AttributeMeta> attributes = new ArrayList<>();
    recurseAttributes(meta.getId(), typeOverrides, "/", version, attributes);
    meta.setAttributes(attributes);
    return Optional.of(meta);
  }

  /**
   * Get the latest metadata version
   *
   * @return Latest metadata version
   */
  @Transactional(readOnly = true)
  public Integer getLatestMetadataVersion() {
    return queryFactory.select(SQLExpressions.max(structureMeta.version)).from(structureMeta).fetchOne();
  }

  /**
   * Parse the complete metadata tree for an application type. Recurses into
   * each STRUCTURE type attribute it can find and puts all their attributes
   * into the result set, too. All found attributes are returned as a flat list,
   * so to prevent name clashes, the attribute names consist also of the path
   * into the attribute, for example "/applicant/postalAddress/streetAddress".
   *
   * The recursion can be guided by giving a set of path overrides that can used
   * to, for example, recurse into "EVENT" while handling the attribute
   * "/extension", instead of simply going into the statically defined
   * "ApplicationExtension" type.
   *
   * This version uses the latest available version of the application type.
   *
   * @param applicationType
   *          The name of the application type to parse
   * @param pathOverrides
   *          Mapping of attributePath -> typeName to override the static types
   * @return Structure meta information
   */
  @Transactional
  public Optional<StructureMeta> findCompleteByApplicationType(String applicationType,
      Map<String, String> typeOverrides) {
    int latestVersion = queryFactory.select(SQLExpressions.max(structureMeta.version)).from(structureMeta)
        .where(structureMeta.applicationType.eq(applicationType)).fetchOne();
    return findCompleteByApplicationType(applicationType, latestVersion, typeOverrides);
  }

  /*
   * Recursively fetch all attributes for given structureType. Every attribute
   * name will be prefixed with given prefix when added into attributeList. If
   * an attribute type is STRUCTURE, instead of adding it into attributeList,
   * recursively add all its attributes. In recursion, if the attributes path is
   * found in typeOverrides, use the corresponding value as the recursion type.
   */
  private void recurseAttributes(int structureTypeId, Map<String, String> typeOverrides, String prefix,
      int metadataVersion, List<AttributeMeta> attributeList) {
    List<AttributeMeta> attributes = queryFactory.select(attributeMetaBean).from(attributeMeta)
        .where(attributeMeta.structure.eq(structureTypeId)).fetch();
    for (AttributeMeta attribute : attributes) {
      attribute.setName(prefix + attribute.getName());
      if (attribute.getDataType() != AttributeDataType.STRUCTURE) {
        attributeList.add(attribute);
      } else {
        // Structure, so recurse
        int childStructureId = attribute.getStructureAttribute();
        String childStructureName = attribute.getName();
        if (typeOverrides.containsKey(childStructureName)) {
          // Type override defined, find its type id
          String overrideType = typeOverrides.get(attribute.getName());
          childStructureId = Optional.ofNullable(queryFactory
              .select(structureMeta.id).from(structureMeta).where(structureMeta.applicationType
                  .eq(overrideType).and(structureMeta.version.eq(metadataVersion)))
              .fetchOne()).orElseThrow(() -> new NoSuchEntityException("No metadata for type", overrideType));
        }
        recurseAttributes(childStructureId, typeOverrides, childStructureName + "/", metadataVersion, attributeList);
      }
    }
  }

  /**
   * Returns metadata with given id.
   *
   * @param id
   *          The id of metadata structure requested.
   * @return metadata with given id.
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
