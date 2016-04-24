package net.cmacpherson.mud;

public class Filter {
  
  private Character c;
  private String prefix;
  private Race.Abr race;
  private int min = -1;
  private int max = -1;
  
  public Filter(Character c) {
    this.c = c;
  }
  public Filter(String prefix) {
    this.prefix = prefix;
  }
  public Filter(Race.Abr race) {
    this.race = race;
  }
  public Filter(int min, int max) {
    this.min = min;
    this.max = max;
  }
  
  public boolean pass(Character ch) {
    if (c != null) {
      if (c == ch)
        return true;
      return Utils.testVisibility(c, ch);
    } else if (prefix != null)
      return prefix.regionMatches(true, 0, ch.name, 0, prefix.length());
    else if (race != null)
      return race == ch.race.abr;
    else if (max == -1)
      return ch.level >= min;
    return ch.level >= min && ch.level <= max;
  }
}
