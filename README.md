# Projet Génie Logiciel, Ensimag.
Groupe : gl14
Équipe : 
- Adnet Willem
- Alga Nour
- Da Cruz Édouard
- Stablo Romain
- Zabiego Hugo

## Conseil de porgrammation : 
### Git : 
#### Pull :
Afin d'éviter les confilts et les problème de merge, je vous conseille d'utiliser l'option rebase de git pull :
```sh
git pull --rebase
```

### Commit :
#### Installation : 
Afin d'avoir des commit(s) de qualité c'est a dire qui permettent au lecteur de comprendre ce qui a été fait, je vous conseil d'utiliser la technologie [commitizen](https://commitizen-tools.github.io/commitizen/).
Je vous laisse aller voir la documentation mais voici les methodes d'instalations :
```sh
pipx ensurepath
pipx install commitizen
pipx upgrade commitizen
```

Pour macOS : 
```sh
brew install commitizen
```

Ou alors vous pouvez utiliser ma version de commitizen : 
```sh
git clone git@github.com:Vlor999/Commitizen-Multilanguage.git
```

Puis lancer la commande suivante pour le configurer corrcetement. 
```sh
cd Commitizen-Multilanguage
./launch-init.sh
```

Si vous n'avez pas poetry : 
```
pip install poetry
```

#### Utilisation : 
Rien de plus simple : 
```sh
cz commit
```
Et lisez !
Si vous utilisez ma version, vous pouvez en plus avoir les explications en différentes langues en écrivant :
```sh
cz -language fr commit
```

**fr** peut-être remplacé par tout ce que vous voulez comme langage.

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
