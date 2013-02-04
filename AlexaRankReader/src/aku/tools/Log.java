package aku.tools;


public final class Log {
	
	private static void printDate(){
		System.out.print(Utils.getCurrentTimeString());
		System.out.print(": ");
	}
	
	public static void log(String ... message){
		printDate();
		
		for(String s:message)System.out.print(s);			
		System.out.println();
	}
	
	public static void log(String message, int t){
		printDate();
		
		for(int i = t;i>0;i--){
			System.out.print("   ");
		}
		
		System.out.println(message);
	}

}
