all: compileMain
	java -cp bin:lib/ojdbc6.jar App

compileMain: compileAllPackages
	javac -d bin -cp lib/ojdbc6.jar -sourcepath src src/App.java

compileAllPackages:
	javac -d bin -cp lib/ojdbc6.jar -sourcepath src src/config/* # src/dao/* src/service/*

clean:
	rm -rf bin