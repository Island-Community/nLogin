package com.nickuc.login.discord;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.StrictMessageKind;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.model.SecondaryPlatformCatalog;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.utils.IOUtil;
import okhttp3.Credentials;
import okhttp3.OkHttpClient.Builder;
import com.nickuc.login.platform.listener.RootListenerContract;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.DiscordVerifier;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.password.PasswordStore;
import java.awt.Color;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nullable;


public class FastDiscordNotifier implements RootListenerContract {
   private boolean enabled;
   private final PasswordStore passwordStore;
   private final DiscordVerifier discordVerifier;
   private JDA jDA;

   public PasswordStore retrievePasswordStore() {
      return this.passwordStore;
   }

   public MessageCreateBuilder processMessageCreateBuilder(String target, String input, String output, String context, String data, String value) {
      MessageCreateBuilder result = new MessageCreateBuilder();
      EmbedBuilder request = this.computeEmbedBuilder(target, input, output, context);
      result.setEmbeds(new MessageEmbed[]{request.build()});
      if (data != null && value != null) {
         result.setComponents(
            new MessageTopLevelComponent[]{ActionRow.of(Button.primary("allow", data), new ActionRowChildComponent[]{Button.secondary("deny", value)})}
         );
      } else if (data != null) {
         result.setComponents(new MessageTopLevelComponent[]{ActionRow.of(Button.primary("allow", data), new ActionRowChildComponent[0])});
      } else if (value != null) {
         result.setComponents(new MessageTopLevelComponent[]{ActionRow.of(Button.secondary("deny", value), new ActionRowChildComponent[0])});
      }

      return result;
   }

   public MessageCreateBuilder resolveMessageCreateBuilder(String target, String input, String output, String context) {
      return this.processMessageCreateBuilder(target, input, output, context, null, null);
   }

   public void executeSpawnLookup(
      SpawnLookup target, SecondaryPlatformCatalog input, String output, String context, String data, String value, String result, String request, String response
   ) {
      this.updateMessage(
         output,
         this.processMessageCreateBuilder(context, data, value, result, request, response).build(),
         inputValue -> StrictMessageKind.STRICT_MESSAGE_KIND.updateSpawnLookup(target, inputValue.getId(), input)
      );
   }

   @Override
   public boolean loadState() {
      if (this.jDA != null) {
         this.jDA.shutdown();
         if (!this.jDA.awaitShutdown(3L, TimeUnit.SECONDS)) {
            this.jDA.shutdownNow();
            this.jDA.awaitShutdown();
         }
      }

      this.jDA = null;
      this.enabled = false;
      return true;
   }

