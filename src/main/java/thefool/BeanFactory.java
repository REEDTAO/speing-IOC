package thefool;

import com.alibaba.druid.util.StringUtils;
import javassist.util.proxy.ProxyFactory;
import org.reflections.Reflections;
import thefool.Annotation.Autowired;
import thefool.Annotation.Service;
import thefool.Annotation.Transactional;
import thefool.MyProxy.AnnotationHandler;
import thefool.MyProxy.ProxyHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 工厂类，生产对象（使用反射技术）
 */
public class BeanFactory {

    private static Map<String, Object> map = new HashMap<>();  // 存储对象


    static {
        try {
            map.put("annoHandlerMap", new HashMap<>());
            //通过反射技术，扫描包并获取反射对象集合
            Reflections edus = new Reflections("thefool");
            Set<Class<?>> clazzs = edus.getTypesAnnotatedWith(Service.class);
            //遍历对象集合
            for (Class<?> clazz : clazzs) {
                // 获取实例化对象
                Object object = clazz.newInstance();
                Service service = clazz.getAnnotation(Service.class);

                //判断MyService注解上是否有自定义对象ID
                if (StringUtils.isEmpty(service.value())) {
                    //由于getName获取的是全限定类名，所以要分割去掉前面包名部分
                    String[] names = clazz.getName().split("\\.");
                    map.put(names[names.length - 1], object);
                } else {
                    map.put(service.value(), object);
                }
            }
            //维护对象之间依赖关系
            for (Map.Entry<String, Object> entrySet : map.entrySet()) {
                Object obj = entrySet.getValue();
                Class clazz = obj.getClass();

                //获取每个类的所有属性
                Field[] fields = clazz.getDeclaredFields();
                //遍历属性，确认是否有使用Autowired注解，有使用注解则需要完成注入
                for (Field field : fields) {
                    //判断是否使用注解的参数
                    if (field.isAnnotationPresent(Autowired.class)) {//有使用注解则注入
                        String name = field.getName();
                        Method[] methods = clazz.getMethods();
                        for (int j = 0; j < methods.length; j++) {
                            Method method = methods[j];
                            if (method.getName().equalsIgnoreCase("set" + name)) {  // 该方法就是 setAccountDao(AccountDao accountDao)
                                method.invoke(obj, map.get(name));
                            }
                        }
                    }
                }
                //判断当前类是否有Transactional注解，若有则使用代理对象
                if (clazz.isAnnotationPresent(Transactional.class)) {

                    // 添加处理链
                    Map<String, List> handlerMap = (Map<String, List>) map.get("annoHandlerMap");
                    List list = handlerMap.get(clazz.getName());
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(BeanFactory.getBean("TransAnnoHandler"));
                    handlerMap.put(clazz.getName(), list);


                    //获取代理工厂
                    ProxyHandler proxyHandler = (ProxyHandler) BeanFactory.getBean("proxyHandler");
                    Class[] face = clazz.getInterfaces();//获取类c实现的所有接口
                    //判断对象是否实现接口
                    if (face != null && face.length > 0) {
                        //实现使用JDK
                        obj = proxyHandler.getJdkProxy(obj);
                    } else {
                        //没实现使用CGLIB
                        obj = proxyHandler.getCglibProxy(obj);
                    }
                }
                // 把处理之后的object重新放到map中
                map.put(entrySet.getKey(), obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 任务二：对外提供获取实例对象的接口（根据id获取）
    public static Object getBean(String id) {
        return map.get(id);
    }

}
