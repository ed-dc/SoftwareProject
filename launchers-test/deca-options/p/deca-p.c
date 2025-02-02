#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <stdbool.h>

#define COLOR_GREEN "\033[92m"
#define COLOR_RED "\033[91m"
#define COLOR_BLUE "\033[94m"
#define COLOR_NEUTRAL "\033[0m"

#define TEST_DIR_OK "src/test/deca/options/option-p/test/OK"
#define TEST_DIR_KO "src/test/deca/options/option-p/test/KO"
#define OUT_DIR_OK "src/test/deca/options/option-p/out/OK"
#define OUT_DIR_KO "src/test/deca/options/option-p/out/KO"
#define DECA_FILE "src/main/bin/decac"

void print_color(const char* color, const char* string, int display) 
{
    if (display) 
    {
        printf("%s%s%s\n", color, string, COLOR_NEUTRAL);
    }
}

void nettoyage() 
{
    printf("Nettoyage: ");
    const char* dirs[] = {OUT_DIR_OK, OUT_DIR_KO};

    for (int i = 0; i < 2; ++i) 
    {
        DIR* dir = opendir(dirs[i]);
        if (dir) {
            struct dirent* entry;
            while ((entry = readdir(dir)) != NULL) 
            {
                if (entry->d_type == DT_REG) 
                {
                    char filepath[1024];
                    snprintf(filepath, sizeof(filepath), "%s/%s", dirs[i], entry->d_name);
                    unlink(filepath);
                }
            }
            closedir(dir);
        }
    }

    DIR* check_dir = opendir(OUT_DIR_OK);
    int is_empty = 1;
    if (check_dir) 
    {
        struct dirent* entry;
        while ((entry = readdir(check_dir)) != NULL) 
        {
            if (entry->d_type == DT_REG) 
            {
                is_empty = false;
                break;
            }
        }
        closedir(check_dir);
    }

    if (is_empty) 
    {
        print_color(COLOR_GREEN, "[ OK ]", true); // Je pourrai porposer a l'utilisateur d'afficher ou non les informations
    } 
    else
    {
        print_color(COLOR_RED, "[ KO ]", true);
    }
}

void files_to_test(const char* input_dir, char files[][256], int* file_count) 
{
    DIR* dir = opendir(input_dir);
    if (dir) 
    {
        struct dirent* entry;
        while ((entry = readdir(dir)) != NULL) 
        {
            if (strstr(entry->d_name, ".deca") != NULL) 
            {
                strcpy(files[*file_count], entry->d_name);
                (*file_count)++;
            }
        }
        closedir(dir);
    }
}

void launch_test_write_file(const char* output_dir, const char* file, const char* output) 
{
    char filepath[1024];
    snprintf(filepath, sizeof(filepath), "%s/%s", output_dir, file);

    FILE* f = fopen(filepath, "w");
    if (f) 
    {
        fputs(output, f);
        fclose(f);
    }
}

int execute_command(const char* command, char* stdout_output, char* stderr_output) 
{
    char stdout_file[] = "/tmp/stdout_temporaire";
    char stderr_file[] = "/tmp/stderr_temporaire";
    
    int stdout_temp = mkstemp(stdout_file);
    int stderr_temp = mkstemp(stderr_file);
    
    char full_command[2048];
    snprintf(full_command, sizeof(full_command), "%s 1>%s 2>%s", command, stdout_file, stderr_file);
    
    int return_status = system(full_command);
    
    FILE* f_stdout = fopen(stdout_file, "r");
    if (f_stdout) 
    {
        size_t data_file = fread(stdout_output, 1, 8192, f_stdout);
        stdout_output[data_file] = '\0';
        fclose(f_stdout);
    }
    
    FILE* f_stderr = fopen(stderr_file, "r");
    if (f_stderr) 
    {
        size_t data_file = fread(stderr_output, 1, 8192, f_stderr);
        stderr_output[data_file] = '\0';
        fclose(f_stderr);
    }
    
    unlink(stdout_file);
    unlink(stderr_file);
    close(stdout_temp);
    close(stderr_temp);
    
    return return_status;
}

const char* gestion_output(int returncode, int is_ok, const char* stdout_output, const char* stderr_output, const int display) 
{
    bool is_good = (returncode == false) == (is_ok == true); // Sile code de retour est bon et doit etre bon alors juste, ou si les deux sont mauvais alors juste
    if (is_good) 
    {
        print_color(COLOR_GREEN, "[ OK ]", display);
    } 
    else 
    {
        print_color(COLOR_RED, "[ KO ]", display);
    }
    return strlen(stderr_output) > 0 ? stderr_output : stdout_output; // Si stderr est vide alors on affiche stdout
}

