# Variables
JAVAC = javac
JAVA = java
JDBC_JAR = lib/ojdbc6.jar
SRC_DIR = src
BIN_DIR = bin
ASUPP_DIR = asupp

# Classpath
CP = $(BIN_DIR):$(JDBC_JAR)

# Cibles principales
all: compileMain
	$(JAVA) -cp $(CP) App

compileMain: compileAllPackages
	$(JAVAC) -d $(BIN_DIR) -cp $(JDBC_JAR) -sourcepath $(SRC_DIR) $(SRC_DIR)/App.java

# Compilation de tous les packages
compileAllPackages: clean-bin
	@echo "Compilation de tous les packages..."
	@mkdir -p $(BIN_DIR)
	$(JAVAC) -d $(BIN_DIR) -cp $(JDBC_JAR) -sourcepath $(SRC_DIR) \
		$(SRC_DIR)/config/*.java \
		$(SRC_DIR)/model/*.java \
		$(SRC_DIR)/dao/*.java \
		$(SRC_DIR)/service/*.java \
		$(SRC_DIR)/interfaceGraphique/*.java
	@echo "Compilation terminée avec succès!"

# Test de l'interface Catalogue
test: compileAllPackages
	@echo "Lancement du test du catalogue..."
	$(JAVA) -cp $(CP) interfaceGraphique.Catalogue

# Test du service ConsulterCatalogue (sans interface graphique)
test-service: compileAllPackages
	@echo "Test du service ConsulterCatalogue..."
	$(JAVA) -cp $(CP) service.TestConsulterCatalogue

# Test de BoutiqueWindow
test-boutique: compileAllPackages
	@echo "Lancement de BoutiqueWindow..."
	$(JAVA) -cp $(CP) interfaceGraphique.BoutiqueWindow

# Compilation de App.java
compile-app: compileAllPackages
	$(JAVAC) -d $(BIN_DIR) -cp $(JDBC_JAR) -sourcepath $(SRC_DIR) $(SRC_DIR)/App.java

# Nettoyage
clean:
	@echo "Nettoyage des fichiers compilés..."
	rm -rf $(BIN_DIR)/*
	rm -rf $(ASUPP_DIR)/*
	@echo "Nettoyage terminé!"

clean-bin:
	@mkdir -p $(BIN_DIR)

# Aide
help:
	@echo "Commandes disponibles:"
	@echo "  make              - Compile et exécute App.java"
	@echo "  make compileMain  - Compile tous les packages et App.java"
	@echo "  make test         - Lance l'interface Catalogue"
	@echo "  make test-service - Teste le service ConsulterCatalogue (console)"
	@echo "  make test-boutique- Lance BoutiqueWindow"
	@echo "  make clean        - Nettoie les fichiers compilés"
	@echo "  make help         - Affiche cette aide"

.PHONY: all compileMain compileAllPackages test test-service test-boutique compile-app clean clean-bin help