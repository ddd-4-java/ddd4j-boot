package io.ddd4j.boot.akka.actor;


import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.Props;

/**
 * Akka 扩展：用于创建注册在 {@link io.ddd4j.core.context.Contexts} 中的 Actor Bean（纯 Java，无 Spring 依赖）。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public class SpringExtension extends AbstractExtensionId<SpringExtension.SpringExt> {

    /**
     * Akka 扩展单例提供者，供 {@code Props.create} 等场景复用。
     */
    public static final SpringExtension SPRING_EXTENSION_PROVIDER = new SpringExtension();

    /**
     * 显式无参构造器（Akka {@link AbstractExtensionId} 要求扩展 Id 可实例化）。
     */
    public SpringExtension() {
    }

    /**
     * 创建 Akka 扩展实例（每次 ActorSystem 注册时调用一次）。
     *
     * @param system 扩展宿主 ActorSystem
     * @return 新建的 {@link SpringExt} 扩展实例
     */
    @Override
    public SpringExt createExtension(ExtendedActorSystem system) {
        return new SpringExt();
    }

    /**
     * Akka 扩展实例：Actor 实例由 {@link SpringActorProducer} 通过
     * {@link io.ddd4j.core.context.Contexts} 按 bean 名查找。
     */
    public static class SpringExt implements Extension {

        /**
         * 显式无参构造器，供 Akka 扩展机制实例化。
         */
        public SpringExt() {
        }

        /**
         * 创建 Actor Props。
         *
         * @param actorBeanName Actor Bean 名称（对应注册到 Contexts 中的 bean 名）
         * @return Props 对象
         */
        public Props props(String actorBeanName) {
            return Props.create(SpringActorProducer.class, new Object[]{actorBeanName});
        }
    }

}
