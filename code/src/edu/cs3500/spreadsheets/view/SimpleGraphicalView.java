package edu.cs3500.spreadsheets.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.EmptyCell;
import edu.cs3500.spreadsheets.model.IReadableCell;
import edu.cs3500.spreadsheets.model.ReadableWorkbook;
import edu.cs3500.spreadsheets.model.StringVisitor;
import edu.cs3500.spreadsheets.model.WorksheetId;

/**
 * Renders a spreadsheet into a graphical user interface.
 */
public class SimpleGraphicalView implements GraphicalView {

  private static final int CELLWIDTH = 100;
  private static final int CELLHEIGHT = 20;
  private static final int SCROLLMULT = 3;
  private static final int MIN_OFFSCREEN_BOUNDS = 1;

  private final ReadableWorkbook model;
  private final JFrame window;
  private final JScrollBar hScroll;
  private final JScrollBar vScroll;
  private final JTextField editBar;
  private final Border selectedBorder;
  private final Border normalBorder;
  private final Border selectedHeaderBorder;
  private final JButton confirm;
  private final JButton cancel;
  private final JPanel grid;
  private final JPanel tabPane;


  private JButton[][] cells;
  private ArrayList<JButton> tabs;
  private WorksheetId<Coord> selected;
  private boolean editing;
  private String currentSheet;

  private int viewWidth;
  private int viewHeight;
  private int xOffset;
  private int yOffset;
  private int minScrollWidth;
  private int minScrollHeight;

  /**
   * Instantiate the view, opening a window.
   *
   * @param model the model to be rendered
   */
  public SimpleGraphicalView(ReadableWorkbook model) {
    this.model = model;

    // Variable setup
    this.xOffset = 0;
    this.yOffset = 0;
    // Look at first worksheet by default
    this.currentSheet = model.getSpreadsheets().get(0);
    System.out.println(currentSheet);

    // Window setup
    this.window = new JFrame("Beyond gOOD Spreadsheet");
    this.hScroll = new JScrollBar(JScrollBar.HORIZONTAL);
    this.vScroll = new JScrollBar(JScrollBar.VERTICAL);
    this.editBar = new JTextField();
    this.editBar.setFocusTraversalKeysEnabled(false);
    this.confirm = new JButton("✓");
    this.cancel = new JButton("x");
    this.cancel.setPreferredSize(new Dimension(20, 20));
    this.confirm.setPreferredSize(new Dimension(20, 20));
    this.cancel.setOpaque(true);
    this.confirm.setOpaque(true);
    this.grid = new JPanel();
    this.tabPane = new JPanel();
    this.tabs = new ArrayList<>();
    this.window.getContentPane().setLayout(new BorderLayout());
    this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.window.setSize(1280, 720);

    // Formatting setup
    this.selectedBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
    this.normalBorder = BorderFactory.createLoweredBevelBorder();
    this.selectedHeaderBorder = BorderFactory.createRaisedBevelBorder();

    // Set window to visible
    this.window.setVisible(true);

    // Put contents in window
    // Add scroll pane
    this.window.getContentPane().add(this.vScroll, BorderLayout.EAST);
    JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new BorderLayout());
    //this.tabPane.set
    //this.tabPane.setPreferredSize(new Dimension(20, 100));
    bottomPanel.add(this.hScroll, BorderLayout.NORTH);
    bottomPanel.add(new JScrollPane(this.tabPane), BorderLayout.SOUTH);
    this.window.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    JPanel editPanel = new JPanel();
    editPanel.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Add edit bar
    editPanel.add(this.cancel);
    editPanel.add(this.confirm);
    editPanel.add(this.editBar, gbc);
    this.window.getContentPane().add(editPanel, BorderLayout.NORTH);