   @Override
   public void updateSpawnLookup(SpawnLookup target, VerifiedServerAdapter input, String output) {
      String context = target.fetchQuickDiscordHandler().getMessage();
      String data = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_FAST_LOUDPROXYSTATE, input, output);
      String value = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_TOP_LOUDPROXYSTATE, input, output);
      String result = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_SAFE_LOUDPROXYSTATE, input, output);
      String request = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_SECURE_LOUDPROXYSTATE, input, output);
      this.executeSpawnLookup(target, SecondaryPlatformCatalog.ACTIVE_SECONDARYPLATFORMCATALOG, context, value, data, result, request, null, null);
   }

   private EmbedBuilder computeEmbedBuilder(String target, String input, String output, String context) {
      EmbedBuilder data = new EmbedBuilder();
      data.setTitle(target);
      data.setDescription(input);
      if (!output.isEmpty()) {
         data.setThumbnail(output);
      }

      if (context.length() == 6) {
         context = "#" + context;
      }

      Color value;
      try {
         value = Color.decode(context);
      } catch (NumberFormatException request) {
         value = Color.GRAY;
      }

      data.setColor(value);
      return data;
   }

   public DiscordVerifier resolveDiscordVerifier() {
      return this.discordVerifier;
   }

   public void updateMessage(String target, MessageCreateData input, @Nullable Consumer<Message> output) {
      this.jDA.retrieveUserById(target).queue(inputValue -> {
         if (inputValue != null) {
            inputValue.openPrivateChannel().queue(inputValue -> inputValue.sendMessage(input).queue(output));
         }
      });
   }

   public JDA fetchJDA() {
      return this.jDA;
   }

   @Override
   public boolean findState() {
      return this.enabled;
   }

   @Override
   public void saveSpawnLookup(SpawnLookup target, VerifiedServerAdapter input, String output) {
      String context = target.fetchQuickDiscordHandler().getMessage();
      String data = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_READY_LOUDPROXYSTATE, input, output);
      String value = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_OPEN_LOUDPROXYSTATE, input, output);
      String result = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_LIVE_LOUDPROXYSTATE, input, output);
      String request = CachedSettingsGateway.computeMessage(LoudProxyState.PRIMARY_ACTIVE_LOUDPROXYSTATE, input, output);
      this.executeSpawnLookup(target, SecondaryPlatformCatalog.ACTIVE_SECONDARYPLATFORMCATALOG, context, value, data, result, request, null, null);
   }

   @Override
   public void processSpawnLookup(SpawnLookup target, VerifiedServerAdapter input, String output) {
      throw new UnsupportedOperationException("Not supported by Discord 2FA!");
   }

   public FastDiscordNotifier(PasswordStore target, DiscordVerifier input) {
      this.passwordStore = target;
      this.discordVerifier = input;
   }

   @Override
   public void dispatchSpawnLookup(SpawnLookup target, VerifiedServerAdapter input) {
      String output = target.fetchQuickDiscordHandler().getMessage();
      if (!StrictMessageKind.STRICT_MESSAGE_KIND.canState(target, SecondaryPlatformCatalog.ACTIVE_SECONDARYPLATFORMCATALOG)) {
         StrictMessageKind.STRICT_MESSAGE_KIND.updateSpawnLookup(target, "awaiting", SecondaryPlatformCatalog.ACTIVE_SECONDARYPLATFORMCATALOG);
         String context = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_OUTGOING_LOUDPROXYSTATE, input);
         String data = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_INCOMING_LOUDPROXYSTATE, input);
         String value = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_SECONDARY_LOUDPROXYSTATE, input);
         String result = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_DIRECT_LOUDPROXYSTATE, input);
         String request = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_LINKED_LOUDPROXYSTATE, input);
         String response = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_ROOT_LOUDPROXYSTATE, input);
         this.executeSpawnLookup(target, SecondaryPlatformCatalog.ACTIVE_SECONDARYPLATFORMCATALOG, output, data, context, value, result, request, response);
      }

      DiscordHandler.cache.put(output, input.getName());
   }

   @Override
   public void performSpawnLookup(SpawnLookup target, VerifiedServerAdapter input) {
      String output = target.fetchQuickDiscordHandler().getMessage();
      if (!StrictMessageKind.STRICT_MESSAGE_KIND.canState(target, SecondaryPlatformCatalog.PENDING_SECONDARYPLATFORMCATALOG)) {
         StrictMessageKind.STRICT_MESSAGE_KIND.updateSpawnLookup(target, "awaiting", SecondaryPlatformCatalog.PENDING_SECONDARYPLATFORMCATALOG);
         String context = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_AUTHENTICATED_LOUDPROXYSTATE, input);
         String data = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_VERIFIED_LOUDPROXYSTATE, input);
         String value = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_SHARED_LOUDPROXYSTATE, input);
         String result = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_PRIVATE_LOUDPROXYSTATE, input);
         String request = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_INTERNAL_LOUDPROXYSTATE, input);
         String response = CachedSettingsGateway.computeMessage(LoudProxyState.CURRENT_UPSTREAM_LOUDPROXYSTATE, input);
         this.executeSpawnLookup(target, SecondaryPlatformCatalog.PENDING_SECONDARYPLATFORMCATALOG, output, data, context, value, result, request, response);
      }

      DiscordHandler.cache.put(output, input.getName());
   }

   @Override
   public void updateTask() {
      PasswordHashLoader target = this.discordVerifier.fetchPasswordHashLoader();
      if (target == null) {
         throw new IllegalStateException(this + " config is not loaded!");
      }

      String input = target.a("authentication.token", target.a("Discord.bot-token", ""));
      boolean output = CachedSettingsGateway.loadState();
      if (input.isEmpty()) {
         PasswordHashContainer.performMessage(
            output
               ? "[Discord] Para usar o Discord, você deve configurar o token do bot na config.yml. O tutorial para pegar estes dados estão disponíveis na config.yml"
               : "[Discord] To use Discord, you must configure the bot token in config.yml. The tutorial to get this data is available in config.yml"
         );
      } else {
         Builder context = IOUtil.newHttpClientBuilder();
         if (target.d("authentication.proxy.enable")) {
            String data = target.b("authentication.proxy.host");
            int value = target.a("authentication.proxy.port");
            if (data != null && !data.isEmpty() && !"https://domain.tld".equals(data)) {
               System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
               context.proxy(new Proxy(Type.HTTP, new InetSocketAddress(data, value)));
               String result = target.b("authentication.proxy.username");
               String request = target.b("authentication.proxy.password");
               if (result != null && request != null) {
                  context.proxyAuthenticator((inputValue, outputValue) -> outputValue.request().newBuilder().header("Proxy-Authorization", Credentials.basic(result, request)).build());
               }
            }
         }

         try {
            this.jDA = JDABuilder.createDefault(input)
               .setAutoReconnect(true)
               .setStatus(OnlineStatus.ONLINE)
               .setHttpClientBuilder(context)
               .addEventListeners(new Object[]{new DiscordHandler(this.passwordStore, this)})
               .build()
               .awaitReady();
            this.jDA
               .updateCommands()
               .addCommands(
                  new CommandData[]{
                     Commands.slash("link", output ? "Vincula sua conta do Minecraft com o Discord." : "Link your Minecraft account with Discord.")
                        .addOption(
                           OptionType.STRING, "code", output ? "Código fornecido durante o processo de vinculação." : "Code provided during linking process."
                        )
                  }
               )
               .queue();
            PasswordHashContainer.processMessage(
               output ? "§a[Discord] Conexão com o Discord foi realizada com sucesso!" : "§a[Discord] Connection with the Discord has been successfully made!"
            );
            this.enabled = true;
            this.passwordStore.processLinkedSessionHandler(true).loadStrictCommandHandler(inputValue -> {
               if (this.jDA == null) {
                  inputValue.performTask();
               } else {
                  Presence outputValue = this.jDA.getPresence();
                  String contextValue = target.a("authentication.presence", target.a("options.presence", ""));
                  if (!contextValue.isEmpty()) {
                     outputValue.setActivity(Activity.playing(contextValue));
                  }

                  outputValue.setStatus(OnlineStatus.ONLINE);
               }
            }, 0L, 5L, TimeUnit.SECONDS);
         } catch (InterruptedException | IllegalArgumentException | IllegalStateException response) {
            this.enabled = false;
            PasswordHashContainer.handleMessage(
               output ? "[Discord] Não foi possível se autenticar no Discord :c" : "[Discord] Unable to authenticate to Discord :c", response
            );
         }
      }
   }
}
