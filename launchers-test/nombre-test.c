#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <stdbool.h>
#include <time.h>
#include <errno.h>

#include "gestionFichier.c"


#define DOSSIER "./src/test/deca/syntax/valid"


FILE *lancementExecutionNom(const char *commande) 
{
    FILE *fp = popen(commande, "r");
    if (!fp) {
        perror("Erreur lors de l'ouverture du processus");
    }
    return fp;
}

const char *gereArgs(int argc, char **argv) {
    if (argc < 2) {
        fprintf(stderr, "Usage: %s [int|float|floatHexa]\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    if (strcmp(argv[1], "int") == 0 || strcmp(argv[1], "float") == 0 || strcmp(argv[1], "floatHexa") == 0) {
        static char commande[512];
        snprintf(commande, sizeof(commande), "%s/%s", DOSSIER, argv[1]);
        return commande;
    }

    fprintf(stderr, "Argument invalide : %s\n", argv[1]);
    exit(EXIT_FAILURE);
}

char *modifFichier(const char *fichier, const char *extension) {
    size_t taille = strlen(fichier);
    char *copie = strdup(fichier);
    if (taille > 4) {
        strcpy(copie + taille - 5, extension);
    }
    return copie;
}

int main(int argc, char **argv) {
    const char *repertoire = gereArgs(argc, argv);
    char **fichiers = listerFichiersDansRepertoire(repertoire);

    if (!fichiers) {
        return EXIT_FAILURE;
    }

    printf("Lancement des tests...\n");
    for (size_t i = 0; fichiers[i] != NULL; i++) 
    {
        char commande[512];
        char* valeurAttendu = recupVal(repertoire, fichiers[i]);
        snprintf(commande, sizeof(commande), "./src/main/bin/decac %s/%s", repertoire, fichiers[i]);

        FILE *fp = lancementExecutionNom(commande);
        if (fp == NULL)
        {
            printf("Erreur lors de l'execution de la commande\n");
            continue;
        }

        char *nouveauNom = modifFichier(fichiers[i], ".ass");
        snprintf(commande, sizeof(commande), "ima %s/%s", repertoire, nouveauNom);

        nouveauNom = modifFichier(fichiers[i], ".ouput");
        fp = lancementExecutionNom(commande);
        char nomFinal[256];
        snprintf(nomFinal, sizeof(nomFinal), "log/nombre/%s", nouveauNom);
        gereTest(fp, commande, nomFinal);
        verifieEgalite(valeurAttendu, nomFinal);
        free(nouveauNom);
    }
    gestionFin();

    libererTableauFichiers(fichiers);
    return EXIT_SUCCESS;
}
