# Installation Guide #

Version: 1.0.0.3

## Important note ##

Mocassin uses given RDF graph as underlying search index for now. That graph should be filled with corresponding triples, for instance, by means of [TNT Base](http://tntbase.org/) (see Mocassin TNTBase Plugin).

Try [online demo](http://kwarc.info/LinkedLectures) from the [KWARC](http://kwarc.info)'s test bed.

## Environment requirements ##
  * JDK 1.6+
  * Virtuoso server 6.1.0+
  * Servlet 2.5+ compliant web container (e.g. Apache Tomcat)

## Virtuoso prerequisites ##
  * Create 'mocassin' user
    1. Open "System Admin > User Accounts" tab in the Virtuoso's admin console, then click "Create New Account"
    1. Fill the required fields (let the username be "mocassinuser"). Select "SPARQL\_UPDATE" in the "Primary Role" combobox, add "SPARQL\_UPDATE" item to the "Account Roles" list
    1. Save account settings
    1. Open ISQL window and execute the following command:
```
DB.DBA.RDF_DEFAULT_USER_PERMS_SET ('mocassinuser', 0);
```

  * Edit the RDF graph settings:
    1. Let the graph URI be 'http://cll.niimm.ksu.ru/mocassin'. Execute the following command in ISQL window:
```
DB.DBA.RDF_GRAPH_USER_PERMS_SET ('http://cll.niimm.ksu.ru/mocassin', 'mocassinuser', 3);
```

  * Set up full-text indexing
    1. Execute the following commands in ISQL window:
```
DB.DBA.RDF_OBJ_FT_RULE_ADD('http://cll.niimm.ksu.ru/mocassin', null, 'mocassin');
DB.DBA.VT_INC_INDEX_DB_DBA_RDF_OBJ ();
DB.DBA.VT_BATCH_UPDATE ('DB.DBA.RDF_OBJ', 'ON', 60);
```

  * Create the inference rules set
    1. Load [OMDoc ontology](http://omdoc.org/ontology) into Virtuoso (you may use Virtuoso's admin console), let its graph URI be "http://cll.niimm.ksu.ru/ontology/omdoc"
    1. Execute the following command in ISQL window:
```
rdfs_rule_set ("http://cll.niimm.ksu.ru/ontology/omdoc/rules", "http://cll.niimm.ksu.ru/ontology/omdoc");
```

## Mocassin configuration file ##
Download [WAR application build](http://cll.niimm.ksu.ru:8080/artifactory/libs-releases-local/ru/ksu/niimm/cll/mocassin/mocassin-ui/1.0.0.3/mocassin-ui-1.0.0.3.war).
Edit configuration file which is located in $WAR\_FILENAME$/WEB-INF/lib/mocassin-ontology-1.0.0.3.jar as follows (besides, set appropriate values for 'connection.url' and 'connection.user.password'!):
```
# Virtuoso connection settings
connection.url=urltoedit
connection.user.name=mocassinuser
connection.user.password=passwordtoedit
graph.iri=<http://cll.niimm.ksu.ru/mocassin>
...
omdoc.rules.set=http://cll.niimm.ksu.ru/ontology/omdoc/rules
```

## Mocassin deployment ##
Rename downloaded WAR to 'mocassin.war' and deploy it on the web container.
Mocassin's page would be available at http://$HOST_NAME$/mocassin