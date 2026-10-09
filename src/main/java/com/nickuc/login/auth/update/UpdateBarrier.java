package com.nickuc.login.auth.update;

import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.listener.SharedListenerContract;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.spawn.SpawnState;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import javax.annotation.Nullable;

public class UpdateBarrier {
   private static KeyPair createKeyPair(byte[] instance, byte[] target) {
      KeyFactory input = KeyFactory.getInstance("RSA");
      X509EncodedKeySpec output = new X509EncodedKeySpec(instance);
      PublicKey context = input.generatePublic(output);
      PKCS8EncodedKeySpec data = new PKCS8EncodedKeySpec(target);
      PrivateKey value = input.generatePrivate(data);
      return new KeyPair(context, value);
   }

   @Nullable
   public static InternalLoginHandler computeInternalLoginHandler(SharedListenerContract instance) {
      PrivateLoginHandler target = instance.getPrivateLoginHandler();

      try {
         Connection input = target.retrieveConnection();
         KeyPair output = null;

         try {
            PreparedStatement context = input.prepareStatement(
               String.format(
                  "SELECT `%s` FROM `%s` WHERE `" + SpawnState.SECONDARY_SPAWNSTATE.a(new Object[0]) + "` = ? LIMIT 1",
                  SpawnState.DIRECT_SPAWNSTATE.a(new Object[0]),
                  SpawnState.INCOMING_SPAWNSTATE.a(new Object[0])
               )
            );

            try {
               context.setString(1, "key_pair");
               ResultSet data = context.executeQuery();

               try {
                  if (data.next()) {
                     DataInputStream value = new DataInputStream(new ByteArrayInputStream(data.getBytes(SpawnState.DIRECT_SPAWNSTATE.a(new Object[0]))));
                     if (value.available() > 0) {
                        int result = Math.max(value.readInt(), 0);
                        if (result > 4194304) {
                           throw new IllegalArgumentException("Public Key too large! got " + result);
                        }

                        byte[] request = new byte[result];
                        value.readFully(request);
                        int response = Math.max(value.readInt(), 0);
                        if (response > 4194304) {
                           throw new IllegalArgumentException("Private Key too large! got " + result);
                        }

                        byte[] source = new byte[response];
                        value.readFully(source);
                        output = createKeyPair(request, source);
                     }
                  }
               } finally {
                  if (Collections.singletonList(data).get(0) != null) {
                     data.close();
                  }
               }
            } catch (Throwable action) {
               if (context != null) {
                  try {
                     context.close();
                  } catch (Throwable position) {
                     action.addSuppressed(position);
                  }
               }

               throw action;
            }

            if (context != null) {
               context.close();
            }
         } catch (Exception task) {
            PasswordHashContainer.handleMessage("Unable to load key pair", task);
            return null;
         }

         if (output == null) {
            try {
               PreparedStatement activeContext = input.prepareStatement(
                  String.format(
                     "UPDATE `%s` SET `%s` = ? WHERE `%s` = ?",
                     SpawnState.INCOMING_SPAWNSTATE.a(new Object[0]),
                     SpawnState.DIRECT_SPAWNSTATE.a(new Object[0]),
                     SpawnState.SECONDARY_SPAWNSTATE.a(new Object[0])
                  )
               );

               label386: {
                  Object item;
                  try {
                     label374: {
                        ByteArrayOutputStream activeData = new ByteArrayOutputStream();
                        DataOutputStream activeValue = new DataOutputStream(activeData);
                        LiveLoginCheckpoint activeResult = new LiveLoginCheckpoint();
                        PasswordHashContainer.processMessage(
                           CachedSettingsGateway.loadState()
                              ? "Gerando par de chaves RSA... (isso pode demorar um pouco)"
                              : "Generating RSA keypair... (this may take a while)"
                        );

                        try {
                           KeyPairGenerator pendingTarget = KeyPairGenerator.getInstance("RSA");
                           pendingTarget.initialize(4096);
                           output = pendingTarget.generateKeyPair();
                        } catch (Exception spawn) {
                           throw new RuntimeException("Unable to create server keypair", spawn);
                        }

                        byte[] pendingInput = output.getPublic().getEncoded();
                        activeValue.writeInt(pendingInput.length);
                        activeValue.write(pendingInput);
                        byte[] pendingOutput = output.getPrivate().getEncoded();
                        activeValue.writeInt(pendingOutput.length);
                        activeValue.write(pendingOutput);
                        byte[] pendingContext = activeData.toByteArray();
                        activeContext.setBytes(1, pendingContext);
                        activeContext.setString(2, "key_pair");
                        int entry = activeContext.executeUpdate();
                        if (entry == 0) {
                           try {
                              PreparedStatement record = input.prepareStatement(
                                 String.format(
                                    "INSERT INTO `%s` (`%s`, `%s`) VALUES (?, ?)",
                                    SpawnState.INCOMING_SPAWNSTATE.a(new Object[0]),
                                    SpawnState.SECONDARY_SPAWNSTATE.a(new Object[0]),
                                    SpawnState.DIRECT_SPAWNSTATE.a(new Object[0])
                                 )
                              );

                              try {
                                 record.setString(1, "key_pair");
                                 record.setBytes(2, pendingContext);
                                 record.execute();
                              } catch (Throwable state) {
                                 if (record != null) {
                                    try {
                                       record.close();
                                    } catch (Throwable location) {
                                       state.addSuppressed(location);
                                    }
                                 }

                                 throw state;
                              }

                              if (record != null) {
                                 record.close();
                              }
                           } catch (Exception job) {
                              PasswordHashContainer.handleMessage("Unable to generate key pair (2)", job);
                              item = null;
                              break label374;
                           }
                        }

                        PasswordHashContainer.processMessage(
                           CachedSettingsGateway.loadState()
                              ? "§aO par de chaves RSA foi gerado com sucesso. Demorou " + activeResult.fetchMessage() + "s"
                              : "§aThe RSA key pair was successfully generated. Took " + activeResult.fetchMessage() + "s"
                        );
                        break label386;
                     }
                  } catch (Throwable future) {
                     if (activeContext != null) {
                        try {
                           activeContext.close();
                        } catch (Throwable backend) {
                           future.addSuppressed(backend);
                        }
                     }

                     throw future;
                  }

                  if (activeContext != null) {
                     activeContext.close();
                  }

                  return (InternalLoginHandler)item;
               }

               if (activeContext != null) {
                  activeContext.close();
               }
            } catch (Exception activeInput) {
               PasswordHashContainer.handleMessage("Unable to generate key pair (1)", activeInput);
               return null;
            }
         }

         return new InternalLoginHandler(output, null);
      } finally {
         if (Collections.singletonList(target).get(0) != null) {
            target.close();
         }
      }
   }
}
