package net.saff.glinda.core;

import java.util.TimeZone;


public class Glinda {
  public static void main(String... args) {
    // TODO: make this somehow testable
	  // TODO (Jun 17, 2008 9:04:46 AM): Make this somehow right
    TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
    new Glinda().doMain(args);
  }

  private void doMain(String[] args) {
    GlindaInvocation.fromLocation(getLocation(), args).printOutput(System.out);
  }

  private String getLocation() {
    return System.getProperty("net.saff.LoqBookLocation");
  }
}

// TODO: separate running jars from testing jars
