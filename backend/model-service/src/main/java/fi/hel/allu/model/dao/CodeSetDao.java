package fi.hel.allu.model.dao;

import static com.querydsl.core.types.Projections.bean;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import static fi.hel.allu.QCodeset.codeset;
import fi.hel.allu.common.domain.types.CodeSetType;
import fi.hel.allu.model.domain.CodeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class CodeSetDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<CodeSet> codeSetBean = bean(CodeSet.class, codeset.all());

  @Transactional(readOnly = true)
  public Optional<CodeSet> findById(int codeSetId) {
    final CodeSet codeSet = queryFactory.select(codeSetBean).from(codeset)
        .where(codeset.id.eq(codeSetId)).fetchOne();
    return Optional.ofNullable(codeSet);
  }

  @Transactional(readOnly = true)
  public List<CodeSet> findByType(CodeSetType type) {
    return queryFactory.select(codeSetBean).from(codeset).where(codeset.type.eq(type)).fetch();
  }

  @Transactional(readOnly = true)
  public Optional<CodeSet> findByTypeAndCode(CodeSetType type, String code) {
    final CodeSet codeSet = queryFactory.select(codeSetBean).from(codeset)
        .where(codeset.type.eq(type).and(codeset.code.eq(code))).fetchOne();
    return Optional.ofNullable(codeSet);
  }

  @Transactional
  public CodeSet insert(CodeSet codeSet) {
    final int id = queryFactory.insert(codeset).populate(codeSet).executeWithKey(codeset.id);
    return findById(id).get();
  }
}
