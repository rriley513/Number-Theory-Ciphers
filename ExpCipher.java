/*EXPONENTIATION CIPHER, but Exponentiation is a long word, so I abbreviated
it to ExpCipher
Encipher function is C =* P^e (mod q), Decipher is P =* C^d (mod q)
* there is no shorthand for "is congruent to" in basic text, but that's what I mean since
we're using mods*/
public class ExpCipher{

  //instance variables
  private String message;
  private int exp=1, q=2, d=1, mod = 26;
  private boolean enciphered=false;//default
  private final String decipherFailed =
    "Decipher failed! Your message is not enciphered!",
    encipherFailed = "Encipher failed! Your message is already enciphered!",
    letters = Functions.getLetters();


  //constructor. Accepts a message String and an int and returns an
  //ExpCipher object
  public ExpCipher(String myMessage, int exp, int q){
    message = myMessage;
    validExp(exp, q);
  }

  //secondary "constructor", makes a EC object whose message is the text
  //contained in the given file, with the given exp and q values
  public static ExpCipher ECFromText(String fileName, int exp, int q){
    String message = Functions.fileToString(fileName);
    return new ExpCipher(message, exp, q);
  }

  //checker and setter fr the EC values exp, q, and d
  //if exp and q fail any of the rules below, sets
  private void validExp(int myExp, int myQ){
    //RULES: 0<exp<q, (exp,q-1)=1
    if (myExp>0 && myExp < myQ && (Functions.GCD(myExp, myQ-1)==1)){
      exp = myExp;
      q = myQ;
      d = Functions.getInverseMod(exp, q-1); //d is inv(exp) (mod q-1)
    }
    else {
      System.out.println("An error was encountered with your exp or  q values."
      +" Default values of 1 and 2 will be used, respectively.");
      //exp=1 q=2 d=1 defaults
    }
  }//end validExp

  //getters & setters--------------------------------------------------------
  public String getMessage(){return message;}
  public void setMessage(String newMessage){message = newMessage;}
  public int getExp(){return exp;}
  public void setExp(int myExp){this.validExp(myExp, q);}
  public int getQ(){return q;}
  public void setQ(int myQ){this.validExp(exp, myQ);}
  public int getD(){return d;}
  //no setD because d depends on exp and q
  public boolean getEnciphered(){return enciphered;}
  public void setEnciphered(boolean myEnciphered){enciphered = myEnciphered;}

  //--------------------------------------------------------------------------

  //Returns a string representation of a ExpCipher object. Gives message
  public String toString(){
    String s = "An ExpCipher Object.\nMessage:\n"+message+"\ne="+exp+
      "\nq="+q+"\nd="+d+"\nEnciphered? "+enciphered;
    return s;
  }

  //applies the exponentiation cipher to the message, if not already enciphered
  //otherwise returns the current string
  public String encipher(){
    //don't encipher already enciphered messages
    if (enciphered) System.out.println(encipherFailed);
    else{
      message = message.toUpperCase();
      String newMessage = "";
      int len = message.length(), index;

      //Note: Like Hill cipher, expCipher NEEDS to restrict iteself to letters
      //(no punctuation)
      for (int i=0; i<len; i++){
        index = letters.indexOf(message.charAt(i));
        if (index>-1)
          //fixed overflow with custom powers method
          //the line below is where the magic happens! C=P^e (mod q)
          newMessage = newMessage+Functions.modPower(index, exp, q)+" ";
      }//end for

      //done w new Message
      message = newMessage;
      enciphered = true;
    }//end else
    return message;
  }//end encipher

  public String decipher(){
    if (enciphered){
      int C;
      String newMessage="";
      //enciphered message is numbers delimited by a space
      String[] lets = message.split(" ");

      for (int i=0; i<lets.length; i++)
        /*note: even tho we're using mod q, not mod 26, b/c the exponent of
        d "undoes" the exponent of exp (look up a proof for more details)
        we get the original value*/
        newMessage = newMessage +
          letters.charAt(Functions.modPower(Integer.parseInt(lets[i]), d, q));

      message = newMessage;
      enciphered = false;
    }
    else System.out.println(decipherFailed);

    return message;
  }

  public static void main(String[] args){
    ExpCipher ec = ECFromText("frankensteinCh1.txt", 17, 61);
    System.out.println(ec.encipher()+"\n"+ec.decipher());
  }

}
