package net.cmacpherson.mud;

public class ForestAlgorithm implements Generator.Algorithm {

  @Override
  public Room[][][] generate(ServerThread server,
                             Generator.Options opts) {
    
    if (opts instanceof ForestOptions) {
      ForestOptions o = (ForestOptions)opts;
      //TODO finish algorithm
      Room[][][] map = new Room[0][0][0];
      
      
      
      return map;
    } else {
      server.print("Unsupported Generator Options:");
      server.print("  Expected: " + ForestOptions.class.getName());
      server.print("  Received: " + opts.getClass().getName());
      return null;
    }
  }
  
  public class ForestOptions implements Generator.Options {
    @Override
    public Class<?> getAlgorithmClass() {
      return ForestAlgorithm.class;
    }
    
    //TODO vars needed for algorithm
    public Biome biome;
    public int width;
    public int length;
    public int depth;
    public int air;
    public Coordinate attachmentPoint;
    public Direction attachmentHeading;
  }
}
