package com.nickuc.login.premium;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.auth.login.ActiveLoginCheckpoint;
import com.nickuc.login.model.NoticeKind;
import com.nickuc.login.model.ProxyState;
import com.nickuc.login.model.UpstreamSpawnState;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.ScalarNode;
import com.nickuc.login.platform.listener.InternalListenerContract;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;


public class OpenPasswordHashProvider {
   private boolean enabled;
   private UpstreamSpawnState upstreamSpawnState = UpstreamSpawnState.PENDING_UPSTREAMSPAWNSTATE;
   private boolean activeEnabled;
   private NoticeKind noticeKind = NoticeKind.PENDING_NOTICEKIND;
   private boolean pendingEnabled;
   private boolean currentEnabled;
   private ProxyState proxyState;

   public void updateProxyState(ProxyState target) {
      this.proxyState = target;
   }

   public void performState(boolean target) {
      this.currentEnabled = target;
   }

   public NoticeKind retrieveNoticeKind() {
      return this.noticeKind;
   }

   public boolean fetchState() {
      return this.currentEnabled;
   }

   public void executeNoticeKind(NoticeKind target) {
      this.noticeKind = target;
   }

   public void handleState(boolean target) {
      this.enabled = target;
   }

   public boolean findState() {
      return this.enabled;
   }

   private OpenPasswordHashProvider() {
      this.enabled = true;
      this.proxyState = ProxyState.PROXY_STATE;
      this.activeEnabled = true;
      this.pendingEnabled = true;
      this.currentEnabled = true;
   }

   private void handleTable(Map<String, String> target, InternalListenerContract input, String output) {
      for (String result : input.retrieveBusyLoginProcessor().fetchNames()) {
         target.put(result, output);
      }
   }

   @Override
   public String toString() {
      return "SetupOptions(hashingAlgorithm="
         + this.loadUpstreamSpawnState()
         + ", dialogConfig="
         + this.retrieveNoticeKind()
         + ", enableUsernameAppendix="
         + this.findState()
         + ", premiumNickname="
         + this.retrieveProxyState()
         + ", enablePremiumQuestion="
         + this.resolveState()
         + ", skipPremiumRegister="
         + this.retrieveState()
         + ", skipBedrockRegister="
         + this.fetchState()
         + ")";
   }

   public boolean resolveState() {
      return this.activeEnabled;
   }

   public ProxyState retrieveProxyState() {
      return this.proxyState;
   }

   private void executeFile(File target, Map<String, String> input) {
      Yaml output = ActiveLoginCheckpoint.processYaml(true);
      MappingNode context = ActiveLoginCheckpoint.loadMappingNode(output, target);
      ActiveLoginCheckpoint.handleMappingNode(context, "", (targetValue, inputValue) -> {
         String outputValue = (String)input.get(targetValue);
         return outputValue == null ? null : new ScalarNode(inputValue.getTag(), outputValue, inputValue.getStartMark(), inputValue.getEndMark(), ScalarStyle.PLAIN);
      });
      FileWriter data = new FileWriter(target);

      try {
         output.serialize(context, data);
      } catch (Throwable response) {
         try {
            data.close();
         } catch (Throwable request) {
            response.addSuppressed(request);
         }

         throw response;
      }

      data.close();
   }

   public boolean retrieveState() {
      return this.pendingEnabled;
   }

   public void executeState(boolean target) {
      this.pendingEnabled = target;
   }

   public static OpenPasswordHashProvider loadOpenPasswordHashProvider() {
      return new OpenPasswordHashProvider();
   }

   public void handleUpstreamSpawnState(UpstreamSpawnState target) {
      this.upstreamSpawnState = target;
   }

   public void processState(boolean target) {
      this.activeEnabled = target;
   }

   public UpstreamSpawnState loadUpstreamSpawnState() {
      return this.upstreamSpawnState;
   }

   public void updatePasswordStore(PasswordStore target) {
      File input = target.a().retrieveFile();
      File output = new File(target.resolveFile() + File.separator + "premium", "config.yml");
      HashMap context = new HashMap();
      this.handleTable(context, SpawnState.ACTIVE_SECURE_SPAWNSTATE, this.upstreamSpawnState.name().toUpperCase());
      this.handleTable(context, SpawnState.PENDING_CACHED_SPAWNSTATE, this.noticeKind.name().toUpperCase());
      HashMap data = new HashMap();
      this.handleTable(data, QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION, Boolean.toString(this.enabled));
      this.handleTable(data, QuickPremiumOption.QUICK_PREMIUM_OPTION, this.proxyState.name().toUpperCase());
      this.handleTable(data, QuickPremiumOption.PENDING_QUICKPREMIUMOPTION, Boolean.toString(this.activeEnabled));
      this.handleTable(data, QuickPremiumOption.UPSTREAM_QUICKPREMIUMOPTION, Boolean.toString(this.pendingEnabled));
      this.handleTable(data, QuickPremiumOption.AUTHENTICATED_QUICKPREMIUMOPTION, Boolean.toString(this.currentEnabled));
      this.executeFile(input, context);
      if (output.exists()) {
         this.executeFile(output, data);
      }

      Pbkdf2Linker.loadLoudNoticeCatalog(target);
      target.b().fetchCollection().forEach(instance -> instance.buildCompletableFuture(CachedSettingsGateway.computeMessage(LoudProxyState.SHARED_LOUDPROXYSTATE, instance)));
      target.fetchLocalSettingsRepository().handleTask();
      target.fetchLocalSettingsRepository().processTask();
   }
}
