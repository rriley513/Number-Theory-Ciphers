/*VERY IMPORTANT: in a functional RSA we would have to use a BigInt like class,
since p & q should be primes at least one hundred digits long. I'm not
interested in extending this to BigInt, so I'll leave that as an exercise for
 the user. Additionally, some of the math methods are not the most efficient,
 so their runtimes might be rather frightening with such large primes...*/

public class RSACipher{

  //instance variables
  private String message;
  //in RSA, the "locking key" n and e are public, but p and q are secret
  private int p=1, q=1, e=1, n, phiN, d=1; //p, q are prime, n=pq, (e,phi(n))=1, d=inv(e) mod(phi(n))
  private boolean enciphered = false;

  private final String decipherFailed =
    "Decipher failed! Your message is not enciphered!",
    encipherFailed = "Encipher failed! Your message is already enciphered!",
    letters = Functions.getLetters();

  //constructors--------------------------------------------------------------

  //accepts a message to en/de-cipher, and values p, q, and e for the cipher
  //and returns an RSA cipher object
  public RSACipher(String myMessage, int P, int Q, int E){
    message = myMessage;
    if (Functions.isPrime(P)) p=P; else System.out.println("Not a prime: "+P);
    if (Functions.isPrime(Q)) q=Q; else System.out.println("Not a prime: "+Q)
    //don't do unnecessary calc if p, q is invalid. This is why e defaults to 1 :)
    if (p!=1 && q!=1) this.setValues(E);
  }

  //accepts fileName containing a message to encipher, and the values for p, q, and e
  //returns an RSA cipher object with those values
  public static RSACipher RSAFromText(String fileName, int p, int q, int e){
    String message = Functions.fileToString(fileName);
    return new RSACipher(message, p, q, e);
  }
  //end constructors----------------------------------------------------------

  //helper: makes sure that the given value of e is valid (in correct range),
  //and relatively prime to phi(n)
  private void setValues(int E){
    //called if p or q have been set, so we need to recalculate n and phi(n)
    n=p*q;
    //IMPORTANT: calculating phi(n) would be expensive, and is equivalent to
    //the much easier (p-1)*(q-1)
    phiN = (p-1)*(q-1);

    if (E<1) System.out.println("Warning: e is too small");
    else if (E>=phiN) System.out.println("Warning: e is too large");
    else if (Functions.GCD(E, phiN)!=1)
      System.out.println("Warning: (e, phi(n)) != 1");
    else {
      e=E;
      d=Functions.getInverseMod(e, phiN);
    }
  }

  //getters & setters----------------------------------------------------------
  public String getMessage(){return message;}
  public void setMessage(String m){message = m;}
  public int getP(){return p;}
  public void setP(int P){
    if (Functions.isPrime(P)) {
      p = P;
      this.setValues(e);
    } else System.out.println("Not a prime: "+P);
  }
  public int getQ(){return q;}
  public void setQ(int Q){
    if (Functions.isPrime(Q)) {
      q = Q;
      this.setValues(e);
    } else System.out.println("Not a prime: "+q);
  }
  public int getE(){return e;}
  public void setE(int e) {this.setValues(e);}
  //no setters for n, phi(n), d b/c they depend on other values
  public int getN(){return n;}
  public int getPhiN(){return phiN;}
  public int getD(){return d;}
  public boolean getEnciphered(){return enciphered;}
  public void setEnciphered(boolean enc){enciphered = enc;}
  //end getters & setters------------------------------------------------------

  public String toString(){
    String s = "Message: "+message+"\np="+p+"\nq="+q+"\nn="+n+"\nphi(n)="+phiN+
      "\ne="+e+"\nEnciphered? "+enciphered;
    return s;
  }

  /*applies the RSA encipher to the message, sets the enciphered message, and
  changes status to enciphered if the message is not already enciphered.

  almost identical to ExpCipher encipher, but calculating e's inverse mod(n)
  (which ExpCipher always does) would not only be expensive but pointless*/
  public String encipher(){
    if (enciphered) System.out.println(encipherFailed);
    else{
      message = message.toUpperCase();
      String newMessage = "";
      int len = message.length(), index;

      //Note: Like Hill cipher, thsi NEEDS to restrict iteself to letters
      //(no punctuation)
      for (int i=0; i<len; i++){
        index = letters.indexOf(message.charAt(i));
        if (index>-1)
          //overflow fixed by modPower
          newMessage = newMessage+Functions.modPower(index, e, n)+" ";
      }//end for

      //done w new Message
      message = newMessage;
      enciphered = true;
    }//end else
    return message;
  }//end encipher

  /*deciphers the message, sets the message, and changes status to not enciphered
  if the message is enciphered
  This method looks a lot like that from ExpCipher but the power calculation is
  very distinct.*/
  public String decipher(){
    if (enciphered){
      int C;
      String newMessage="";
      //enciphered message is numbers delimited by a space
      String[] lets = message.split(" ");

      for (int i=0; i<lets.length; i++)
        newMessage = newMessage +
          letters.charAt(Functions.modPower(Integer.parseInt(lets[i]), d, n));
        //newMessage = newMessage+letters.charAt((int) Math.pow(Integer.parseInt(lets[i]),d)%q);

      message = newMessage;
      enciphered = false;
    }
    else System.out.println(decipherFailed);

    return message;
  }

  public static void main(String[] args){

    RSACipher r = RSAFromText("frankenstein.txt", 7, 11, 17);
    System.out.println(r);
    System.out.println(r.encipher());
    System.out.println(r.decipher());
  }

}//end class
