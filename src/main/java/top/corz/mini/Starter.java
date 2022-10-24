package top.corz.mini;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import top.corz.mini.utils.HostUtils;

@EnableScheduling
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class})
@ComponentScan(basePackages = {"top"})
public class Starter {

	public static void main(String[] args)
	{
		String fileName = "miniapp.pid";
		
    	SpringApplication application = new SpringApplication(Starter.class);
    	application.addListeners(new ApplicationPidFileWriter(fileName));
    	ConfigurableApplicationContext cac = application.run(args);
    	Environment env = cac.getBean(Environment.class);
    	System.out.println( "Startup at: " + HostUtils.PhysicsHost() + ":" + env.getProperty("server.port"));
    	
	}
}
