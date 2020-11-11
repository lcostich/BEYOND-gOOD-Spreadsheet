# BEYOND-gOOD-Spreadsheet
CS3500 (Object-Oriented Design) F19 Project - A Spreadsheet Program by Lee Costich and Lowell Camp, 2019.

built using the [Pair Programming](https://en.wikipedia.org/wiki/Pair_programming) development technique.

The purpose of this program is to be an attempt at cloning popular spreadsheet programs such as Microsoft Excel or Google Sheets, complete with filesaving, multi-tab spreadsheets, cross-cell references, infinite sheet scalability, and several custom cell functions. Currently available functions are CONCAT for Strings, SUM, PRODUCT, and Less Than ("<"), each of which will work on cells in multiple different spreadsheets across a workbook. Currently available references are area references and column references.

# Development
This program was developed over the course of two months by Lee Costich and Lowell Camp as the final assignment project for CS3500 at Northeastern University. All files in the [sexp](code/src/edu/cs3500/spreadsheets/sexp/) folder were provdided by the class administrators. This folder set the foundation for the project, as different data inputs in the spreadsheet are all treated as Symbolic Expressions, and the Parser (found in [Parser.java](code/src/edu/cs3500/spreadsheets/sexp/Parser.java)) is essential for reading input 'save' files.

We began by building the model and improving the efficiency of cell references, moving on to focus on the textual view (reading/writing files) and graphical view, fixing bugs and adding features in as we went along. A more complete record of design ideas, changes, by-version feature implementations, and instructions for how to create a properly-formatted input file can be found in the textual README [here](code/resources/README.txt). All test files can be found in the [Resources](code/resources) folder, and are a good starting point for playing around with the program. Pictures of what the program looks like with various test files loaded can be found [here](code/resources/Screenshots/), in the Screenshots folder.

Testing for the program was completed using JUnit, and all test libraries can be found within the test folder, [here](code/test/edu/cs3500/spreadsheets).

# Remarks
Commits do not strictly reflect the percentage of each partner's contribution to this project, as Pair Programming was used for every single part of the project, and was invaluable to the collaborative process.

Privacy of this repository is subject to change, as CS3500 administrators may choose to re-use the Spreadsheet project in a later semester. This repository was created to house a final version of the project, removing more sensitive class information stored in prior commits.
