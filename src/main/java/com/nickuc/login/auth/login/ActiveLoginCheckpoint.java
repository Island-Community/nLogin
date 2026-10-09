package com.nickuc.login.auth.login;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.representer.Representer;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.function.BiFunction;

public class ActiveLoginCheckpoint {
   public static MappingNode loadMappingNode(Yaml instance, File target) {
      FileReader output = new FileReader(target);

      Node input;
      try {
         input = instance.compose(output);
      } catch (Throwable result) {
         try {
            output.close();
         } catch (Throwable value) {
            result.addSuppressed(value);
         }

         throw result;
      }

      output.close();
      if (!(input instanceof MappingNode)) {
         throw new IllegalStateException("Unexpected root node type: " + input.getClass().getCanonicalName());
      } else {
         return (MappingNode)input;
      }
   }

   public static Yaml processYaml(boolean instance) {
      LoaderOptions target = new LoaderOptions();
      target.setProcessComments(instance);
      DumperOptions input = new DumperOptions();
      input.setProcessComments(instance);
      return new Yaml(new Constructor(target), new Representer(input), input, target);
   }

   public static void handleMappingNode(MappingNode instance, String target, BiFunction<String, Node, Node> input) {
      List output = instance.getValue();

      for (int context = 0; context < output.size(); context++) {
         NodeTuple data = (NodeTuple)output.get(context);
         Node value = data.getKeyNode();
         if (!(value instanceof ScalarNode)) {
            throw new IllegalStateException("Unexpected key node type: " + value.getClass().getCanonicalName() + " " + value);
         }

         ScalarNode result = (ScalarNode)value;
         String request = target + (target.isEmpty() ? "" : ".") + result.getValue();
         Node response = data.getValueNode();
         if (response instanceof MappingNode) {
            handleMappingNode((MappingNode)response, request, input);
         } else {
            Node source = (Node)input.apply(request, response);
            if (source != null) {
               output.set(context, new NodeTuple(value, source));
            }
         }
      }
   }
}
