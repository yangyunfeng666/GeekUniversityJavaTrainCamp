package com.example.demo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.example.demo")
@ConditionalOnProperty(prefix = "testModel.start",name = "enabled",havingValue = "true",matchIfMissing = true)
public class TestAutoConfiguration {



  @Bean(name = "student100")
  Student get() {
    Student student = new Student();
    return student;
  }







}
