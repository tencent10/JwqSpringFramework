package com.jwqspring;

import com.annotation.JwqAutowired;
import com.annotation.JwqComponent;
import com.annotation.JwqComponentScan;
import com.annotation.JwqScope;
import com.bean.BeanDefinition;
import com.enums.ScopeEnum;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 1.手写Spring扫描逻辑
 * 2.手写实现@Autowired注解的实现原理
 * 3.手写实现Spirng中Bean初始化的实现原理
 * 4.手写实现Spring中BeanPostProcessor的实现原理
 */
public class JwqSpringApplicationContext {

    private ConcurrentHashMap<String, BeanDefinition> definitionConcurrentHashMap = new ConcurrentHashMap<String, BeanDefinition>();

    private ConcurrentHashMap<String,Object> singletonBeanPool = new ConcurrentHashMap<String, Object>();

    private List<BeanPostProcessor> beanPostProcessorList=new ArrayList<BeanPostProcessor>();


    public JwqSpringApplicationContext(Class configClazz) {
        /*思考：spring在启动的时候要做什么事情？扫描并创建bean,创建什么样的bean?非懒加载的单例*/
        List<Class> list = scanBeans(configClazz);

        if (list.size() > 0) {
            for (Class aClass : list) {
                if (aClass.isAnnotationPresent(JwqComponent.class)) {
                    //有该注解的才让框架进行管理
                    JwqComponent jwqComponent = ((JwqComponent) aClass.getAnnotation(JwqComponent.class));
                    String beanName = jwqComponent.value();
                    BeanDefinition beanDefinition = new BeanDefinition();
                    beanDefinition.setaClass(aClass);
                    if (aClass.isAnnotationPresent(JwqScope.class)) {
                        JwqScope jwqScope = (JwqScope) aClass.getAnnotation(JwqScope.class);
                        String scopeName = jwqScope.value();
                        beanDefinition.setScope(scopeName);
                    }else {
                        beanDefinition.setScope(ScopeEnum.SINGLETON.getScopeName());//没有加注解，默认单例
                    }
                    definitionConcurrentHashMap.put(beanName, beanDefinition);
                    //针对JwqBeanPostProcessor的扫描
                    //判断某个类是否实现了某个类，或者某个类是否派生了某个类
                    if (BeanPostProcessor.class.isAssignableFrom(aClass)){
                        //拿到这个类的实例
                        try {
                            BeanPostProcessor beanPostProcessor = (BeanPostProcessor) aClass.getDeclaredConstructor().newInstance();
                            //将这个beanPostProcessor实例存放到beanPostProcessorList中
                            beanPostProcessorList.add(beanPostProcessor);
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            //对单例的bean进行实例化
            for (String beanName : definitionConcurrentHashMap.keySet()) {
                String scope = definitionConcurrentHashMap.get(beanName).getScope();
                if (scope.equals(ScopeEnum.SINGLETON.getScopeName())) {
                    //创建bean
                    Object bean = createBean(beanName);
                    //单例的bean需要保存，每次都是拿到的同一个
                    singletonBeanPool.put(beanName,bean);

                }
            }


        }



    }

    //生成单例bean
    private Object createBean(String beanName) {
        //创造bean,回顾一下bean的生命周期，实例化（new），赋值（依赖注入），Aware,初始化，销毁
        //反射得到一个对象（实例化）
        Class aClass = definitionConcurrentHashMap.get(beanName).getaClass();
        try {
            Object object = aClass.getDeclaredConstructor().newInstance();
            //填充属性
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(JwqAutowired.class)) {
                    //存在@JwqAutowired的属性进行填充  对属性赋值
                    //@JwqAutowired
                    //private UserService userService;  即对属性userService 赋一个对象  对象如何得到呢   getBean()
                    Object bean = getBean(field.getName());
                    field.setAccessible(true);
                    field.set(object,bean);
                }
            }
            //Aware 自动属性赋值
            if (object instanceof BeanNameAware){
                ((BeanNameAware) object).setBeanName(beanName);
            }

            //postProcessBeforeInitialization 初始化之前  程序员定义的逻辑
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postProcessBeforeInitialization(object,beanName);
            }

            //初始化 InitializingBean
            if (object instanceof InitializingBean) {
                ((InitializingBean) object).afterPropertiesSet();
            }

            //postProcessAfterInitialization 初始化之后  程序员定义的逻辑  aop
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postProcessAfterInitialization(object,beanName);
            }

            return object;

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
       return null;
    }

    //扫描
    private List<Class> scanBeans(Class configClazz) {
        System.err.println("===========JwqSpringApplicationContext扫描开始===========");
        List<Class> list = new ArrayList<Class>();

        //扫描范围：AppConfig配置类配置的范围，即"com.service"
        if (configClazz.isAnnotationPresent(JwqComponentScan.class)) { //定义的配置类存在扫描注解
            //拿到注解对象
            JwqComponentScan jwqComponentScan = (JwqComponentScan) configClazz.getAnnotation(JwqComponentScan.class);
            String value = jwqComponentScan.value(); //扫描路径 com.service
            System.err.println("配置文件（类）所指定的扫描路径为：" + value);
            //根据扫描路径，拿到该路径下面所有的文件，文件有JwqComponent注解让JwqSpring进行管理
            //如何扫描呢？当前线程的ClassLoader加载目录，首先需要对value转换成一个目录的格式/com/service
            value = value.replace(".", "/");
            ClassLoader classLoader = JwqSpringApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(value);//目录
            assert resource != null;
            File file = new File(resource.getFile());//file是一个目录对象
            // System.err.println(file.getAbsolutePath());//E:\new_project\JwqSpringFramework\target\classes\com\service
            //遍历目录下所有文件
            File[] files = file.listFiles();
            assert files != null;
            for (File fi : files) {
                //需要加载.class的文件，其他的如./txt不加载
                if (fi.getName().endsWith("class")) {
                    //E:\new_project\JwqSpringFramework\target\classes\com\service\UserService.class
                    String absolutePath = fi.getAbsolutePath();
                    //拿到全限定类名,以便进行类加载(文件变对象)
                    absolutePath = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.indexOf(".class"));
                    String packagePath = absolutePath.replace("\\", ".");//com.service.OrderService com.service.UserService
                    try {
                        //加载到类
                        Class<?> aClass = classLoader.loadClass(packagePath);
                        list.add(aClass);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

            System.err.println("===========JwqSpringApplicationContext扫描结束，扫描到了"+list.size()+"个类============");
            return list;
        }
        return Collections.emptyList();
    }

    public Object getBean(String beanName) {
        //prototype的bean是每次调用getBean方法获取
        if (definitionConcurrentHashMap.get(beanName).getScope().equals(ScopeEnum.PROTOTYPE.getScopeName())) {
            // prototype
            System.err.println(beanName+":prototype:直接创建bean");
            return createBean(beanName);
        } else {
            //singleton  单例 先去单例池找
            System.err.println(beanName+":singleton:先去单例池找 并放到单例池");
            Object o = singletonBeanPool.get(beanName);
            if (null == o){
                Object bean = createBean(beanName);
                assert bean != null;
                singletonBeanPool.put(beanName,bean);
                return bean;
            }else {
                return o;
            }
        }
    }
}
