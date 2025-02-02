#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <stdbool.h>
#include <time.h>

#define VERT "\033[32m"
#define NORMAL "\033[0m"
#define ROUGE "\033[31m"
#define ORANGE "\033[33m"

int nbTests = 0;
int nbTestsOK = 0;

char* recupVal(const char* repertoire, char* fichier)
{
    char* path = malloc(strlen(repertoire) + strlen(fichier) + 2);
    snprintf(path, strlen(repertoire) + strlen(fichier) + 2, "%s/%s", repertoire, fichier);

    FILE* file = fopen(path, "r");
    if (file == NULL)
    {
        perror("Erreur lors de l'ouverture du fichier");
        return NULL;
    }
    int taille = 4096;
    char* buffer = malloc(taille);
    for(int i = 0; i < 5; i++)
    {
        fgets(buffer, taille, file);
    }
    fclose(file);
    buffer = strtok(buffer, "\n");
    buffer = buffer + 2;
    return buffer;
}
void touch(const char *path)
{
    // Création du fichier de sortie
    FILE *fichier = fopen(path, "w");
    if (fichier == NULL)
    {
        fprintf(stderr, "Erreur lors de l'ouverture du fichier %s\n", path);
        perror("Veuillez changer les droits d'accées");
        return;
    }
    fclose(fichier);
}

void ecrireSortieFichier(FILE *fp, const char *nomFichier) 
{
    touch(nomFichier);
    // Création du fichier de sortie ou écrasement si il existe déjà

    FILE *fichier = fopen(nomFichier, "w");
    if (fichier == NULL) {
        fprintf(stderr, "Erreur lors de l'ouverture du fichier %s\n", nomFichier);
        perror("Veuillez changer les droits d'accées");
        return;
    }

    char buffer[4096];
    while (fgets(buffer, sizeof(buffer), fp) != NULL) 
    {
        fprintf(fichier, "%s", buffer);
    }

    fclose(fichier);
}

void libererTableauFichiers(char **fichiers) {
    for (size_t i = 0; fichiers[i]; i++) {
        free(fichiers[i]);
    }
    free(fichiers);
}

void gereTest(FILE *fp, const char *commande, const char *fichierSortie) 
{
    time_t currentTime = time(NULL);
    ecrireSortieFichier(fp, fichierSortie);
    double diff = difftime(time(NULL),currentTime);

    int status = pclose(fp);
    if (status == -1) {
        perror("Erreur lors de la fermeture du processus");
    } 
    else 
    {
        if (WEXITSTATUS(status) == 0) 
        {
            printf("\r[ " VERT "OK" NORMAL " ] : %s - %.2f secondes\n", commande, diff);
            nbTestsOK++;
        } 
        else 
        {
            printf("\r[ " ROUGE "KO" NORMAL " ] : %s - %.2f secondes\n", commande, diff);
        }
        nbTests++;
    }
}

void verifieEgalite(char* valeurAttendu,char* nomFinal)
{
    FILE* file = fopen(nomFinal, "r");
    if (file == NULL)
    {
        perror("Erreur lors de l'ouverture du fichier");
        return;
    }
    int taille = 4096;
    char* buffer = malloc(taille);
    for(int i = 0; i < 5; i++)
    {
        fgets(buffer, taille, file);
    }
    fclose(file);
    buffer = strtok(buffer, "\n");
    if(strcmp(buffer, valeurAttendu) == 0)
    {
        printf("[ " VERT "OK" NORMAL " ] : %s\n", nomFinal);
        nbTestsOK++;
    }
    else
    {
        printf("[ " ROUGE "KO" NORMAL " ] : %s\n", nomFinal);
    }
    printf("\n");
    nbTests++;
}

char** listerFichiersDansRepertoire(const char *repertoire) 
{
    DIR *dir = opendir(repertoire);
    if (dir == NULL) 
    {
        perror("Erreur lors de l'ouverture du répertoire");
        return NULL;
    }

    size_t capacite = 20; // Nous n'arrons surement pas plus de 20 fichiers
    char **tableauChemin = malloc(capacite * sizeof(char *));
    if (tableauChemin == NULL) 
    {
        perror("Erreur lors de l'allocation de mémoire");
        closedir(dir);
        return NULL;
    }

    size_t i = 0;
    struct dirent *entree;
    while ((entree = readdir(dir)) != NULL) 
    {
        if (strcmp(entree->d_name, ".") != 0 && strcmp(entree->d_name, "..") != 0)
        {
            const char* extention = strrchr(entree->d_name, '.');
            if (extention == NULL || (strcmp(extention, ".sh") != 0 && strcmp(extention, ".deca") != 0))
            {
                continue;
            }
            if (i >= capacite) 
            {
                capacite *= 2;
                char **temp = realloc(tableauChemin, capacite * sizeof(char *));
                if (temp == NULL) 
                {
                    perror("Erreur lors de la réallocation de mémoire");
                    for (size_t j = 0; j < i; j++) 
                    {
                        free(tableauChemin[j]);
                    }
                    free(tableauChemin);
                    closedir(dir);
                    return NULL;
                }
                tableauChemin = temp;
            }

            tableauChemin[i] = strdup(entree->d_name);
            if (tableauChemin[i] == NULL) 
            {
                perror("Erreur d'allocation mémoire pour un nom de fichier");
                for (size_t j = 0; j < i; j++) 
                {
                    free(tableauChemin[j]);
                }
                free(tableauChemin);
                closedir(dir);
                return NULL;
            }
            i++;
        }
    }
    return tableauChemin;
}

void gestionFin()
{
    printf("Fin des tests\n");
    printf("\n=====================\n");

    if(nbTests == nbTestsOK)
    {
        printf(VERT "Tous les tests ont réussi" NORMAL "\n");
    }
    else if( nbTestsOK >= nbTests / 2 )
    {
        printf(ORANGE "%d tests ont échoué" NORMAL "\n", nbTests - nbTestsOK);
    }
    else
    {
        printf(ROUGE "%d tests ont échoué " NORMAL "\n", nbTests - nbTestsOK);
    }
    
    printf("=====================\n");
}
