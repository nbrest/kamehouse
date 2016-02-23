package ar.com.nicobrest.mobileinspections.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *        Class to generate a thread safe sequence number to use as id
 *        
 * @since v0.03
 * @author nbrest
 */
public class IdGenerator { 
  
  private static final AtomicInteger sequence = new AtomicInteger(1);

  private IdGenerator() {}

  /**      
   *      Return next number in the sequence
   *      
   * @since v0.03
   * @author nbrest
   * @return Long
   */
  public static Long getId() {
    return Long.valueOf(sequence.getAndIncrement());
  } 
}