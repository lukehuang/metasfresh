package org.compiere.acct;

import org.adempiere.model.InterfaceWrapperHelper;
import org.compiere.Adempiere;
import org.compiere.model.I_C_ProjectIssue;
import org.compiere.util.TimeUtil;

import de.metas.acct.api.AcctSchema;
import de.metas.acct.api.AcctSchemaId;
import de.metas.costing.CostAmount;
import de.metas.costing.CostDetailCreateRequest;
import de.metas.costing.CostDetailReverseRequest;
import de.metas.costing.CostingDocumentRef;
import de.metas.costing.CostingMethod;
import de.metas.costing.ICostingService;
import de.metas.quantity.Quantity;

/*
 * #%L
 * de.metas.acct.base
 * %%
 * Copyright (C) 2018 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

public class DocLine_ProjectIssue extends DocLine<Doc_ProjectIssue>
{

	public DocLine_ProjectIssue(I_C_ProjectIssue projectIssue, Doc_ProjectIssue doc)
	{
		super(InterfaceWrapperHelper.getPO(projectIssue), doc);
		setQty(Quantity.of(projectIssue.getMovementQty(), getProductStockingUOM()), true);
	}

	public CostAmount getCreateCosts(final AcctSchema as)
	{
		final ICostingService costDetailService = Adempiere.getBean(ICostingService.class);

		final AcctSchemaId acctSchemaId = as.getId();
		final CostingMethod costingMethod = as.getCosting().getCostingMethod();

		if (isReversalLine())
		{
			return costDetailService.createReversalCostDetails(CostDetailReverseRequest.builder()
					.acctSchemaId(acctSchemaId)
					.reversalDocumentRef(CostingDocumentRef.ofProjectIssueId(get_ID()))
					.initialDocumentRef(CostingDocumentRef.ofProjectIssueId(getReversalLine_ID()))
					.date(TimeUtil.asLocalDate(getDateDoc()))
					.build())
					.getTotalAmount(costingMethod);
		}
		else
		{
			return costDetailService.createCostDetail(
					CostDetailCreateRequest.builder()
							.acctSchemaId(acctSchemaId)
							.clientId(getClientId())
							.orgId(getOrgId())
							.productId(getProductId())
							.attributeSetInstanceId(getAttributeSetInstanceId())
							.documentRef(CostingDocumentRef.ofProjectIssueId(get_ID()))
							.qty(getQty())
							.amt(CostAmount.zero(as.getCurrencyId())) // N/A
							.date(TimeUtil.asLocalDate(getDateDoc()))
							.build())
					.getTotalAmount(costingMethod);
		}
	}
}
