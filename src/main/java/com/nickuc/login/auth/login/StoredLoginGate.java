package com.nickuc.login.auth.login;

import com.nickuc.login.model.DirectProxyState;


public class StoredLoginGate {
   public final String name;
   public final String activeName;
   public final String pendingName;
   public final String currentName;
   public final String primaryName;
   public final int count;
   public final String mainName;
   public final DirectProxyState directProxyState;
   public final boolean enabled;
   public final String localName;

   public StoredLoginGate(
      String target, String input, String output, String context, String data, String value, String result, int request, DirectProxyState response, boolean source
   ) {
      this.pendingName = target;
      this.currentName = input;
      this.primaryName = output;
      this.name = context;
      this.activeName = data;
      this.mainName = value;
      this.localName = result;
      this.count = request;
      this.directProxyState = response;
      this.enabled = source;
   }
}
