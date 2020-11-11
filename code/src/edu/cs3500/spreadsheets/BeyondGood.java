package edu.cs3500.spreadsheets;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringReader;

import javax.swing.JOptionPane;

import edu.cs3500.spreadsheets.controller.EditableGraphicalController;
import edu.cs3500.spreadsheets.controller.LockedGraphicalController;
import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.SimpleWorkbookBuilder;
import edu.cs3500.spreadsheets.model.Workbook;
import edu.cs3500.spreadsheets.model.WorkbookReader;
import edu.cs3500.spreadsheets.model.WorksheetId;
import edu.cs3500.spreadsheets.view.TextualView;

/**
 * The main class for our program.
 */
public class BeyondGood {
  /**
   * The main entry point.
   *
   * @param args any command-line arguments
   */
  public static void main(String[] args) throws IllegalArgumentException {

    //Cmd line input must be in 1 of 4 forms:
    //"-in filename -eval cellname"
    //"-in filename -save newfilename"
    //"-in filename -gui"
    //"-gui"
    //To determine what the input is, look at args length first
    int argsLength = args.length;

    //If it's 1, then we know it's the 4th option:
    if (argsLength == 1) {
      String fn = args[0];
      if (fn.equals("-gui")) {
        //Cmd line input is in the form of:
        //"-gui"
        //Therefore, we need to open a new GUI
        StringReader blankRd = new StringReader("");
        Workbook model = initSpreadsheet(blankRd);
        LockedGraphicalController controller = new LockedGraphicalController(model);
      } else if (fn.equals("-edit")) {
        //Cmd line input is in the form of:
        //"-edit"
        //Therefore, we need to open a new Editable GUI
        StringReader blankRd = new StringReader("");
        Workbook model = initSpreadsheet(blankRd);
        EditableGraphicalController controller = new EditableGraphicalController(model);
      } else {
        JOptionPane.showMessageDialog(null, "ERROR: Invalid Cmd-Line Input");
      }
    } else {
      //otherwise, look at args[2]:
      String fn = args[2];
      String file = args[1];

      //Case 1: Cell Evaluation:
      if (fn.equals("-eval")) {
        //Cmd line input is in the form of:
        //"-in filename -eval cellname"
        //Therefore, we are looking for args 3, 0-indexed
        String reqCell = args[3];

        //Extracting a coord from cellname
        String colName = reqCell.replaceAll("[0-9]", "");
        int rowIndex = Integer.parseInt(reqCell.replaceAll("[\\D]", ""));
        int colIndex = Coord.colNameToIndex(colName);

        try {
          BufferedReader br = new BufferedReader(new FileReader(file));
          Workbook model = initSpreadsheet(br);

          //getValue
          Coord toPrint = new Coord(colIndex, rowIndex);
          System.out.print(model.getValue(new WorksheetId<>("Spreadsheet", toPrint)));
        } catch (FileNotFoundException fnf) {
          JOptionPane.showMessageDialog(null, "ERROR: Unable To Find File");
        }
      }
      //Case 2: TextualView Save
      else if (fn.equals("-save")) {
        //Cmd line input is in the form of:
        //"-in filename -save newfilename"
        //Therefore, we are looking for args 3, 0-indexed
        String newFile = args[3];

        //Creating a new PrintWriter Appendable & TextualView
        try {
          PrintWriter out = new PrintWriter(newFile);
          BufferedReader br = new BufferedReader(new FileReader(file));
          Workbook model = initSpreadsheet(br);
          TextualView textView = new TextualView(model, out);
          textView.initView();
          out.flush();
          out.close();
        } catch (FileNotFoundException fnf) {
          JOptionPane.showMessageDialog(null, "ERROR: Unable To Build File");
        }
      }
      //Case 3: Open Read-only GUI
      else if (fn.equals("-gui")) {
        //Cmd line input is in the form of:
        //"-in filename -gui"
        //Therefore, we need to open a GUI for args[1]
        try {
          BufferedReader br = new BufferedReader(new FileReader(file));
          Workbook model = initSpreadsheet(br);
          LockedGraphicalController app = new LockedGraphicalController(model);
        } catch (FileNotFoundException fnf) {
          JOptionPane.showMessageDialog(null, "ERROR: Unable To Find File");
        }
      }
      //Case 4: Open Write GUI
      else if (fn.equals("-edit")) {
        //Cmd line input is in the form of:
        //"-in filename -edit"
        //Therefore, we need to open a new Editable GUI for args[1]
        try {
          BufferedReader br = new BufferedReader(new FileReader(file));
          Workbook model = initSpreadsheet(br);
          EditableGraphicalController egc = new EditableGraphicalController(model);
        } catch (FileNotFoundException fnf) {
          JOptionPane.showMessageDialog(null, "ERROR: Unable To Find File");
        }
      } else {
        JOptionPane.showMessageDialog(null, "ERROR: Invalid Cmd-Line Input");
      }
    }
  }

  public static Workbook initSpreadsheet(Readable contents) {
    return new WorkbookReader().read(new SimpleWorkbookBuilder(), contents);
  }
}