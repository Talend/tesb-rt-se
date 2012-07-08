How to install and run Shibboleth IDP.

1. Download Shibboleth IDP from 
http://shibboleth.net/downloads/identity-provider/latest/
and unzip it.

2. Run the install script via "sh install.sh" and extract it to a 
suitable location, example to "/home/username/work/shibboleth-idp",
this location will be referred to as ${shibboleth-home}.
During the installation, call the IDP "idp.apache.org" and enter 
some custom password for the keystore.

3. Copy ${shibboleth-home}/idp.war into a Tomcat 6 (note not 7) webapps directory,
${tomcat.home}/webapps. 
Copy the files in ${shibboleth.home}/lib/endorsed into ${tomcat.home}/endorsed directory.
Start Tomcat and check that the IDP
is accessible via "http://localhost:8080/idp/profile/Status", which should show "ok".  

4. Copy the files from "conf" directory relative to where this README.txt is located to
${shibboleth.home}/conf. Replace the "${shibboleth.home}" in ${shibboleth.home}/conf/relying-party.xml and ${shibboleth.home}/conf/handler.xml with the actual location such as "/home/username/work/shibboleth-idp".   

5. Copy the files from "credentials" directory relative to where this README.txt is located to
${shibboleth.home}/credentials

6. Create Kerberos store:
(see http://coheigea.blogspot.ie/2011/10/using-kerberos-with-web-services-part-i.html) 
 6.1 Install:
  sudo apt-get install krb5-kdc krb5-admin-server
  set "SOCIAL.COM" as the value of the default realm
 6.2 Edit the configuration
  sudo vi /etc/krb5.conf
  
  Under the "[realms]" section, add a "default_domain" entry for social.com. The entire entry should look like:

            SOCIAL.COM = {
                         kdc = localhost
                         admin_server = localhost
                         default_domain = social.com
            }

    Under the "[domain_realm]" section, add the following:     

            social.com = SOCIAL.COM  
            .social.com = SOCIAL.COM

    Finally, add a logging section:

            [logging]

                    kdc = FILE:/var/log/krb5kdc.log
                    admin_server = FILE:/var/log/kadmin.log
                    default = FILE:/var/log/krb5lib.log 

  
 6.3 Create a new principal

     sudo kdb5_util create -s
     sudo kadmin.local 
     addprinc barry
     (and enter password '1234')
     quit

 6.4 kinit barry 
     (and enter password '1234')

 6.5 Start KDC:
     sudo krb5kdc

 6.6 Start IDP web application:
     cd ${tomcat.home}
     bin/catalina.sh run

 Repeat 6.5 and 6.6 whenever you need to start KDC and IDP.

 Note that instead of Kerberos, IDP can be configured to use the same database that Social.com uses (in the reality) for keeping the account details. Users are encouraged to experiment with this option.
     

