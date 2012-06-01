package com.gmail.at.faint545.utils;

import java.io.IOException;

public class ExceptionHandler {
  public static String translate(Exception e) {
    if(e instanceof IOException) {
      return "Could not connect to SABNzbd. Please check your settings.";
    }
    return null;
  }
}
