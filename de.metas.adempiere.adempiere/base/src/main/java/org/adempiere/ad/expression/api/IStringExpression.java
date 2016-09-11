package org.adempiere.ad.expression.api;

/*
 * #%L
 * de.metas.adempiere.adempiere.base
 * %%
 * Copyright (C) 2015 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

import java.util.List;

import org.adempiere.ad.expression.api.IExpressionEvaluator.OnVariableNotFound;
import org.adempiere.ad.expression.api.impl.StringExpressionCompiler;
import org.adempiere.ad.expression.exceptions.ExpressionEvaluationException;
import org.adempiere.ad.expression.json.JsonStringExpressionDeserializer;
import org.compiere.util.Evaluatee;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * String Expression
 * 
 * @author tsa
 * 
 */
@JsonDeserialize(using = JsonStringExpressionDeserializer.class)
public interface IStringExpression extends IExpression<String>
{
	/**
	 * Null String Expression Object
	 */
	IStringExpression NULL = NullStringExpression.instance;

	@Override
	default Class<String> getValueClass()
	{
		return String.class;
	}

	/**
	 * Gets internal string expression chunks. Don't use it directly, the API will use it only
	 * 
	 * @return
	 */
	List<Object> getExpressionChunks();

	@Override
	IExpressionEvaluator<IStringExpression, String> getEvaluator();

	/**
	 * Resolves all variables which available and returns a new string expression.
	 * 
	 * @param ctx
	 * @return string expression with all available variables resolved.
	 * @throws ExpressionEvaluationException
	 */
	default IStringExpression resolvePartial(final Evaluatee ctx) throws ExpressionEvaluationException
	{
		final String expressionStr = getEvaluator().evaluate(ctx, this, OnVariableNotFound.Preserve);
		return StringExpressionCompiler.instance.compile(expressionStr);
	}

}
