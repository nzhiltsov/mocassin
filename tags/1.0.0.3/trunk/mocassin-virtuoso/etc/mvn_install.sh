# Maven installation of Virtuoso Jena Provider and Virtuoso JDBC driver jars that could be downloaded from here:
# http://virtuoso.openlinksw.com/wiki/main/Main/VirtJenaProvider/virt_jena.jar
# http://virtuoso.openlinksw.com/wiki/main/Main/VirtJenaProvider/virtjdbc3.jar
mvn install:install-file -DgroupId=com.openlink.virtuoso -DartifactId=virtuoso-jdbc-3 -Dversion=1.0.0 -Dpackaging=jar -Dfile=virtjdbc3.jar
mvn install:install-file -DgroupId=com.openlink.virtuoso -DartifactId=virtuoso-jena -Dversion=1.0.0 -Dpackaging=jar -Dfile=virt_jena.jar