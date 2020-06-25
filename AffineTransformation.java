/*An affine transformation uses an integer a (mod 26) so that GCD(a, 26)=1
to encipher a message with the function C = aP + k (mod 26)
where C is cipher, P is plaintext, and k is shift*/

public class AffineTransformation{

  private String message;
  private int shift;
  private boolean enciphered = false; //sample
  private final String decipherFailed =
    "Decipher failed! Your message is not enciphered!",
    encipherFailed = "Encipher failed! Your message is already enciphered!";

  //new instance vars
  private int scalar, inverse, sample = 1, mod = 26; //scalar is the "a" above
  private final String scalarError =
    "Please use a scalar that does not divide 26";

  //constructor; uses first checks that scalar is not a divisor of 26
  public AffineTransformation(String Message, int Scalar, int Shift){
    message = Message;
    shift = Shift%mod;
    if (this.validScalar(Scalar)) {
      scalar = Scalar%mod;
      inverse = Functions.getInverseMod(scalar, mod);
    }
    else {
      System.out.println(scalarError);
      scalar = sample;
      inverse = sample;
    }
  }

  //secondary "constructor", makes a AT object whose message is the text
  //contained in the given file, with the given scalar and shift
  public static AffineTransformation ATFromText(String fileName, int scalar,
    int shift){
      String message = Functions.fileToString(fileName); //text to string
      return new AffineTransformation(message, scalar, shift);
    }

  //important!! Checks that a scalar is valid, ie GCD(scalar, 26)=1
  public boolean validScalar(int scalar){
    return (Functions.GCD(scalar % mod, mod)==1);
  }

  //getters & setters----------------------------------------------

  public String getMessage(){return message;}
  public int getShift(){return shift;}
  public boolean isEnciphered(){return enciphered;}
  public void setMessage(String newMessage){message = newMessage;}
  public void setShift(int newShift){
      if (enciphered) System.out.println(
        "You can't change the shift of an enciphered message.");
      else shift = newShift;
    }
  public void setEnciphered(boolean newEnciphered) {
    enciphered = newEnciphered;}
  public int getScalar(){return scalar;}
  public void setScalar(int newScalar){
    //can't set scalar of enciphered message
    if (enciphered) System.out.println(
      "You can't change the scalar of an enciphered message.");
    //actually set scalar
    else if (this.validScalar(scalar)) {
      scalar = newScalar%26;
      inverse = Functions.getInverseMod(scalar, mod);
    }
    //message isn't enciphered but GCD(scalar, 26)!=1
    else System.out.println(scalarError);
  }
  public int getInverse(){return inverse;}
  //note: you CANNOT set the inverse bc it is dependent on the SCALAR

  //error messages, needed for inheritance
  public String encipherFailed(){return encipherFailed;}
  public String decipherFailed(){return decipherFailed;}
  //end getters & setters ---------------------------------------------

  //we love our printing methods
  public String toString(){
    String res = "Message: " + message + "\nEnciphered? " + enciphered +
      "\nShift: " + shift + "\nscalar: " + scalar;
    return res;
  }

  /*applies the affine transformation to the given string, using given
  scalar and shift, and optional punctuation.
  Static because might need to be applied to strings where we don't know
  the decipher function*/
  public static String applyTransformation(String message, int scalar,
    int shift, boolean includePunctuation){

    //note that in the string below, each char is at its cipher position (A=0...)
    String letters = Functions.getLetters();

    char c; //placeholders
    int index, mod = 26; //static, so need to redeclare mod
    //capitalization is a social construct, makes cipher easier to calculate
    message = message.toUpperCase();
    String newMessage = "";

    for (int i=0; i<message.length(); i++){
      c = message.charAt(i);
      index = letters.indexOf(c);
      //if letters, apply shift
      if (index > -1)  //!!!the only new part is the scalar below!!!
        newMessage = newMessage + letters.charAt((scalar*index + shift)%mod);
      //otherwise, include punctuation
      else if (includePunctuation) newMessage = newMessage + c;
    }//end for

    return newMessage;
  }//end applyTransformation

  //applies an affine transformation to the message if it is not already
  //enciphered. Includes punctuation if indicated
  public String encipher(boolean includePunctuation){
    if (enciphered) System.out.println(encipherFailed);

    else {//let's letters encipher!
      message = applyTransformation(message, scalar, shift, includePunctuation); //overwrite with enciphered message
      enciphered = true;
    }//end else

    return message;
  }

  /*if the string is enciphered, deciphers it by applying an affine
  transformation with the inverse of the scalar and the negative of
  the shift. Assumes punctuation is true because "we don't know if it is"*/
  public String decipher(){
    //assume includePunctuation is true
    if (enciphered){
      message = applyTransformation(message, inverse, -shift, true);
      enciphered = false;
    }
    else System.out.println(decipherFailed);

    return message;
  }

  //main method
  public static void main (String[] args){
    AffineTransformation myAT =  ATFromText("frankenstein.txt", 5, 0);

    System.out.println(myAT.encipher(true));
    System.out.println(myAT.decipher());
  }

}//end class
