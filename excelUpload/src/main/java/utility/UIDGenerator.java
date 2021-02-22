package utility;

import java.math.BigInteger;
import java.util.Random;

public class UIDGenerator {



public static String GenerateRandomNumber(int length) {
	 /*Random rand = new Random();
	   System.out.println(rand.nextInt());
	   BigInteger result = new BigInteger(1, rand); // (2^4-1) = 15 is the maximum value
	   return (String) result.toString().subSequence(0, length-1);*/
	  UIDGenerator uniqueNum =new UIDGenerator();
	// String uniqueVal =uniqueNum.getRandomNumber("0", 19);
	 for(int i=0;i<=200;i++){
		 String uniqueVal =uniqueNum.getRandomNumber("0", 19);
		 System.out.println(uniqueVal);
	 }
	
	   return null;
		
}

	public static String getRandomNumber(String prefix,int length){
	String num="";
	if(length<=11){
		length=12;
	}
	if(prefix!=null && prefix.length()!=0 && !"0".equals(prefix)){
		length=length-(prefix).length();
	}
	Random rndm=new Random(0);
	for(int i=1;i<=length;i++){
		String val=String.valueOf(getRandomInteger(10, 0));
		num = num + ("0".equals(val) ?  "1" : val) ;
	}
	
	String randomNum="";
	if("0".equals(prefix)){
		randomNum=num;
	}
	else{
		randomNum = prefix+num;
	}
	return randomNum;
}

public static int getRandom(int max){
   return (int) (Math.random()*max);
}

public static int getRandomInteger(int maximum, int minimum){
   return ((int) (Math.random()*(maximum - minimum))) + minimum;
}

public static void main(String args[]){
	UIDGenerator uniqueNum =new UIDGenerator();
	 for(int i=0;i<=200;i++){
		 String uniqueVal =uniqueNum.getRandomNumber("0", 19);
		 System.out.println(uniqueVal);
	 }

}
}