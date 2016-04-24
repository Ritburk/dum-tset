package net.cmacpherson.mud;

public class Coordinate {

  public int x;
  public int y;
  public int z;
  
  public Coordinate(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public static Coordinate parse(String str) {
    str = str.substring(1, str.length() - 1);
    String[] split = str.split(", ");
    return new Coordinate(Integer.parseInt(split[0].split(":")[1]),
                          Integer.parseInt(split[1].split(":")[1]),
                          Integer.parseInt(split[2].split(":")[1]));
  }
  
  public String toString() {
    return "[x:" + x + ", y:" + y + ", z:" + z + "]";
  }
}
