package com.nickuc.login.protocol;

import com.nickuc.login.platform.player.ChainedPlayerContract;
import io.netty.util.AttributeKey;
import javax.annotation.Nullable;


public class DialogHandler {
   public byte mask;
   public static final AttributeKey<DialogHandler> attributeKey = BusyLoginListener.loadAttributeKey("nlogin-dialogs");
   public static final byte activeMask = 0;
   public static final byte pendingMask = 2;
   public static final byte currentMask = 1;
   @Nullable
   public final ChainedPlayerContract chainedPlayerContract;

   private DialogHandler(@Nullable ChainedPlayerContract target, byte input) {
      this.chainedPlayerContract = target;
      this.mask = input;
   }
}
