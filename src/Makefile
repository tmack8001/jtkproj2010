
OPTS=-g

CLASSPATH=.:../javaclient.jar

build:
	javac $(OPTS) -classpath $(CLASSPATH) *.java

ci: build doc 
	svn up
	svn ci

doc:
	javadoc -d ../javadocs -classpath $(CLASSPATH) -private *.java

test:
	java -classpath $(CLASSPATH) JTKLocal

clean:
	$(RM) *.class

