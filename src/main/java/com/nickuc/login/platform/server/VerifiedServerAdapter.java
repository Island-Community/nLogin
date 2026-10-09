package com.nickuc.login.platform.server;

import com.nickuc.login.model.RemotePremiumState;
import com.nickuc.login.protocol.ChainedSessionHandler;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;

public interface VerifiedServerAdapter extends OutgoingSenderAdapter {
   default InetSocketAddress retrieveInetSocketAddress() {
      InetSocketAddress target = this.fetchInetSocketAddress();
      if (target == null) {
         throw new IllegalArgumentException("Address unavailable for " + this);
      } else {
         return target;
      }
   }

   void processMessage(String target);

   void performIndirectSessionHandler(IndirectSessionHandler<?> target, RemotePremiumState input, Object output, byte[] context);

   int getCount();

   void performMessage(String target);

   void saveMessage(String target, String input, int output, int context, int data);

   CompletableFuture<Void> buildCompletableFuture(String target);

   void performObject(Object target);

   @Nullable
   InetSocketAddress fetchInetSocketAddress();

   ChainedSessionHandler getChainedSessionHandler();

   void processMessageForValue(String target);

   boolean findState();

   void executeTask();

   boolean loadState();

   String findMessage();

   LinkedSessionHandler getLinkedSessionHandler();

   @Override
   default void dispatchMessage(String target) {
      this.performObject(target);
   }

   default String resolveMessage() {
      return this.retrieveInetSocketAddress().getAddress().getHostAddress();
   }

   @Override
   <T> T findObject();

   UUID getUniqueId();

   Optional<String> loadOptional();

   void handleMessage(String target);
}
