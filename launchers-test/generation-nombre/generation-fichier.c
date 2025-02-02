#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <time.h>

#include <stdbool.h>
#include "generation-nombre.c"

void gestionCommentaire(FILE* file, const char* type, void* valeur) {
    time_t t = time(NULL);
    fprintf(file, "// Description: \n");
    fprintf(file, "// Test de programme lexicalement correct automatisé\n");
    fprintf(file, "// \n");
    fprintf(file, "// Type: %s\n", type);
    if(strcmp(type, "int") == 0)
    {
        fprintf(file, "//%d\n", *(int*)valeur);
    }
    else if (strcmp(type, "float") == 0)
    {
        fprintf(file, "//%s\n", (char*)valeur);
    }
    else if(strcmp(type, "floatHexa") == 0)
    {
        fprintf(file, "//%s\n", (char*)valeur);
    }
    fprintf(file, "\n");
    fprintf(file, "// Historique: \n");
    fprintf(file, "// %s\n", ctime(&t));
}

void gestionContenu(FILE* file, const char* format, void* valeur) 
{
    fprintf(file, "{\n");
    fprintf(file, "    print(");
    if(strcmp(format, "%d") == 0)
    {
        fprintf(file, format, *(int*)valeur);
    }
    else if (strcmp(format, "%s") == 0)
    {
        fprintf(file, format, (char*)valeur);
    }
    else
    {
        fprintf(file, format, (char*)valeur);
    }
    fprintf(file, ");\n");
    fprintf(file, "}\n");
}

void gereTestDossier(const char* type) 
{
    char command[50];
    sprintf(command, "mkdir -p src/test/deca/syntax/valid/%s", type);
    system(command);
}

void creationEcritureFichier(void* valeur, const char* type, const char* format, int index) {
    char fileName[256];
    sprintf(fileName, "src/test/deca/syntax/valid/%s/test-numero-%d.deca", type, index);
    gereTestDossier(type);
    FILE* file = fopen(fileName, "w");
    if (file == NULL) {
        printf("Erreur lors de l'ouverture du fichier\n");
        exit(EXIT_FAILURE);
    }
    gestionCommentaire(file, type, valeur);
    gestionContenu(file, format, valeur);
    fclose(file);
}

void gereFichiersCreation(void* liste, const char* type, const char* format, int taille, size_t tailleType) 
{
    for (int i = 0; i < taille; i++) 
    {
        void* valeur = (char*)liste + i * tailleType;
        creationEcritureFichier(valeur, type, format, i);
    }
}

int main(int argc, char** argv) 
{
    valueArgs args = gereArgs(argc, argv);
    srand(time(NULL));

    if(strcmp(args.type, INT) == 0)
    {
        int* listeInt = generationListe(args.value, sizeof(int), generateInt);
        printf("Génération des fichiers pour les entiers : ");
        gereFichiersCreation(listeInt, "int", "%d", args.value, sizeof(int));
        deleteListe(listeInt);
        printf("OK\n");
    }
    else if(strcmp(args.type, FLOAT) == 0)
    {
        size_t taille = 50 * sizeof(char);
        char* listeFloat = generationListe(args.value, taille, generateFloat);
        printf("Génération des fichiers pour les flottants : ");
        gereFichiersCreation(listeFloat, "float", "%s", args.value, taille);
        deleteListe(listeFloat);
        printf("OK\n");
    }
    else if(strcmp(args.type, FLOATHEXA) == 0)
    {
        size_t taille = 50 * sizeof(char);
        char* listeFloatHexa = generationListe(args.value, taille, generateFloatHexa);
        printf("Génération des fichiers pour les flottants hexadécimaux : ");
        gereFichiersCreation(listeFloatHexa, "floatHexa", "%s", args.value, taille);
        deleteListe(listeFloatHexa);
        printf("OK\n");
    }
    
    return EXIT_SUCCESS;
}
