# Projet Génie Logiciel, Ensimag.


## Le Projet :
### Liens :
Lien vers le [Projet-GL](https://chamilo.grenoble-inp.fr/courses/ENSIMAG4MMPGL6/index.php) sur Chamillo. 

Lien vers la [Documentation](https://projet-gl.pages.ensimag.fr).

### Planning : 
Le planning se situe dans le dossier **planning** et se nomme **Planning.pdf**
Ce fichier est a mettre a jour régulièrement afin de mettre en lumiére notre avancé.

### La Documentation : 
Il est nécessaire de faire petit a petit notre documentation de code. Il est dit de la faire mais je ne sais pas exactement comment faire.

### Lancement : 
Pour voir les tokens reconnus par le lexer, il suffit de lancer la comamnde suivante :
```sh
mvn compile
./src/test/script/launchers/test_lex ./nom/fichier/a/lexer/en/format/fichier.deca
```

Pour voir la syntaxe abstraite, il suffit de lancer la commande suivante : 
```sh
mvn compile
./src/test/script/launchers/test_synt ./nom/fichier/a/lexer/en/format/fichier.deca
```

Pour creer le fichier assebleur il suffit de lancer la commande suivante : 
```sh
./src/main/bin/decac ./nom/fichier/en/format/fichier.deca
```

Il en sortira le fichier :
```sh
./nom/fichier/en/format/fichier.ass
```

Pour pouvoir l'executer il faut utiliser **ima** avec la commande suivante : 
```sh
ima ./nom/fichier/en/format/fichier.ass
```