char* output_file_creat(const char* input_file) 
{
    char* copy = strcpy(malloc(strlen(input_file) + 1), input_file);
    char* output_file = malloc(1024);
    output_file = copy;
    output_file[strlen(output_file) - 5] = '\0';
    output_file = strcat(output_file, "-out.deca");
    return output_file;
}

int compare_files(const char* file1, const char* file2) 
{
    FILE *f1 = fopen(file1, "r");
    FILE *f2 = fopen(file2, "r");
    
    if (f1 == NULL || f2 == NULL) 
    {
        // On ferme celui qui est ouvert et qui n'est pas null
        if (f1)
        {
            fclose(f1);
        }
        if (f2) 
        {
            fclose(f2);
        }
        print_color(COLOR_RED, "[ KO ]", 1);
        return false;
    }

    char ch1, ch2;
    int is_identical = true;

    while (true) 
    {
        ch1 = fgetc(f1);
        ch2 = fgetc(f2);

        if (ch1 != ch2) 
        {
            is_identical = false;
            break;
        }

        if (ch1 == EOF || ch2 == EOF)
        {
            break;
        }
    }

    fclose(f1);
    fclose(f2);

    if (is_identical) 
    {
        print_color(COLOR_GREEN, "[ OK ]", true);
        return true;
    } 
    else 
    {
        print_color(COLOR_RED, "[ KO ]", false);
        return false;
    }
}

void gestion_compare_files(const char* directory, const int display) 
{
    char files[256][256];
    int file_count = 0;
    files_to_test(directory, files, &file_count);

    for (int i = 0; i < file_count; i++) 
    {
        char input_file[1024];
        snprintf(input_file, sizeof(input_file), "%s/%s", directory, files[i]);

        char* temp_file = output_file_creat(files[i]);
        char temp_path[1024];
        snprintf(temp_path, sizeof(temp_path), "%s/%s", directory, temp_file);

        char command[1024];
        snprintf(command, sizeof(command), "%s -p %s", DECA_FILE, input_file);

        char stdout_output[8192] = {0};
        char stderr_output[8192] = {0};
        
        int returncode = execute_command(command, stdout_output, stderr_output);

        const char* to_write = gestion_output(returncode, 1, stdout_output, stderr_output, display);
        launch_test_write_file(directory, temp_file, to_write);

        printf("Comparaison: %s -> %s: ", files[i], temp_file);
        compare_files(input_file, temp_path);

        unlink(temp_path);
        free(temp_file);
    }
}

void gestion_files(const char* input_dir, const char* output_dir, int is_ok, const int display) 
{
    char files[256][256];
    int file_count = 0;
    files_to_test(input_dir, files, &file_count);

    for (int i = 0; i < file_count; i++) 
    {
        char input_file[1024];
        snprintf(input_file, sizeof(input_file), "%s/%s", input_dir, files[i]);

        char* output_file = output_file_creat(files[i]);
        printf("%s -> %s: ", files[i], output_file);

        char command[1024];
        snprintf(command, sizeof(command), "%s -p %s", DECA_FILE, input_file);

        char stdout_output[8192] = {0};
        char stderr_output[8192] = {0};
        
        int returncode = execute_command(command, stdout_output, stderr_output);

        const char* to_write = gestion_output(returncode, is_ok, stdout_output, stderr_output, display);
        launch_test_write_file(output_dir, output_file, to_write);
        
        free(output_file);
    }
}

int main() {
    printf("%s[Test deca-p: C]\n=============%s\n", COLOR_BLUE, COLOR_NEUTRAL);

    nettoyage();

    printf("=============%s\n%s[Test OK]%s\n", COLOR_NEUTRAL, COLOR_BLUE, COLOR_NEUTRAL);
    gestion_files(TEST_DIR_OK, OUT_DIR_OK, true, true);

    printf("=============%s\n%s[Test KO]%s\n", COLOR_NEUTRAL, COLOR_BLUE, COLOR_NEUTRAL);
    gestion_files(TEST_DIR_KO, OUT_DIR_KO, false, true);

    printf("=============%s\n%s[Comparaison des fichiers]%s\n", COLOR_NEUTRAL, COLOR_BLUE, COLOR_NEUTRAL);
    gestion_compare_files(OUT_DIR_OK, false);

    printf("=============%s\n", COLOR_NEUTRAL);
    return 0;
}