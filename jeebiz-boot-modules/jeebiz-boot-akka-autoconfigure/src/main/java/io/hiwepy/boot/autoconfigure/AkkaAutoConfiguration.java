package io.hiwepy.boot.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AkkaAutoConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public ActorSystem actorSystem() {
        ActorSystem system = ActorSystem.create("kylin");
        ((SpringExtension.SpringExt)SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)).initialize(this.applicationContext);
        return system;
    }

    public AkkaConfiguration() {
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof AkkaConfiguration)) {
            return false;
        } else {
            AkkaConfiguration other = (AkkaConfiguration)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$applicationContext = this.getApplicationContext();
                Object other$applicationContext = other.getApplicationContext();
                if (this$applicationContext == null) {
                    if (other$applicationContext != null) {
                        return false;
                    }
                } else if (!this$applicationContext.equals(other$applicationContext)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof AkkaConfiguration;
    }

    public int hashCode() {
        int PRIME = true;
        int result = 1;
        Object $applicationContext = this.getApplicationContext();
        result = result * 59 + ($applicationContext == null ? 43 : $applicationContext.hashCode());
        return result;
    }

    public String toString() {
        return "AkkaConfiguration(applicationContext=" + this.getApplicationContext() + ")";
    }

}
