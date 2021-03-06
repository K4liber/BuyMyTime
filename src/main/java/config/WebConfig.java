package config;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import web.services.SessionListener;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages={"web"})
public class WebConfig extends WebMvcConfigurerAdapter{
	
	@Bean
	public MultipartResolver multipartResolver() throws IOException {
		return new CommonsMultipartResolver();
	}
	
	//THYMELEAF CONFIG
	
	@Bean
    public ITemplateResolver templateResolver() {
    	SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
    	templateResolver.setPrefix("/WEB-INF/templates/");
    	templateResolver.setSuffix(".html");
    	templateResolver.setTemplateMode("HTML5");
	return templateResolver;
    }
	
    @Bean    
    public SpringTemplateEngine templateEngine() {
    	SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    	templateEngine.setTemplateResolver(templateResolver());
    	templateEngine.addDialect(new SpringSecurityDialect());
	return templateEngine;
    }
    
    @Bean
    public ViewResolver viewResolver(){
    	ThymeleafViewResolver viewResolver = new ThymeleafViewResolver(); 
    	viewResolver.setTemplateEngine(templateEngine());
    	return viewResolver;
    }
	
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer){
		configurer.enable();
	}
	
	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}
	
}
