/*File ImgMod02a.java
Copyright 2004, R.G.Baldwin

This is an update of the program named ImgMod02.
This update is designed to use
BufferedImage.getRaster in place of PixelGrabber.

The purpose of this program is to make it easy
to experiment with the modification of pixel
data in an image and to display the modified
version of the image along with the original
version of the image.

The program extracts the pixel data from an
image file into a 3D array of type:

int[row][column][depth].

The first two dimensions of the array correspond
to the rows and columns of pixels in the image.
The third dimension always has a value of 4 and
contains the following values by index value:

0 alpha
1 red
2 green
3 blue

Note that these values are stored as type int
rather than type unsigned byte which is the
format of pixel data in the original image.
This type conversion eliminates many problems
involving the requirement to perform unsigned
arithmetic on unsigned byte data.

The program supports gif and jpg files and
possibly some other file types as well.

Operation:  This program provides a framework
that is designed to invoke another program to
process the pixels extracted from an image.
In other words, this program extracts the pixels
and puts them in a format that is relatively
easy to work with.  A second program is invoked
to actually process the pixels.  Typical usage
is as follows:

java ImgMod02a ProcessProgramName ImageFileName

For test purposes, the source code includes a
class definition for an image processing program
named ProgramTest.

If the ImageFileName is not specified on the
command line, the program will search for an
image file in the current directory named
junk.gif and will process it using the
processing program specified by the second
command-line argument.

If both command-line arguments are omitted, the
program will search for an image file in the
current directory named junk.gif and will
process it using the built-in processing program
named ProgramTest.

The image file must be provided by the user in
all cases.  However, it doesn't have to be in
the curent directory if a path to the file is
specified on the command line.

When the program is started, the original image
and the processed image are displayed in a frame
with the original image above the processed
image.  A Replot button appears at the bottom of
the frame.  If the user clicks the Replot
button, the image processing method is rerun,
the image is reprocessed and the new version of
the processed image replaces the old version.

The processing program may provide a  GUI for
data input making it possible for the user to
modify the behavior of the image processing
method each time it is run.  This capability is
illustrated in the built-in processing program
named ProgramTest.

The image processing programming must implement
the interface named ImgIntfc02.  That interface
declares a single method with the following
signature:

int[][][] processImg(int[][][] threeDPix,
                     int imgRows,
                     int imgCols);

The first parameter is a reference to the 3D
array of pixel data stored as type int.  The
last two parameters specify the number of rows
of pixels and the number of columns of pixels in
the image.

The image processing program cannot have a
parameterized constructor.  This is because an
object of the class is instantiated by invoking
the newInstance method of the class named Class
on the name of the image processing program
provided as a String on the command line.  This
approach to object instantiation does not
support parameterized constructors.

If the image processing program has a main
method, it will be ignored.

The processImg method receives a 3D array
containing pixel data.  It should make a copy of
the incoming array and modify the copy rather
than modifying the original.  Then the program
should return a reference to the modified copy
of the 3D pixel array.

The program also receives the width and the
height of the image represented by the pixels in
the 3D array.

The processImg method is free to modify the
values of the pixels in the array in any manner
before returning the modified array.  Note
however that native pixel data consists of four
unsigned bytes.  If the modification of the
pixel data produces negative values or positive
value greater than 255, this should be dealt
with before returning the modified pixel data.
Otherwise, the returned values will simply be
masked to eight bits before display, and the
result of displaying those masked bits may not
be as expected.

There are at least two ways to deal with this
situation.  One way is to simply clip all
negative values at zero and to clip all values
greater than 255 at 255.  The other way is to
perform a further modification so as to map the
range from -x to +y into the range from 0 to 255.
There is no one correct way for all situations.

When the processImg method returns, this program
causes the original image and the modified image
to be displayed in a frame on the screen with
the original image above the modified image.

If the user doesn't specify an image processing
program, this program will instantiate and use
an object of the class named ProgramTest and an
image file named junk.gif.  The class definition
for the ProgramTest class is included in this
source code file.  The image file named junk.gif
must be provided by the user in the current
directory.  Just about any gif file of an
appropriate size will do.  Make certain that it
is small enough so that two copies will fit on
the screen when stacked one above the other.

The processing program named ProgramTest draws a
diagonal white line across the image starting at
the top left corner.  The program provides a
dialog box that allows the user to specify the
slope of the line.  To change the slope, type a
new slope into the text field and press the
Replot button on the main graphic frame.  It
isn't necessary to press the Enter key after
typing the new slope value into the text field,
but doing so won't cause any harm.  (Note that
only positive slope values can be used.  Entry
of a negative slope value will cause an exception
to be thrown.)

Other than to add the white line, the image
processing program named ProgramTest does not
modify the image.  It does draw a visible white
line across transparent areas, making the pixels
underneath the line non-transparent.  However,
it may be difficult to see the white line
against the default yellow background in the
frame.

If the program is unable to load the image file
within ten seconds, it will abort with an error
message.

Some operational details follow.

This program reads an image file from the disk
and saves it in memory under the name rawImg.
Then it declares a one-dimensional array of type
int of sufficient size to contain one int value
for every pixel in the image. Each int value
will be populated with one alpha byte and three
color bytes.  The name of the array is oneDPix.

Then the program instantiates an object of type
PixelGrabber, which associates the rawImg with
the one-dimensional array of type int.
Following this, the program invokes the
grabPixels method on the object of type
PixelGrabber to cause the pixels in the rawImg
to be extracted into int values and stored in
the array named oneDPix.

Then the program copies the pixel values from
the oneDPix array into the threeDPix array,
converting them to type int in the process.  The
threeDPix array is passed to an image processing
program.

The image processing program returns a modified
version of the 3D array of pixel data.

This program then creates a new version of the
oneDPix array containing the modified pixel data.
It uses the createImage method of the Component
class along with the constructor for the
MemoryImageSource class to create a new image
from the modified pixel data.  The name of the
new image is modImg.

Finally, the program overrides the paint method
where it uses the drawImage method to display
both the raw image and the modified image on the
same Frame object.  The raw image is displayed
above the modified image.

Tested using SDK 1.4.2 under WinXP.
************************************************/
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import com.sun.image.codec.jpeg.*;
import java.io.*;
import javax.imageio.ImageIO; //LSWM Mod

