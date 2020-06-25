import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text file

public class Functions{

//basic / text methods----------------------------------------------------

  //accepts a file name and returns a String representing the text stored
  //in the file. Returns an empty string by default
  public static String fileToString(String fileName){
    String result = "";

    try {
      Scanner reader = new Scanner(new File(fileName));
      while (reader.hasNextLine()) {
        //String data = myReader.nextLine();
        result = result + reader.nextLine() + "\n";
      }
      reader.close();
    }//end try
    catch (FileNotFoundException e) {
      System.out.println("File not found! :(");
    }//end catch
    return result;
  }

  public static String getLetters(){
    return "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  }

//mathematical methods----------------------------------------------------

  //iteratively tests if a given integer is prime
  public static boolean isPrime(int n){
    //1 is not prime
    if (n==1) return false;

    int end = n/2;

    for (int i=2; i<=end; i++){
        if (n%i==0) return false;
      }//end for
      return true;
    }//end isPrime

  /*basically a lazy version of Euclidean algorith
  finds the GCD of the integers a and b and returns it
  yes, this is more generally faster than iteratively checking all numbers
  up to max(a, b) as divisors of both a and b
  Copied from open source*/
  public static int GCD(int a, int b){
    while (a != b) {
      if (a<0 || b<0) return 0; //prevent infinite loop
          if(a > b)
                a = a - b;
            else
                b = b - a;
        }
    return a;
  }

  //finds the inverse of some integer mod n, where the inverse x is defined
  //as nx=1(mod "mod"). If no inverse exists, returns 0
  public static int getInverseMod(int n, int mod){
    //computers are really good at integer multiplication
    //it's easier just to calculate the inverse iteratively
    int inverse = 0;
    n = n % mod;

    if (GCD(n, mod)==1){//if inverse exists (check prevents inf recursion)
      //note: we know inverse exists, so don't worry about returning mod-1 in error
      while (inverse<mod && (inverse*n)%mod!=1) inverse++;
    }//end if

    return inverse;
  }//end getInverse

  /*an mod-power calculation that is able to calculate large squares
  under a given mod using successive squaring. Accepts a number n, the power
  the number is raised to, and the mod p, and returns the least POSITIVE
  residue n^power (mod p)*/
  public static int modPower(int n, int power, int p){
   int result=1;
   while (power>0){
      if (power%2==1) {
         result = (result*n)%p;
      }//end if
      n = (n*n)%p;//successive squaring
      power = power/2;
   }//end while
   return result;
 }//end modPower

  //phi(n) is the number of integers between 1 and n-1 that are relatively prime
  //to n. Yes, it's titled phiOfN but the input is x. Variable names are difficult.
  public static int phiOfN(int x){

    //base case
    if (x<3) return 1;

    //m*n=x (ALWAYS), m will be decreased and n will be increased until (m,n)=1
    //at that point for some k, n=i^k
    int half = x/2, m=x, n=1, k=0;

    //iteratively look for phi values. We need <= half bc otherwise 4 would fail.
    for (int i=2; i<=half; i++){
        while (m%i==0) {m=m/i; n=n*i; k++;}
        if (m != x) {
          //if m==1, then x=n=i^k, and i must be prime (otherwise we would have
          //already factored out its divisors)
          if (m==1)
            //prime powers: if p is prime and k gte 1, then phi(p^k)=p^k-p^(k-1)
            return n-n/i;//n=i^k, n/k=i^(k-1); see below for k=1 case
          //phi is multiplicative: if (m,n)=1, then phi(mn)=phi(m)*phi(n)
          //note b/c we left while loop, (m,n)=1 and x=mn always
          else return phiOfN(m)*phiOfN(n);
        }//end if
        //note: we don't need to set m, n, k b/c if those values changed,
        //then m!=n is true, so we return something and end the iteration
      }//end for

    //if prime, phi(n)=n-1
    //YES, n prime is technically a case of prime powers,
    //but this simplifies the computation
    return x-1;
  }//end phiOfN
}
