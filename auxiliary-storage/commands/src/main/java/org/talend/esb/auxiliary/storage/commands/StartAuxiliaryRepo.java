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


import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.features.command.FeaturesCommandSupport;

@Command(scope = "tesb", name = "start-aux-store", description = "Start auxiliary storage repository")
public class StartAuxiliaryRepo extends FeaturesCommandSupport {

    private static String FEATURE_NAME = "tesb-aux";

    @Override
    protected void doExecute(FeaturesService admin) throws Exception {

        Feature auxiliaryStoreServerFeature = admin.getFeature(FEATURE_NAME);

        if (auxiliaryStoreServerFeature == null ) {

            admin.addRepository(URI.create("mvn:org.talend.esb.auxiliary.storage/auxiliary-storage-features/" + getProjectVersion() + "/xml"));
            auxiliaryStoreServerFeature = admin.getFeature(FEATURE_NAME);

            if (auxiliaryStoreServerFeature == null) {
                String errorMessage = "Failed to start auxiliary storage repository: feature " + FEATURE_NAME + " is missed";
                System.out.println(errorMessage);
//                log.error(errorMessage);
                return;
            }
        }

        if (!admin.isInstalled(auxiliaryStoreServerFeature)) {
            admin.installFeature(FEATURE_NAME);
        }
    }


    private String getProjectVersion() throws Exception {

        Properties prop = new Properties();
        InputStream in = getClass().getClassLoader().getResourceAsStream("META-INF/maven/org.talend.esb.auxiliary.storage/auxiliary-storage-commands/pom.properties");
        prop.load(in);

        String version = prop.getProperty("version");

        in.close();

        return version;
    }
}
