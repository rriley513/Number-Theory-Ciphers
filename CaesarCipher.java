/*a class that simulates the Caesar Cipher, whose encipher function is
C=P+k (mod 26), where k is the "shift"; in other words, it is an affine transformation
whose scalar is 1; because it is a specification of affine transformation,
it extends that class*/

//for use in try decipher methods
import java.io.File;  // Import the File class
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import java.util.Scanner;

public class CaesarCipher extends AffineTransformation{

  //Constructor for a CaesarCipher object
  //a caesar cipher is just an affine transformation with scalar=1
  public CaesarCipher(String message, int shift){
    //uses same message and shift, with a scalar of sample=1
    super(message, 1, shift);
  }

  //second "constructor" makes a CeasarCipher object whose message is the
  //text of the given file.
  public static CaesarCipher CCFromText(String fileName, int shift){
    String message = Functions.fileToString(fileName);
    return new CaesarCipher(message, shift);
  }

  //prevent changing scalar
  public void setScalar(int newScalar){
    System.out.println("You cannot change the scalar of a CaesarCipher object.");
  }

  /*String representation of the CeasarCipher object
  gives the message, enciphered status, and shift, but not the scalar
  because the scalar is trivial*/
  public String toString(){
    String s = super.toString();
    int index = s.indexOf("scalar");
    return s.substring(0, index);
  }

  //encipher & decipher are the same as super

  //new methods!

  /*this is an ASCII-dependent shift in lieu of the alpha mod 26 shift
  it is static in order to be used to decipher strings where we do not know
   the shift*/
  public static String generalShift(String message, int shift){
    //original and new messages
    String newMessage = "";
    for (int i=0; i<message.length(); i++) //apply shift
      newMessage = newMessage + (char) (message.charAt(i)+shift);
    return newMessage;//return shifted message
  }

  /*NOTICE: The object does NOT know if you used general cipher or
  the alpha encipher (you should be able to tell) so be careful!!
  This general shift, so relies on ascii conversion instead of mod 26*/
  public String generalEncipher(){
    if (this.isEnciphered()) //Don't encipher if message is already enciphered
     System.out.println(super.encipherFailed());

    else {//if not enciphered, go ahead
      this.setMessage(generalShift(this.getMessage(), this.getShift()));
      this.setEnciphered(true);
    }
    //return same message if already enciphered, otherwise new message
    return this.getMessage();
  }

  /*as above, be sure to use the right decipher function (you should be able
  to tell which was used)
  returns a string deciphered with the object's shift value*/
  public String generalDecipher(){
    if (this.isEnciphered()) { //only decipher enciphered messages
      //apply the shift IN REVERSE to obtain original message
      this.setMessage(generalShift(this.getMessage(), -this.getShift()));
      this.setEnciphered(false);
    }
    //don't decipher a message that isn't enciphered
    else System.out.println(super.decipherFailed());
    //return same message if already enciphered, otherwise new message
    return this.getMessage();
  }

  //guess and check section-----------------------------------------------------
  //these are decipher methods that only apply to CeasarCiphers

    /*accepts a message and an int and "guesses" shift values of 0 up to the int,
    and saves message "deciphered" (shifted inversely) by each guessed value
    to an String array, which is returned*/
    public static String[] tryShifts(String message, int numShifts,
      boolean alpha){

      //make array with which to collect (reverse) shifted strings
      String[] messages = new String[numShifts];

      //shift the original string *in reverse* for each int up to numShifts
      //if one of the values is correct, this is equivalent to the decipher method
      for (int i=0; i<numShifts; i++){
        //try to undo shift, save in array.
        //Decipher method depends on alpha parameter
        if (alpha)
          messages[i] = AffineTransformation.applyTransformation(
            message, 1, numShifts, true);
        else messages[i] = generalShift(message, -i);
      }
      return messages;//return array of potentially deciphered strings
    }

    //acepts a Caesar-Cipher enciphered message and saves the result of tryShifts
    //to a file. Useful for proof by observation
    public static void writeShifts(String secretMessage, int ceiling,
      String fileName, boolean alpha) throws IOException{//throws IOException bc fileWriting

      FileWriter w = new FileWriter(fileName); //make FileWriter
      String[] results = tryShifts(secretMessage, ceiling, alpha); //get attempted shifts
      for (int i=0; i<results.length; i++)
        //write each message and it's guessed shift to the file
        w.write(i + "\n" + results[i] + "\n\n");

      w.close(); //close the FileWriter
      System.out.println("successfully wrote to " + fileName);//success message!
    }

    /*tests the above writeShifts method using a generic string and a randomly
    generated shift value for encipher. Runs writeShifts and saves the decipher
    attempts to a file results.txt. The secret shift value can be found by
    observation*/
    public static void testWriteShifts(){
      //suppose we have a message that was enciphered with the Caesar Cipher
      //but we don't know what shift was applied

      String s = "hello world";
      int ceiling = 100; //arbitrary value
      int n = (int) (Math.random()*ceiling);
      CaesarCipher myCC = new CaesarCipher(s, n);
      String secretMessage = myCC.generalEncipher();

      String fileName = "results.txt";

      try{
        writeShifts(secretMessage, ceiling, fileName, false);
      } catch (IOException e) {
        System.out.println("An error occurred.");
      }
    }//end testWriteShifts

  //end guess and check section-------------------------------------------------

  public static void main(String[] args){
    /*
    CaesarCipher myCC = new CaesarCipher("hello world", 1);
    System.out.println(myCC.encipher(true));
    System.out.println(myCC.decipher());
    System.out.println(myCC.generalEncipher());
    System.out.println(myCC.generalDecipher());
    */
    testWriteShifts();
  }

}
