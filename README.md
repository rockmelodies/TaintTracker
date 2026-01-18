# SerialGuardian (TaintTracker) 🛡️

![Java](https://img.shields.io/badge/Java-17%2B-orange)
![License](https://img.shields.io/badge/License-Apache%202.0-blue)
![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![JavaFX](https://img.shields.io/badge/GUI-JavaFX-blueviolet)

**SerialGuardian** is a professional Java Deserialization Vulnerability Audit Tool designed to help security researchers and developers identify potential security risks in their applications.

It combines static analysis (ASM) with project structure scanning to detect dangerous "sinks" (e.g., `readObject`, `Runtime.exec`) and visualize the findings in a modern JavaFX interface.

---

## ✨ Features

- **🔍 Automatic Project Discovery**: 
  - Instantly identifies `.java`, `.class`, and `.jar` files.
  - Scans `pom.xml` for dependencies (Mock implementation).
- **🧠 Bytecode Analysis Engine**: 
  - Uses **ASM** to inspect compiled bytecode directly.
  - Detects high-risk method calls (Sink points).
- **🎨 Modern GUI**: 
  - Built with **JavaFX** for a responsive desktop experience.
  - Interactive project tree and results table.
  - Real-time scanning progress visualization.
- **🛡️ Built-in Sink Database**: 
  - Covers `ObjectInputStream`, `XMLDecoder`, `Runtime`, `ProcessBuilder`, and JNDI lookups.

---

## 🚀 Getting Started

### Prerequisites

- **Java JDK 17** or higher.
- **Maven 3.8+**.

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/SerialGuardian.git
   cd SerialGuardian
   ```

2. Build the project:
   ```bash
   mvn clean package
   ```

### Usage

**Option 1: Run the Executable JAR (Recommended)**
   ```bash
   java -jar target/TaintTracker-1.0-SNAPSHOT.jar
   ```

**Option 2: Run via Maven**
   ```bash
   mvn javafx:run
   ```

### 🖥️ Workflow

1. Launch the application.
2. Click **File -> Open Project...**.
3. Select the root directory of the Java project you want to audit.
4. Watch the console for scan progress.
5. Review identified vulnerabilities in the **Vulnerabilities** tab.

---

## 🛠️ Architecture

```mermaid
graph TD
    A[Main GUI (JavaFX)] --> B[Project Scanner]
    B --> C[File Walker]
    B --> D[Dependency Analyzer]
    A --> E[Bytecode Analyzer (ASM)]
    E --> F[Sink Detector]
    F --> G[Vulnerability Database]
    A --> H[Config / SQLite DB]
```

- **Scanner**: Recursively finds class files and configuration descriptors.
- **Analyzer**: Visits class nodes and method instructions to find patterns matching known sinks.
- **GUI**: Handles user interaction and renders the `ScanContext` results.

---

## 📝 Configuration

Configuration is stored in `config.properties` and local scan history in `tainttracker.sqlite`.

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## ⚠️ Disclaimer

This tool is for **educational and security audit purposes only**. Do not use it on systems you do not have permission to test. The authors are not responsible for any misuse.

---

<p align="center">Made with ❤️ by the SerialGuardian Team</p>