    // Add tabs
    for (String tabName: this.model.getSpreadsheets()) {
      JButton tab = new JButton();
      tab.setOpaque(true);
      this.tabs.add(tab);
      tab.setText(tabName);
      this.tabPane.add(tab);
      tab.addMouseListener(new MouseListener() {

        /**
         * Invoked when the mouse button has been clicked (pressed and released) on a
         * component.
         *
         * @param e the event to be processed
         */
        @Override
        public void mouseClicked(MouseEvent e) {
          setTab(tabName);
        }

        /**
         * Invoked when a mouse button has been pressed on a component.
         *
         * @param e the event to be processed
         */
        @Override
        public void mousePressed(MouseEvent e) {
          //Required, but not used
        }

        /**
         * Invoked when a mouse button has been released on a component.
         *
         * @param e the event to be processed
         */
        @Override
        public void mouseReleased(MouseEvent e) {
          //Required, but not used
        }

        /**
         * Invoked when the mouse enters a component.
         *
         * @param e the event to be processed
         */
        @Override
        public void mouseEntered(MouseEvent e) {
          //Required, but not used
        }

        /**
         * Invoked when the mouse exits a component.
         *
         * @param e the event to be processed
         */
        @Override
        public void mouseExited(MouseEvent e) {
          //Required, but not used
        }
      });
    }


    // Scroll setup
    this.hScroll.addAdjustmentListener(e -> updateScroll(e.getValue() / CELLWIDTH, yOffset));
    this.vScroll.addAdjustmentListener(e -> updateScroll(xOffset, e.getValue() / CELLHEIGHT));
    grid.addMouseWheelListener(e -> {
      int n = e.getWheelRotation();
      if (e.isShiftDown()) {
        // Scroll horizontally
        hScroll.setValue(hScroll.getValue() + (SCROLLMULT * n));
      } else {
        // Scroll vertically
        vScroll.setValue(vScroll.getValue() + (SCROLLMULT * n));
      }
    });

    // Add grid
    updateTabFormat();
    this.window.getContentPane().add(grid, BorderLayout.CENTER);
    this.window.revalidate();
    this.window.repaint();

    this.viewWidth = grid.getWidth() / CELLWIDTH;
    this.viewHeight = grid.getHeight() / CELLHEIGHT;

    grid.setLayout(new GridLayout(this.viewHeight, this.viewWidth));

