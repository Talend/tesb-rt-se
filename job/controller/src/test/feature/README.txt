Build.
 1. mvn package
 2. Add artifacts into \target\multijob-feature-1.0.kar (a zip file) using maven-style layout.
    For example:
    multijob-feature-1.0.kar\repository\talend\job-control-bundle\1.0\job-control-bundle-1.0.jar 
    multijob-feature-1.0.kar\repository\talend\ProviderJob\0.1\ProviderJob-0.1.jar 
    multijob-feature-1.0.kar\repository\talend\ESBProvider2\0.1\ESBProvider2-0.1.jar 

Usage.
 1. copy multijob-feature-1.0.kar into \talend-esb-5.0-SNAPSHOT\container\deploy
 2. execute in console command:
 feature:install CustomerService-feature
