/*A class for analyzing the frequency of letters in a string.
Useful for deciphering a monoalphabetic substitution cipher*/

import java.util.Scanner;

public class FrequencyAnalysis{

  private String message, letters = Functions.getLetters();
  private int[] freqArray;
  private final int numLetters = letters.length();//num letters in Eng alphabet

  public static void main(String[] args){
    FrequencyAnalysis george = FAFromFile(args[0]);
    //FrequencyAnalysis george = new FrequencyAnalysis("hello world");
    System.out.println(prettyPrint(george.doubles()));
    //System.out.println(george.mostCommonLetters());
  }

  //accepts a message and returns a FrequencyAnalysis object, with the message
  //and a frequency analysis of its characters
  public FrequencyAnalysis(String myMessage){
    message =  myMessage.toUpperCase();
    freqArray = this.analyze();
  }

  //second "constructor", makes FrequencyAnalysis object from the text of
  //a file, whose name is the input.
  public static FrequencyAnalysis FAFromFile(String fileName){
    String message = Functions.fileToString(fileName);
    return new FrequencyAnalysis(message);
  }

  //getters & setters
  public String getMessage(){return message;}
  public int[] getFreqArray(){return freqArray;}
  public void setMessage(String newMessage){
    message = newMessage; //fyi, this = new FrequencyAnalysis isn't allowed
    freqArray = this.analyze();
  }

  /*accepts a string and returns a int array whose values correspond to
  the frequency of each letter in thousandths*/
  public int[] analyze(){
    //note: need a total because there might be punctuation
    int total=0, index;
    int[] count = new int[numLetters], //first tally up occurances
           freq = new int[numLetters]; //result array

    for (int i=0; i<message.length(); i++){
      index = letters.indexOf(message.charAt(i));
      if (index >= 0) {//another fancy version of "if is alpha"
        count[index]++; //increment sightings of the letter
        total++;//increase total number of letters seen
      }//end if
    }//end for

    //note freq and count have same length; calculate frequency
    for (int i=0; i<freq.length; i++)
      //the +.5 prevents rounding errors because we always round down
      freq[i] = (int) ((double) count[i] / total * 1000 + .5);

    return freq;
  }//end analyze

  /*calculates the occurance of two of the same letter appearing next to each
  other, and returns an int array of these totals as thousandths of the total
  number of doubles*/
  public int[] doubles(){
    //same setup as analyze()
    int total=0, index;
    int[] count = new int[numLetters], freq = new int[numLetters];
    //last seen and current char for testing double letters
    char last=message.charAt(0), now='\0';

    //start at SECOND letter because we already considered first letter
    //when assigning char last
    for (int i=1; i<message.length(); i++){
      now = message.charAt(i);
      index = letters.indexOf(now);
      if (index >=0 && last == now){
        count[index]++;
        total++;
      }
      last = now;
    }//end for

    for (int i=0; i<freq.length; i++)
      //the +.5 prevents rounding errors because we always round down
      freq[i] = (int) ((double) count[i] / total * 1000 + .5);

    return freq;
  }//end doubles

  /*returns a string of letters and their corresponding frequencies in the
  message, with nice formatting. Note: it is static so that it can be applied
  to constructed frequency arrays (ie the English frequency array)*/
  public static String prettyPrint(int[] freqArray){
    int count = 0;
    String result = "", letters = Functions.getLetters();

    for (int i=0; i<freqArray.length; i++){
      result = result + letters.charAt(i) + ": " + freqArray[i] + "\t";
      count++;
      if (count == 5) {
        result = result + "\n"; //clear line
        count = 0;
      }//end if
    }//end for
    result = result + "\n"; //clear line
    return result;
  }

  //just a pretty print of a frequency array. Assumes proper index order
  public String toString(){
    return prettyPrint(freqArray);
  }

  //returns a string with the most common letters in order. Good for a rough
  //freq analysis. Note: if a letter doesn't appear in the string, it has a
  //frequency of 0
  public String mostCommonLetters(){
    String result = "";
    char c = '\0'; //initialize, but blank char for concatenation
    //index = 0 is default, max is nonzero
    int index=0, len = freqArray.length, max = -1;
    int[] freqArray2 = new int[len];

    //first, copy freqArary into freqArr2
    for (int i=0; i<len; i++) freqArray2[i] = freqArray[i];

    //check if max is zero at beginning, then add last most common char
    while (max != 0){
      max = -1;
      for (int i=0; i<freqArray2.length; i++){
        if (freqArray2[i] > max) {
          index = i;
          max = freqArray2[i];
        }//end if
      }//end for
      freqArray2[index] = 0; //ingore previous maxes
      result = result + letters.charAt(index);
    }//end while
    //includes an extra char when exiting
    return result.substring(0, result.length()-1);
  }//end mostCommonLetters

