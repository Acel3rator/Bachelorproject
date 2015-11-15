package TUDarmstadtTeam2.utils;


import java.util.Comparator;
import java.util.TreeSet;

/**
 * Created by philipp on 15.05.15.
 *
 * A Priority Queue which is optionally boundable.
 */
public class BoundablePriorityQueue<T> {

    private TreeSet<T> set;
    private Comparator<T> comparator;
    private int maxSize;
    private int queueSize;
    private boolean bounded;

    /**
     * Initializes the BoundedPriorityQueue as an unbounded
     * queue. It also sets the comparator to sort the elements.
     *
     * @param comparator how to order the elements of the queue
     */
    public BoundablePriorityQueue(Comparator<T> comparator){
        bounded = false;
        this.comparator = comparator;
        this.set = new TreeSet<T>(comparator);
        maxSize = Integer.MAX_VALUE;
        queueSize = 0;
    }

    /**
     * Tries to add an element to the queue.
     *
     * If there is still space in the queue, it adds the given
     * element, otherwise it will check if the given element is better
     * than the last element.
     * If it is the case, the last element will be replaced with the
     * given element, otherwise it is dropped.
     *
     * @param element the element to add
     * @return true if element have been added or if the last element
     * have been overwritten with the given element, otherwise false.
     */
    public boolean add(T element){
        if(queueSize < maxSize){
            boolean bo =  set.add(element);
            if(bo){
                queueSize++;
            }
            return bo;
        }
        else {
            if (comparator.compare(set.last(),element) < 0){
                set.pollLast();
                return set.add(element);
            }
            else {
                return false;
            }
        }
    }

    /**
     * To return the best element which is currently in the queue.
     *
     * @return currently best element in queue.
     */
    public T poll(){
        queueSize --;
        return set.pollFirst();
    }

    /**
     * To bound the amount of elements in the queue with the
     * current size of the queue.
     */
    public void boundQueue(){
        if(Config.PRINT_OUTPUTS) {
            System.out.println("Queue now bounded with size: " + queueSize);
        }
        this.maxSize = queueSize;
        bounded = true;
    }

    public boolean isEmpty(){
        return set.isEmpty();
    }
    public boolean isBounded(){
        return bounded;
    }

}
