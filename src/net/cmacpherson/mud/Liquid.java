package net.cmacpherson.mud;

public class Liquid {
  public final Type type;
  public final String name;
  public final String color;
  public final String smell;
  public final Spell[] affects;
  
  private Liquid(Type type,
                String name,
                String color,
                String smell,
                Spell[] affects) {
    this.type = type;
    this.name = name;
    this.color = color;
    this.smell = smell;
    this.affects = affects;
  }
  
  public static Liquid valueOf(String name) {
    return DESCRIPTIONS[Type.valueOf(name).ordinal()];
  }
  
  public static final Liquid[] DESCRIPTIONS = new Liquid[] {
    new Liquid(Type.WATER, "water", "transparent", "no odor", new Spell[] {})
  };
  
  public enum Type {
    WATER
  }
}
