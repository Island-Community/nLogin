package com.nickuc.login.auth.locale;

import java.util.Locale;

public class LocaleBarrier {
   private static final Locale locale = new Locale("pt", "BR");

   public static String createMessage(LocaleFlow instance, boolean target) {
      return target
         ? instance.retrieveMessage()
            + "/"
            + instance.resolveMessage()
            + "/"
            + instance.findMessage()
            + " às "
            + instance.fetchMessage()
            + ":"
            + instance.getMessageForMessage()
            + ":"
            + instance.loadMessageForMessage()
         : instance.resolveMessage()
            + "/"
            + instance.retrieveMessage()
            + "/"
            + instance.findMessage()
            + " at "
            + instance.fetchMessage()
            + ":"
            + instance.getMessageForMessage()
            + ":"
            + instance.loadMessageForMessage();
   }

   public static LocaleFlow buildLocaleFlow(long instance, boolean input) {
      return new LocaleFlow(instance, input ? locale : Locale.getDefault());
   }

   public static String handleMessage(LocaleFlow instance, boolean target) {
      return target
         ? instance.retrieveMessage() + "/" + instance.resolveMessage() + "/" + instance.findMessage()
         : instance.resolveMessage() + "/" + instance.retrieveMessage() + "/" + instance.findMessage();
   }

   public static String loadMessage(LocaleFlow instance) {
      return instance.fetchMessage() + ":" + instance.getMessageForMessage() + ":" + instance.loadMessageForMessage();
   }

   public static String computeMessage(long instance, boolean input) {
      return createMessage(buildLocaleFlow(instance, input), input);
   }
}
