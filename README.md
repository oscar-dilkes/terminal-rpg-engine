# Terminal RPG Engine

Welcome to the Terminal RPG Engine, a versatile and customizable RPG engine designed for the terminal environment. This engine allows you to create and experience text-based role-playing games using entity files in the .dot format and action files in the .xml format. To get started, follow the instructions below:

## Table of Contents

1. [Requirements](#requirements)
2. [Installation](#installation)
3. [Configuration](#configuration)
4. [Demo](#demo)
5. [Usage](#usage)
6. [Example Files](#example-files)

## Requirements

Before using the Terminal RPG Engine, ensure you have the following prerequisites:

- Java Runtime Environment (JRE) installed on your machine.
- A terminal with support for Java applications.

## Installation

1. Download the Terminal RPG Engine files from the [GitHub repository](https://github.com/oscardilkes/terminal-rpg-engine).

2. Extract the contents to a directory of your choice.

3. Navigate to the root directory of the extracted files.

## Configuration

In the `config` directory, you will find example entity files with the .dot extension and action files with the .xml extension. Examine these files to understand the format and structure required for creating entities and defining actions.

## Demo

Explore the demo folder to test an example version of the game. Follow these steps:

1. Open your terminal.

2. Navigate to the `demo` directory within the Terminal RPG Engine root directory.

3. Launch the jar file "TerminalRPGEngine.jar" using the following command:

 ```bash
 java -jar TerminalRPGEngine.jar
 ```

## Usage
To create your own game:

1. Design entities based on the example .dot file in the 'config' directory.

2. Define actions based on the example .xml file in the 'config' directory.

3. Place these files in the config sub-directory within the directory containing the .jar.

## Example Files
### Entity (.dot) File Example:
```dot
digraph layout {
    splines = ortho;
    graph [ranksep="1" nodesep="1"];
    node [shape = "rect"];

    subgraph locations {

        subgraph cluster001 {
            node [shape = "none"];
            cabin [description = "A log cabin in the woods"];
            subgraph artefacts {
                node [shape = "diamond"];
                potion [description = "A bottle of magic potion"];
                axe [description = "A razor sharp axe"];
                coin [description = "A silver coin"];
            }
            subgraph furniture {
                node [shape = "hexagon"];
                trapdoor [description = "A locked wooden trapdoor in the floor"];
            }
        }
```
### Action (.xml) File Example:
```xml
<actions>
    <action>
        <triggers>
            <keyphrase>open</keyphrase>
            <keyphrase>unlock</keyphrase>
        </triggers>
        <subjects>
            <entity>trapdoor</entity>
            <entity>key</entity>
        </subjects>
        <consumed>
            <entity>key</entity>
        </consumed>
        <produced>
            <entity>cellar</entity>
        </produced>
        <narration>You unlock the door and see steps leading down into a cellar</narration>
    </action>
```
