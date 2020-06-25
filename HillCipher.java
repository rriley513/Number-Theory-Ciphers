/*Matrix class generously provided by Code Project through CPOL 1.02

  This class provides message encipher and decipher via the Hill Cipher,
  which uses Matrix multiplication to encipher letters in "blocks".
  Its encipher function is C=AP+K; where A is the scalar Matrix, K is the
  shift matrix, and C and P are the cipher and plaintext matrices respectively
  representing letters from the message.

  IMPORTANT: for ease of Matrix operations, the scalar and shift are stored
  as *double* arrays, and rounded *down* when converting to ints for modular
  aritmetic
*/

import matrix.*;

public class HillCipher{

  String message;
  int size, determinant;
  Matrix scalar, shift, inverse;
  boolean enciphered;

  private final String decipherFailed =
    "Decipher failed! Your message is not enciphered!",
    encipherFailed = "Encipher failed! Your message is already enciphered!";

  //accepts a String message, a 2-d double array representing the scalar, and
  //a 1-d double array representing the shift.
  public HillCipher(String Message, double[][] Scalar, double[] Shift)
    throws NoSquareException{//exception when checking valid & making inverse

    message = Message.toUpperCase();
    size = Scalar.length;
    //validScalar tests if the scalar is valid and returns an ID matrix if not
    this.validScalar(Scalar);

    //shift needs to be the right size, and be a n x 1 matrix, but a 1-d
    //array is a 1 x n matrix, so we need to convert it.
    if (Shift.length == size) {
      shift = doubleArrToMatrix(Shift);
    }
    //the int-int constructor for Matrix assumes an empty (zeroes) 2-d array
    //which is what we want the default to be
    else {
      System.out.println("Warning: your shift matrix has size " + Shift.length +
      " but it should have size " + size + ". A substitute zero matrix of " +
      "size " + size + " x 1 wil be used.");
      shift = new Matrix(size, 1);
    }
    enciphered = false;
  }//end constructor

  /*returns a square Indentity Matrix of the given size, ie a matrix I of
  size n x n, so that for any matrix A of size n x m, IA=AI=A*/
  private static Matrix IDMatrix(int len){
    double[][] id = new double[len][len];
    for (int i=0; i<len; i++) id[i][i] = 1.0;
    return new Matrix(id);
  }

  //accepts a double array and returns an 1 x n Matrix representation of the array
  private static Matrix doubleArrToMatrix(double[] a){
    double[][] b = new double[1][a.length];
    b[0] = a;//n x 1 format
    return MatrixMathematics.transpose(new Matrix(b));//transpose gets 1 x n format
  }

  /*for a matrix to have an inverse, it must be invertible (det!=0)
  we need an inverse for deciphering
  moreover, because we are in mod 26, the determinant needs to have an
  inverse mod 26 (ie GCD(det, 26)=1) */
  public void validScalar(double[][] a) throws NoSquareException{
    if (size!=a[0].length){ //not square
      System.out.println("Warning: your scalar matrix has dimensions " + size
        + " x " + a[0].length + " and is not square. A substitute " +
        "identity matrix of size " + size + " x " + size + " will be used.");
      scalar = IDMatrix(size);
      determinant = 1;
      inverse = scalar; //the inverse of the ID matrix is itself
    }
    else {
      Matrix m = new Matrix(a);
      int det = (int) MatrixMathematics.determinant(m);
      int mod = 26;
      //"everything is correct" case
      if (Functions.GCD(det, mod) == 1) {
        scalar = m;
        determinant=det;
        inverse = MatrixMathematics.inverse(scalar);
      }
      else {
        System.out.println("Your scalar is invalid because its determinant"+
        " shares a factor with  26. A substitute identity matrix of size "+
        size+" x "+ size+" will be used.");
        scalar = IDMatrix(size);
        determinant = 1;
        inverse = scalar; //the inverse of the ID matrix is itself
      }//end else
    }//end else
  }

