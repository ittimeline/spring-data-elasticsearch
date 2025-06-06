/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.elasticsearch.repository.query;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.BaseQuery;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.data.elasticsearch.repository.support.QueryStringProcessor;
import org.springframework.data.repository.query.ValueExpressionDelegate;
import org.springframework.util.Assert;

/**
 * Was originally named ReactiveElasticsearchStringQuery.
 *
 * @author Christoph Strobl
 * @author Taylor Ono
 * @author Haibo Liu
 * @since 3.2
 */
public class ReactiveRepositoryStringQuery extends AbstractReactiveElasticsearchRepositoryQuery {

	private final String query;

	public ReactiveRepositoryStringQuery(ReactiveElasticsearchQueryMethod queryMethod,
			ReactiveElasticsearchOperations operations, ValueExpressionDelegate valueExpressionDelegate) {

		this(queryMethod.getAnnotatedQuery(), queryMethod, operations, valueExpressionDelegate);
	}

	public ReactiveRepositoryStringQuery(String query, ReactiveElasticsearchQueryMethod queryMethod,
			ReactiveElasticsearchOperations operations, ValueExpressionDelegate valueExpressionDelegate) {
		super(queryMethod, operations, valueExpressionDelegate.createValueContextProvider(queryMethod.getParameters()));

		Assert.notNull(query, "query must not be null");

		this.query = query;
	}

	@Override
	protected BaseQuery createQuery(ElasticsearchParametersParameterAccessor parameterAccessor) {
		ConversionService conversionService = getElasticsearchOperations().getElasticsearchConverter()
				.getConversionService();
		String processed = new QueryStringProcessor(query, queryMethod, conversionService, evaluationContextProvider)
				.createQuery(parameterAccessor);
		return new StringQuery(processed);
	}

	@Override
	boolean isCountQuery() {
		return queryMethod.hasCountQueryAnnotation();
	}

	@Override
	boolean isDeleteQuery() {
		return false;
	}

	@Override
	boolean isExistsQuery() {
		return false;
	}

	@Override
	boolean isLimiting() {
		return false;
	}
}
