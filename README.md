# Deca Compiler - ENSIMAG Software Project

The Deca compiler is a comprehensive Software Engineering project developed during my second year at Ensimag. It compiles Deca, a simplified object-oriented sublanguage of Java, designed to teach the fundamentals of compiler construction. The compiler processes Deca code through lexical analysis, syntax analysis, and code generation, producing assembly code for the IMA (Intermediate Machine Architecture), a virtual machine created for educational purposes.

Originally hosted on GitLab with GitLab CI for continuous integration, the CI tests have not been migrated to GitHub Actions.

## 🚀 Features

- **Lexical Analysis**: Tokenizes Deca source code
- **Syntax Analysis**: Parses tokens into an Abstract Syntax Tree (AST)
- **Code Generation**: Produces IMA assembly code
- **Testing Framework**: Comprehensive test scripts for each compilation phase
- **Educational Focus**: Designed to demonstrate compiler construction principles

## 📁 Project Structure

```
├── src/
│   ├── main/
│   │   └── bin/
│   │       └── decac          # Main compiler executable
│   └── test/
│       └── script/
│           └── launchers/     # Test launchers
├── planning/
│   └── Planning.pdf          # Project timeline and milestones
└── docs/                     # Code documentation
```

## 🛠️ Getting Started

### Prerequisites

- Java Development Kit (JDK)
- Maven
- IMA virtual machine

### Building the Project

```bash
mvn compile
```

## 📖 Usage

### Lexical Analysis Testing

To see the tokens returned by the lexer:

```bash
mvn compile
./src/test/script/launchers/test_lex ./path/to/your/file.deca
```

### Syntax Analysis Testing

To test the abstract syntax tree generation:

```bash
mvn compile
./src/test/script/launchers/test_synt ./path/to/your/file.deca
```

### Full Compilation

To create an assembly file from Deca source code:

```bash
./src/main/bin/decac ./path/to/your/file.deca
```

This will generate:
```bash
./path/to/your/file.ass
```

### Running Compiled Code

To execute the generated assembly code using the IMA virtual machine:

```bash
ima ./path/to/your/file.ass
```

## 📋 Project Management

### Planning
The project timeline is located in the **planning** folder as **Planning.pdf**. This file should be updated regularly to track project progress and milestones.

### Documentation
Code documentation is developed incrementally throughout the project. Proper documentation practices are essential for maintaining code quality and facilitating team collaboration.

## 🧪 Testing

The project includes comprehensive testing scripts for each compilation phase:
- Lexical analysis tests
- Syntax analysis tests
- Code generation tests

Each phase can be tested independently to ensure the compiler functions correctly at every stage.

## 🎓 Educational Context

This project serves as a practical introduction to:
- Compiler design principles
- Lexical and syntax analysis
- Code generation techniques
- Software engineering best practices
- Team collaboration and project management

## 🔧 Technical Details

- **Target Language**: Deca (Java subset)
- **Output Format**: IMA assembly code
- **Architecture**: Multi-phase compiler design
- **Build System**: Maven
- **Testing**: Custom shell scripts

## 📝 Notes

- Originally developed with GitLab CI/CD integration
- CI tests require migration to GitHub Actions for current hosting
- Regular updates to planning documentation recommended
- Code documentation should be maintained alongside development

---

*This project was developed as part of the Software Engineering curriculum at Ensimag.*
