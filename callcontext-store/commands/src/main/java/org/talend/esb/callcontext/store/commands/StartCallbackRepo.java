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


import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.features.command.FeaturesCommandSupport;

@Command(scope = "tesb", name = "start-callback-repo", description = "Start call context repository")
public class StartCallbackRepo extends FeaturesCommandSupport {

    private static String FEATURE_NAME = "tesb-ccs";

    @Override
    protected void doExecute(FeaturesService admin) throws Exception {

        session.execute("tesb:start-sam");

        Feature callContextServerFeature = admin.getFeature(FEATURE_NAME);

        if (callContextServerFeature == null ) {

            admin.addRepository(URI.create("mvn:org.talend.esb.callcontext.store/callcontext-store-features/" + getProjectVersion() + "/xml"));
            callContextServerFeature = admin.getFeature(FEATURE_NAME);

            if (callContextServerFeature == null) {
                String errorMessage = "Failed to start call context repository: feature " + FEATURE_NAME + " is missed";
                System.out.println(errorMessage);
                log.error(errorMessage);
                return;
            }
        }

        if (!admin.isInstalled(callContextServerFeature)) {
            admin.installFeature(FEATURE_NAME);
        }
    }


    private String getProjectVersion() throws Exception {

        Properties prop = new Properties();
        InputStream in = getClass().getClassLoader().getResourceAsStream("META-INF/maven/org.talend.esb.callcontext.store/callcontext-store-commands/pom.properties");
        prop.load(in);

        String version = prop.getProperty("version");

        in.close();

        return version;
    }
}
