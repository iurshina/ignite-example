package ignite;

import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class NodeStarter {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext configApplicationContext =
                new AnnotationConfigApplicationContext(Context.class);
        IgniteConfiguration conf = configApplicationContext.getBean(IgniteConfiguration.class);

        Ignition.start(conf);
    }
}
