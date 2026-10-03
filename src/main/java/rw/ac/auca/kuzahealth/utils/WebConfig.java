package rw.ac.auca.kuzahealth.utils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
        private final ApiLoggingInterceptor apiLoggingInterceptor;
        private final String[] allowedOrigins;

        public WebConfig(ApiLoggingInterceptor apiLoggingInterceptor,
                @Value("${app.cors.allowed-origins}") String[] allowedOrigins) {
            this.apiLoggingInterceptor = apiLoggingInterceptor;
            this.allowedOrigins = allowedOrigins;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(apiLoggingInterceptor);
        }


}
