package com.nickuc.login.platform.sender;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IncomingSenderAdapter<K> {
   default boolean isState(K target, boolean input) {
      Object output = this.handleObject((K)target, input);
      return output instanceof Boolean ? (Boolean)output : Boolean.parseBoolean(output.toString());
   }

   default double loadRatio(K target, double input) {
      Object context = this.handleObject((K)target, input);
      return context instanceof Double ? (Double)context : Double.parseDouble(context.toString());
   }

   @Nonnull
   default <T> T buildObject(K target) {
      Object input = this.buildObjectForObject((K)target);
      if (input == null) {
         throw new IllegalStateException("Key " + target + " not found!");
      }

      try {
         return (T)input;
      } catch (Throwable context) {
         throw new RuntimeException("Class cast exception, key " + target + (input != null ? ", value class " + input.getClass().getCanonicalName() : ""), context);
      }
   }

   @Nonnull
   default List<Integer> buildCollection(K target, List<Integer> input) {
      return this.processObject((K)target, input);
   }

   default short processCode(K target, short input) {
      Object output = this.handleObject((K)target, input);
      return output instanceof Short ? (Short)output : Short.parseShort(output.toString());
   }

   @Nonnull
   default List<?> computeCollection(K target, List<?> input) {
      return this.processObject((K)target, input);
   }

   @Nullable
   default List<?> handleCollection(K target) {
      return this.handleObject((K)target);
   }

   @Nullable
   default List<String> resolveCollection(K target) {
      return this.handleObject((K)target);
   }

   default short handleCode(K target) {
      Object input = this.buildObjectForObject((K)target);
      if (input instanceof Short) {
         return (Short)input;
      } else {
         return input != null ? Short.parseShort(input.toString()) : 0;
      }
   }

   default boolean checkState(K target) {
      Object input = this.buildObjectForObject((K)target);
      return input instanceof Boolean ? (Boolean)input : input != null && Boolean.parseBoolean(input.toString());
   }

   boolean isState(K target);

   default int resolveCount(K target) {
      Object input = this.buildObjectForObject((K)target);
      if (input instanceof Integer) {
         return (Integer)input;
      } else {
         return input != null ? Integer.parseInt(input.toString()) : 0;
      }
   }

   @Nonnull
   default Object handleObject(K target, Object input) {
      if (input == null) {
         throw new IllegalArgumentException("Default value cannot be null!");
      }

      Object output = this.buildObjectForObject((K)target);
      return output != null ? output : input;
   }

   default long loadTime(K target) {
      Object input = this.buildObjectForObject((K)target);
      if (input instanceof Long) {
         return (Long)input;
      } else {
         return input != null ? Long.parseLong(input.toString()) : 0L;
      }
   }

   default long createTime(K target, long input) {
      Object context = this.handleObject((K)target, input);
      return context instanceof Long ? (Long)context : Long.parseLong(context.toString());
   }

   @Nullable
   default <T> T handleObject(K target) {
      Object input = this.buildObjectForObject((K)target);

      try {
         return (T)input;
      } catch (Throwable context) {
         throw new RuntimeException("Class cast exception, key " + target + (input != null ? ", value class " + input.getClass().getCanonicalName() : ""), context);
      }
   }

   @Nullable
   Object createObject(K target);

   @Nonnull
   default <T> T processObject(K target, T input) {
      Object output = this.handleObject((K)target, input);

      try {
         return (T)output;
      } catch (Throwable data) {
         throw new RuntimeException("Class cast exception, key " + target + (output != null ? ", value class " + output.getClass().getCanonicalName() : ""), data);
      }
   }

   default double computeRatio(K target) {
      Object input = this.buildObjectForObject((K)target);
      if (input instanceof Double) {
         return (Double)input;
      } else {
         return input != null ? Double.parseDouble(input.toString()) : 0.0;
      }
   }

   default int computeCount(K target, int input) {
      Object output = this.handleObject((K)target, input);
      return output instanceof Integer ? (Integer)output : Integer.parseInt(output.toString());
   }

   @Nonnull
   default String loadMessage(K target, String input) {
      Object output = this.handleObject((K)target, input);
      return output.toString();
   }

   @Nonnull
   default List<String> buildCollectionForCollection(K target, List<String> input) {
      return this.processObject((K)target, input);
   }

   @Nullable
   default Object buildObjectForObject(K target) {
      if (target == null) {
         throw new IllegalArgumentException("Key cannot be null!");
      } else {
         return this.isState((K)target) ? this.createObject((K)target) : null;
      }
   }

   @Nullable
   default List<Integer> buildCollection(K target) {
      return this.handleObject((K)target);
   }

   default String createMessage(K target) {
      Object input = this.buildObjectForObject((K)target);
      return input != null ? input.toString() : null;
   }
}
