
/==================== "BEYOND gOOD" SPREADSHEET DOCUMENTATION ====================/

/===== Design for Version 3.0 =====/


Column References (Major Change #1):

 - Added ColumnReference, which extends Reference and is therefore a new type of Reference, which is sensible.

 - CellGrid is a new way to store cells, and is a parameter of CellContext.

 - CellGrid.getCol(int col) and CellGrid.getRow(int row) now exist, both returning a generic Set<T>, making it easy to iterate through specific rows and columns.

   - This will allow us to easily implement row references, as well, because the code for checking and retrieving sets of cells is already there. We can refactor ColumnReference to look at rows (numbers) rather than a conversion of columns (letters).

Workbooks (Major Change #2):

 - Reformatted Program to handle Workbooks, which contain Spreadsheets.

 - All dependency checking has been moved to workbooks, as they can look at all the spreadsheets in the workbook whereas a spreadsheet can only look at its own cells.

 - IMPORTANT:

   - File Formatting has changed. If you want to view a file with multiple spreadsheets, the file must look like:

	spreadsheet_name{
	#Cell Input, example:
	A1 =(SUM 1 1)
	}

   - Here is an example of multiple spreadsheets, which is built upon in testColumnReferences.txt (located in resources) :

	Spreadsheet1{
	A1 =(SUM 1 1)
	A2 =(PRODUCT A1 3)
	B2 =(SUM A1 A2)
	B3 Spreadsheet2!A3
	}
	Spreadsheet2{
	A1 =(SUM Spreadsheet1!A1 4)
	A2 =(SUM Spreadsheet1!A:A)
	A3 =(PRODUCT Spreadsheet1!A1:B2)
	}

   - Notice the syntax for referring to other spreadsheets, and that a reference to another spreadsheet is only used once for each kind of reference (Spreadsheet1!A1:B2 rather than Spreadsheet1!A1:Spreadsheet1!B2).

 - If the bracket format is not present, and the file is therefore in the old format, our program will assume that all input is meant for one spreadsheet, and will build as such.

 - The workbook tab feature is located at the bottom of the window when using the graphical views. A left click will change your view to be the spreadsheet indicated by clicked tab.

 - If your selector is on a different tab than the one you are currently viewing, and you make any update to the selector (arrow keys, etc.), the view will snap to where your selector is.

 - The majority of tests are the same as before, just reformatted to use Workbooks. These were still used in order to test that our dependencies and references worked at the Workbook level. For tests using larger amounts of cells/data, we simply used the editor to create new cells at-will and checked their values.


/========== PREVIOUS VERSIONS: ==========/


/===== Design for v2.1 =====/


Working:

 - Opening the editor

 - Saving a currently open file to save.txt

 - Formatting looked correct based on the provided screenshot, but the colors for Confirm/Cancel buttons (Green/Red, respectively) only appeared on Windows.

 - Cell selector graphics. This also looked much better on Windows than it did on Mac, see Code Review for notes.

 - Delete key, only on Windows (no, fn+delete does not work on Mac).

 - Arrow key and cursor selections.

 - Confirming / Cancelling edits, and seeing the changes reflected in all cells (dependency recalculation) 

Not Working:

 - Nothing that we could see in the provider implementation was not working. All features appeared to work as intended.

 - Mac compatibility errors.


/===== Design for v2.0 =====/


 - Added support in main for '-edit' opening blank / pre-existing files in the editor.

 - Next to our already existing text box intended for editing, we have added a confirm / deny change button area, with each button marked by a check or an 'X', respectively.
	 - Worth noting that in v1.0, we already had a perfectly functioning infinite scroll and selection mechanism.

 - We have changed the GraphicalView to be an Interface, now having a LockedGraphicalController and an EditableGraphicalController as separate classes to distinguish between read-only views and a writeable view.

 - EditableGraphicalController uses the method 'private void update(...)' (line 42) to update an edited cell and its dependencies after an edit. Please note that located within this method, and a few other methods, there are prints to the System. This is to ensure that the program is performing the intended action on the intended items (cells) as it is running. This information will not interfere with use of the graphical program, and is available to the user if they wish.

 - Functionality for all of the KeyActions is located with GraphicalMapper.java. Please note that pressing 'TAB' will also move the selector, just like Google Sheets or Excel!

 - !!! We have now implemented 2 extra functionalities: using arrow keys to move the user's selected cell, and using the delete key to clear a cell's contents.

 - testTriangular.txt : Contains cells A1-A50, demonstrating calculation of the first 50 triangular numbers in succession. Note that changing the value of one cell will immediately change the value of all cells depending on that one cell, resulting in an error if a circular dependency is created.

The JAR is called code.jar


/===== Design Changes from v1.0 to v2.0 =====/


 - Fixed Caching for Formulas (Formulas now clear BOTH their cache and their components' caches)

 - Fixed JavaDoc comments and Tests based on feedback.

 - main in BeyondGood now exits gracefully (popup window with error message) if an invalid / incorrect cmd line input is given.

 - Raw values are now stored in a reachable place, so as to make them 'editable' via the editor.


/===== Design for v1.0 =====/


Light bug fixes within the model, mainly pertaining to local nested formulas.
Added read-only shell for Spreadsheet.
Added function to get a copy of the set of all non-empty cell coordinates to aid in scrolling and saving.

We decided views should be able to render raw and actual values for each cell. This makes up the entirety of the view interface (for now?).

We used a custom JScrollBar to control scrolling, and allow for 2D scrolling on Mac (only 1D supported on Windows) through a JMouseWheelListener. The grid of the spreadsheet is a persistent grid of JButtons whose contents change upon scrolling, rather than moving the buttons themselves. This was done to make the initial coding easier, but means that implementing smooth scrolling would be difficult.
We also decided to put the cell editor field in its own location above the grid, rather than replacing the selected cell.

The textual view gets a list of all the occupied cells and writes the raw values to the output Appendable, so as to avoid 'baking' all formulas upon saving.

Test1: Displays scrolling and off-screen references (Z5 and Z6). Also demonstrates cell clipping.
Test2: Demonstrates Strings, Booleans, Error cells, and automatic scroll bar sizing.
Test3: Demonstrates arithmetic functions, including nested functions and functions with references.

The JAR is called code.jar


/===== Design Changes from v0.5 to v1.0 =====/


 - In terms of the model's internal logic, the spreadsheet itself now caches evaluated cells for faster retrieval and calculation of formulas, especially nested formulas (the one test that we failed on Handins tests for this). TLDR: Cache cell output.

 - Exposed coords and a limited version of cells for the view/controller to use in future (1.0 and beyond) versions.


/==================== END ====================/