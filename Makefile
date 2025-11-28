all: compileMain
	java -cp bin:lib/ojdbc6.jar App

compileMain: compileAllPackages
	javac -d bin -cp lib/ojdbc6.jar -sourcepath src src/App.java

compileAllPackages:
	javac -d bin -cp lib/ojdbc6.jar -sourcepath src src/config/* # src/dao/* src/service/*

test:
	javac -d bin -cp bin:lib/ojdbc6.jar: -sourcepath src  src/interfaceGraphique/BoutiqueWindow.java src/interfaceGraphique/Catalogue.java src/interfaceGraphique/Commande.java src/interfaceGraphique/Login.java
	java -cp  bin:lib/ojdbc6.jar: interfaceGraphique.Catalogue