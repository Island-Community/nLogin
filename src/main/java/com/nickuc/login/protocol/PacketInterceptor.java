package com.nickuc.login.protocol;

import com.nickuc.login.auth.login.LenientLoginFlow;
import com.github.retrooper.packetevents.wrapper.login.client.WrapperLoginClientLoginStart;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.security.hashing.Sha256Digest;
import io.netty.util.AttributeKey;
import javax.annotation.Nullable;


public class PacketInterceptor {
   @Nullable
   private final byte[] values;
   public static final AttributeKey<PacketInterceptor> attributeKey = BusyLoginListener.loadAttributeKey("nlogin-login-data");
   @Nullable
   private final SpawnLookup spawnLookup;
   private final String name;
   @Nullable
   private final Sha256Digest sha256Digest;
   private final Object object;
   private final WrapperLoginClientLoginStart wrapperLoginClientLoginStart;
   private LenientLoginFlow lenientLoginFlow;
   private final String activeName;

   @Nullable
   public Sha256Digest resolveShaDigest() {
      return this.sha256Digest;
   }

   public PacketInterceptor resolvePacketInterceptor(byte[] target) {
      return new PacketInterceptor(this.spawnLookup, this.activeName, this.name, this.object, this.wrapperLoginClientLoginStart, this.sha256Digest, target);
   }

   public WrapperLoginClientLoginStart findWrapperLoginClientLoginStart() {
      return this.wrapperLoginClientLoginStart;
   }

   public String resolveMessage() {
      return this.name;
   }

   public PacketInterceptor(
      @Nullable SpawnLookup target, String input, String output, Object context, WrapperLoginClientLoginStart data, @Nullable Sha256Digest value, @Nullable byte[] result
   ) {
      this.spawnLookup = target;
      this.activeName = input;
      this.name = output;
      this.object = context;
      this.wrapperLoginClientLoginStart = data;
      this.sha256Digest = value;
      this.values = result;
   }

   @Nullable
   public byte[] findPayload() {
      return this.values;
   }

   public void sendLenientLoginFlow(LenientLoginFlow target) {
      this.lenientLoginFlow = target;
   }

   public Object resolveObject() {
      return this.object;
   }

   @Nullable
   public SpawnLookup loadSpawnLookup() {
      return this.spawnLookup;
   }

   public String findMessage() {
      return this.activeName;
   }

   public LenientLoginFlow loadLenientLoginFlow() {
      return this.lenientLoginFlow;
   }
}
