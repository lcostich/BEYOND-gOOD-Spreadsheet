package edu.cs3500.spreadsheets.controller;

/**
 * Listens for actions from a Spreadsheet's GUI.
 */
public interface CommandListener {
  void onCommand(String command);
}