class ImgMod02a extends Frame{
  Image rawImg;
  BufferedImage buffImage;
  int imgCols;//Number of horizontal pixels
  int imgRows;//Number of rows of pixels
  Image modImg;//Reference to modified image

  //Inset values for the Frame
  int inTop;
  int inLeft;

  //Default image processing program.  This
  // program will be executed to process the
  // image if the name of another program is not
  // entered on the command line.  Note that the
  // class file for this program is included in
  // this source code file.
  static String theProcessingClass = //"ProgramTest";
                                   "dowatermark"; //LSWM Mod
                  
  //Default image file name.  This image file
  // will be processed if another file name is
  // not entered on the command line.  You must
  // provide this file in the current directory.
  static String theImgFile = "wild.png";
  static String theModFile = "modded.png"; //LSWM Mod

  MediaTracker tracker;
  Display display = new Display();//A Canvas
  Button replotButton = new Button("Go"); //LSWM Mod

  //References to arrays that store pixel data.
  int[][][] threeDPix;
  int[][][] threeDPixMod;
  int[] oneDPix;

  //Reference to the image processing object.
  ImgIntfc02 imageProcessingObject;
  //-------------------------------------------//

  public static void main(String[] args){

    //Get names for the image processing program
    // and the image file to be processed.
    // Program supports gif files and jpg files
    // and possibly some other file types as
    // well.
    if(args.length == 0){
      //Use default processing class and default
      // image file.  No code required here.
      // Class and file names were specified
      // above.  This case is provided for
      // informtion purposes only.
    }else if(args.length == 1){
      theProcessingClass = args[0];
      //Use default image file
    }else if(args.length == 2){
      theProcessingClass = args[0];
      theImgFile = args[1];
    }
    //LSWM Mod
    else if(args.length == 3){
      theProcessingClass = args[0];
      theImgFile = args[1];
      theModFile = args[2];
    }
    //End Mod
    else{
      System.out.println("Invalid args");
      System.exit(1);
    }//end else

    //Display name of processing program and
    // image file.
    System.out.println("Processing program: "
                           + theProcessingClass);
    System.out.println("Image file: "
                                 + theImgFile);
    System.out.println("Modded Image file: " + theModFile); //LSWM Mod
    
    //Instantiate an object of this class
    ImgMod02a obj = new ImgMod02a();
  }//end main
  //-------------------------------------------//

