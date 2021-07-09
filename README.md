## SPARQL to CYPHER Neo4j extension 
#### A neo4j extension to translate sparql to cypher

### Requirements
* Java SE 11
Note: Neo4j version 4.1.0 and desktop version 1.3.4 were used at the time of writing.

### Quick Start
* Create a build using the command `mvn clean package`. Quick tip for Intellij users, just double click control to open up the `Run Anything` window where you can type the mvn commands.
* Two jar files will be created in the target folder, copy the one which doesnot start with `original-...`
* Paste the copied file in Neo4j database plugins folder as follows:
	* Select three dots on top-right corner of database and select `Manage`
	* Right next to `Open Folder` there will be a dropdown button, click on it and select `Plugins` which should open the folder that contains all the plugins for a specific database.
	* Paste the jar file in this plugins folder.
* Beneath the open folder, there should be some tabs such as Details, Logs, Settings, Plugins etc. Click on Settings tab which will list the Neo4j configuration. Scroll down to the bottom of the file and paste the following two lines:
	* dbms.unmanaged_extension_classes=org.dbis.server=/serverx
	* dbms.jvm.additional=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
* Please note /serverx is the URL where one can access the result from the neo4j extension and it is configurable. The second line contains the address for remote debugging. For debugging inside IntelliJ, users can select on Run -> Attach to Process -> select the process on port 5005. For more information on remote debugging, please check out https://neo4j.com/docs/java-reference/current/server-debugging/
* The extension is now configured and the result should now be available at the following URL:
	* http://localhost:7474/serverx/translate/sparql

### Note
* The sparql query in the extension is currently hardcoded but can be extended by using a web form.
  