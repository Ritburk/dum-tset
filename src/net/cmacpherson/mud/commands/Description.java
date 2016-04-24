package net.cmacpherson.mud.commands;

import net.cmacpherson.mud.Character;
import net.cmacpherson.mud.*;

public class Description implements Command {
  @Override
  public boolean execute(Character c, String cmd, String line, Output o) {
    if (line == null) {
      for (String s : c.description)
        o.toChar(s, c);
    } else {
      String args[] = Utils.splitInput(line);
      if (args[0].equals("+")) {
        String s = "";
        s += Utils.readArg(line)[1];
        try {
          c.addDescriptionLine(s);
          o.toChar("Line added.", c);
        } catch (DescriptionLengthException e) {
          o.toChar("Unable to add line: max length is " + Character.MAX_DESCRIPTION_LINE_LENGTH + ".", c);
        } catch (DescriptionSizeException e) {
          o.toChar("Your description is already full.", c);
        }
      } else if (args[0].equals("-")) {
        int n = 0;
        if (args.length > 1)
          try {
            n = Integer.parseInt(args[1]);
            if (n < -1)
              throw new NumberFormatException();
          } catch (NumberFormatException e) {
            o.toChar("Invalid line number.", c);
            return true;
          }
        try {
          c.removeDescriptionLine(n);
          o.toChar("Line removed.", c);
        } catch (DescriptionSizeException e) {
          o.toChar("There is no line " + n + " to remove.", c);
        } catch (DescriptionEmptyException e) {
          o.toChar("Your description is already empty.", c);
        }
      } else if (args[0].equalsIgnoreCase("clear")) {
        c.clearDescription();
        o.toChar("Description cleared.", c);
      } else if (args.length >= 3 &&
                 args[0].regionMatches(true, 0, "insert", 0, args[0].length()) &&
                 args[1].regionMatches(true, 0, "before", 0, args[1].length())) {
        int n = 0;
        try {
          n = Integer.parseInt(args[3]);
          if (n < -1)
            throw new NumberFormatException();
        } catch (NumberFormatException e) {
          o.toChar("Invalid line number.", c);
          return true;
        }
        String s = "";
        s += Utils.readArg(Utils.readArg(Utils.readArg(Utils.readArg(line)[1])[1])[1])[1];
        try {
          c.addDescriptionLine(s, n);
          o.toChar("Line inserted.", c);
        } catch (DescriptionLengthException e) {
          o.toChar("Unable to add line: max length is " + Character.MAX_DESCRIPTION_LINE_LENGTH + ".", c);
        } catch (DescriptionSizeException e) {
          o.toChar("Your description is already full.", c);
        }
      } else
        o.toChar(getHelp(), c);
    }
    return true;
  }

  @Override
  public String getHelp() {
    return "Syntax: description + [<line>]%N" +
           "        description - [<line#>]%N" + 
           "        description clear%N" +
           "        description insert before <line#> [<line>]";
  }
}
