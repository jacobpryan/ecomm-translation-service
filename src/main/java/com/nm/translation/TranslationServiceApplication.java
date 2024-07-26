package com.nm.translation;

import com.nm.translation.service.PollingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main Class - This is where we start the Application Service and Immediately Start Running the PollingService
 */
@Slf4j
@SpringBootApplication
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = { "com.nm.translation" })
public class TranslationServiceApplication implements CommandLineRunner {
	@Autowired
	private PollingService pollingService;

	/**
	 * Main Method for ecomm-translation-service
	 */
	public static void main(String[] args) {
		try {
			ConfigurableApplicationContext ac = SpringApplication.run(TranslationServiceApplication.class, args);
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				log.info("#####: Running in main TranslationServiceApplication");
				ac.close();
				log.info("#####: Exit complete.");
			}));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is where the Application starts Polling the SQS with a newly built Connection.
	 * FixedDelayScheduler was set up because it is reliable and a good starting point for any enhancements.
	 */
	@Override
	public void run(String... arg0) {
		try {
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

			/**
			 * Scheduler controls when the Polling Service is started and restarted.
			 */
			log.info("#####: Starting Polling Scheduler");
			executor.scheduleWithFixedDelay(() -> {
				try {
					pollingService.run();
				} catch (Exception ex) {
					log.error("Application Run Exception: ", ex);
				}
			}, 0, 100, TimeUnit.MILLISECONDS);
		} catch (Exception exception) {
			log.error("EXCEPTION: ", exception);
		}
	}
}
