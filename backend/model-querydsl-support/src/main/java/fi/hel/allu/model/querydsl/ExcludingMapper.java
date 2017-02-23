package fi.hel.allu.model.querydsl;

/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.common.collect.Maps;
import com.querydsl.core.QueryException;
import com.querydsl.core.types.Path;
import com.querydsl.core.util.ReflectionUtils;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.dml.AbstractMapper;
import com.querydsl.sql.types.Null;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Creates mapping in the same way as QueryDSL <code>DefaultMapper</code>, but excludes given set of columns in order to allow updates
 * to tables consisting of columns that are "read-only" (or at least not writable in the given situation).
 *
 * The functionality in this class has been copied from QueryDSL class com.querydsl.sql.dml.DefaultMapper.
 */
public class ExcludingMapper extends AbstractMapper<Object> {

  public enum NullHandling {
    DEFAULT,
    WITH_NULL_BINDINGS
  };

  private final boolean withNullBindings;
  private final Set<Path<?>> excludedPaths;

  public ExcludingMapper() {
    this(NullHandling.DEFAULT, Collections.emptySet());
  }

  /**
   * Constructor.
   *
   * @param nullHandling    Use <code>DEFAULT</code> for not mapping null values to database and <code>WITH_NULL_BINDINGS</code> for
   *                        including nulls.
   * @param excludedPaths   <code>Path</code>s to be excluded from the mapping.
   */
  public ExcludingMapper(NullHandling nullHandling, Collection<Path<?>> excludedPaths) {
    this.withNullBindings = nullHandling == NullHandling.DEFAULT ? false : true;
    this.excludedPaths = new HashSet<>(excludedPaths);
  }

  @Override
  public Map<Path<?>, Object> createMap(RelationalPath<?> entity, Object bean) {
    try {
      Map<Path<?>, Object> values = Maps.newLinkedHashMap();
      Class<?> beanClass = bean.getClass();
      Map<String, Path<?>> columns = getColumns(entity);
      // populate in column order
      for (Map.Entry<String, Path<?>> entry : columns.entrySet()) {
        Path<?> path = entry.getValue();
        if (excludedPaths.contains(path)) {
          continue;
        }
        Field beanField = ReflectionUtils.getFieldOrNull(beanClass, entry.getKey());
        if (beanField != null && !Modifier.isStatic(beanField.getModifiers())) {
          beanField.setAccessible(true);
          Object propertyValue = beanField.get(bean);
          if (propertyValue != null) {
            values.put(path, propertyValue);
          } else if (withNullBindings && !isPrimaryKeyColumn(entity, path)) {
            values.put(path, Null.DEFAULT);
          }
        }
      }
      return values;
    } catch (IllegalAccessException e) {
      throw new QueryException(e);
    }
  }



}
