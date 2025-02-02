#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import subprocess
import os
from sys import argv
from concurrent.futures import ProcessPoolExecutor

COLOR = {
    "GREEN": "\033[92m",
    "RED": "\033[91m",
    "BLUE": "\033[94m",
    "NEUTRAL": "\033[0m"
}

DECA_FILE = "src/main/bin/decac"


def print_color(color, string, display=True, end="\n"):
    if display:
        print(color + string + COLOR["NEUTRAL"], end=end)


def files_to_test():
    """Commande  : find . -name "*.deca" | grep "/valid/" > files.txt"""
    succes = subprocess.run(["find . -name \"*.deca\" | grep \"/valid/\""], shell=True, capture_output=True, text=True)
    files = succes.stdout.strip().split("\n")
    return files


def verif_create_dir(path):
    try:
        os.makedirs(path)
    except FileExistsError:
        pass


def gestion_output(succes):
    if succes.returncode == 0:
        return True, None
    else:
        return False, succes.stderr


def process_file(file):
    """Process a single file with the DECA tool."""
    succes = subprocess.run([DECA_FILE, "-v", file], capture_output=True, text=True)
    success, error = gestion_output(succes)
    return file, success, error


def write_error_files(error_files):
    if not error_files:
        print_color(COLOR["GREEN"], "Tous les fichiers ont été compilés avec succès")
    else:
        print_color(COLOR["NEUTRAL"], "Les fichiers problématiques ont été listés dans le fichier : log/deca-v/error_files.txt")
    verif_create_dir("log/deca-v")
    with open("log/deca-v/error_files.txt", "w") as file:
        for file_name, error in error_files.items():
            file.write(f"{file_name} : {error}\n")


def test_v(files, display):
    error_files = {}
    with ProcessPoolExecutor() as executor:
        results = executor.map(process_file, files)
    
    for file, success, error in results:
        print_color(COLOR["NEUTRAL"], f"file : {file}", display, end="")
        if success:
            print_color(COLOR["GREEN"], "[ OK ]", display)
        else:
            print_color(COLOR["RED"], "[ KO ]", display)
            if display:
                print_color(COLOR["NEUTRAL"], error, display)
            error_files[file] = error

    write_error_files(error_files)
    return len(error_files)


def gere_display(argv):
    display = True
    check_out = 0
    if len(argv) == 1:
        return True, 0
    elif len(argv) >= 2:
        for arg in argv[1:]:
            if arg not in ["-q", "-c"]:
                print("Usage : python3 deca-v.py [-q]")
                exit(1)
            if arg == "-q":
                display = False
            if arg == "-c":
                check_out = 1
    else:
        print("Usage : python3 deca-v.py [-q]")
        exit(1)
    return display, check_out


def main(display, check_out):
    files = files_to_test()

    print_color(COLOR["BLUE"], "[Test deca-v: Python]")
    nombre_ko = test_v(files, display)

    print("=============")
    print_color(COLOR["BLUE"], "[Résultat]")
    print(f"Nombre de fichiers compilés : {len(files) - nombre_ko}/{len(files)}")
    if nombre_ko != 0:
        exit(check_out)
    else:
        exit(0)


if __name__ == "__main__":
    display, check_out = gere_display(argv)
    main(display, check_out)
