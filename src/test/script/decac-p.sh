#!/bin/sh
DIR="./launchers-test"
DECA_OPTIONS="deca-options/p"
BIN="./bin"

PYTHON_SCRIPT="$DIR/$DECA_OPTIONS/deca-p.py"
C_SOURCE="$DIR/$DECA_OPTIONS/deca-p.c"
JAVA_SCRIPT="$DIR/$DECA_OPTIONS/DecaP.java"

C_BINARY="$BIN/deca-p"
JAVA_BINARY="$BIN/launchers/test/deca/options/p/DecaP.class"
JAVA_FILE="launchers.test.deca.options.p.DecaP"

# Rendre le script Python exécutable
if [ -f "$PYTHON_SCRIPT" ]; then
    chmod +x "$PYTHON_SCRIPT"
else
    echo "Erreur : Script Python introuvable : $PYTHON_SCRIPT"
    exit 1
fi

# Compilation du fichier C si nécessaire
compile_c() {
    if [ ! -f "$C_BINARY" ]; then
        mkdir -p "$BIN"
        gcc $CFLAGS "$C_SOURCE" -o "$C_BINARY"
        if [ $? -eq 0 ]; then
            echo "Compilation C terminée avec succès."
        else
            echo "Erreur lors de la compilation C."
            exit 1
        fi
    fi
}


# Compilation du fichier Java si nécessaire
compile_java() {
    if [ ! -f "$JAVA_BINARY" ]; then
        mkdir -p "$BIN"
        javac -d "$BIN" "$JAVA_SCRIPT"
        if [ $? -eq 0 ]; then
            echo "Compilation Java terminée avec succès."
        else
            echo "Erreur lors de la compilation Java."
            exit 1
        fi
    fi
}

case "$1" in
    py)
        ./"$PYTHON_SCRIPT"
        ;;
    c)
        compile_c
        ./"$C_BINARY"
        ;;
    java)
        compile_java
        java -cp "$BIN" "$JAVA_FILE"
        ;;
    "")
        compile_c
        compile_java
        ./"$PYTHON_SCRIPT"
        ./"$C_BINARY"
        java -cp "$BIN" "$JAVA_FILE"
        ;;
    perf)
        compile_c
        compile_java
        time ./"$PYTHON_SCRIPT"
        time ./"$C_BINARY"
        time java -cp "$BIN" "$JAVA_FILE"
        ;;
    *)
        echo "Valeur de ARGS invalide : $ARGS. Utilisez 'py', 'c', 'java', ou laissez vide."
        exit 1
        ;;
esac
