package com.ijianjian;

import java.net.MalformedURLException;
import java.util.List;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.google.common.collect.Lists;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableCaching
public class Main {
public static void main(String[] args) {
	new SpringApplicationBuilder(Main.class).run(args);
}

@Bean
public BCryptPasswordEncoder bCryptPasswordEncoder() {
	return new BCryptPasswordEncoder();
}

@Bean
public static PropertySourcesPlaceholderConfigurer properties() {
	PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
	YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
	List<Resource> rs = Lists.newArrayList();
	try {
		FileUrlResource config = new FileUrlResource("config.yml");
		System.out.println("configFile___" + config.exists());
		if (config.exists()) {
			rs.add(config);// File引入
		} else {
			rs.add(new ClassPathResource("config.yml"));// class引入
		}
		FileUrlResource configChannel = new FileUrlResource("config_hw.yml");
		System.out.println("configFile___" + config.exists());
		if (config.exists()) {
			rs.add(configChannel);// File引入
		} else {
			rs.add(new ClassPathResource("config_hw.yml"));// class引入
		}

		yaml.setResources(rs.toArray(new Resource[rs.size()]));
	} catch (MalformedURLException e) {
		e.printStackTrace();
	}
	configurer.setProperties(yaml.getObject());
	return configurer;
}
}
