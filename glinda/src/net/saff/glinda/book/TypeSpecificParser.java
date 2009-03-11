// Copyright 2008 Google Inc. All Rights Reserved.

package net.saff.glinda.book;

import java.io.Serializable;
import java.text.ParseException;

public interface TypeSpecificParser extends Serializable {
  Object parse(String string) throws ParseException;
}