  public ImgMod02a(){//constructor
    //Get an image from the specified file.  Can
    // be in a different directory if the path
    // was entered with the file name on the
    // command line.
    rawImg = Toolkit.getDefaultToolkit().
                            getImage(theImgFile);


    //Use a MediaTracker object to block until
    // the image is loaded or ten seconds has
    // elapsed.
    tracker = new MediaTracker(this);
    tracker.addImage(rawImg,1);

    try{
      if(!tracker.waitForID(1,10000)){
        System.out.println("Load error.");
        System.exit(1);
      }//end if
    }catch(InterruptedException e){
      e.printStackTrace();
      System.exit(1);
    }//end catch

    //Make certain that the file was successfully
    // loaded.
    if((tracker.statusAll(false)
                   & MediaTracker.ERRORED
                   & MediaTracker.ABORTED) != 0){
      System.out.println(
                      "Load errored or aborted");
      System.exit(1);
    }//end if

    //Raw image has been loaded.  Get width and
    // height of the raw image.
    imgCols = rawImg.getWidth(this);
    imgRows = rawImg.getHeight(this);

    this.setTitle("Copyright 2004, Baldwin");
    this.setBackground(Color.WHITE); //LSWM Mod
    this.add(display);
    this.add(replotButton,BorderLayout.SOUTH);
    //Make it possible to get insets and the
    // height of the button.
    setVisible(true);
    //Get and store inset data for the Frame and
    // the height of the button.
    inTop = this.getInsets().top;
    inLeft = this.getInsets().left;
    int buttonHeight =
                   replotButton.getSize().height;
    //Size the frame so that a small amount of
    // yellow background will show on the right
    // and on the bottom when both images are
    // displayed, one above the other.  Also, the
    // placement of the images on the Canvas
    // causes a small amount of background to
    // show between the images.
    this.setSize(2*inLeft+imgCols + 1,inTop
                 + buttonHeight + 2*imgRows + 7);

    //=========================================//
    //Anonymous inner class listener for replot
    // button.  This actionPerformed method is
    // invoked when the user clicks the Replot
    // button.  It is also invoked at startup
    // when this program posts an ActionEvent to
    // the system event queue attributing the
    // event to the Replot button.
    replotButton.addActionListener(
      new ActionListener(){
        public void actionPerformed(ActionEvent e) {
          //Pass a 3D array of pixel data to the
          // processing object and get a modified
          // 3D array of pixel data back.  The
          // creation of the 3D array of pixel
          // data is explained later.
          threeDPixMod =
                imageProcessingObject.processImg(
                      threeDPix,imgRows,imgCols);
          //Convert the modified pixel data to a
          // 1D array of pixel data.  The 1D
          // array is explained later.
          oneDPix = convertToOneDim(
                   threeDPixMod,imgCols,imgRows);
          //Use the createImage() method to
          // create a new image from the 1D array
          // of pixel data.
          modImg = createImage(new MemoryImageSource(imgCols,imgRows,oneDPix,0,imgCols));          
          //Repaint the image display frame with
          // the original image at the top and
          // the modified pixel data at the
          // bottom.
          
          //System.out.println(modImg.toString());
          
          //LSWM mod
          if (imageProcessingObject.write() == true)
            try {
              BufferedImage bi = new BufferedImage(modImg.getWidth(null), modImg.getHeight(null), BufferedImage.TYPE_INT_ARGB); Graphics2D g = (Graphics2D)bi.createGraphics(); g.drawImage(modImg, 0, 0, null);
              ImageIO.write(bi, "png", new File(theModFile));
            }
            catch (Exception ex) {
              System.out.println("Couldn't write image to file: " + ex);
              ex.printStackTrace();
            }
          //End mod
          
          display.repaint();
        }//end actionPerformed

      
      }//end ActionListener
    );//end addActionListener
    //End anonymous inner class.
    //=========================================//

    //Create a 1D array object to receive the
    // pixel representation of the image
    oneDPix = new int[imgCols * imgRows];

    //Create an empty BufferedImage object
    buffImage = new BufferedImage(
                    imgCols,
                    imgRows,
                    BufferedImage.TYPE_INT_ARGB);

    // Draw Image into BufferedImage
    Graphics g = buffImage.getGraphics();
    g.drawImage(rawImg, 0, 0, null);

    //Convert the BufferedImage to numeric pixel
    // representation.
    DataBufferInt dataBufferInt =
            (DataBufferInt)buffImage.getRaster().
                                 getDataBuffer();
    oneDPix = dataBufferInt.getData();

    //Convert the pixel byte data in the 1D
    // array to int data in a 3D array to
    // make it easier to work with the pixel
    // data later.  Recall that pixel data is
    // unsigned byte data and Java does not
    // support unsigned arithmetic.
    // Performing unsigned arithmetic on byte
    // data is particularly cumbersome.
    threeDPix = convertToThreeDim(
                        oneDPix,imgCols,imgRows);

    //Instantiate a new object of the image
    // processing class.  Note that this
    // object is instantiated using the
    // newInstance method of the class named
    // Class.  This approach does not support
    // the use of a parameterized
    // constructor.
    try{
      imageProcessingObject = (
               ImgIntfc02)Class.forName(
               theProcessingClass).newInstance();

       //Post a counterfit ActionEvent to the
       // system event queue and attribute it
       // to the Replot button.  (See the
       // anonymous ActionListener class
       // defined above that registers an
       // ActionListener object on the RePlot
       // button.)  Posting this event causes
       // the image processing method to be
       // invoked, passing the 3D array of
       // pixel data to the method, and
       // receiving a 3D array of modified
       // pixel data back from the method.
      if (imageProcessingObject.firstEvent()) //LSWM Mod
        Toolkit.getDefaultToolkit().
                getSystemEventQueue().postEvent(
                  new ActionEvent(
                    replotButton,
                    ActionEvent.ACTION_PERFORMED,
                    "Replot"));

      //At this point, the image has been
      // processed and both the original
      // image and the the modified image
      // have been displayed.  From this
      // point forward, each time the user
      // clicks the Replot button, the image
      // will be processed again and the new
      // modified image will be displayed
      // along with the original image.

    }catch(Exception e){
      System.out.println(e);
    }//end catch

    //Cause the composite of the frame, the
    // canvas, and the button to become visible.
    this.setVisible(true);
    //=========================================//

    //Anonymous inner class listener to terminate
    // program.
    this.addWindowListener(
      new WindowAdapter(){
        public void windowClosing(WindowEvent e){
          System.exit(0);//terminate the program
        }//end windowClosing()
      }//end WindowAdapter
    );//end addWindowListener
    //=========================================//

  }//end constructor
  //===========================================//

