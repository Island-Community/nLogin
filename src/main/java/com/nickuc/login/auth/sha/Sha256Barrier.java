package com.nickuc.login.auth.sha;

import java.nio.file.Path;
import java.util.List;
import javax.annotation.Nullable;


public class Sha256Barrier {
   @Nullable
   private final Path path;
   private final String name;
   private final List<String> entries;
   private final String activeName;

   public String getName() {
      return this.activeName;
   }

   public Sha256Barrier(String target, String input, List<String> output, @Nullable Path context) {
      this.activeName = target;
      this.name = input;
      this.entries = output;
      this.path = context;
   }

   @Nullable
   public Path fetchPath() {
      return this.path;
   }

   public List<String> retrieveCollection() {
      return this.entries;
   }

   @Override
   public String toString() {
      return "SharedServer.PluginInfo(name="
         + this.getName()
         + ", version="
         + this.getVersion()
         + ", authors="
         + this.retrieveCollection()
         + ", path="
         + this.fetchPath()
         + ")";
   }

   public String getVersion() {
      return this.name;
   }
}
