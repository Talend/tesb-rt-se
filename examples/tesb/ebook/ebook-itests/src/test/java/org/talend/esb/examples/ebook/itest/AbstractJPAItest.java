/*  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.talend.esb.examples.ebook.itest;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.vmOption;
import static org.ops4j.pax.exam.CoreOptions.when;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import org.junit.runner.RunWith;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.UrlReference;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.talend.esb.examples.ebook.model.Book;
import org.talend.esb.examples.ebook.model.Format;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public abstract class AbstractJPAItest {
    private static final String LOGGING_CFG = "etc/org.ops4j.pax.logging.cfg";
    @Inject
    BundleContext bundleContext;
    protected UrlReference ebooksFeatures;
    
    /**
     * Helps to diagnose bundles that are not resolved as it will throw a detailed exception
     * 
     * @throws BundleException
     */
    public void resolveBundles() throws BundleException {
        Bundle[] bundles = bundleContext.getBundles();
        for (Bundle bundle : bundles) {
            if (bundle.getState() == Bundle.INSTALLED) {
                System.out.println("Found non resolved bundle " + bundle.getBundleId() + ":"
                    + bundle.getSymbolicName() + ":" + bundle.getVersion());
                bundle.start();
            }
        }
    }

    public Bundle getBundleByName(String symbolicName) {
        for (Bundle b : bundleContext.getBundles()) {
            if (b.getSymbolicName().equals(symbolicName)) {
                return b;
            }
        }
        return null;
    }
    
    protected Book createBook(String title) {
        Book book = new Book();
        book.setId("myid" + UUID.randomUUID());
        book.setTitle(title);
        book.setCreator("Me");
        try {
            Format format = new Format();
            format.setFile(new URI("file:/test"));
            book.getFormats().add(format);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return book;
    }
    
    public <T> T tryTo(String message, Callable<T> func) throws TimeoutException {
        return tryTo(message, func, 5000);
    }
    
    public <T> T tryTo(String message, Callable<T> func, long timeout) throws TimeoutException {
        Throwable lastException = null;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeout) {
            try {
                T result = func.call();
                if (result != null) {
                    return result;
                }
                lastException = null;
            } catch (Throwable e) {
                lastException = e;
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                continue;
            }
        }
        TimeoutException ex = new TimeoutException("Timeout while trying to " + message);
        if (lastException != null) {
            ex.addSuppressed(lastException);
        }
        throw ex;
    }
    
    protected Option baseOptions() {
        MavenArtifactUrlReference karaf = maven().groupId("org.apache.karaf").artifactId("apache-karaf")
            .version("4.0.3").type("tar.gz");
        ebooksFeatures = maven().groupId("org.talend.esb.examples.ebook").artifactId("ebook-features")
            .versionAsInProject().type("xml");
        return CoreOptions.composite(
            //KarafDistributionOption.debugConfiguration("8000", true),
            karafDistributionConfiguration().frameworkUrl(karaf).name("Apache Karaf")
                .unpackDirectory(new File("target/exam")).useDeployFolder(false),
            keepRuntimeFolder(),
            //logLevel(LogLevel.INFO),
            editConfigurationFilePut(LOGGING_CFG, "log4j.rootLogger", "INFO, stdout"),
            editConfigurationFilePut(LOGGING_CFG, "log4j.logger.org.apache.karaf.features", "WARN"),
            editConfigurationFilePut(LOGGING_CFG, "log4j.logger.org.apache.karaf.shell.impl.action.osgi.CommandExtension", "WARN"),
            editConfigurationFilePut(LOGGING_CFG, "log4j.logger.org.apache.aries.transaction.blueprint", "DEBUG"),
            //editConfigurationFilePut(LOGGING_CFG, "log4j.logger.org.springframework.transaction.jta.JtaTransactionManager", "DEBUG"),
            editConfigurationFilePut(LOGGING_CFG, "log4j.logger.org.apache.camel.spring.spi.TransactionErrorHandler", "DEBUG"),
            //editConfigurationFilePut(LOGGING_CFG, "log4j.logger.org.apache.aries.transaction.parsing", "DEBUG"),
            //editConfigurationFilePut(LOGGING_CFG, "log4j.logger.org.apache.aries.jpa.blueprint.impl", "DEBUG"),
            localRepoConfig()
        );
    }

    Option localRepoConfig() {
        String localRepo = System.getProperty("maven.repo.local");
        if (localRepo == null) {
            localRepo = System.getProperty("org.ops4j.pax.url.mvn.localRepository");
        }
        return when(localRepo != null).useOptions(vmOption("-Dorg.ops4j.pax.url.mvn.localRepository=" + localRepo));
    }

}
