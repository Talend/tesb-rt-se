/*
 * ============================================================================
 *
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 5/7 rue Salomon De Rothschild, 92150 Suresnes, France
 *
 * ============================================================================
 */
package org.talend.esb.auxiliary.storage.commands;


import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.features.command.FeaturesCommandSupport;


@Command(scope = "tesb", name = "stop-aux-store", description = "Stop auxiliary storage repository")
public class StopAuxiliaryRepo extends FeaturesCommandSupport {


     @Override
     protected void doExecute(FeaturesService admin) throws Exception {

         if (admin.isInstalled(admin.getFeature("tesb-aux"))) {
             admin.uninstallFeature("tesb-aux");
         }

         if (admin.isInstalled(admin.getFeature("tesb-aux-common"))) {
             admin.uninstallFeature("tesb-aux-common");
         }

         if (admin.isInstalled(admin.getFeature("tesb-aux-client-rest"))) {
             admin.uninstallFeature("tesb-aux-client-rest");
         }

         if (admin.isInstalled(admin.getFeature("tesb-aux-service-rest"))) {
             admin.uninstallFeature("tesb-aux-service-rest");
         }

         if (admin.isInstalled(admin.getFeature("tesb-aux-persistence"))) {
             admin.uninstallFeature("tesb-aux-persistence");
         }


         if (admin.isInstalled(admin.getFeature("tesb-aux-server"))) {
             admin.uninstallFeature("tesb-aux-server");
         }
    }
}
