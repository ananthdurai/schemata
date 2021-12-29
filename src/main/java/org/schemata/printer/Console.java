package org.schemata.printer;

public class Console {

  public static final String TEXT_RED = "\033[0;31m";
  public static final String TEXT_GREEN = "\033[0;32m";
  public static final String TEXT_RESET = "\u001B[0m";

  public static void printSuccess(String message) {
    System.out.println(TEXT_GREEN + message + TEXT_RESET);
  }

  public static void printError(String message) {
    System.out.println(TEXT_RED + message + TEXT_RESET);
  }
}
