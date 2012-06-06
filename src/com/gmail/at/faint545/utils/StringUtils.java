/* 
 * Copyright 2011 Alex Fu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 		
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gmail.at.faint545.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils
{
  /**
   * Checks a string to see if it is empty.
   * A string is empty if it is null or has 0 characters.
   * @param string The string to test.
   * @return True if the string is empty and false otherwise.
   */
  public static boolean isEmpty(String string) {
    if(string == null) {
      return true;
    }
    else if(string.length() < 1) {
      return true;
    }
    else {
      return false;
    }
  }
  
  public static String unixTimeToDate(long timeStampInSeconds) {
    Date date = new Date(timeStampInSeconds*1000);
    StringBuilder dateString = new StringBuilder();
    dateString.append(date.toString());
    return dateString.toString();
  }
  
  public static String unixTimeToShortDate(long timeStampInSeconds) {
    Date finishedDate = new Date(timeStampInSeconds*1000);
    // Calendar date = Calendar.getInstance();
    // date.setTimeInMillis(timeStampInSeconds*1000);

    Date now = new Date();
    // Calendar now = Calendar.getInstance();
    SimpleDateFormat formatter;

    if(finishedDate.getMonth() == now.getMonth() &&
        finishedDate.getDate() == now.getDate() &&
        finishedDate.getYear() == now.getYear()) {
      formatter = new SimpleDateFormat("hh:mm a");
    }
    else {
      formatter = new SimpleDateFormat("MMM d");
    }
    return formatter.format(finishedDate);
  }
  
  public static String normalizeSeconds(long seconds) {
    long minutes, hours;
    if(seconds >= 60) {
      minutes = seconds/60;
      if(minutes >= 60) {
        hours = minutes/60;
        return hours + " hour(s)";
      }
      else return minutes + " minute(s)";
    }
    else return seconds + " second(s)";
    
  }

  /*
   * Capitalize the first letter of a word/string.
   * Named after PHP's ucfirst() function.
   */
  public static String ucfirst(String input)
  {
    String firstLetter = String.valueOf(input.charAt(0));
    return firstLetter.toUpperCase() + input.substring(1);
  }
  
  public static String basepath(String pathname) {  	
  	return pathname.substring(0, pathname.lastIndexOf("."));
  }
  
  public static String extension(String pathname) {  	
  	return pathname.substring(pathname.lastIndexOf("."),pathname.length());
  }
}
