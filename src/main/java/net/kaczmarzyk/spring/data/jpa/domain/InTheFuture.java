/**
 * Copyright 2014-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kaczmarzyk.spring.data.jpa.domain;

import net.kaczmarzyk.spring.data.jpa.utils.Converter;
import net.kaczmarzyk.spring.data.jpa.utils.QueryContext;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class InTheFuture<T> extends PathSpecification<T> implements ZeroArgSpecification {

	private static final long serialVersionUID = 1L;

	private final Converter converter;

	public InTheFuture(QueryContext queryContext, String path, String[] args, Converter converter) {
		super(queryContext, path);
		this.converter = converter;
	}

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
		Expression<? extends Comparable> rootPath = path(root);
		Class<?> typeOnPath = rootPath.getJavaType();

		String time;
		if (isLocalZoneClassType(typeOnPath)) {
			//as OffsetDateTime.now() queries system clock with its default timezone
			time = OffsetDateTime.now().format(formatterForClassType(typeOnPath));
		} else if (isUtcZoneClassType(typeOnPath)) {
			DateTimeFormatter formatter = formatterForClassType(typeOnPath).withZone(ZoneOffset.UTC);
			//as Instant.now() queries system UTC clock
			time = formatter.format(Instant.now());
		} else {
			throw new IllegalArgumentException("This class type isn't suitable for this predicate");
		}

		criteriaBuilder.currentTimestamp();

		Comparable<Object> now = (Comparable<Object>) converter.convert(time, typeOnPath);

		return criteriaBuilder.greaterThan(rootPath, now);
	}

	private boolean isLocalZoneClassType(Class<?> typeOnPath) {
		return typeOnPath.isAssignableFrom(LocalDate.class)
				|| typeOnPath.isAssignableFrom(LocalDateTime.class)
				|| typeOnPath.isAssignableFrom(OffsetDateTime.class);
	}

	private boolean isUtcZoneClassType(Class<?> typeOnPath) {
		return typeOnPath.isAssignableFrom(Instant.class)
				|| typeOnPath.isAssignableFrom(Date.class)
				|| typeOnPath.isAssignableFrom(Timestamp.class)
				|| typeOnPath.isAssignableFrom(Calendar.class);
	}

	private DateTimeFormatter formatterForClassType(Class<?> typeOnPath) {
		return DateTimeFormatter.ofPattern(converter.getDateFormat(typeOnPath));
	}

}
