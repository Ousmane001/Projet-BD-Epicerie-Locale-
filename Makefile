all: compileMain
	java -cp bin:lib/ojdbc11.jar main

compileMain: compileAllPackages
	javac -d bin -cp lib/ojdbc11.jar -sourcepath src src/App.java

compileAllPackages:
	javac -d bin -sourcepath src src/config/* 