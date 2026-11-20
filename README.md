# Programming in Java — Assignment 5 (CO5)

**Repository:** CSA0909-1911260190-ASSIGNMENT5  
**Topics:** Java I/O, files, serialization, JDBC, transactions and database metadata.

## Structure

- `Task01`–`Task05`: Part A design answers, record layouts, pseudocode, analysis and alternatives.
- `Task06`: Part B1 file store and buffered line counter.
- `Task07`: Part B2 adversarial input and PreparedStatement login demonstration.
- `Task08`: Part B3 Statement / PreparedStatement / batch experiments.
- `Task09`: Part B4 file-to-database migration and metadata inspection.

## Requirements

- JDK 17 or later
- MySQL Server 8.x
- MySQL Connector/J on the classpath for JDBC tasks

Set database credentials as environment variables before running JDBC programs:
- `DB_URL` (example: `jdbc:mysql://localhost:3306/assignment5`)
- `DB_USER`
- `DB_PASSWORD`

Do not commit real passwords.

## Compile and run

Open a terminal in the relevant task folder:

```bash
javac taskN.java
java taskN
```

For JDBC tasks, add MySQL Connector/J to the classpath. On Windows:

```powershell
javac -cp ".;mysql-connector-j-<version>.jar" taskN.java
java -cp ".;mysql-connector-j-<version>.jar" taskN
```

Each task folder contains its own README with task-specific steps and expected behaviour.

## Important submission note

Run the programs on your own machine and add your actual console/database output screenshots and measured timings to the relevant task folders. The repository provides reproducible code and report templates; it does not claim hardware-specific benchmark results or screenshots that have not been collected.
