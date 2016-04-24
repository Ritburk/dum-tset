package net.cmacpherson.mud;

import java.io.File;
import java.util.ArrayList;

public class Generator {
  
  protected ServerThread server;
  protected Biome[] biomes;
  private ArrayList<Algorithm> algorithms = new ArrayList<Algorithm>();
  
  public Generator(ServerThread server) {
    this.server = server;
    if (!init())
      server = null;
    algorithms.add(new ForestAlgorithm());
  }
  
  private boolean init() {
    server.print("Loading biomes...");
    File[] files = new File(Globals.BIOMES_PATH).listFiles();
    ArrayList<Biome> biomes = new ArrayList<Biome>();
    for (File file : files) {
      try {
        Biome b = Biome.load(server, file);
        if (b == null)
          server.print("Error loading biome.");
      } catch (Exception e) {
        server.print("Error loading biome from file: " + file.getAbsolutePath());
        e.printStackTrace();
        return false;
      }
    }
    this.biomes = biomes.toArray(new Biome[] {});
    return true;
  }

  public void createInitialMap(Options opts) {
    //TODO initial map
    
    Algorithm algo = null;
    for (Algorithm a : algorithms)
      if (a.getClass().equals(opts.getAlgorithmClass()))
        algo = a;
    if (algo == null)
      server.print("Error finding inital algorithm: " + opts.getAlgorithmClass().getName());
    // etc...
    commit(algo.generate(opts));
    // etc...
  }
  
  public void recoverMap() {
    File[] files = new File(Globals.WORLD_PATH).listFiles();
    for (File file : files) {
      try {
        Plane plane = new Plane();
        plane.load(XML.INSTANCE.parse(file));
        plane.organize();
        server.universe.add(plane);
      } catch (Exception e) {
        server.print("Error loading plane from file: " + file.getAbsolutePath());
        e.printStackTrace();
      }
    }
  }
  
  public void extendMap(Room attachmentPoint, Direction dir) {
    //TODO extend map
    
  }
  
  private void commit(Room[][][] rooms) {
    //TODO commit generated rooms to server's universe
    
  }
  
  public interface Options {
    public Class<?> getAlgorithmClass();
  }
  
  public interface Algorithm {
    public Room[][][] generate(Options opts);
  }
}
