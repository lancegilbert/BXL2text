public class PadStack {

  String identifier = "";
  String holeDiam = "";
  String surface = "";
  String plated = "";
  int shapeCount = 0;
  int currentShape = 0;
  Pad [] padList = null;

  String [] args;
  float [] height = null;
  float [] width = null;
  String [] shapeType = null;
  String [] layer = null;

  public PadStack(String BXLPadStackDefinition) {
    args = BXLPadStackDefinition.split("\n");
    for (int index = 0; index < args.length; index++) {
      //      System.out.println("Args[" + index + "] : " + args[index]);
      args[index] = args[index].trim();
      args[index] = args[index].replaceAll("[\"():]","");
      args[index] = args[index].replaceAll("    "," ");
      args[index] = args[index].replaceAll("   "," ");
      args[index] = args[index].replaceAll("  "," ");
    }
    for (int index = 0; index < args.length; index++) {
      //      System.out.println("Args[" + index + "] : " + args[index]);
      String [] args2 = args[index].split(" ");
      //      System.out.println(args[index]);
      if (args2[0].equals("PadStack")) {
        identifier = args2[1];
        holeDiam = args2[3];
        surface = args2[5];
        plated = args2[7];
      } else if (args2[0].equals("Shapes")) {
        shapeCount = Integer.parseInt(args2[1]);
        width = new float [shapeCount];
        height = new float [shapeCount];
        shapeType = new String [shapeCount];
        layer = new String [shapeCount];
      } else if (args2[0].equals("PadShape")) {
        shapeType[currentShape] = args2[1];
        width[currentShape] = Float.parseFloat(args2[3]);
        height[currentShape] = Float.parseFloat(args2[5]);
        // padType[currentShape] = args2[7]; // not implemented yet
        layer[currentShape] = args2[9];
        currentShape++;
      }
    }
  }

  public boolean is(String ident) {
    return identifier.equals(ident);
  }

  public Pad BXLDefToPad(String BXLPadDef) {
    String pinNum = "";
    String pinName = "";
    long rotation = 0;

    String xPos = "";
    String yPos = "";

    BXLPadDef = BXLPadDef.trim();
    BXLPadDef = BXLPadDef.replaceAll("[^a-zA-Z0-9.-]", " ");
    //    BXLPadDef = BXLPadDef.replaceAll("[\"():,]","");
    BXLPadDef = BXLPadDef.replaceAll("    "," ");
    BXLPadDef = BXLPadDef.replaceAll("   "," ");
    BXLPadDef = BXLPadDef.replaceAll("  "," ");
    BXLPadDef = BXLPadDef.trim();

    String [] args = BXLPadDef.split(" ");

    // System.out.println(BXLPadDef);

    for (int index = 0; index < args.length - 1; index++) {
      // System.out.println(args[index]);
      if (args[index].equals("Number")) {
        pinNum = args[index + 1];
        if (pinNum.equals("")) {// may be empty if weird chars removed
          pinNum = "empty";
        }
        index++;
      }
      if (args[index].equals("PinName")) {
        pinName = args[index + 1];
        if (pinName.equals("")) {// may be empty if weird chars removed
          pinName = "empty";
        }
        index++;
      }
      if (args[index].equals("Origin")) {
        xPos = args[index + 1];
        yPos = args[index + 2];
        // System.out.println("xPos, yPos : " + xPos + ", " + yPos);
        index += 2;
      }
      if (args[index].equals("Rotate")) {
        rotation = Long.parseLong(args[index + 1]);
        index++;
      } 
    }

    // now we arrange kicad equivalents of the BXL values

    long kicadShapeXsizeNm = 0;
    long kicadShapeYsizeNm = 0;
    long kicadPadPositionXNm = 0;
    long kicadPadPositionYNm = 0;
    char kicadShape = ' ';
    long kicadShapeOrientation = 0;
    long kicadHoleDiamNm = 0;
    String kicadPadAttributeType = "null";    

    // dimensions in nanometres
    kicadShapeXsizeNm = (long)(width[0]*25400);
    kicadShapeYsizeNm = (long)(height[0]*25400);

    // position in nanometres
    kicadPadPositionXNm = (long)(Float.parseFloat(xPos)*25400);
    kicadPadPositionYNm = (long)(Float.parseFloat(yPos)*25400);

    // shape
    //    System.out.println("Shape type from BXL: " + shapeType[0]);
    if (shapeType[0].equals("Rectangle") ||
        shapeType[0].equals("SQUARE")) {
      kicadShape = 'R';
    } else if (shapeType[0].equals("Circle")) {
      kicadShape = 'C';
    }

    // rotation in decidegrees for kicad
    kicadShapeOrientation = rotation*10;

    // hole size in nanometres
    kicadHoleDiamNm = (long)(Float.parseFloat(holeDiam)*25400);

    // pad attribute
    if (kicadHoleDiamNm != 0) {
      kicadPadAttributeType = "STD";
    } else {
      kicadPadAttributeType = "SMD";
    } // hmm, maybe consider "HOLE" for diam !=0 && Plated == false

    // with a bunch of kicad equivalent values, we can call
    // a constructor in the Pad class and let it do the rest
    // of the work.

    Pad newPad = new Pad();
    newPad.populateBXLElement(kicadShapeXsizeNm,
                           kicadShapeYsizeNm,
                           kicadPadPositionXNm,
                           kicadPadPositionYNm,
                           kicadShape,
                           kicadShapeOrientation,
                           kicadHoleDiamNm,
                           kicadPadAttributeType,
                           pinNum,
                           pinName);
    return newPad;
  }

  public String fpText(long x, long y, int rot) {    
    return "#well it's a start ain't it";
  }

}