#!/usr/bin/env python3
from sys import argv
from random import randint
from datetime import datetime
import os

DIR = "src/test/deca/syntax/invalid/lexer"

def list_of_chars(nombre):
    output = []
    for i in range(nombre):
        output.append(chr(randint(1500, 55000)))
    return output

def options(args):
    nombre = 10
    if len(args) == 2:
        nombre = args[1]
        try:
            nombre = int(nombre)
        except ValueError:
            print("Le nombre doit être un entier")
            exit(1)
    return nombre

def description():
    date = datetime.now().strftime("%d/%m/%Y")
    output = f"""
    // Description:
    //    tests de programme lexicalement Incorrect.
    //
    // Resultats:
    //    Ligne 12 : Erreur de syntaxe. 
    //
    // Historique:
    //    cree le {date}\n\n"""
    return output

def write_content(char):
    output = "{\n" + f"\tprint({char})\n" + "}"
    return output

def gen_file(chars):
    for char in chars:
        with open(f"{DIR}/char_{ord(char)}.deca", "w") as f:
            f.write(description())
            f.write(write_content(char))

def supprimer_fichier():
    if os.path.exists(DIR):
        files = [file for file in os.listdir(DIR) if file.startswith("char_") and file.endswith(".deca")]
        for file in files:
            os.remove(os.path.join(DIR, file))
        print(f"{len(files)} fichiers supprimés dans {DIR}")
    else:
        print(f"Le répertoire {DIR} n'existe pas. Rien à supprimer.")


def main(nombre):
    chars = list_of_chars(nombre)
    supprimer_fichier()
    gen_file(chars)


if __name__ == "__main__":
    nombre = options(argv)
    main(nombre)