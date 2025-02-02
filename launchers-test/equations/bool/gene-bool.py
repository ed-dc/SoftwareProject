#! /usr/bin/env python3

import sys
import os
import subprocess
from random import randint
from datetime import datetime


liste_booleen = ["true", "false"]
liste_operateurs = ["&&", "||", "!"]
lettre = "abcdefghijklmnopqrstuvwxyz"

DIR = "src/test/deca/context/valid/binaire/classique/"
DECA_FILE = "src/main/bin/decac"
IMA = "ima"


def isDirectory(path):
    return os.path.isdir(path)

def gere_args():
    nombre = 10
    display = False
    if len(sys.argv) > 3:
        print("Usage: python3 gene-bool.py <nombre> [-d]")
        sys.exit(1)
    for arg in sys.argv[1:]:
        if arg.isdigit():
            nombre = int(arg)
        elif arg == "-d":
            display = True
    return nombre, display

def clean_dir():
    for file in os.listdir(DIR):
        os.remove(DIR + file)
    print("Directory cleaned : ✅")

def creation_dir():
    if isDirectory(DIR):
        clean_dir()
        print("Directory already exists")
        return
    os.mkdir(DIR)
    print("Directory created : ✅")

def found_value(output):
    python_conv = {"&&": "and", "||": "or", "!": "not ", "true": "True", "false": "False", ";": ""}
    for key, value in python_conv.items():
        output = output.replace(key, value)
    value = eval(output)
    value = value.__str__().lower()
    return value

def genere_fichier():
    # Le principe est de generer un fichier de test valide qui creer des tests sur les booleans de la forme :
    # a = true;
    # b = false;
    # c = (a && b) || a || b && !a;
    # d = c && !a;
    # print(c);
    # de facon aleatoire
    taille = randint(1, 50)
    output = ""
    for i in range(taille):
        op = liste_operateurs[randint(0, 2)]
        if op == "!":
            output += f"{op}{liste_booleen[randint(0, 1)]} {liste_operateurs[randint(0, 1)]} "
        else:
            output += f"{liste_booleen[randint(0, 1)]} {op} "
    output += f"{liste_booleen[randint(0, 1)]};"

    value = found_value(output)
    symbole = lettre[randint(0,25)]
    output = "\n\tboolean " + symbole + " = " + output
    output += f"\n\tprint({symbole});\n"

    return output, value

def gene_tete(value):
    """Format  : 
        // Description:
        //    Programme minimaliste initialisant une variable et la modifiant.
        //
        // Resultats: res
        //    
        //
        // Historique:
        //    cree le 01/01/2025
    """
    date = datetime.now().strftime("%d/%m/%Y")
    tete = "// Description:\n"
    tete += "//    Programme test boolean automatique\n"
    tete += "//\n"
    tete += f"// Resultats:\t{value}\n//\n"
    tete += "// Historique:\n"
    tete += f"//    cree le {date}\n\n"
    return tete

def genere_fichiers(nombre, display=False):
    list_fichier = []
    for i in range(1, nombre + 1):
        filename = f"bool-{i}.deca"
        with open(DIR + filename, "w") as file:
            output, value = genere_fichier()
            tete = gene_tete(value)
            file.write(tete)
            file.write("{")
            file.write(output)
            file.write("}")
        if display:
            print(f"File {filename} generated : ✅")
        list_fichier.append(filename)
    return list_fichier

def expected(file_deca):
    with open(DIR + file_deca, "r") as f:
        for line in f:
            if "Resultats" in line:
                txt = line.split(":")[1].replace("\t", "").strip()
                return txt
    return "Error"

def launch_test(file_ass, display=False):
    succes = subprocess.run([IMA, DIR + file_ass], capture_output=True, text=True)
    if succes.returncode == 0:
        output = succes.stdout.strip()
        file_deca = file_ass.replace(".ass", ".deca")
        expected_result = expected(file_deca)
        if output == expected_result:
            if display:
                print(f"Test {file_ass} : ✅")
            return 1
        else:
            if display:
                print(f"Test {file_ass} : ❌")
                print(f"Expected : {expected_result}")
                print(f"Got : {output}")
    else:
        if display:
            print(f"Test {file_ass} : ❌")
            print(succes.stderr)
    return 0

def test_operation(liste_fichier, display=False):
    liste_outfiles = {}
    total = 0
    ok = 0
    for file in liste_fichier:
        if display:
            print(f"Testing {DIR + file} : ", end="")
        expected_result = expected(file)
        outfile = file.replace(".deca", ".ass")
        succes = subprocess.run([DECA_FILE, DIR + file], capture_output=True, text=True)
        if succes.returncode == 0:
            if display:
                print("✅")
            liste_outfiles[outfile] = expected_result
        else:
            if display:
                print("❌")
                print(succes.stderr)
    for file in liste_outfiles:
        ok += launch_test(file, display)
        total += 1
    print(f"Réussi : {ok}/{total}")
    

def main():
    nombre, display = gere_args()
    creation_dir()
    liste_fichier = genere_fichiers(nombre, display)
    test_operation(liste_fichier, display)
    

if __name__ == "__main__":
    main()