  //returns a Hill Cipher object whose message is the text contained in the
  //given file, with the given scalar and shift values
  public static HillCipher HCFromText(String fileName, double[][] scalar,
  double[] shift)
    throws NoSquareException{//NoSquareException from Constructor call
      String message = Functions.fileToString(fileName);
      return new HillCipher(message, scalar, shift);
  }

  //getters & setters----------------------------------------------------------
  public String getMessage(){return message;}
  public void setMessage(String newMessage){message = newMessage;}
  public int getSize(){return size;}
  public void setSize(int newSize){
    System.out.println(
      "Warning: changing size will erase current scalar and shift");
    size = newSize;
    scalar = IDMatrix(size);
    shift = new Matrix(size, 1);
  }
  public double[][] getScalar(){return scalar.getValues();}
  public void setScalar(double[][] newScalar) throws NoSquareException{
    validScalar(newScalar);//updates inverse and determinant if needed
  }
  public int getDeterminant(){return determinant;}
  public double[][] getInverse(){return inverse.getValues();}
  //NO setInverse or setDeterminant because they are dependent on scalar
  //for getShift, we need to transpose from a n x 1 matrix to a 1 x n array
  public double[] getShift(){
    double[][] res = MatrixMathematics.transpose(shift).getValues();
    return res[0]; //the values are stored as a double[] in the first index
  }
  public void setShift(double[] newShift){
    if (newShift.length==size) shift = doubleArrToMatrix(newShift);
    else System.out.println("setShift failed. Wrong size.");
  }
  public boolean isEnciphered(){return enciphered;}
  public void setEnciphered(boolean newEnciphered){enciphered = newEnciphered;}
  //end getters & setters-------------------------------------------------------

  //a String representation of the HillCipher object
  //gives the message, size, scalar, shift, and enciphered status
  public String toString(){
    String s = message + "\n" + "Matrix sizes: " + size + "\nScalar:\n" +
      scalar + "\nShift:\n" + shift + "\nDeterminant: "+ determinant +
      "\nEnciphered? " + enciphered;
    return s;
  }

  //A mod method that accepts a double and an integer mod, converts the double
  //to an int, and returns the int's least *POSITIVE* residue
  private static int betterMod(double n, int mod){
    int k = ((int) n)%mod;
    if (k<0) return k+mod;
    return k;//implicit else
  }