    this.cells = new JButton[viewWidth][viewHeight];
    for (int y = 0; y < this.viewHeight; y++) {
      for (int x = 0; x < this.viewWidth; x++) {
        JButton toAdd = new JButton();
        toAdd.setFocusTraversalKeysEnabled(false);
        if (x == 0 || y == 0) {
          toAdd.setBackground(Color.LIGHT_GRAY);
        } else {
          toAdd.setBackground(Color.WHITE);
          int finalX = x;
          int finalY = y;
          toAdd.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
              if (e.getButton() == 1) {
                click(finalX, finalY);
                if (e.getClickCount() > 1) {
                  requestEdit(finalX, finalY);
                }
              }
            }

            /**
             * Invoked when a mouse button has been pressed on a component.
             *
             * @param e the event to be processed
             */
            @Override
            public void mousePressed(MouseEvent e) {
              // Necessary for interface, but doesn't do anything.
            }

            /**
             * Invoked when a mouse button has been released on a component.
             *
             * @param e the event to be processed
             */
            @Override
            public void mouseReleased(MouseEvent e) {
              // Necessary for interface, but doesn't do anything.
            }

            /**
             * Invoked when the mouse enters a component.
             *
             * @param e the event to be processed
             */
            @Override
            public void mouseEntered(MouseEvent e) {
              // Necessary for interface, but doesn't do anything.
            }

            /**
             * Invoked when the mouse exits a component.
             *
             * @param e the event to be processed
             */
            @Override
            public void mouseExited(MouseEvent e) {
              // Necessary for interface, but doesn't do anything.
            }
          });
        }
        toAdd.setOpaque(true);
        toAdd.setBorder(normalBorder);
        // Put button into the window
        grid.add(toAdd);
        this.cells[x][y] = toAdd;
      }
    }


    refreshMinScroll();
    this.vScroll.setValues(0, viewHeight * CELLHEIGHT,
            0, minScrollHeight);
    this.hScroll.setValues(0, viewWidth * CELLWIDTH,
            0, minScrollWidth);
    fullRefresh();
  }


  // ====================================== PUBLIC METHODS =========================================

  /**
   * To draw a single cell.
   *
   * @param coord the location of the cell to be drawn
   * @param value the IReadableCell to be drawn
   */
  @Override
  public void drawCell(WorksheetId<Coord> coord, IReadableCell value) {
    if (coord.sheet.equals(this.currentSheet)) {
      modifyButtonContents(coord, value);
    }
  }

  /**
   * Changes the current sheet in the view.
   * @param tab the sheet represented by the tab
   */
  public void setTab(String tab) {
    this.currentSheet = tab;
    fullRefresh();
    updateSelectedFormat();
    updateTabFormat();
  }

  /**
   * To draw the raw value of a cell.
   *
   * @param coord the location of the cell with raw value to be drawn
   * @param raw   the String to be drawn
   */
  @Override
  public void drawRaw(WorksheetId<Coord> coord, String raw) {
    this.selected = coord;
    this.editBar.setText(raw);
    this.editBar.requestFocus();
    setTab(selected.sheet);
  }

  /**
   * Add a keyboard shortcut to the specified context.
   *
   * @param shortcut the shortcut to add
   * @param command  the command to run
   * @param context  The context to add the command.
   */
  public void addKeyboardAction(String shortcut, ActionListener command, CommandContext context) {
    KeyStroke stroke = KeyStroke.getKeyStroke(shortcut);
    switch (context) {
      case WINDOW:
        window.getRootPane().registerKeyboardAction(command, stroke,
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        break;
      case EDIT:
        this.editBar.registerKeyboardAction(command, stroke, JComponent.WHEN_FOCUSED);
        break;
      case GRID:
        this.grid.registerKeyboardAction(command, stroke,
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        this.tabPane.registerKeyboardAction(command, stroke,
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        break;
      default:
        // Necessary for well-roundedness, but will never reach the default case
        break;
    }
  }

  /**
   * Retrieve the contents of the edit bar.
   *
   * @return
   */
  public String getEditText() {
    return this.editBar.getText();
  }

  /**
   * Reset the edit bar to the raw contents of the edited cell.
   */
  public void cancelEdit() {
    this.editBar.setText(model.getRaw(selected));
    this.focusOnGrid();
  }

  /**
   * Add a listener to the confirm button.
   *
   * @param listener the listener to add
   */
  public void registerConfirmListener(ActionListener listener) {
    this.confirm.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        listener.actionPerformed(new ActionEvent(this, e.getID(), "confirm"));
      }

      @Override
      public void mousePressed(MouseEvent e) {
        // Necessary for interface, but doesn't do anything.
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        // Necessary for interface, but doesn't do anything.
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        // Necessary for interface, but doesn't do anything.
      }

      @Override
      public void mouseExited(MouseEvent e) {
        // Necessary for interface, but doesn't do anything.
      }
    });
  }

  /**
   * Add a listener to the cancel button.
   *
   * @param listener the listener to add
   */
  public void registerCancelListener(ActionListener listener) {
    this.cancel.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        listener.actionPerformed(new ActionEvent(this, e.getID(), "confirm"));
      }

      @Override
      public void mousePressed(MouseEvent e) {
        // Necessary for interface, but doesn't do anything.
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        // Necessary for interface, but doesn't do anything.
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        // Necessary for interface, but doesn't do anything.
      }

      @Override
      public void mouseExited(MouseEvent e) {
        // Necessary for interface, but doesn't do anything.
      }
    });
  }

  /**
   * Moves the selector in a single direction.
   *
   * @param d the direction to move
   */
  public void moveSelect(Direction d) {
    if (selected == null) {
      return;
    }
    WorksheetId<Coord> newSelection = selected;
    switch (d) {
      case NORTH:
        if (selected.ref.row > 1) {
          newSelection = new WorksheetId<>(selected.sheet,
                  new Coord(selected.ref.col, selected.ref.row - 1));
        }
        break;
      case SOUTH:
        newSelection = new WorksheetId<>(selected.sheet,
                new Coord(selected.ref.col, selected.ref.row + 1));
        break;
      case EAST:
        newSelection = new WorksheetId<>(selected.sheet,
                new Coord(selected.ref.col + 1, selected.ref.row));
        break;
      case WEST:
        if (selected.ref.col > 1) {
          newSelection = new WorksheetId<>(selected.sheet,
                  new Coord(selected.ref.col - 1, selected.ref.row));
        }
        break;
      default:
        // Necessary for well-roundedness, but will never reach the default case.
        break;
    }
    this.updateSelection(newSelection);
  }

  /**
   * Moves the focus back to the selected cell on the grid.
   */
  public void focusOnGrid() {
    if (selected == null) {
      return;
    }
    scrollToSelection();
    int viewX = selected.ref.col - this.xOffset;
    int viewY = selected.ref.row - this.yOffset;
    this.cells[viewX][viewY].requestFocus();

  }

  /**
   * Pulls focus to the edit bar.
   */
  public void editMode() {
    this.editBar.requestFocus();
    scrollToSelection();
  }

  /**
   * Returns the coordinate of the selected cell.
   *
   * @return the selected coordinates
   */
  @Override
  public WorksheetId<Coord> getSelection() {
    return this.selected;
  }

  //NOTE: Never used for this class. Only used in MockGraphicalView testing.
  @Override
  public String returnAsString() {
    return "";
  }

  // ===================================== PRIVATE METHODS =========================================


  // ======================================= IO handlers ===========================================

  /**
   * Handle a click event at the given view-space coordinate.
   *
   * @param x button x coordinate
   * @param y button y coordinate
   */
  private void click(int x, int y) {
    // Set the selected coordinate
    updateSelection(coordFromView(x, y));
  }

  private void updateSelection(WorksheetId<Coord> toSelect) {
    // Only update if necessary
    if (!toSelect.equals(selected)) {
      this.selected = toSelect;
      this.currentSheet = toSelect.sheet;
      updateTabFormat();
      this.cancelEdit();
      this.editBar.setText(model.getRaw(toSelect));
      if (this.editing) {
        this.editing = false;
        // Any specialized editing functionality here
      }
      scrollToSelection();
    }
  }

  private void scrollToSelection() {
    if (selected == null) {
      return;
    }
    int xOff = xOffset;
    if (selected.ref.col <= xOffset) {
      xOff = selected.ref.col - 1;
    } else if (selected.ref.col >= xOffset + viewWidth) {
      xOff = selected.ref.col - viewWidth + 1;
    }
    int yOff = yOffset;
    if (selected.ref.row <= yOffset) {
      yOff = selected.ref.row - 1;
    } else if (selected.ref.row >= yOffset + viewHeight) {
      yOff = selected.ref.row - viewHeight + 1;
    }
    hScroll.setValue(xOff * CELLWIDTH);
    vScroll.setValue(yOff * CELLHEIGHT);
    if (this.currentSheet != selected.sheet) {
      this.currentSheet = selected.sheet;
      updateTabFormat();
      if (xOff == this.xOffset && yOff == this.yOffset) {
        fullRefresh();
      }
    }
    updateScroll(xOff, yOff);
    updateSelectedFormat();
  }

  /**
   * Request to start editing the cell at the given button.
   *
   * @param x button x coordinate
   * @param y button y coordinate
   */
  private void requestEdit(int x, int y) {
    this.cancelEdit();
    this.editBar.requestFocus();
    this.editing = true;
  }

  /**
   * Update the scroll position.
   *
   * @param xOff New leftmost column
   * @param yOff New top row
   */
  private void updateScroll(int xOff, int yOff) {
    if (xOff == this.xOffset && yOff == this.yOffset) {
      // Nothing's changed. No need to update the view.
      return;
    }
    this.xOffset = xOff;
    this.yOffset = yOff;
    fullRefresh();
    refreshScrollBounds();
    updateSelectedFormat();
  }

  // ======================================= View updates ==========================================

  /**
   * Update all cells in view.
   */
  private void fullRefresh() {
    for (int x = 0; x < this.viewWidth; x++) {
      for (int y = 0; y < this.viewHeight; y++) {
        if (x != 0 && y == 0) {
          this.modifyButtonContents(x, y, Coord.colIndexToName(x + xOffset));
        } else if (y != 0 && x == 0) {
          this.modifyButtonContents(x, y, "" + (y + yOffset));
        } else if (x != 0 && y != 0) {
          this.updateCell(this.coordFromView(x, y));
        }
      }
    }
  }

  /**
   * Refreshes a single cell at the given coordinate.
   *
   * @param c coordinate of the cell to refresh
   */
  private void updateCell(WorksheetId<Coord> c) {
    this.modifyButtonContents(c, this.model.getValue(c));
  }

  /**
   * Update a cell at a given coordinate to display the given ReadableCell.
   *
   * @param c            coordinate of the cell to update
   * @param cellContents contents to put in the cell display
   */
  private void modifyButtonContents(WorksheetId<Coord> c, IReadableCell cellContents) {
    if (c.sheet.equals(this.currentSheet)) {
      // Bounding box cull for focus
      int viewX = c.ref.col - this.xOffset;
      int viewY = c.ref.row - this.yOffset;
      // Viewport culling
      if (viewX >= 0 && viewX < this.viewWidth
              && viewY >= 0 && viewY < this.viewHeight) {
        // Focus on selected button
        modifyButtonContents(viewX, viewY, cellContents.accept(new StringVisitor()));
      }
    }

    // Check if it's empty
    if (cellContents instanceof EmptyCell) {
      refreshMinScroll();
    } else {
      if (this.minScrollWidth < c.ref.col * CELLWIDTH) {
        this.minScrollWidth = c.ref.col * CELLWIDTH;
      }
      if (this.minScrollHeight < c.ref.row * CELLHEIGHT) {
        this.minScrollHeight = c.ref.row * CELLHEIGHT;
      }
    }
  }

  /**
   * Modify the button at the given display-space coordinates to show the given String.
   *
   * @param x        button x coordinate
   * @param y        button y coordinate
   * @param contents string to display on the button
   */
  private void modifyButtonContents(int x, int y, String contents) {
    this.cells[x][y].setText(contents);
    this.window.repaint();
  }

  /**
   * Update the formatting to display the selected cell.
   */
  private void updateSelectedFormat() {
    // Bounding box cull for focus
    int viewX = -1;
    int viewY = -1;
    if (selected != null) {
      if (selected.sheet.equals(currentSheet)) {
        viewX = this.selected.ref.col - this.xOffset;
        viewY = this.selected.ref.row - this.yOffset;
      }
    }


    for (int col = 0; col < this.cells.length; col++) {
      for (int row = 0; row < this.cells[0].length; row++) {
        if (col == 0 && row != 0 && row == viewY) {
          // Format row header
          this.cells[col][row].setBorder(selectedHeaderBorder);
        } else if (row == 0 && col != 0 && col == viewX) {
          // Format column header
          this.cells[col][row].setBorder(selectedHeaderBorder);
        } else if (cells[col][row].getBorder() != normalBorder) {
          // Restore abnormal format
          this.cells[col][row].setBorder(normalBorder);
        }
      }
    }

    if (this.selected == null) {
      return;
    }


    // Viewport culling
    if (viewX > 0 && viewX < this.viewWidth && viewY > 0 && viewY < this.viewHeight
            && this.selected.sheet.equals(currentSheet)) {
      // Focus on selected button
      this.cells[viewX][viewY].setBorder(selectedBorder);
    }
  }

  private void updateTabFormat() {
    for (JButton tabButton : this.tabs) {
      if (tabButton.getText().equals(this.currentSheet)) {
        tabButton.setBorder(selectedBorder);
      } else {
        tabButton.setBorder(normalBorder);
      }
    }
  }

  // ======================================== Helpers ==============================================

  /**
   * Refreshes the bounding coordinates of the spreadsheet contents from scratch.
   *
   * @return the Coordinate at the botttom right of the bounding box
   */
  private Coord boundingCoord() {
    int maxRow = 1;
    int maxCol = 1;
    for (Coord c : model.getNonEmptyCoords(currentSheet)) {
      maxRow = Math.max(c.row, maxRow);
      maxCol = Math.max(c.col, maxCol);
    }
    return new Coord(maxCol, maxRow);
  }


  /**
   * Transform view-space coordinates into global coordinates.
   *
   * @param x button x
   * @param y button y
   * @return cell Coordinate
   */
  private WorksheetId<Coord> coordFromView(int x, int y) {
    return new WorksheetId<>(currentSheet, new Coord(x + this.xOffset, y + this.yOffset));
  }

  /**
   * Refresh the minimum scrolling size.
   */
  private void refreshMinScroll() {
    Coord bc = boundingCoord();
    this.minScrollWidth = Math.max(viewWidth + MIN_OFFSCREEN_BOUNDS,
            bc.col) * CELLWIDTH;
    this.minScrollHeight = Math.max(viewHeight + MIN_OFFSCREEN_BOUNDS,
            bc.row) * CELLHEIGHT;

    refreshScrollBounds();
  }

  /**
   * Update the bounds of the scroll bars.
   */
  private void refreshScrollBounds() {
    int hMax = Math.max(minScrollWidth,
            (xOffset + viewWidth + MIN_OFFSCREEN_BOUNDS) * CELLWIDTH);
    int vMax = Math.max(minScrollHeight,
            (yOffset + viewHeight + MIN_OFFSCREEN_BOUNDS) * CELLHEIGHT);

    this.hScroll.setMaximum(hMax);
    this.vScroll.setMaximum(vMax);
  }
}
