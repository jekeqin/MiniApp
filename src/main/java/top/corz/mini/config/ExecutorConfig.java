package top.corz.mini.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class ExecutorConfig{

	/**
	 * 线程池配置
	 * 使用方式：
	 * 1、
	 * 		@Autowrite
	 * 		private Executor executr;
	 * 
	 * 2、	@Resource(name = "async")
	 * 		private ThreadPoolTaskExecutor executor;
	 * 
	 * 3、
	 * 		@Async("async")
	 * 
	 * @return
	 */
	@Primary
	@Bean("async")
	public Executor asyncTaskExecutor() {
		int core = Math.max(Runtime.getRuntime().availableProcessors(), 4);
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		// 核心线程数
		executor.setCorePoolSize(core);
		// 最大线程数
		executor.setMaxPoolSize(core*2);
		// 最大缓存队列
		executor.setQueueCapacity(Integer.MAX_VALUE);
		// 线程名称前缀
		executor.setThreadNamePrefix("Pool-");
		// shutdown 时，等待当前线程执行完
		executor.setWaitForTasksToCompleteOnShutdown(true);
		// 非核心线程空闲回收时间
		executor.setKeepAliveSeconds(30);
		
		// 队列满时拒绝策略
		// AbortPolicy				队列满时，丢弃新任务，并抛出异常 RejectedExecutionException
		// DiscardPolicy			队列满时，丢弃新任务，不抛出异常
		// DiscardOldestPolicy		队列满时，丢弃队头任务，再重新参数入队
		// CallerRunsPolicy			队列满时，由主线程自己去执行任务
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		
		executor.initialize();

		return executor;
	}
	
}
