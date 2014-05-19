/*
 * ============================================================================
 *
 * Copyright (C) 2011 - 2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 *
 * ============================================================================
 */
package org.talend.esb.callcontext.store.commands;


import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.features.command.FeaturesCommandSupport;


@Command(scope = "tesb", name = "stop-callback-repo", description = "Stop call context repository")
public class StopCallbackRepo extends FeaturesCommandSupport {


     @Override
     protected void doExecute(FeaturesService admin) throws Exception {

         if (admin.isInstalled(admin.getFeature("tesb-ccs"))) {
             admin.uninstallFeature("tesb-ccs");
         }

         if (admin.isInstalled(admin.getFeature("tesb-ccs-common"))) {
             admin.uninstallFeature("tesb-ccs-common");
         }

         if (admin.isInstalled(admin.getFeature("tesb-ccs-client-rest"))) {
             admin.uninstallFeature("tesb-ccs-client-rest");
         }

         if (admin.isInstalled(admin.getFeature("tesb-ccs-service-rest"))) {
             admin.uninstallFeature("tesb-ccs-service-rest");
         }

         if (admin.isInstalled(admin.getFeature("tesb-ccs-persistence"))) {
             admin.uninstallFeature("tesb-ccs-persistence");
         }


         if (admin.isInstalled(admin.getFeature("tesb-ccs-server"))) {
             admin.uninstallFeature("tesb-ccs-server");
         }
    }
}
