export CLASSPATH=bin:lib/junit-4.5-SNAPSHOT-20080319-0812.jar:lib/timeandmoney-v0_5_1.jar:lib/jmock-2.3.0-RC2.jar:lib/cglib-nodep-2.1_3.jar:lib/objenesis-1.0.jar
java -classpath ${CLASSPATH} -agentlib:hprof=cpu=samples,depth=40 org.junit.runner.JUnitCore test.net.saff.HowDoesEverything
cat java.hprof.txt
