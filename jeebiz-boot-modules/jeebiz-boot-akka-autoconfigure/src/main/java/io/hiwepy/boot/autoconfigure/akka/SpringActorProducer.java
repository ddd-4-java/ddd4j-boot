package io.hiwepy.boot.autoconfigure.akka;


import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import org.springframework.context.ApplicationContext;

public class SpringActorProducer implements IndirectActorProducer {
    private final ApplicationContext applicationContext;
    private final String beanActorName;

    public SpringActorProducer(ApplicationContext applicationContext, String beanActorName) {
        this.applicationContext = applicationContext;
        this.beanActorName = beanActorName;
    }

    public Actor produce() {
        return (Actor)this.applicationContext.getBean(this.beanActorName);
    }

    public Class<? extends Actor> actorClass() {
        return this.applicationContext.getType(this.beanActorName);
    }
}
