#! /bin/sh

chmod +x launchers-test/deca-options/v/deca-v.py

quiet=false

gestion_args(){
    if [ "$1" = "-q" ]; then
        quiet=true
    elif [ "$1" = "" ]; then
        quiet=false
    else
        echo "Erreur : Argument invalide."
        exit 1
    fi
}

gestion_args $1

if $quiet ; then
    ./launchers-test/deca-options/v/deca-v.py -q
else
    ./launchers-test/deca-options/v/deca-v.py
fi

if [ $? -ne 0 ]; then
    echo "Erreur : launchers-test/deca-options/v/deca-v.py"
    exit 1
fi

