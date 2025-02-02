#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys
import os
import subprocess
from pathlib import Path

COLOR = {
    "GREEN": "\033[92m",
    "RED": "\033[91m",
    "BLUE": "\033[94m",
    "NEUTRAL": "\033[0m"
}

TEST_DIR_OK = "src/test/deca/options/option-p/test/ok"
TEST_DIR_KO = "src/test/deca/options/option-p/test/ko"

OUT_DIR_OK = "src/test/deca/options/option-p/out/ok"
OUT_DIR_KO = "src/test/deca/options/option-p/out/ko"

DECA_FILE = "src/main/bin/decac"

POS = 96

def print_color(color, string, display=True, info_align=(0, 0)):
    taille = info_align[0]
    pos = info_align[1]
    if display:
        print(f"{color}{string:>{pos - taille}}{COLOR['NEUTRAL']}")


def files_to_test(input_dir):
    files = []
    for file in os.listdir(input_dir):
        if file.endswith(".deca"):
            files.append(file)
    return files


def launch_test_write_file(output_dir, file, output):
    out_file = output_dir + "/" + file
    if os.path.exists(output_dir) is False:
        os.mkdir(output_dir)
    with open(out_file, "w") as f:
        f.write(output)


def gestion_output(succes, is_ok, display=False, taille=0):
    obj_return = 0 if is_ok else 1
    output = succes.stdout if is_ok else succes.stderr
    if succes.returncode == obj_return:
        print_color(COLOR["GREEN"], "✅", display, (taille, POS))
        return output
    else:
        print_color(COLOR["RED"], "❌", display, (taille, POS))
        return output


def nettoyage():
    affiche = "Nettoyage: "
    print(affiche, end="")
    taille = len(affiche)
    
    for dir_path in [OUT_DIR_OK, OUT_DIR_KO]:
        dir_path = Path(dir_path)
        for file in dir_path.glob("*"):
            file.unlink()

    out = subprocess.run(["ls", "-A", str(OUT_DIR_OK)], capture_output=True, text=True)
    if out.stdout == "":
        print_color(COLOR["GREEN"], "✅", True, (taille, POS))
    else:
        print_color(COLOR["RED"], "❌", True, (taille, POS))


def gestion_files(input, output, is_ok, display=False):
    files = files_to_test(input)
    for file in files :
        output_file = file.replace(".deca", "-out.deca")
        affiche = file + " -> " + output_file + ": "
        print(affiche, end="")
        succes = subprocess.run([DECA_FILE, "-p", input + "/" + file], capture_output=True, text=True)
        taille = len(affiche)
        to_write = gestion_output(succes, is_ok, display, taille)
        launch_test_write_file(output, output_file, to_write)


def compare_files(file1, file2, taille):
    succes = subprocess.run(["diff", file1, file2], capture_output=True, text=True)
    if succes.returncode == 0:
        print_color(COLOR["GREEN"], "✅", True, (taille, POS))
        return True
    else:
        print_color(COLOR["RED"], "❌", True, (taille, POS))
        return False


def gestion_compare_files(directory, display=False):
    """Comparaison des fichiers dans le répertoire directory et de ces memes fichiers mais compiler une fois de plus"""
    files = files_to_test(directory)
    for file in files:
        temp_file = file.replace(".deca", "-out.deca")
        succes = subprocess.run([DECA_FILE, "-p", directory + "/" + file], capture_output=True, text=True)
        to_write = gestion_output(succes, True, display)
        launch_test_write_file(directory, temp_file, to_write)
        affiche = "Comparaison: " +  file + " -> " + temp_file + ": "
        print(affiche, end="")
        taille = len(affiche)
        compare_files(directory + "/" + file, directory + "/" + temp_file, taille)
        subprocess.run(["rm", "-f", directory + "/" + temp_file])


def main():
    is_ok = True

    print_color(COLOR["BLUE"], "[Test deca-p: Python]")
    print("=============")
    nettoyage()

    print("=============")
    print_color(COLOR["BLUE"], "[Test ok]")
    gestion_files(TEST_DIR_OK, OUT_DIR_OK, is_ok, True)

    print("=============")
    print_color(COLOR["BLUE"], "[Test ko]")
    gestion_files(TEST_DIR_KO, OUT_DIR_KO, not is_ok, True)

    print("=============")
    print_color(COLOR["BLUE"],"[Comparaison des fichiers]")
    gestion_compare_files(OUT_DIR_OK)

    print("=============")

if __name__ == "__main__":
    main()