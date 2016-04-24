package net.cmacpherson.mud;

public class Race {
  
  public Name name;
  public Abr abr;
  public int tnl;
  
  public Race(Name name) {
    this.name = name;
    this.abr = Abr.values()[name.ordinal()];
    this.tnl = TNL[name.ordinal()];
  }
  
  public Race(Abr abr) {
    this.abr = abr;
    this.name = Name.values()[abr.ordinal()];
    this.tnl = TNL[abr.ordinal()];
  }
  
  public static Race.Abr valueOf(String str) {
    Abr abr = null;
    try {
      abr = Abr.valueOf(str);
    } catch (IllegalArgumentException e) {}
    if (abr == null)
      for (Name name : Name.values())
        if (str.regionMatches(true, 0, name.name(), 0, str.length()))
          abr = Abr.values()[name.ordinal()];
    return abr;
  }
  
  public static final int[] TNL = new int[] {
    1000,
    1000,
    1000
  };
  
  public static final int[][] STATS = new int[][] {
    {10, 10, 10, 10, 10, 10},
    { 9, 11,  9, 11, 10, 10},
    {11,  9, 11,  9, 11,  9}
  };
  
  public enum Abr {
    HUM,
    ELF,
    DWF
  }
  
  public enum Name {
    HUMAN,
    ELF,
    DWARF
  }
}
