package org.openmrs.module.kenyaemr.chore;

import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.chore.AbstractChore;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;

/**
 * updates concept ids for chv assignment obs
 */
@Component("kenyaemr.chore.UpdateChvUsernameObsConceptIds")
public class UpdateChvUsernameObsConceptIds extends AbstractChore {

    @Override
    public void perform(PrintWriter out) {
        String updateConceptSql = "UPDATE obs SET concept_id=165587, value_text=NULL WHERE concept_id=164141;";


        Context.getAdministrationService().executeSQL(updateConceptSql, false);


        out.println("Completed updating chv username concepts");

    }
}
