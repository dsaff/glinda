package net.saff.glinda.interpretation.invoking;

import java.util.LinkedList;
import java.util.List;

public class ArgumentStrings {
  public List<String> argsList;

  private ArgumentStrings(List<String> args) {
    this.argsList = new LinkedList<String>(args);
  }

  public ArgumentStrings(List<String> args, int stringArgumentCountNeeded) {
    this(args);
    collapseToLength(stringArgumentCountNeeded);
  }

  void collapseToLength(int desiredLength) {
    while (size() > desiredLength)
      combineLastTwo();

    while (size() < desiredLength)
      if (lastHasSpace())
        splitLast();
      else
        addNull();
  }

  private boolean addNull() {
    return argsList.add(null);
  }

  private void splitLast() {
    String lastArg = removeLast();
    int firstSpace = lastArg.indexOf(' ');
    argsList.add(lastArg.substring(0, firstSpace));
    argsList.add(lastArg.substring(firstSpace + 1));
  }

  boolean lastHasSpace() {
    boolean hasOneItem = argsList.size() > 0;
    return hasOneItem && getLast() != null && getLast().contains(" ");
  }

  private void combineLastTwo() {
    String lastArg = removeLast();
    argsList.set(lastIndex(), getLast() + " " + lastArg);
  }

  private String getLast() {
    return argsList.get(lastIndex());
  }

  private String removeLast() {
    return argsList.remove(lastIndex());
  }

  private int lastIndex() {
    return argsList.size() - 1;
  }

  public String car() {
    return argsList.get(0);
  }


  public ArgumentStrings cdr() {
    return new ArgumentStrings(argsList.subList(1, argsList.size()));
  }

  private int size() {
    return argsList.size();
  }
}
