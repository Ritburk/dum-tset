package net.cmacpherson.mud;

public class Dice {

  private int dice;
  private int sides;
  private int base;
  
  private Dice() {};
  
  public Dice(String s) throws InvalidDiceFormatException {
    if (!s.contains("d"))
      throw new InvalidDiceFormatException();
    String[] split = s.split("[d+-]");
    if (split.length != 2 &&
        split.length != 3)
      throw new InvalidDiceFormatException();
    try {
      dice = Integer.parseInt(split[0]);
      sides = Integer.parseInt(split[1]);
      if (split.length == 3)
        base = (s.contains("-") ? -1 : 1) * Integer.parseInt(split[2]);
    } catch (NumberFormatException e) {
      throw new InvalidDiceFormatException();
    }
    if (dice < 0 || sides < 0)
      throw new InvalidDiceFormatException();
  }
  
  public long roll() {
    if (dice == 0 || sides == 0) return 0;
    if (sides == 1) return dice;
    long n = 0;
    for (int i = 0; i < dice; i++)
      n += (long)(Math.random() * sides) + (long)1;
    return n + base;
  }
  
  public long min() {
    return dice + base;
  }
  
  public long max() {
    return dice * sides + base;
  }
  
  public static int rollD4() {return roll(4);}
  public static int rollD6() {return roll(6);}
  public static int rollD8() {return roll(8);}
  public static int rollD10() {return roll(10);}
  public static int rollD12() {return roll(12);}
  public static int rollD20() {return roll(20);}
  public static int rollD100() {return roll(100);}
  
  private static int roll(int i) {
    return (int)(Math.random() * i) + 1;
  }
  
  @Override
  public String toString() {
    return dice + "d" + sides + (base > 0 ? "+" : "") + base;
  }
  
  public Dice clone() {
    Dice d = new Dice();
    d.dice = dice;
    d.sides = sides;
    d.base = base;
    return d;
  }
}
