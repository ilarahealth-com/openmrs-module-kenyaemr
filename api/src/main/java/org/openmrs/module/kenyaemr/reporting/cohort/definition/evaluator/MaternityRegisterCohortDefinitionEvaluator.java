/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.MaternityRegisterCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Evaluator for Maternity
 */
@Handler(supports = {MaternityRegisterCohortDefinition.class})
public class MaternityRegisterCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());
	@Autowired
	EvaluationService evaluationService;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		MaternityRegisterCohortDefinition definition = (MaternityRegisterCohortDefinition) cohortDefinition;

        if (definition == null)
            return null;

		Cohort newCohort = new Cohort();

		context = ObjectUtil.nvl(context, new EvaluationContext());

		Date startDate = (Date)context.getParameterValue("startDate");
		Date endDate = (Date)context.getParameterValue("endDate");
		String facilityOptions = (String) context.getParameterValue("facility");

		SqlQueryBuilder builder = new SqlQueryBuilder();
		String qry = "";
		Integer reportFacilityId = null;
		if (StringUtils.isNotBlank(facilityOptions) && facilityOptions.equalsIgnoreCase("All")) {
			qry = "SELECT ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld inner join kenyaemr_etl.etl_mch_enrollment e\n" +
					" on e.patient_id = ld.patient_id where e.visit_date <= ld.visit_date\n" +
					" and e.date_of_discontinuation is  null and date(ld.visit_date)\n" +
					"BETWEEN date(:startDate) AND date(:endDate);";
		} else {
			reportFacilityId = Integer.valueOf(facilityOptions);
			qry = "SELECT ld.patient_id from kenyaemr_etl.etl_mchs_delivery ld inner join kenyaemr_etl.etl_mch_enrollment e\n" +
					" on e.patient_id = ld.patient_id " +
					" where ld.location_id in (:facilityList) and e.visit_date <= ld.visit_date\n" +
					" and e.date_of_discontinuation is  null and date(ld.visit_date)\n" +
					"BETWEEN date(:startDate) AND date(:endDate);";
		}

		builder.append(qry);
		builder.addParameter("endDate", endDate);
		builder.addParameter("startDate", startDate);
		if (reportFacilityId != null) {
			builder.addParameter("facilityList", reportFacilityId);
		}

		List<Integer> ptIds = evaluationService.evaluateToList(builder, Integer.class, context);
		newCohort.setMemberIds(new HashSet<Integer>(ptIds));

        return new EvaluatedCohort(newCohort, definition, context);
    }

}
