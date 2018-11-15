import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * ADAPTED FROM CIS 121 ASSIGNMENT
 * Facade for computing an unweighted shortest path between two vertices in a graph. We represent
 * paths as ordered lists of integers corresponding to vertices.
 *
 * @author Max Scheiber (scheiber), Lewis Ellis (ellisl), 15sp
 * @author davix
 */
public final class BFS {
    private BFS() {
    }

    /**
     * Returns a shortest path from {@code src} to {@code tgt} by executing a breadth-first search.
     * If there are multiple shortest paths, this method may return any one of them.
     * <p/>
     * Do NOT modify this method header.
     *
     * @param g the graph
     * @param src the vertex from which to search
     * @param tgt the vertex to find via {@code src}
     * @return an ordered list of vertices on a shortest path from {@code src} to {@code tgt}, or an
     * empty list if there is no path from {@code src} to {@code tgt}. The first element should be
     * {@code src} and the last element should be {@code tgt}. If {@code src == tgt}, a list
     * containing just that element is returned.
     * @throws IllegalArgumentException if {@code src} or {@code tgt} is not in the graph
     * @throws IllegalArgumentException if the specified graph is null
     */
    public static List<Word> getShortestPath(Word src, Word tgt) {
        
        if(src==tgt) {
            List<Word> path = new LinkedList<>();
            path.add(src);
            return path;
        }
        Set<Word> discovered = new HashSet<>();
        Map<Word, Word> parents = new HashMap<>();
        Queue<Word> discoverOrder = new LinkedList<>();
        discoverOrder.add(src);
        discovered.add(src);
        Set<Word> neighbors;
        Word current;
        while(!discoverOrder.isEmpty()) {
            current = discoverOrder.poll();
            neighbors = current.neighbors();
            for(Word v : neighbors) {
                if(!discovered.contains(v)) {
                    discovered.add(v);
                    parents.put(v, current);
                    discoverOrder.add(v);
                }
                if(v==tgt) {
                   return compilePath(src, tgt, parents);
                }
            }
        }
        
        return new LinkedList<>();
    }

    static List<Word> compilePath(Word src, Word tgt, Map<Word, Word> parents) {
        List<Word> backpath = new ArrayList<>();
        Word current = tgt;
        while(current!=src) {
            backpath.add(current);
            current = parents.get(current);
        }
        backpath.add(current);
        List<Word> path = new LinkedList<>();
        for(int i=backpath.size()-1; i>=0; i--) {
            path.add(backpath.get(i));
        }
        return path;
    }
}
