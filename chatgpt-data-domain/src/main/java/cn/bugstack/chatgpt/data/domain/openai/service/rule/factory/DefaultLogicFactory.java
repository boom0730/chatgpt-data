package cn.bugstack.chatgpt.data.domain.openai.service.rule.factory;

import cn.bugstack.chatgpt.data.domain.openai.annotation.LogicStrategy;
import cn.bugstack.chatgpt.data.domain.openai.service.rule.ILogicFilter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 规则工厂
 * @create 2023-09-16 17:42
 */
@Service
public class DefaultLogicFactory {
    //初始化map来存放两个过滤器的bean
    public Map<String, ILogicFilter> logicFilterMap = new ConcurrentHashMap<>();

    /**
     * 这里的代码一定要好生理解
     * 通过这段代码，DefaultLogicFactory 类能够根据逻辑过滤器的注解动态地将其存储在一个 ConcurrentHashMap 中。
     * 这种设计模式使得逻辑过滤器的注册和管理更加灵活，可以根据需要轻松扩展和修改逻辑过滤器。
     *
     * 工厂模式是一种创建对象的设计模式，它定义了一个接口用于创建对象，但由子类决定实例化哪一个类。工厂模式使得创建对象的过程延迟到子类。
     * 在你的代码中，DefaultLogicFactory 类就是一个工厂，它负责创建和管理 ILogicFilter 实例的集合。
     *
     * 策略模式是一种行为设计模式
     * LogicStrategy 注解用于定义每个 ILogicFilter 实现类对应的逻辑模式（策略），这样在运行时可以根据需要选择和执行不同的策略。
     *
     * 首先自定义注解LogicStrategy里面 就配置的是logic的几个种类LogicModel的enum枚举包含了code info 这中形式
     * 然后在初始化工厂的时候 通过传入进来的 List<ILogicFilter> logicFilters
     * 将list中的logicfilter都拿出来 然后通过AnnotationUtils.findAnnotation找注解的形式 找到与之对应的filter放到map中
     */
    public DefaultLogicFactory(List<ILogicFilter> logicFilters) {
        logicFilters.forEach(logic -> {
            LogicStrategy strategy = AnnotationUtils.findAnnotation(logic.getClass(), LogicStrategy.class);
            if (null != strategy) {
                logicFilterMap.put(strategy.logicMode().getCode(), logic);
            }
        });
    }

    public Map<String, ILogicFilter> openLogicFilter() {
        return logicFilterMap;
    }


    /**
     * 规则逻辑枚举
     */
    public enum LogicModel {

        ACCESS_LIMIT("ACCESS_LIMIT", "访问次数过滤"),
        SENSITIVE_WORD("SENSITIVE_WORD", "敏感词过滤"),
        ;

        private String code;
        private String info;

        LogicModel(String code, String info) {
            this.code = code;
            this.info = info;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }

}