  /*zero paramater helper method for encipher and decipher
  reads off the (alpha) characters in the message and stores them in a Matrix
  object. If the message is not enciphered, it enciphers the characters
  and if the message is enciphered, deciphers the characters, using the
  respective encipher decipher functions.
  returns the en/de-ciphered string but does not overwrite message*/
  private String applyTransformation() throws IllegalDimensionException{
    String newMessage = "", letters = Functions.getLetters(),
          extraStr = "*_"; //extraStr is for controlling extra chars
    int count = 0, total = message.length(), //for checking if done
      index, //index helps to see if char is a letter
      n=0; //n is num letters in this set already
    //letsArr contains set of letters to be enciphered
    double[] letsArr = new double[size];
    //letsMatrix=matrix representation of letsArr, result=transformed letsMatrix
    Matrix letsMatrix, result;

    /*used in decipher
    here, we first multiply the inverse of scalar by "determinant" to transform
    the elts into whole numbers again. However, we need to "undo" this
    multiplication, so we multiply by the determinant's inverse mod 26, "inv"
    this leaves us with the equivalent inverse, but with whole numbers,
    which are needed for deciphering*/
    double inv = (double) Functions.getInverseMod((int) determinant, 26);
    Matrix specialInverse =
      inverse.multiplyByConstant(determinant).multiplyByConstant(inv);

    //enough setup, on to ciphers!
    while (count < total){//while there is more of the message left
      while (n<size && count < total){//while more letters to add to set
        //if we are at a letter, letters.indexOf() will be >= 0
        index = letters.indexOf(message.charAt(count));
        if (index >= 0) {//if is a letter, add to the array
          letsArr[n] = (double) index; //double b/c matrices use doubles
          n++;//increment chars processed
        }//end if letter
        count++;//increment total chars seen, regardless of letter or not
      }//end while n<size
      //now letsArr is full,
      letsMatrix = doubleArrToMatrix(letsArr);//these are our letters

      //Encipher and Decipher functions are fairly different

      if (enciphered)//don't do encipher, ie do DECIPHER with P=inv(A)*(C-K);
      //specialInverse = inv(A), letsMatrix=C, shift=K
        result = MatrixMathematics.multiply(specialInverse,//inv(A)*...
          MatrixMathematics.subtract(letsMatrix, shift));//C-K
      //encipher part; encipher with C=AP+K; letsMatrix=P, scalar=A, shift=K
      else result = MatrixMathematics.add(//+ section
        MatrixMathematics.multiply(scalar, letsMatrix),//A*P
        shift);//+K

      //now convert result to text;
      //first put nums back into an array for iteration; overwriting, it's fine
      letsArr = MatrixMathematics.transpose(result).getValues()[0];

      //transfer over characters. note: intentionally adds "leftover" chars
      if (n!=0){//we don't want to transfer over an empty array/all extra chars
        for (int i=0; i<size; i++){//convert to new message
          newMessage=newMessage+letters.charAt(betterMod(letsArr[i], 26));
        }
      }

      /*Handling extra chars
      If size does not divide message.length(), we will have no less than one
      leftover space which is factored into the encipher Matrix, no matter how
      we represent this extra space. Because it is involved in encipher's
      Matrix multiplication, we need it for the decipher as well. However,
      we also need to be able to tell how many extra chars are at the end of
      the ciphertext, to disclude them from the final deciphered message.
      Hence for every "leftover"/extra character added in encipher, we also add
      a symbol at the end of the string, allowing us to count the "extra
      characters" and remove them
      */
      if (count==total){//end of message
        if (enciphered){//if deciphering, need to remove chars
        int numRemove=0;
        boolean hasExtra = message.contains(extraStr);
        while (hasExtra){
          //we know this sequence is at the end of the message, so remove it
          message = message.substring(0, message.length()-2);
          hasExtra = message.contains(extraStr);
          numRemove++;//increment number of chars to remove
        }//end while, remove extra chars
        newMessage=newMessage.substring(0, newMessage.length()-numRemove);
      }//end if enciphered

      else{//enciphering
        int extras = size-n;//number of extra chars; extraStr symbol
        for (int i=0; i<extras; i++) newMessage = newMessage + extraStr;
      }
    }//end if count==total


    n=0;//reset n
    }//end while more letters
    return newMessage;
  }

  /*applies Hill Cipher and returns the transformed message
  IllegalDimensionException comes from applyTransformation, which uses
  MatrixMathematics.multiply(matrices)*/
  public String encipher() throws IllegalDimensionException{
    //only encipher if not already enciphered
    if (enciphered) System.out.println(encipherFailed);
    else {
      message = this.applyTransformation();//actually does the encipher
      enciphered = true;
    }
    return message;
  }

  /*undos the Hill Cipher and returns the deciphered message
  IllegalDimensionException comes from applyTransformation, which uses
  MatrixMathematics.multiply(matrices)*/
  public String decipher() throws IllegalDimensionException{
    //only try to decipher an enciphered message
    if (enciphered) {
      message = this.applyTransformation();//actually does the decipher
      enciphered = false;
    }
    else System.out.println(decipherFailed);
    return message;
  }

  public static void main(String[] args){
    String text = "frankenstein2.txt";
    double[][] scalar = {{1., 2., 3.}, {0., 1., 4.}, {5., 6., 0.}},
              scalar3 = {{3.,0., 0.},{0.,3., 0.}, {0., 0., 3.}},
              scalarID4 = {{1., 0., 0., 0.}, {0., 1., 0., 0.},
                          {0.,0.,1., 0.}, {0.,0.,0.,1.}},
              scalar2 = {{3.,0.}, {0., 3.}};
    double[] shift3 = {1.,1., 1.},
            shiftID = {0., 0., 0., 0.},
            shift2 = {1.,1.};

    try{
      HillCipher henry = HCFromText(text, scalar, shift3);
      System.out.println(henry.encipher());
      System.out.println(henry.decipher());
    }    catch(Exception e){System.out.println(e);}

  }
}
