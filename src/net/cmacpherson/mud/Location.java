package net.cmacpherson.mud;

public class Location {

  public Plane plane;
  public Area area;
  public Room room;
  public long id;
  
  public Location(Plane plane,
                  Area area,
                  Room room,
                  long id) {
    this.plane = plane;
    this.area = area;
    this.room = room;
    this.id = id;
  }
  
  @Override
  public boolean equals(Object o) {
    if (o instanceof Location)
      return id == ((Location)o).id;
    return false;
  }

  @Override
  public String toString() {
    return "[" + plane.name + ":" + area.name + ":" + room.name + ":" + id + "]";
  }
}
