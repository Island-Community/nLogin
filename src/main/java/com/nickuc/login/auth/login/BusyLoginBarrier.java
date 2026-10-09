package com.nickuc.login.auth.login;

import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.notification.NoticeSender;
import com.nickuc.login.platform.server.VerifiedServerAdapter;


public class BusyLoginBarrier {
   private final NoticeSender noticeSender;
   private final PrivateLoginOption privateLoginOption;

   public BusyLoginBarrier(PrivateLoginOption target, NoticeSender input) {
      this.privateLoginOption = target;
      this.noticeSender = input;
   }

   public NoticeSender findNoticeSender() {
      return this.noticeSender;
   }

   public static BusyLoginBarrier[] resolveValues(VerifiedServerAdapter instance, PrivateLoginOption... target) {
      if (target.length == 0) {
         throw new IllegalArgumentException("Button array cannot be empty!");
      }

      BusyLoginBarrier[] input = new BusyLoginBarrier[target.length];

      for (int output = 0; output < input.length; output++) {
         PrivateLoginOption context = target[output];
         input[output] = context.handleBusyLoginBarrier(instance);
      }

      return input;
   }

   public PrivateLoginOption resolvePrivateLoginOption() {
      return this.privateLoginOption;
   }
}
