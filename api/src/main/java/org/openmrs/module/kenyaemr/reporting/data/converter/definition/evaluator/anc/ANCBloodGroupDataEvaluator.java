/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.anc;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.anc.ANCBloodGlucoseDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.anc.ANCBloodGroupDataDefinition;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.evaluator.EncounterDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Handler(supports= ANCBloodGroupDataDefinition.class, order=50)
public class ANCBloodGroupDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "SELECT v.encounter_id,\n" +
                "  (case e.blood_group when 690 then \"A POSITIVE\" when 692 then \"A NEGATIVE\" when 694 then \"B POSITIVE\" when 696 then \"B NEGATIVE\" when 699 then \"O POSITIVE\" when 701 then \"O NEGATIVE\" when 1230 then \"AB POSITIVE\" when 1231 then \"AB NEGATIVE\" else null end)\n" +
                " from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "  INNER JOIN  kenyaemr_etl.etl_mch_enrollment e  on e.patient_id = v.patient_id\n" +
                "  GROUP BY v.encounter_id;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
