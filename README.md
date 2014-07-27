TomcatLTPAValve
===============

##SSO between Tomcat and Websphere (beta).

Websphere is a great application server that provides out of the box a Single Sign On mechanism based on cookies and digital signatures that works very well.

Webphere and Webpshere Portal are used in large companies. Tipically Websphere Portal is used as a centralized point to access the rest of services. Webphere provides most of them, but sometimes there are custom services or third party services that run other infrastructure, like Tomcat, where the SSO simply doesn't work. Here is where this project can help you.


##NOTES

* LTPA is based on cookies, so all the application servers (including tomcat) shoud be located under the same domain.
* So far this is a beta version where the roles are fixed.
* The valve uses the ltpatoken2 cookie if exists, but is not ablle to generate it. This mean that, at least so far, this is only a one-way SSO. If you use Websphere Portal as your welcome web page this is not a problem.

## USAGE

* First of all, you need the websphere LTPA encryption keys. To do this, you need to export the LTPA keys from your websphere (Security-->Global Security --> LTPA --> Export Keys) and copy the com.ibm.websphere.ltpa.3DESKey entry from the generated file. Keep also the password you have used to generate the keys with you.

* Copy the JAR into the $TOMCAT\_HOME/lib/ folder. You can generate it using maven. There is also a dependency from commons-codec so you will need also to copy it $TOMCAT\_HOME/lib/

* Edit the server.xml file and configure the valve.

```
<Valve className="org.tomcatltpa.authenticator.LTPASSOAuthenticatorValve"
roleList="AuthenticatedUsers,anotherRole"
dnPrefix="uid"
keyPassword="the_password_used_to_export_the_ltpa_keys"
ltpa3DESKey="The_3DESKey_an_intelligible_set_of_characters"/>
```