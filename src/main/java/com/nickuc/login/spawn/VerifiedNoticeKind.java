package com.nickuc.login.spawn;

import com.nickuc.login.auth.login.ClosedLoginBarrier;
import com.nickuc.login.platform.sender.TightSenderAdapter;
import java.util.List;
import javax.annotation.Nullable;


public enum VerifiedNoticeKind implements TightSenderAdapter {
   VERIFIED_NOTICE_KIND("at.favre.lib", "bytes", "1.6.1", "Bytes", true, LoginLocator.resolveLoginLocator("bytes", "at{}favre{}lib{}bytes")),
   ACTIVE_VERIFIEDNOTICEKIND(
      "at.favre.lib",
      "bcrypt",
      "0.10.2",
      "BCrypt",
      true,
      LoginLocator.resolveLoginLocator("bcrypt", "at{}favre{}lib{}crypto{}bcrypt"),
      LoginLocator.resolveLoginLocator("bytes", "at{}favre{}lib{}bytes")
   ),
   PENDING_VERIFIEDNOTICEKIND("net.java.dev.jna", "jna", "5.17.0", "com.sun.jna.Native", true),
   CURRENT_VERIFIEDNOTICEKIND("de.mkammerer", "argon2-jvm", "2.8", "Argon2", true, LoginLocator.resolveLoginLocator("argon2", "de{}mkammerer{}argon2")),
   PRIMARY_VERIFIEDNOTICEKIND("javax.xml.bind", "jaxb-core", "2.3.0", "com.sun.xml.bind.api.impl.NameConverter"),
   MAIN_VERIFIEDNOTICEKIND("javax.xml.bind", "jaxb-impl", "2.3.0", "com.sun.xml.bind.AccessorFactory"),
   LOCAL_VERIFIEDNOTICEKIND("javax.xml.bind", "jaxb-api", "2.3.0", "javax.xml.bind.JAXB"),
   REMOTE_VERIFIEDNOTICEKIND("com.sun.mail", "jakarta.mail", "1.6.8", "javax.mail.Authenticator"),
   CACHED_VERIFIEDNOTICEKIND("com.sun.activation", "jakarta.activation", "1.2.1", "javax.activation.DataSource"),
   STORED_VERIFIEDNOTICEKIND("org.apache.commons", "commons-email", "1.6.0", "org.apache.commons.mail.SimpleEmail"),
   VERIFIED_VERIFIEDNOTICEKIND(
      "net.dv8tion",
      "JDA",
      "6.2.1-min",
      "api.JDA",
      LoginLocator.resolveLoginLocator("jda", "net{}dv8tion{}jda"),
      LoginLocator.resolveLoginLocator("jda.lib.okhttp3", "ok_http3".replace("_", "")),
      LoginLocator.resolveLoginLocator("jda.lib.fasterxml", "com{}fasterxml"),
      LoginLocator.resolveLoginLocator("jda.lib.iwebpp", "com{}iwebpp"),
      LoginLocator.resolveLoginLocator("jda.lib.neovisionaries", "com{}neovisionaries"),
      LoginLocator.resolveLoginLocator("jda.lib.gnu.trove", "gnu{}trove"),
      LoginLocator.resolveLoginLocator("jda.lib.kotlin", "kotlin"),
      LoginLocator.resolveLoginLocator("jda.lib.okio", "okio"),
      LoginLocator.resolveLoginLocator("jda.lib.apache.commons", "org{}apache{}commons"),
      LoginLocator.resolveLoginLocator("jda.lib.jetbrains.annotations", "org{}jetbrains{}annotations")
   ),
   AUTHENTICATED_VERIFIEDNOTICEKIND(
      "com.github.retrooper",
      "packetevents-spigot",
      "2.13.0",
      "PacketEvents",
      LoginLocator.resolveLoginLocator("packetevents.api", "com{}github{}retrooper{}packetevents"),
      LoginLocator.resolveLoginLocator("packetevents.impl", "io{}github{}retrooper{}packetevents")
   ),
   SHARED_VERIFIEDNOTICEKIND(
      "com.github.retrooper",
      "packetevents-velocity",
      "2.13.0",
      "PacketEvents",
      LoginLocator.resolveLoginLocator("packetevents.api", "com{}github{}retrooper{}packetevents"),
      LoginLocator.resolveLoginLocator("packetevents.impl", "io{}github{}retrooper{}packetevents")
   ),
   PRIVATE_VERIFIEDNOTICEKIND(
      "com.github.retrooper",
      "packetevents-bungeecord",
      "2.13.0",
      "PacketEvents",
      LoginLocator.resolveLoginLocator("packetevents.api", "com{}github{}retrooper{}packetevents"),
      LoginLocator.resolveLoginLocator("packetevents.impl", "io{}github{}retrooper{}packetevents")
   ),
   INTERNAL_VERIFIEDNOTICEKIND("net.kyori", "adventure-api", "4.25.0", "net.kyori.adventure.text.serializer.ComponentSerializer"),
   UPSTREAM_VERIFIEDNOTICEKIND(
      "net.kyori", "adventure-text-serializer-legacy", "4.25.0", "net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer"
   );

   private final ClosedLoginBarrier closedLoginBarrier;

   @Override
   public boolean fetchState() {
      return this.closedLoginBarrier.fetchState();
   }

   @Nullable
   @Override
   public String getMessage() {
      return this.closedLoginBarrier.getMessage();
   }

   public ClosedLoginBarrier findClosedLoginBarrier() {
      return this.closedLoginBarrier;
   }

   @Override
   public List<LoginLocator> loadCollection() {
      return this.closedLoginBarrier.loadCollection();
   }

   @Override
   public String getVersion() {
      return this.closedLoginBarrier.getVersion();
   }

   @Override
   public String fetchMessage() {
      return this.closedLoginBarrier.fetchMessage();
   }

   VerifiedNoticeKind(String output, String context, String data, String value, boolean result, LoginLocator... request) {
      this.closedLoginBarrier = new ClosedLoginBarrier(output, context, data, value, true, result, request);
   }

   @Override
   public String retrieveMessage() {
      return this.closedLoginBarrier.retrieveMessage();
   }

   VerifiedNoticeKind(ClosedLoginBarrier output) {
      this.closedLoginBarrier = output;
   }

   VerifiedNoticeKind(String output, String context, String data, String value, LoginLocator... result) {
      this(output, context, data, value, false, result);
   }

   @Override
   public boolean getState() {
      return this.closedLoginBarrier.getState();
   }
}
