package ar.com.nicobrest.mobileinspections.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @since v0.03
 * @author nbrest
 *      
 *      Class to generate a thread safe sequence number to use as id
 */
public class IdGenerator { 
  
  private static final AtomicInteger sequence = new AtomicInteger(1);

  private IdGenerator() {}

  /**
   * @since v0.03
   * @author nbrest
   * @return Long
   * 
   *     Return next number in the sequence
   */
  public static Long getId() {
    return Long.valueOf(sequence.getAndIncrement());
  } 
}