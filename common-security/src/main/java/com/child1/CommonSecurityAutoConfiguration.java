package com.child1;

import com.child1.commonsecurity.JwtService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


@AutoConfiguration
@ComponentScan(basePackages = "com.child1.commonsecurity")
public class CommonSecurityAutoConfiguration {
    // Spring will automatically discover and register all @Service, @Component beans
    // in the com.child1.commonsecurity package
}