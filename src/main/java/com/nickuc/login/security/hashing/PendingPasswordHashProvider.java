package com.nickuc.login.security.hashing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class PendingPasswordHashProvider {
   public final Map<String, Object> sessions = new LinkedHashMap<>();
   private final PendingPasswordHashProvider pendingPasswordHashProvider;

   public List<Boolean> handleCollection(String target) {
      List input = this.createCollection(target);
      ArrayList output = new ArrayList();

      for (Object data : input) {
         if (data instanceof Boolean) {
            output.add((Boolean)data);
         }
      }

      return output;
   }

   public List<Double> buildCollection(String target) {
      List input = this.createCollection(target);
      ArrayList output = new ArrayList();

      for (Object data : input) {
         if (data instanceof Number) {
            output.add(((Number)data).doubleValue());
         }
      }

      return output;
   }

   public List<String> processCollection(String target) {
      List input = this.createCollection(target);
      ArrayList output = new ArrayList();

      for (Object data : input) {
         if (data instanceof String) {
            output.add((String)data);
         }
      }

      return output;
   }

   public boolean checkState(String target) {
      return this.resolveObject(target, null) != null;
   }

   public void updateMessage(String target, Object input) {
      if (input instanceof Map) {
         input = new PendingPasswordHashProvider(
            (Map<?, ?>)input, this.pendingPasswordHashProvider == null ? null : this.pendingPasswordHashProvider.buildPendingPasswordHashProvider(target)
         );
      }

      PendingPasswordHashProvider output = this.processPendingPasswordHashProvider(target);
      if (output == this) {
         if (input == null) {
            this.sessions.remove(target);
         } else {
            this.sessions.put(target, input);
         }
      } else {
         output.updateMessage(this.loadMessage(target), input);
      }
   }

   public List<Short> buildCollectionForCollection(String target) {
      List input = this.createCollection(target);
      ArrayList output = new ArrayList();

      for (Object data : input) {
         if (data instanceof Number) {
            output.add(((Number)data).shortValue());
         }
      }

      return output;
   }

   public double createRatio(String target) {
      Object input = this.computeObject(target);
      return this.loadRatio(target, input instanceof Number ? ((Number)input).doubleValue() : 0.0);
   }

   public float computeFactor(String target, float input) {
      Object output = this.resolveObject(target, input);
      return output instanceof Number ? ((Number)output).floatValue() : input;
   }

   public boolean isState(String target) {
      Object input = this.computeObject(target);
      return this.hasState(target, input instanceof Boolean ? (Boolean)input : false);
   }

   public Object handleObject(String target) {
      return this.resolveObject(target, this.computeObject(target));
   }

   public PendingPasswordHashProvider(Map<?, ?> target, PendingPasswordHashProvider input) {
      this.pendingPasswordHashProvider = input;

      for (Entry context : target.entrySet()) {
         String data = context.getKey() == null ? "null" : context.getKey().toString();
         if (context.getValue() instanceof Map) {
            this.sessions
               .put(data, new PendingPasswordHashProvider((Map<?, ?>)context.getValue(), input == null ? null : input.buildPendingPasswordHashProvider(data)));
         } else {
            this.sessions.put(data, context.getValue());
         }
      }
   }

   public <T> T resolveObject(String target, T input) {
      PendingPasswordHashProvider output = this.processPendingPasswordHashProvider(target);
      Object context;
      if (output == this) {
         context = this.sessions.get(target);
      } else {
         context = output.resolveObject(this.loadMessage(target), input);
      }

      if (context == null && input instanceof PendingPasswordHashProvider) {
         this.sessions.put(target, input);
      }

      return (T)(context != null ? context : input);
   }

   public List<Float> computeCollection(String target) {
      List input = this.createCollection(target);
      ArrayList output = new ArrayList();

      for (Object data : input) {
         if (data instanceof Number) {
            output.add(((Number)data).floatValue());
         }
      }

      return output;
   }

   public double loadRatio(String target, double input) {
      Object context = this.resolveObject(target, input);
      return context instanceof Number ? ((Number)context).doubleValue() : input;
   }

   public byte resolveMask(String target, byte input) {
      Object output = this.resolveObject(target, input);
      return output instanceof Number ? ((Number)output).byteValue() : input;
   }

   private String loadMessage(String target) {
      int input = target.indexOf(46);
      return input == -1 ? target : target.substring(input + 1);
   }

   public List<?> createCollection(String target) {
      Object input = this.computeObject(target);
      return this.resolveCollection(target, input instanceof List ? (List)input : Collections.EMPTY_LIST);
   }

   public List<Integer> loadCollection(String target) {
      List input = this.createCollection(target);
      ArrayList output = new ArrayList();

      for (Object data : input) {
         if (data instanceof Number) {
            output.add(((Number)data).intValue());
         }
      }

      return output;
   }

   public char processMarker(String target, char input) {
      Object output = this.resolveObject(target, input);
      return output instanceof Character ? (Character)output : input;
   }

   public int resolveCount(String target) {
      Object input = this.computeObject(target);
      return this.resolveCount(target, input instanceof Number ? ((Number)input).intValue() : 0);
   }

   public short createCode(String target, short input) {
      Object output = this.resolveObject(target, input);
      return output instanceof Number ? ((Number)output).shortValue() : input;
   }

   public float handleFactor(String target) {
      Object input = this.computeObject(target);
      return this.computeFactor(target, input instanceof Number ? ((Number)input).floatValue() : 0.0F);
   }

   public PendingPasswordHashProvider(PendingPasswordHashProvider target) {
      this(new LinkedHashMap(), target);
   }

   public String loadMessage(String target, String input) {
      Object output = this.resolveObject(target, input);
      return output instanceof String ? (String)output : input;
   }

   public boolean hasState(String target, boolean input) {
      Object output = this.resolveObject(target, input);
      return output instanceof Boolean ? (Boolean)output : input;
   }

   public PendingPasswordHashProvider() {
      this(null);
   }

   public String loadMessageForMessage(String target) {
      Object input = this.computeObject(target);
      return this.loadMessage(target, input instanceof String ? (String)input : "");
   }

   public long processTime(String target, long input) {
      Object context = this.resolveObject(target, input);
      return context instanceof Number ? ((Number)context).longValue() : input;
   }

   public short processCode(String target) {
      Object input = this.computeObject(target);
      return this.createCode(target, input instanceof Number ? ((Number)input).shortValue() : 0);
   }

   public PendingPasswordHashProvider buildPendingPasswordHashProvider(String target) {
      Object input = this.computeObject(target);
      return this.resolveObject(
         target,
         (PendingPasswordHashProvider)(input instanceof PendingPasswordHashProvider
            ? input
            : new PendingPasswordHashProvider(
               this.pendingPasswordHashProvider == null ? null : this.pendingPasswordHashProvider.buildPendingPasswordHashProvider(target)
            ))
      );
   }

   public Collection<String> resolveCollection() {
      return new LinkedHashSet<>(this.sessions.keySet());
   }

   public long buildTime(String target) {
      Object input = this.computeObject(target);
      return this.processTime(target, input instanceof Number ? ((Number)input).longValue() : 0L);
   }

   public List<Byte> handleCollectionForCollection(String target) {
      List input = this.createCollection(target);
      ArrayList output = new ArrayList();

      for (Object data : input) {
         if (data instanceof Number) {
            output.add(((Number)data).byteValue());
         }
      }

      return output;
   }

   public int resolveCount(String target, int input) {
      Object output = this.resolveObject(target, input);
      return output instanceof Number ? ((Number)output).intValue() : input;
   }

   public List<Long> createCollectionForCollection(String target) {
      List input = this.createCollection(target);
      ArrayList output = new ArrayList();

      for (Object data : input) {
         if (data instanceof Number) {
            output.add(((Number)data).longValue());
         }
      }

      return output;
   }

   public List<Character> resolveCollection(String target) {
      List input = this.createCollection(target);
      ArrayList output = new ArrayList();

      for (Object data : input) {
         if (data instanceof Character) {
            output.add((Character)data);
         }
      }

      return output;
   }

   public List<?> resolveCollection(String target, List<?> input) {
      Object output = this.resolveObject(target, input);
      return output instanceof List ? (List)output : input;
   }

   private PendingPasswordHashProvider processPendingPasswordHashProvider(String target) {
      int input = target.indexOf(46);
      if (input == -1) {
         return this;
      }

      String output = target.substring(0, input);
      Object context = this.sessions.get(output);
      if (context == null) {
         context = new PendingPasswordHashProvider(
            this.pendingPasswordHashProvider == null ? null : this.pendingPasswordHashProvider.buildPendingPasswordHashProvider(output)
         );
         this.sessions.put(output, context);
      }

      return (PendingPasswordHashProvider)context;
   }

   public byte resolveMask(String target) {
      Object input = this.computeObject(target);
      return this.resolveMask(target, input instanceof Number ? ((Number)input).byteValue() : 0);
   }

   public Object computeObject(String target) {
      return this.pendingPasswordHashProvider == null ? null : this.pendingPasswordHashProvider.handleObject(target);
   }

   public char buildMarker(String target) {
      Object input = this.computeObject(target);
      return this.processMarker(target, input instanceof Character ? (Character)input : '\u0000');
   }
}
