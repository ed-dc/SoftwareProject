#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <stdbool.h>
#include <sys/types.h>
#include <time.h>
#include <errno.h>

#include "gestionFichier.c"

bool isDirectoryExist(const char *path)
{
    struct stat stats;
    if (stat(path, &stats) == 0)
    {
        if (stats.st_mode & S_IFDIR)
        {
            return true;
        }
    }
    return false;
}

void construireCheminSortie(const char *commande, char *fichierSortie, size_t tailleMax) 
{
    const char *nomScript = strrchr(commande, '/');
    if (nomScript == NULL) 
    {
        nomScript = commande;
    } 
    else 
    {
        nomScript++;
    }

    char *nomScriptSansExtension = strdup(nomScript);
    char *pos = strchr(nomScriptSansExtension, '.');
    if (pos != NULL) 
    {
        *pos = '\0';
    }

    snprintf(fichierSortie, tailleMax, "log/test/%s.output", nomScriptSansExtension);
    free(nomScriptSansExtension);
}

void lancementExecutionNom(const char *commande) 
{
    char fichierSortie[256];
    construireCheminSortie(commande, fichierSortie, sizeof(fichierSortie));

    FILE *fp = popen(commande, "r");
    if (fp == NULL) 
    {
        perror("Erreur lors de l'ouverture du processus");
        return;
    }

    gereTest(fp, commande, fichierSortie);
}

int main() {
    const char *repertoire = "./src/test/script";
    char **fichiers = listerFichiersDansRepertoire(repertoire);
    if (fichiers == NULL) 
    {
        return EXIT_FAILURE;
    }

    printf("Lancement des tests...\n");
    for (int i = 0; fichiers[i] != NULL; i++) 
    {
        char commande[512];
        snprintf(commande, sizeof(commande), "%s/%s", repertoire, fichiers[i]);
        lancementExecutionNom(commande);
    }
    gestionFin();

    libererTableauFichiers(fichiers);
    return EXIT_SUCCESS;
}
