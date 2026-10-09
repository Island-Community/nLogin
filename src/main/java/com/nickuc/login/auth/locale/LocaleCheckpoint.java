package com.nickuc.login.auth.locale;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class LocaleCheckpoint {
   public static List<String> handleCollection(List<String> instance, String[] target) {
      if (target.length == 0) {
         return instance;
      }

      String input = target[target.length - 1].toLowerCase(Locale.ENGLISH);
      return input.isEmpty() ? instance : instance.stream().filter(targetValue -> targetValue.toLowerCase(Locale.ENGLISH).startsWith(input)).collect(Collectors.toList());
   }
}