  //Inner class for canvas object on which to
  // display the two images.
  class Display extends Canvas{
    //Override the paint method to display both
    // the rawImg and the modImg on the same
    // Canvas object, separated by one row of
    // pixels in the background color.
    public void paint(Graphics g){
      //First confirm that the image has been
      // completely loaded and neither image
      // reference is null.
      if (tracker.statusID(1, false) ==
                          MediaTracker.COMPLETE){
        if((rawImg != null) &&
                               (modImg != null)){
          g.drawImage(rawImg,0,0,this);
          g.drawImage(modImg,0,imgRows + 1,this);
        }//end if
      }//end if
    }//end paint()
  }//end class myCanvas
//=============================================//

  //Save pixel values as type int to make
  // arithmetic easier later.

  //The purpose of this method is to convert the
  // data in the int oneDPix array into a 3D
  // array of ints.
  //The dimensions of the 3D array are row,
  // col, and color in that order.
  //Row and col correspond to the rows and
  // columns of the image.  Color corresponds to
  // transparency and color information at the
  // following index levels in the third
  // dimension:
  // 0 alpha
  // 1 red
  // 2 green
  // 3 blue
  // The structure of this code is determined by
  // the way that the pixel data is formatted
  // into the 1D array of ints produced by the
  // grabPixels method of the PixelGrabber
  // object.
  int[][][] convertToThreeDim(
          int[] oneDPix,int imgCols,int imgRows){
    //Create the new 3D array to be populated
    // with color data.
    int[][][] data =
                    new int[imgRows][imgCols][4];

    for(int row = 0;row < imgRows;row++){
      //Extract a row of pixel data into a
      // temporary array of ints
      int[] aRow = new int[imgCols];
      for(int col = 0; col < imgCols;col++){
        int element = row * imgCols + col;
        aRow[col] = oneDPix[element];
      }//end for loop on col

      //Move the data into the 3D array.  Note
      // the use of bitwise AND and bitwise right
      // shift operations to mask all but the
      // correct set of eight bits.
      for(int col = 0;col < imgCols;col++){
        //Alpha data
        data[row][col][0] = (aRow[col] >> 24)
                                          & 0xFF;
        //Red data
        data[row][col][1] = (aRow[col] >> 16)
                                          & 0xFF;
        //Green data
        data[row][col][2] = (aRow[col] >> 8)
                                          & 0xFF;
        //Blue data
        data[row][col][3] = (aRow[col])
                                          & 0xFF;
      }//end for loop on col
    }//end for loop on row
    return data;
  }//end convertToThreeDim
  //-------------------------------------------//

