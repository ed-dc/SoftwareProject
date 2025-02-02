#!/bin/sh

# Auteur : gl14
# Version initiale : 01/01/2025

# Gestion des arguments pour le mode verbose
verbose=false
log_reset=false
parallelexec=false

compteur=0
all_tests=0

while [ "$#" -gt 0 ]; do
    case "$1" in
        -v|--verbose)
            verbose=true
            ;;
        -n)
            log_reset=true
            ;;
        -p)
            parallelexec=true
            ;;
        *)
            echo "Usage: $0 [-v|--verbose] [-n]" >&2
            exit 1
            ;;
    esac
    shift
done

# Fichier de log où la sortie sera redirigée, création si n'existe pas
if [ ! -d "log/test/token" ]; then
    mkdir -p log/test/token
fi

log_file="log/test/token/output_$(date '+%Y%m%d').log"

# Si le mode -n est activé, réinitialisation du fichier de log
if [ "$log_reset" = true ]; then
    echo "Début des tests : $(date)" > "$log_file"
else
    echo "Début des tests : $(date)" >> "$log_file"
fi

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

# Fonction d'affichage conditionnel
log() 
{
    if [ "$verbose" = true ]; then
        echo "$1"
    fi
    echo "$1" >> "$log_file"
}

format_output() 
{
    printf "Résultat test_lex sur %-90s : %s\n" "$1" "$2"
}

test_lex_invalide() 
{
    if test_lex "$1" 2>&1 | tee -a "$log_file" | grep -q -e "$1:[0-9][0-9]*:"
    then
        etat="✅"
        compteur=$((compteur+1))
    else
        etat="❌"
    fi
    echo $etat > $2
    all_tests=$((all_tests+1))
    output=$(format_output "$1" "$etat")
    log "$output"
}

test_lex_valide() {
    if test_lex "$1" 2>&1 | tee -a "$log_file" | grep -q -e "$1:[0-9][0-9]*:"
    then
        etat="❌"
    else
        etat="✅"
        compteur=$((compteur+1))
    fi
    echo $etat > $2
    all_tests=$((all_tests+1))
    output=$(format_output "$1" "$etat")
    log "$output"
}

valid_test(){
    # Tester tous les fichiers valides
    compteur_valid=0
    for cas_de_test in $(find . -name "*.deca" | grep "/valid/")
    do
        if [ -f "$cas_de_test" ]; then
            file=log/lex/valid-$compteur_valid.output
            if [ -f "$file" ]; then
                rm $file
            fi
            touch $file
            test_lex_valide "$cas_de_test" $file
            compteur_valid=$((compteur_valid+1))
        fi
    done
}

invalid_test(){
    # Tester un cas d'erreur de syntaxe spécifique
    compteur_invalid=0
    for cas_de_test in $(find . -name "*.deca" | grep "/invalid/lexer/")
    do
        if [ -f "$cas_de_test" ]; then
            file=log/lex/invalid-$compteur_invalid.output
            if [ -f "$file" ]; then
                rm $file
            fi
            touch $file
            test_lex_invalide "$cas_de_test" $file
            compteur_invalid=$((compteur_invalid+1))
        fi
    done
}

compte_valid(){
    for file in $(find log/lex -name "*.output")
    do
        if [ "$(cat $file)" = "✅" ]; then
            compteur=$((compteur+1))
        fi
        all_tests=$((all_tests+1))
    done
}

#clean the dir : log/lex if exist
if [ -d "log/lex/" ]; then
    rm -r log/lex/
    mkdir -p log/lex/
else
    mkdir -p log/lex/
fi 

#Lancement en parallele des tests
if [ "$parallelexec" = true ]; then
    valid_test &
    invalid_test &
    wait
    compte_valid
else
    valid_test
    invalid_test
fi


log "Analyse des résultats du fichier : $log_file"
if grep -q "Succes inattendu" "$log_file" || grep -q "Echec inattendu" "$log_file"; then
    etat="❌"
else
    etat="✅"
fi

log "Nombre de tests réussis : $compteur/$all_tests"
log "Résultat des tests : $etat"
if [ "$etat" = "❌" ]; then
    exit 1
fi
exit 0