  //Sums up the calculated frequencies (freqArray) as an accuracy check
  //it should sum up close to 1,000. Or close enough to 1000
  public int sumFreq(){
    int sum = 0;
    for (int i=0; i<freqArray.length; i++) sum = sum+freqArray[i];
    return sum;
  }

  //decipher section--------------------------------------------------------
  /*this array represents the letter frequency for ENGLISH.
  each letter corresponds to its base mod 26 index (A=0, B=1...)
  Statistic source: https://www.dcode.fr/frequency-analysis
  Source #s add to 1.03, likely due to rounding
  Like in the FrequencyAnalysis class, the statistics are stored in
  THOUSANDTHS*/
  public static int[] engFreqArray(){
    int[] engFreq = {82, 15, 28, 43, 127, 22, 20, 61, 70, 2, 8, 40, 24, 67, 75,
      19, 1, 60, 63, 91, 28, 10, 24, 2, 2, 1};
    return engFreq; //note cannot return directly an array created the way above
  }

  //A string with letters in order from most to least common.
  //this is the string that would be returned if we made a Frequency array
  //object with the English freq and called mostCommonLetters
  public static String engFreqStr(){
    return "ETAOINSHRLDCUMWFGYPBVKJXQZ";
  }

  //'guesses' a letter's substitution by replacing each occurance of that
  //letter with the new one in the given message
  public static String subLetter(String message, char find, char substitute){
      String findS = String.valueOf(find),
            substituteS = String.valueOf(substitute);
      return message.replace(findS, substituteS);
  }

  /*an interactive frequency analyzer and decoder. The user can see her
  frequecy table and sub letters accordingly, comparing it to an
  english frequency table. Accepts a String (the message to be analyzed)
  and returns the string with any substitutions made.*/
  public static String frequencyDecipher(String message){
    System.out.println("Welcome to interactive decipher!\n");
    //allow for frequency analysis. See FrequencyAnalysis class for "George"
    FrequencyAnalysis george = new FrequencyAnalysis(message);
    message = george.getMessage(); //basically, message.toUpperCase
    //so many strings
    String sentinel = "STOP", help = "HELP", undo = "UNDO", freq = "FREQ",
      eng = "ENG", sub="SUB", mem = "MESSAGE",
      input="MESSAGE", //initially display message
      helpInfo = "Type STOP to quit.\nType SUB and then enter two " +
        "characters in the format a b to replace one with the other.\nType " +
        "UNDO to undo that substitution (warning: you can only undo the most"+
        " recent substitution).\nType MESSAGE to see the current "+
        "message.\nType FREQ to see your frequency chart.\nType ENG to see"+
        " the English frequency chart. Warning: be careful about substituting"+
        " letters that are already in the message.",
        lastMessage = message,
        engFreq = FrequencyAnalysis.prettyPrint(engFreqArray());
    char replace = '#', with = '#'; //placeholders


    Scanner scan = new Scanner(System.in);
    //loop. Fencepost? Don't know her (in all seriousness, I want to display
    //the message initially, which is why it doesn't make sense to Fencepost)
    while (!input.equals(sentinel)){

      //help display
      if (input.equals(help)) System.out.println(helpInfo);
      //display frequency table
      else if (input.equals(freq)) {
        george.setMessage(message); //refresh FrequencyAnalysis
        System.out.println(george);
      }
      //display English language frequency table
      else if (input.equals(eng)) System.out.println(engFreq);
      //display current message
      else if (input.equals(mem)) System.out.println(
        "Your message is currently\n\n" + message + "\n");
      //undo last substitution; I thought this could be a useful feature :)
      else if (input.equals(undo)) {
        if (message.equals(lastMessage)) //can only undo most recent sub
          System.out.println("You can't undo anymore");
        else message = lastMessage;
      }
      //substitute some letters!
      else if (input.equals(sub)){
        lastMessage = message;
        System.out.println("Enter the char you want to replace:");
        replace = scan.next().toUpperCase().charAt(0); //get uppercase then char
        System.out.println("Enter the char you want to replace " + replace +
          " with:");
          with = scan.next().toUpperCase().charAt(0);//""
          message = subLetter(message, replace, with);
          scan.nextLine();
        }//end if sub
        //next inputs
        System.out.println("\nEnter STOP to stop and HELP for more information.");
        input = scan.nextLine();

      }//end while

      System.out.println("\nGoodbye! Your final message is\n" + message);
      return message;
    }//end FrequencyAnalysis
    //-------------------------------------------------------------------------

}//end class

/*note: I'm not going to name anything "FreqAnal" and any other substring of
"FrequencyAnalysis" sounds horrible. Hence I'm calling my FrequencyAnalysis
objects "George"*/