  //The purpose of this method is to convert the
  // data in the 3D array of ints back into the
  // 1d array of type int.  This is the reverse
  // of the method named convertToThreeDim.
  public int[] convertToOneDim(
         int[][][] data,int imgCols,int imgRows){
    //Create the 1D array of type int to be
    // populated with pixel data, one int value
    // per pixel, with four color and alpha bytes
    // per int value.
    int[] oneDPix = new int[
                          imgCols * imgRows * 4];

    //Move the data into the 1D array.  Note the
    // use of the bitwise OR operator and the
    // bitwise left-shift operators to put the
    // four 8-bit bytes into each int.
    for(int row = 0,cnt = 0;row < imgRows;row++){
      for(int col = 0;col < imgCols;col++){
        oneDPix[cnt] = ((data[row][col][0] << 24)
                                   & 0xFF000000)
                     | ((data[row][col][1] << 16)
                                   & 0x00FF0000)
                      | ((data[row][col][2] << 8)
                                   & 0x0000FF00)
                           | ((data[row][col][3])
                                   & 0x000000FF);
        cnt++;
      }//end for loop on col

    }//end for loop on row

    return oneDPix;
  }//end convertToOneDim
}//end ImgMod02a.java class
//=============================================//

//The ProgramTest class

//The purpose of this class is to provide a
// simple example of an image processing class
// that is compatible with the program named
// ImgMod02a.

//The constructor for the class displays a small
// frame on the screen with a single textfield.
// The purpose of the text field is to allow the
// user to enter a value that represents the
// slope of a line.  In operation, the user
// types a value into the text field and then
// clicks the Replot button on the main image
// display frame.  The user is not required to
// press the Enter key after typing the new
// value, but it doesn't do any harm to do so.

//The method named processImage receives a 3D
// array containing alpha, red, green, and blue
// values for an image.  The values are received
// as type int (not type byte).

// The threeDPix array that is received is
// modified to cause a white diagonal line to be
// drawn down and to the right from the upper
// left-most corner of the image.  The slope of
// the line is controlled by the value that is
// typed into the text field.  Initially, this
// value is 1.0.  The image is not modified in
// any other way.

//To cause a new line to be drawn, type a slope
// value into the text field and click the Replot
// button at the bottom of the image display
// frame.

//This class extends Frame.  However, a
// compatible class is not required to extend the
// Frame class. This example extends Frame
// because it provides a GUI for user data input.

//A compatible class is required to implement the
// interface named ImgIntfc02.

class ProgramTest extends Frame
                           implements ImgIntfc02{

  double slope;//Controls the slope of the line
  String inputData;//Obtained via the TextField
  TextField inputField;//Reference to TextField

  //Constructor must take no parameters
  ProgramTest(){
    //Create and display the user-input GUI.
    setLayout(new FlowLayout());

    Label instructions = new Label(
               "Type a slope value and Replot.");
    add(instructions);

    inputField = new TextField("1.0",5);
    add(inputField);

    setTitle("Copyright 2004, Baldwin");
    setBounds(400,0,200,100);
    setVisible(true);
  }//end constructor

  //The following method must be defined to
  // implement the ImgIntfc02 interface.
  public int[][][] processImg(
                             int[][][] threeDPix,
                             int imgRows,
                             int imgCols){

    //Display some interesting information
    System.out.println("Program test");
    System.out.println("Width = " + imgCols);
    System.out.println("Height = " + imgRows);

    //Make a working copy of the 3D array to
    // avoid making permanent changes to the
    // image data.
    int[][][] temp3D =
                    new int[imgRows][imgCols][4];
    for(int row = 0;row < imgRows;row++){
      for(int col = 0;col < imgCols;col++){
        temp3D[row][col][0] =
                          threeDPix[row][col][0];
        temp3D[row][col][1] =
                          threeDPix[row][col][1];
        temp3D[row][col][2] =
                          threeDPix[row][col][2];
        temp3D[row][col][3] =
                          threeDPix[row][col][3];
      }//end inner loop
    }//end outer loop

    //Get slope value from the TextField
    slope = Double.parseDouble(
                           inputField.getText());

    //Draw a white diagonal line on the image
    for(int col = 0;col < imgCols;col++){
      int row = (int)(slope*col);
      if(row > imgRows -1)break;
      //Set values for alpha, red, green, and
      // blue colors.
      temp3D[row][col][0] = (byte)0xff;
      temp3D[row][col][1] = (byte)0xff;
      temp3D[row][col][2] = (byte)0xff;
      temp3D[row][col][3] = (byte)0xff;
    }//end for loop
    //Return the modified array of image data.
    return temp3D;
  }//end processImg
  
  //LSWM Mod
  public boolean write(){
    return true;
  }
  
  public boolean firstEvent(){
    return true;
  }
  //End Mod
  
}//end class ProgramTest
