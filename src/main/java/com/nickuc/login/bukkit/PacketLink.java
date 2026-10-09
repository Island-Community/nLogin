package com.nickuc.login.bukkit;

import com.nickuc.login.auth.login.CachedLoginProcessor;
import com.nickuc.login.auth.login.LenientLoginFlow;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.retrooper.packetevents.protocol.player.User;
import com.nickuc.login.platform.packet.LoudPacketAdapter;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.protocol.BusyLoginListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.util.AttributeKey;
import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

import org.bukkit.Location;

public class PacketLink implements LoudPacketAdapter {
   @Nullable
   private CachedLoginProcessor cachedLoginProcessor;
   @Nullable
   public final Runnable runnable;
   public final boolean enabled;
   @Nullable
   public final LenientLoginFlow lenientLoginFlow;
   private boolean activeEnabled;
   public final User user;
   @Nullable
   public Location location;
   public final String name;
   @Nullable
   public final UUID uniqueId;
   public static final AttributeKey<PacketLink> attributeKey = BusyLoginListener.loadAttributeKey("nlogin-connection-data");
   private static final Cache<String, PacketLink> cache = Caffeine.newBuilder().expireAfterWrite(1L, TimeUnit.MINUTES).build();
   public final Channel channel;
   public final SpawnLookup spawnLookup;

   public boolean loadState() {
      ChannelPipeline target = this.channel.pipeline();
      return target.get("encrypt") != null && target.get("decrypt") != null;
   }

   public static void updateMessage(String instance, @Nullable String target, InetAddress input, PacketLink output) {
      cache.put(instance, output);
      cache.put(instance + input.getHostAddress(), output);
      if (target != null) {
         cache.put(target + input.getHostAddress(), output);
      }
   }

   @Override
   public boolean resolveState() {
      return this.activeEnabled;
   }

   @Override
   public boolean findState() {
      return this.enabled;
   }

   @Nullable
   @Override
   public CachedLoginProcessor resolveCachedLoginProcessor() {
      return this.cachedLoginProcessor;
   }

   @Override
   public SpawnLookup loadSpawnLookup() {
      return this.spawnLookup;
   }

   public static PacketLink processPacketLink(String instance, InetAddress target, @Nullable InetAddress input) {
      PacketLink output = null;
      if (target != null) {
         output = (PacketLink)cache.getIfPresent(instance + target.getHostAddress());
      }

      if (output == null && input != null) {
         output = (PacketLink)cache.getIfPresent(instance + input.getHostAddress());
      }

      if (output == null) {
         output = (PacketLink)cache.getIfPresent(instance);
      }

      return output;
   }

   public PacketLink(
      User target, SpawnLookup input, String output, @Nullable UUID context, boolean data, @Nullable Runnable value, Channel result, @Nullable LenientLoginFlow request
   ) {
      this.user = target;
      this.spawnLookup = input;
      this.name = output;
      this.uniqueId = context;
      this.enabled = data;
      this.runnable = value;
      this.channel = result;
      this.lenientLoginFlow = request;
   }

   @Override
   public String loadMessage() {
      return this.name;
   }

   @Override
   public void sendMessage(String target, String input) {
      this.cachedLoginProcessor = new CachedLoginProcessor(target, input);
   }

   @Override
   public void updateTask() {
      this.activeEnabled = true;
   }
}
