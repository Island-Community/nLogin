package com.nickuc.login.protocol;

import com.nickuc.login.account.SharedNoticeKind;
import com.nickuc.login.auth.login.CachedLoginProcessor;
import com.nickuc.login.auth.floodgate.FloodgateBarrier;
import com.nickuc.login.platform.packet.LoudPacketAdapter;
import com.nickuc.login.premium.SpawnLookup;
import io.netty.util.AttributeKey;
import javax.annotation.Nullable;


public class AccountAdapter implements LoudPacketAdapter {
   @Nullable
   private CachedLoginProcessor cachedLoginProcessor;
   public final SpawnLookup spawnLookup;
   public final SharedNoticeKind sharedNoticeKind;
   public final String name;
   public static final AttributeKey<AccountAdapter> attributeKey = BusyLoginListener.loadAttributeKey("nlogin-connection-data");
   public final FloodgateBarrier floodgateBarrier;
   private boolean enabled;

   @Override
   public String loadMessage() {
      return this.name;
   }

   @Override
   public boolean resolveState() {
      return this.enabled;
   }

   @Nullable
   @Override
   public CachedLoginProcessor resolveCachedLoginProcessor() {
      return this.cachedLoginProcessor;
   }

   public AccountAdapter createAccountAdapter(SpawnLookup target, String input) {
      if (target == null) {
         throw new IllegalArgumentException("Account cannot be null!");
      } else if (input == null) {
         throw new IllegalArgumentException("Real name cannot be null!");
      } else {
         return new AccountAdapter(target, input, this.floodgateBarrier, this.sharedNoticeKind, this.cachedLoginProcessor, this.enabled);
      }
   }

   @Override
   public void sendMessage(String target, String input) {
      this.cachedLoginProcessor = new CachedLoginProcessor(target, input);
   }

   @Override
   public SpawnLookup loadSpawnLookup() {
      return this.spawnLookup;
   }

   private AccountAdapter(SpawnLookup target, String input, FloodgateBarrier output, SharedNoticeKind context, @Nullable CachedLoginProcessor data, boolean value) {
      this.spawnLookup = target;
      this.name = input;
      this.floodgateBarrier = output;
      this.sharedNoticeKind = context;
      this.cachedLoginProcessor = data;
      this.enabled = value;
   }

   @Override
   public void updateTask() {
      this.enabled = true;
   }

   @Override
   public boolean findState() {
      return this.floodgateBarrier != null;
   }

   public AccountAdapter(SpawnLookup target, String input, FloodgateBarrier output, SharedNoticeKind context) {
      this.spawnLookup = target;
      this.name = input;
      this.floodgateBarrier = output;
      this.sharedNoticeKind = context;
   }
}
