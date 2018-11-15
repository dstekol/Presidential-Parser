import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a word, and stores its connections to words that follow or precede it
 * in sentences
 * @author Daniel
 *
 */
public class Word implements Comparable<Word>{
    static Map<String, Word> allWords = new HashMap<>();
    final String word;
    boolean isActualWord;
    Map<Word, Integer> outNeighbors;
    Map<Word, Integer> inNeighbors;
    Map<Word, Map<Word,Integer>> internalEdges;
    
    public Word(String w) {
        if(w.equals("")) {
            throw new IllegalArgumentException("cant be emtpy");
        }
        word = w.toUpperCase();
        allWords.put(word, this);
        outNeighbors = new HashMap<>();
        inNeighbors = new HashMap<>();
        internalEdges = new HashMap<>();
        isActualWord = Character.isLetter(w.charAt(0));
    }
    
    /**
     * Creates edges for successive words in graph, and adds internal edge links (from predecessor to successor0
     * @param textWords the list of all words in the text
     * @param spot the word index to process
     */
    public static void addInfo(List<String> textWords, int spot) {        
        String wStr = textWords.get(spot);
        String nextStr = spot+1<textWords.size()? textWords.get(spot+1) : null;
        String prevStr = spot-1>=0 ? textWords.get(spot-1) : null;
        
        if(wStr==null || wStr.equals("")) {
            throw new IllegalArgumentException(spot+" "+textWords);
        }
        Word w = getOrCreate(wStr);
        Word prev = prevStr==null ? null : getOrCreate(prevStr);
        Word next = nextStr==null ? null : getOrCreate(nextStr);
                
        if(prev!=null) {
            addFlow(prev, w);
        }
        if(next!=null) {
            addFlow(w, next);
        }
        if(prev!=null && next!=null) {
            w.linkEdges(prev, next);
        }        
    }
    
    /**
     * Retrieves a Word object from the graph, or null if the word does not exist
     * @param s the string corresponding to the Word object to retrieve
     * @return
     */
    public static Word getWord(String s) {
        s = s.toUpperCase();
        if(allWords.containsKey(s)) {
            return allWords.get(s);
        }
        else {
            return null;
        }
    }
    
    /**
     * Gets a random word from the graph
     * @return random word
     */
	public static Word getRandomWord() {
    		return (Word) allWords.values().toArray()[getRand(0, allWords.size() - 1)];
    }
	
	/**
	 * Returns the number of words in the graph
	 * @return
	 */
	public static int getUniqueWordCount() {
		return allWords.values().size();
	}
    
	/**
	 * This method is invoked on a Word object, and is passed that words predecessor: using that information, it randomly picks the next word based on the outgoing edge weights.
	 * If the previous word appears somewhere in the text as a predecessor of the current word the choice of the next word will be made only from the words that appeared in those contexts.
	 * If the previous word does not actually precede the current word anywhere in the text (or if it is null), the next word is chosen from the overall set of out-neighbors.
	 * @param prev
	 * @return
	 */
    public Word pickNextWord(Word prev) {
        Map<Word, Integer> edgeWeights;
        if(internalEdges.containsKey(prev)) {
            edgeWeights = internalEdges.get(prev);
        }
        else {
            edgeWeights = outNeighbors;
        }
        
        if(edgeWeights.keySet().isEmpty()) {
            return null;
        }
        
        int sum = 0;
        for(int i : edgeWeights.values()) {
            sum += i;
        }
        int rand = getRand(1, sum);
        int counter = 0;
        for(Word currentWord : edgeWeights.keySet()) {
            counter+=edgeWeights.get(currentWord);
            if(counter>=rand) {
            		if(currentWord.isActualWord) {}
                return currentWord;
            }
        }
        return null;
    }
    
