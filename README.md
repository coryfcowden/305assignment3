# CSC 305 FINAL PROJECT
*Cory Cowden and Xiomara Alcala*

1. Open the Project
  - Click File > Open
  - Select the project folder
  - Wait for IntelliJ to import dependencies *(Maven will load everything!)*
2. Run the Application
  - Open the file 
    ``src/main/java/org/example/Main.java``
  - Right click inside the file
  - Select **Run 'Main.main()'**

This launches the full UI
3. Use the Application
  - Once the Window opens:
  - Paste a **Github folder URL**
  - Example: ``https://github.com/coryfcowden/305assignment3/tree/main``
  - Click **OK**
  - Wait a few seconds while
  - Files download, metrics are calculated, UML is generated

All panels update automatically:
- **Grid Tab:** File complexity * size
- **Metrics Tab:** Abstractness/Instability Diagram
- **Diagram Tab:** Full UML class diagram
- **Left Panel:** File explorer
- **Bottom Bar:** Status updates

# Main Purpose of the Project
This tool helps developers analyze any Java codebase by:
- Fetching source files from GitHub
- Parsing class relationships
- Measuring software quality metrics
- Displaying UML diagrams and visual summaries

It is essentially a lightweight code analysis tool built using:
- Java Swing
- Regex-based code parsing
- PlantUML
- A clean MVC-like architecture using a shared Blackboard model