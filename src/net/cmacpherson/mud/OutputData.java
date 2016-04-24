package net.cmacpherson.mud;

public final class OutputData {
  public final String LINE;
  public final boolean NEWLINE;
  
  public OutputData(String line,
                    boolean newline) {
    LINE = Globals.COLOR_STR + line;
    NEWLINE = newline;
  }
  
  public static OutputData newline() {return new OutputData("", true);}
  public static OutputData line(String line) {return new OutputData(line, true);}
  //prompt?  
}
