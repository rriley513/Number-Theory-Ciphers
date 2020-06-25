/*A Vigenere Cipher uses a keyword to get varying shift values for enciphering
The encipher function is C_i=P_i+k_i, where C_i is the ith index of of the
ciphertext, P_i is the ith index of the plaintext, and k_i is the ith index
mod (length of the keyword) of the keyword.
Its advantage is that it avoids traditional FrequencyAnalysis, but if a
 portion of the plaintext is obtained, it can be easy to discover the keyword
 and decode the entire message.*/

public class VigenereCipher{

  //instance variables
  private String message, keyword;
  private int[] keywordVals;
  private boolean enciphered = false;
  private final String decipherFailed =
    "Decipher failed! Your message is not enciphered!",
    encipherFailed = "Encipher failed! Your message is already enciphered!",
    letters = Functions.getLetters();
  private int mod = 26;

  //constructor
  public VigenereCipher(String myMessage, String myKeyword){
    message = myMessage;
    keyword = myKeyword;
    this.processKeyword();
  }

  //pseudo-constructor
  public static VigenereCipher VCFromText(String fileName, String keyword){
    String message = Functions.fileToString(fileName);
    return new VigenereCipher(message, keyword);
  }

  //sets the value of keywordVals, an instane variable that records the mod 26
  //values of the keyword. Ignores non-letter values
  private void processKeyword(){
    keyword = keyword.toUpperCase(); //necessary for indexing
    //index is the mod26 value of the char,
    //count protects us from errors with letters from non-letters in the keyword
    int len = keyword.length(), count = 0, index;
    int[] vals = new int[len];//holds keyword values
    for (int i=0; i<len; i++){
      index = letters.indexOf(keyword.charAt(i));
      if (index>-1){
        vals[count] = index;//set value
        count++;//increment number of LETTERS seen
      }//end if
    }//end for
    if (count==len) keywordVals = vals;
    //else there were non-letters in the keyword, so there are extra spaces
    //in the keywordVals array that we want to get rid of
    else{
      int[] vals2 = new int[count];
      for (int i=0; i<count; i++) vals2[i] = vals[i];
      keywordVals = vals2;
    }
  }//end processKeyword

  //getters & setters--------------------------------------------------------
  public String getMessage(){return message;}
  public void setMessage(String newMessage){message = newMessage;}
  public String getKeyword(){return keyword;}
  public void setKeyword(String newKeyword){
    if (enciphered)
      System.out.println("You cannot change the keyword of an enciphered message.");
    else {
      keyword = newKeyword;
      processKeyword();
    }
  }
  public int[] getKeywordVals(){return keywordVals;}
  //no setKeywordVals because it depends on keyword
  public boolean getEnciphered(){return enciphered;}
  public void setEnciphered(boolean newEnciphered){enciphered = newEnciphered;}
  //--------------------------------------------------------------------------

  //Returns a string representation of a VigenereCipher object,
  //a string containing the message, the keyword, and if the message is enciphered
  public String toString(){
    String s = "Message:\n"+message+"\nKeyword: "+keyword+"\nEnciphered? "+
      enciphered;
    return s;
  }

  /*applies viginere en/de-cipher to the object's message variable, depending
  on cipher status, and returns the string (does NOT change message or
  enciphered variables)*/
  private String applyTransformation(boolean includePunctuation){
    //handle special case of no letters in keyword, which is the trivial case
    if (keywordVals.length==0) return message;

    //moving on to actual cipher!
    char c; //placeholders
    //index is mod val of current char, keyIndex is index in keyword
    //shift value is +- keywordVals[j], depending on enciphered
    int index, keyIndex = 0, shiftVal;
    //capitalization is a social construct, makes cipher easier to calculate
    message = message.toUpperCase();
    String newMessage = "";

    for (int i=0; i<message.length(); i++){
      c = message.charAt(i);
      index = letters.indexOf(c);
      //if alpha, apply shift
      if (index > -1){
        //adjust for enciphered or deciphered;
        if (enciphered) shiftVal = -keywordVals[keyIndex];
        else shiftVal = keywordVals[keyIndex];
        //do mod calculations here for negative correction below
        shiftVal = (shiftVal + index) % mod;
        //mod is terrible and doens't calculate least POSITIVE residue
        if (shiftVal<0) shiftVal=shiftVal+mod;//fix to POSITIVE
        //add to new message!
        newMessage = newMessage + letters.charAt(shiftVal);
        //loop keyIndex
        if (keyIndex<keywordVals.length-1) keyIndex++;
        else keyIndex = 0;
      }
      //otherwise, include punctuation
      else if (includePunctuation) newMessage = newMessage + c;
    }//end for

    return newMessage;
  }//end applyTransformation

  /*applies Viginere Cipher and returns the transformed message
  if the message is not already enciphered; otherwise returns the OG message*/
  public String encipher(boolean includePunctuation){
    //only encipher if not already enciphered
    if (enciphered) System.out.println(encipherFailed);
    else {
      message = this.applyTransformation(includePunctuation);//actually does the encipher
      enciphered = true;
    }
    return message;
  }

  /*undos Viginere Cipher and returns the transformed message
  if the message is already enciphered; otherwise returns the OG message*/
  public String decipher(){
    //only try to decipher an enciphered message
    if (enciphered) {//assume includePunctuation since we don't know
      message = this.applyTransformation(true);//actually does the decipher
      enciphered = false;
    }
    else System.out.println(decipherFailed);
    return message;
  }

  public static void main(String[] args){
    String message = args[0], keyword = args[1];
    VigenereCipher vc = new VigenereCipher(message, keyword);
    System.out.println(vc.getMessage()+"\n"+vc.getKeyword()+"\n"+vc.getEnciphered());
    System.out.println("\n\n"+vc.encipher(true)+"\n"+vc.decipher());
    vc.setMessage("hello world");
    vc.setKeyword("");
    System.out.println(vc);
    vc.decipher();
    System.out.println(vc.encipher(false));

  }//end main
}//end VigenereCipher

/*note: I could have implemented the same "general shift" method from
"Ceasar Cipher" (reliance on ASCII conversion rather than mod 26) but
implementing it would not be very interesting, and I do consider the ASCII
conversion to be "below" this number theory project, so I neglected that
method :) */
