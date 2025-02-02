#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>
#include <stdbool.h>
#include <math.h>

#define INT "int"
#define FLOAT "float"
#define FLOATHEXA "floatHexa"

#define LIST_VAL_HEXA "0123456789ABCDEF"

typedef struct valueArgs {
    char* type;
    int value;
} valueArgs;

valueArgs gereArgs(int argc, char** argv) 
{
    if (argc == 3) 
    {
        valueArgs args;
        if (strcmp(argv[1], "int") == 0) 
        {
            args.type = INT;
        } 
        else if (strcmp(argv[1], "float") == 0) 
        {
            args.type = FLOAT;
        }
        else if (strcmp(argv[1], "floatHexa") == 0) 
        {
            args.type = FLOATHEXA;
        }
        else 
        {
            printf("Format des arguments : ./generation-nombre [int|float|floatHexa] [NBValeur]\n");
            printf("Format avec le Makefile : make compile; make generateur ARGS=\"[int|float|floatHexa] [NBValeur]\"\n");
            printf("Mettre le  : make compile seulement si le projet n'a pas encore été compilé");
            exit(EXIT_FAILURE);
        }
        args.value = atoi(argv[2]);
        if (args.value <= 0) 
        {
            printf("Le nombre de valeurs doit être supérieur à 0\n");
            exit(EXIT_FAILURE);
        }
        else if (args.value > 50000)
        {
            fprintf(stderr, "Le nombre de valeurs doit être inférieur à 50000\n");
            fprintf(stderr, "Sincérement, vous voulez vraiment générer autant de valeurs ?");
            exit(EXIT_FAILURE);
        }
        return args;
    } 
    else 
    {
        printf("Format des arguments : ./generation-nombre [int|float|floatHexa] [NBValeur]\n");
        printf("Format avec le Makefile : make compile; make generateur ARGS=\"[int|float|floatHexa] [NBValeur]\"\n");
        printf("Mettre le  : make compile seulement si le projet n'a pas encore été compilé");
        exit(EXIT_FAILURE);
    }
}

void* generationListe(int taille, size_t typeSize, void (*generateFunc)(void*)) 
{
    void* liste = malloc(taille * typeSize);
    if (liste == NULL) 
    {
        printf("Erreur lors de l'allocation mémoire\n");
        exit(EXIT_FAILURE);
    }
    for (int i = 0; i < taille; i++) 
    {
        generateFunc((char*)liste + i * typeSize);
    }
    return liste;
}

void deleteListe(void* liste) 
{
    free(liste);
}

void generateInt(void* element) {
    int result;

    if (rand() % 20 == 0) 
    {
        result = 0;
    } 
    else 
    {
        int isNegative = rand() % 2;
        int tete = (rand() % 9) + 1;
        int numDigits = rand() % 7;

        result = tete;
        for (int i = 0; i < numDigits; i++) 
        {
            result = result * 10 + (rand() % 10);
        }

        if (isNegative) // Pas de nombre négatif 
        {
            result = result * 1;
        }
    }

    *(int*)element = result;
}


void generateFloatHexa(void* element) 
{
    char* result = (char*)element;

    char hexPart1[15], hexPart2[15];  
    int lenPart1 = rand() % 5 + 1;
    int lenPart2 = rand() % 5 + 1;

    for (int i = 0; i < lenPart1; i++) {
        hexPart1[i] = LIST_VAL_HEXA[rand() % 16];
    }
    hexPart1[lenPart1] = '\0';

    for (int i = 0; i < lenPart2; i++) {
        hexPart2[i] = LIST_VAL_HEXA[rand() % 16];
    }
    hexPart2[lenPart2] = '\0';

    char* sign;
    switch (rand() % 3)
    {
        case 0:
            sign = "";
            break;
        case 1:
            sign = "+";
            break;
        case 2:
            sign = "-";
            break;
    }
    int exponent = rand() % 100;

    char* suffix;
    switch (rand() % 3)
    {
        case 0:
            suffix = "F";
            break;
        case 1:
            suffix = "f";
            break;
        case 2:
            suffix = " \0";
            break;
    }

    snprintf(result, 50, "0x%s.%sP%s%d%s", hexPart1, hexPart2, sign, exponent, suffix);
}

void generateFloatDec(void* element) 
{
    int integerPart = rand() % 1000; // 0 jusqu'a 999
    int frac = rand() % 1000 ;
    int div = pow(10, (int)log10(frac) + 1);

    float result = integerPart + (float)frac / div; // 0.0 jusqu'a 999.999 on a créée un DEC

    if (rand() % 2 == 0) 
    {
        // On rajoute E ou e
        char suffix = (rand() % 2 == 0) ? 'E' : 'e';
        int exponent = rand() % 100;
        char* signe = "";
        switch (rand() % 3)
        {
            case 0:
                signe = " "; 
                break;
            case 1:
                signe = "+";
                break;
            case 2:
                signe = "-";
                break;
            default:
                break;
        }
        snprintf((char*)element, 50, "%f%c%s%d", result, suffix, signe, exponent);
    }

    char* end = "";
    switch (rand() % 3)
    {
    case 0:
        end = "F";
        break;
    case 1:
        end = "f";
        break;
    case 2:
        end = "";
    default:
        break;
    }
    snprintf((char*)element, 50, "%f%s", result, end);
}

void generateFloat(void* element) 
{
    if(rand() % 2 == 0)
    {
        generateFloatHexa(element);
    }
    else
    {
        generateFloatDec(element);
    }
}