    /**
     * Returns a random integer in the range from min to max, inclusive
     * @param min minimum of range
     * @param max maximum of range
     * @return generated random number
     */
    private static int getRand(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    
    /**
     * Within the word object that this method is invoked on, this method links the incoming edge from the previous word to the outgoing to edge to the next word.
     * These internal edge links are used in the phrase generator.
     * @param prev
     * @param next
     */
    private void linkEdges(Word prev, Word next) {
        if(!internalEdges.containsKey(prev)) {
            internalEdges.put(prev, new HashMap<Word, Integer>());
        }
        if(!internalEdges.get(prev).containsKey(next)) {
            internalEdges.get(prev).put(next, 0);
        }
        int currentValue = internalEdges.get(prev).get(next);
        internalEdges.get(prev).put(next, currentValue+1);
    }
    
    /**
     * Increases the weight of the edge from the previous word to the second word, (optionally creating an edge if none exists)
     * @param prev
     * @param w
     */
    private static void addFlow(Word prev, Word w) {
        prev.addForwardFlow(w);
        w.addBackwardFlow(prev);
        
    }
    
    /**
     * Called by the addFlow method: updates the edge record of the "previous" word
     * @param w
     */
    void addBackwardFlow(Word w) {
        if(!inNeighbors.containsKey(w)) {
            inNeighbors.put(w, 0);
        }
        int currentValue = inNeighbors.get(w);
        inNeighbors.put(w, currentValue+1);
    }
    
    /**
     * Called by the addFlow method: updates the edge record of the "current" word
     * @param w
     */
    void addForwardFlow(Word w) {
        if(!outNeighbors.containsKey(w)) {
            outNeighbors.put(w, 0);
        }
        int currentValue = outNeighbors.get(w);
        outNeighbors.put(w, currentValue+1);
    }
    
    /**
     *
     * @return the number of times that the word is used in the text
     */
    public int getUsage() {
        int sum = 0;
        for(int i : inNeighbors.values()) {
            sum+=i;
        }
        return sum;
    }
    
    /**
     * Either retrieves a Word object corresponding to the string provided, or creates a new one if none yet exists
     * @param wStr the string to use for retrieval
     * @return The Word corresponding to the provided string
     */
    private static Word getOrCreate(String wStr) {
        wStr = wStr.toUpperCase();
        if(!allWords.containsKey(wStr)) {
            Word w = new Word(wStr);
            allWords.put(wStr, w);
        }
        return allWords.get(wStr);
    }
    
    /**
     * Returns set of both in and out neighbors 
     * @return combined neighbor set
     */
    public Set<Word> neighbors(){
        Set<Word> neighbors = new HashSet<>();
        neighbors.addAll(inNeighbors.keySet());
        neighbors.addAll(outNeighbors.keySet());
        return neighbors;
    }
/**
     * Returns unmodifiable set of out neighbors
     * @return unmodifiable set of out neighbors
     */
    public Set<Word> outNeighbors(){
        return Collections.unmodifiableSet(outNeighbors.keySet());
    }
    
    /**
     * Returns unmodifiable set of in neighbors
     * @return unmodifiable set of in neighbors
     */
    public Set<Word> inNeighbors(){
        return Collections.unmodifiableSet(inNeighbors.keySet());
    }
/**
     * Returns a score characterizing how well the input string corresponds to 
     * the word graph. Low scores indicate high correspondence. This can be 
     * used for ranking how well presidents' speeches match the input string.
     * This is done by finding the length of the path within the graph that contains 
     * all the words in the query.
     * If some words in the query are absent in the graph, the difference-score is 
     * increased to indicate lower similarity.
     * Note - the path computations do not account for edge weights.
     * @param str string to check against graph
     * @return score characterizing how different the input string is from the graph
     */
    public static double differenceScore(String str) {
        String[] arr = str.split("\\s+");
        List<Word> words = new ArrayList<>();
        String filtered;
        for(String s : arr) {
            filtered = s.replaceAll("[^A-Za-z]", "").trim();
            words.add(getWord(filtered));
        }
        return getPathLength(words);
    }
    
    /**
     * Resets the Word Graph.
     * @return void
     */
    public static void reset() {
    		allWords = new HashMap<>();
    }

/**
     * Computes the length of the path (using BFS) connecting the words in the string 
     * to each other within the graph (short paths indicate that the words are
     * close to each other in the original text, meaning the string is a good match)
     * If one of the words in the query is not present 
     * @param words the list of words in the query
     * @return the length of the path within the graph that connects all these words
     */
    public static double getPathLength(List<Word> words) {
        double weight = 0;
        for(Word w : words) {
            if(w==null) {
                weight += words.size();
            }
            
        }
        Word[] nullArr = {null};
        words.removeAll(Arrays.asList(nullArr));
        Iterator<Word> iter = words.iterator();
        Word current;
        double currentWeight;
        if(iter.hasNext()) {
            Word next = iter.next();
            while(iter.hasNext()) {
                current = next;
                next = iter.next();
                currentWeight = getShortestPath(current, next);
                weight += currentWeight==-1 ? words.size() : currentWeight;
            }
        }
        
        
        return weight;
    }
    
    /**
     * finds the length of the shortest path connecting the src and tgt words
     * @param src start word of the path
     * @param tgt end word of the path
     * @return the length of the connecting path
     */
    private static double getShortestPath(Word src, Word tgt) {
        int pathLength = BFS.getShortestPath(src, tgt).size()-1;
        return pathLength;
    }
    
    @Override
    public String toString() {
        return word.toLowerCase();
    }
    
    public int compareTo(Word w) {
        return Integer.compare(this.getUsage(), w.getUsage());
    }
}
