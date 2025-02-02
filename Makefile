CC = gcc
BIN = bin
CFLAGS = -Wall -Wextra -g
ENDFLAGS = -lm
LOG = log
TEST = test
NOMBRE_DIR = nombre
DIR = launchers-test
DECA_OPTIONS = deca-options

DECAC = decac-test.c
OUTPUT_DECAC = decac-test

NOMBRE = nombre-test.c
OUTPUT_NOMBRE = nombre-test

GENE_DIR = generation-nombre
GENE = generation-fichier.c
OUTPUT_GENERE = generation-fichier

MAIN = launcher-test.c
OUTPUT_MAIN = launcher-test-main

PYTHON_SCRIPT = $(DIR)/$(DECA_OPTIONS)/deca-p.py
C_SOURCE = $(DIR)/$(DECA_OPTIONS)/deca-p.c
JAVA_SCRIPT = $(DIR)/$(DECA_OPTIONS)/DecaP.java

C_BINARY = $(BIN)/deca-p
JAVA_BINARY = $(BIN)/launchers/test/deca/options/DecaP.class
JAVA_FILE = launchers.test.deca.options.DecaP

all : compile

compile :
	@echo "Compilation du projet"
	mkdir -p $(BIN) $(LOG)
	$(CC) $(CFLAGS) $(DIR)/$(MAIN) -o $(BIN)/$(OUTPUT_MAIN)
	$(CC) $(CFLAGS) $(DIR)/$(DECAC) -o $(BIN)/$(OUTPUT_DECAC)
	$(CC) $(CFLAGS) $(DIR)/$(GENE_DIR)/$(GENE) -o $(BIN)/$(OUTPUT_GENERE) $(ENDFLAGS)
	$(CC) $(CFLAGS) $(DIR)/$(NOMBRE) -o $(BIN)/$(OUTPUT_NOMBRE)
	@echo "Compilation terminée"
	@echo "Lancement compilation mvn"
	- mvn clean install compile

run :
	mkdir -p $(BIN) $(LOG) $(LOG)/$(TEST)
	./$(BIN)/$(OUTPUT_MAIN)

decac :
	mkdir -p $(BIN) $(LOG) $(LOG)/$(TEST)
	./$(BIN)/$(OUTPUT_DECAC) $(ARGS)

generateur :
	mkdir -p $(BIN) $(LOG) $(LOG)/$(TEST)
	./$(BIN)/$(OUTPUT_GENERE) $(ARGS)

test-nombre :
	mkdir -p $(BIN) $(LOG) $(LOG)/$(NOMBRE_DIR)
	./$(BIN)/$(OUTPUT_NOMBRE) $(ARGS)

test-deca-p:
	./src/test/script/decac-p.sh

test-nombre-all:
	mkdir -p $(BIN) $(LOG) $(LOG)/$(NOMBRE_DIR)
	./$(BIN)/$(OUTPUT_GENERE) $(ARGS)
	./$(BIN)/$(OUTPUT_NOMBRE) $(ARGS)

all-test: clean compile
	mkdir -p $(BIN) $(LOG) $(LOG)/$(TEST)
	./$(BIN)/$(OUTPUT_DECAC)
	./$(BIN)/$(OUTPUT_MAIN)

clean :
	rm -rf $(BIN)/* $(LOG)/*
	rm -rf src/test/deca/syntax/valid/int
	rm -rf src/test/deca/syntax/valid/float
	rm -rf src/test/deca/syntax/valid/floatHexa

help :
	@echo "make compile : compile the project"
	@echo "make run : run the project"
	@echo "make decac [ARGS: number for the -r X extention]: run the decac test"
	@echo "make generateur ARGS=\"[int|float|floatHexa] [number]\" : run the generation creation"
	@echo "make clean : clean the project"
	@echo "make help : display this message"
