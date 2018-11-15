import java.util.ArrayList;
import java.util.Scanner;

public class Driver {

	static Scanner s = new Scanner(System.in);
	
	static String name = "Trump";
	
    public static void main(String[] args) {
    		
    		Scanner s = new Scanner(System.in);
    		
    		String userProgramChoice;
    		
    		do {
        		System.out.println("-- President Parser --");
        		System.out.println("(Phrase Generator) : type G");
        		System.out.println("(Phrase Matcher) : type M");
        		System.out.println("(Quick Info) : type I");
        		System.out.println("(Quit) : type Q");
    			
    			userProgramChoice = s.nextLine();
    			
    			if (!userProgramChoice.equalsIgnoreCase("Q")) {
    				switch(userProgramChoice.toUpperCase()) {
    				case "G":
    					System.out.println("-- Phrase Generator --");
    					String presNameGen;
    					
    					boolean successGen = false;
    					do {
        					System.out.print("Enter a president's name: ");
        					presNameGen = s.nextLine().trim();
        					if (!presNameGen.equals("")) {
        						successGen = parsePresident(presNameGen, false);
        						if (!successGen) {
        							System.out.println("President not found. Try again.");
        						}
        					}
        					else {
        						System.out.println("Invalid name. Try again.");
        					}
    					} while(presNameGen.equals("") && successGen);
    					
    					String userPhraseChoice;
    					do {
    						System.out.println("(Run Generator) : type G");
    						System.out.println("(Quit to Main Menu) : type Q");
    						userPhraseChoice = s.nextLine();
    						if (userPhraseChoice.equalsIgnoreCase("G")) {	
    							System.out.print("Enter a phrase length: ");
    							String length = s.nextLine();
    							if (isInt(length)) {
    								int lengthInt = Integer.parseInt(length);
    								Word w = Word.getRandomWord();
    								Word prev = null;
    								Word temp;
    								
    								for (int i = 0; i < lengthInt; i++) {
    									System.out.print(w+" ");
    									temp = w;
    									w = w.pickNextWord(prev);
    									prev = temp;
    								}
    								
    								System.out.println("");
    							}
    						}
    						else if (!userPhraseChoice.equalsIgnoreCase("Q")) {
    							System.out.println("Input not recognized. Try again.");
    						}
    					} while (!userPhraseChoice.equalsIgnoreCase("Q"));
    					
    					Word.reset();
    					
    					break;
    				case "M":
    					System.out.println("-- Phrase Matcher --");
					String userMatchChoice;
					String bestPresident = "N / A";
					double lowestScore = Double.MAX_VALUE;
					
					do {
						System.out.println("(Run Matcher) : type M");
						System.out.println("(Quit to Main Menu) : type Q");
						userMatchChoice = s.nextLine();
						if (userMatchChoice.equalsIgnoreCase("M")) {
								System.out.print("Enter in a phrase: ");
								String toMatch = s.nextLine();
								String userPresidentChoice;
								do {
								System.out.println("(Include a president): type their name");
								System.out.println("(Calculate): type C");
								userPresidentChoice = s.nextLine().trim();
								if (userPresidentChoice.equalsIgnoreCase("C")) {
									System.out.println("Most likely president: "+bestPresident);
								}
								else if (!userPresidentChoice.equals("")){
									if (parsePresident(userPresidentChoice, false)) {
										double score = Word.differenceScore(toMatch);
										if (score < lowestScore) {
											lowestScore = score;
											bestPresident = userPresidentChoice;
										}
										Word.reset();
										}
									else {
										System.out.println("President not found. Try again.");
									}
								}
							}
							while(!userPresidentChoice.equals("C"));
						}
						else if (!userMatchChoice.equalsIgnoreCase("Q")) {
							System.out.println("Input not recognized. Try again.");
						}
					} while (!userMatchChoice.equalsIgnoreCase("Q"));
					
    					break;
    				case "I":
    					System.out.println("-- President Info --");
    					String presNameInfo;
    					
    					boolean successInfo = false;
    					do {
        					System.out.print("Enter a president's name: ");
        					presNameInfo = s.nextLine().trim();
        					if (!presNameInfo.equals("")) {
        						successInfo = parsePresident(presNameInfo, true);
        						if (!successInfo) {
        							System.out.println("President not found. Try again.");
        						}
        					}
        					else {
        						System.out.println("Invalid name. Try again.");
        					}
    					} while(presNameInfo.equals("") && successInfo);
    					
    					
    					break;
    				default:
    					System.out.println("Input not recognized. Try again.");
    					break;
    				}
    			}
    		} while(!userProgramChoice.equalsIgnoreCase("Q"));
    		
    		s.close();
    }
    
    public static boolean parsePresident(String name, boolean printInfo) {
    	System.out.print("Parsing president '"+name+"' . . . ");
		String presidentLink = WebCrawler.getPresidentLink(name);
		
		if (presidentLink == null) {
			return false;
		}
		
		ArrayList<String> speechLinks = WebCrawler.getPresidentSpeechLinks(presidentLink);
		
		if (speechLinks == null) {
			return false;
		}
		
		for (int i = 0; i < speechLinks.size(); i++) {
			String speechLink = speechLinks.get(i);
			String speech = WebCrawler.getSpeechFromSpeechLink(speechLink);
			speech = speech.replaceAll("[.,\\'#!$%\\^&\\*;\\[\\]:{}=\\-\\—\\–\\’_`~]","");
			Parser.parse(speech);
		}
		
		System.out.println("finished parsing.");
		
		if (printInfo) {
			System.out.println(name+"'s speech count: "+speechLinks.size());
			System.out.println(name+"'s vocabulary size: "+Word.getUniqueWordCount());
			System.out.println(name+"'s average unique words per speech: "+Word.getUniqueWordCount() / speechLinks.size());
		}
		
		return true;
    }
    
    /**
     * checks whether a string can be parsed as an int
     * @param input string to check
     * @return whether parsing succeeds
     */
    public static boolean isInt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }
}
