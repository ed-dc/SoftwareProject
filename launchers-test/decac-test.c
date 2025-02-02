#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define ROUGE "\033[31m"
#define VERT "\033[32m"
#define NORMAL "\033[0m"

#define FICHIER "src/test/deca/context/valid/provided/hello-world.deca"
#define FICHIER2 "src/test/deca/context/valid/provided/hello-world.deca" // A remplacer par d'autre fichier quand on en aura besoin
#define FICHIER3 "src/test/deca/context/valid/provided/hello-world.deca" // A remplacer par d'autre fichier quand on en aura besoin. Important pour les tests de -P

int launchCommand(char* command)
{
    FILE *pipe;
    char output[1024];
    int exit_status;

    pipe = popen(command, "r");
    if (pipe == NULL) {
        printf("Error while opening the pipe\n");
        return 1;
    }

    fgets(output, sizeof(output), pipe);
    
    exit_status = pclose(pipe);
    if (exit_status != 0) 
    {
        printf("Error while closing the pipe\n");
        return 1;
    }

    if (strstr(output, "erreur") != NULL || strstr(output, "error") != NULL) 
    {
        printf("Error while executing the command\n");
        return 1;
    }

    return 0;
}

int test_r(char* val)
{
    char* commandValue = "./src/main/bin/decac -r "; 
    char* command = malloc(strlen(commandValue) + 3 + strlen(FICHIER) + 1);
    strcpy(command, commandValue);
    strcat(command, val);
    strcat(command, " ");
    strcat(command, FICHIER);
    int output = launchCommand(command);
    int inpoutVal = atoi(val);
    if((output == 1) && (inpoutVal > 16 || inpoutVal < 4))
    {
        output = 0;
    }
    free(command);
    return output;
}

int main(int argc, char* argv[])
{
    char* listSuffix[7] = {"-p", "-v", "-n", "-r", "-d", "-P", "-w"};
    char* commandInit = "./src/main/bin/decac ";
    int* status = malloc(7 * sizeof(int));
    for(int i = 0; i < 7; i++)
    {
        if(i == 3)
        {
            if(argc > 1)
            {
                status[i] = test_r(argv[1]);
            }
            else
            {
                status[i] = test_r("14");
            }
            continue;
        }
        char* command = malloc(strlen(commandInit) + strlen(listSuffix[i]) + strlen(FICHIER) + 1);
        strcpy(command, commandInit);
        strcat(command, listSuffix[i]);
        strcat(command, " ");
        strcat(command, FICHIER);
        status[i] = launchCommand(command);
        free(command);
    }

    for(int i = 0; i < 7; i++)
    {
        if(status[i] == 0)
        {
            printf(VERT "Test %s réussi\n" NORMAL, listSuffix[i]);
        }
        else
        {
            printf(ROUGE "Test %s échoué\n" NORMAL, listSuffix[i]);
        }
    }
    return 0;
}