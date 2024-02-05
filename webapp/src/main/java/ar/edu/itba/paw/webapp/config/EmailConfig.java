package ar.edu.itba.paw.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Properties;

@EnableWebMvc
@EnableAsync
@PropertySource("classpath:application.properties")
@Configuration
public class EmailConfig {
    @Autowired
    private Environment environment;

    @Autowired
    private MessageSource messageSource;

    // Retrieved from: https://howtodoinjava.com/spring-core/send-email-with-spring-javamailsenderimpl-example/
    @Bean
    public JavaMailSender getJavaMailSender() {
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(environment.getProperty("mailer.host"));
        mailSender.setPort(Integer.parseInt(environment.getProperty("mailer.port")));

        mailSender.setUsername(environment.getProperty("mailer.email"));
        mailSender.setPassword(environment.getProperty("mailer.password"));

        final Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }

    // Retrieved from: https://www.baeldung.com/spring-email-templates
    @Bean
    public ITemplateResolver thymeleafTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/mail/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    // Retrieved from: https://www.baeldung.com/spring-email-templates
    @Bean
    public TemplateEngine thymeleafTemplateEngine(ITemplateResolver templateResolver) {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        templateEngine.setTemplateEngineMessageSource(messageSource);
        return templateEngine;
    }
}
