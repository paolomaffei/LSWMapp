/*File ImgIntfc02.java.java
Copyright 2004, R.G.Baldwin

The purpose of this interface is to declare
the one method required by image-processing
classes that are compatible with the program
named ImgMod02.java.

Tested using SDK 1.4.2 under WinXP
===============================================*/

interface ImgIntfc02{
  int[][][] processImg(int[][][] threeDPix,
                       int imgRows,
                       int imgCols);
  //LSWM Mod
  public boolean write();
  public boolean firstEvent();
  //End mod
}//end ImgIntfc02
