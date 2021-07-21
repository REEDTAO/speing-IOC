package thefool.MyProxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import thefool.Annotation.Autowired;
import thefool.Annotation.Service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("proxyHandler")
public class ProxyHandler{
    @Autowired("annoHandlerMap")
    public Map<String,List<AnnotationHandler>> annoHandlerMap;

    public Map<String, List<AnnotationHandler>> getAnnoHandlerMap() {
        return annoHandlerMap;
    }

    public void setAnnoHandlerMap(Map<String, List<AnnotationHandler>> annoHandlerMap) {
        this.annoHandlerMap = annoHandlerMap;
    }

    /**
     * jdk动态代理
     * @param o
     * @return
     */
    public Object getJdkProxy(Object o) {
        return Proxy.newProxyInstance(o.getClass().getClassLoader(), o.getClass().getInterfaces(), new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object invoke = null;
                String name = o.getClass().getName();
                List<AnnotationHandler> annotationHandlersForClass = annoHandlerMap.get(name.substring(0,(name.indexOf("$")==-1?name.length():name.indexOf("$"))));
                List<AnnotationHandler> annotationHandlersForMethod = annoHandlerMap.get((name.indexOf("$")==-1?name.length():name.indexOf("$"))+"."+method.getName());
                List<AnnotationHandler> annotationHandlers= new ArrayList<>();
                if(annotationHandlersForClass!=null)
                    annotationHandlers.addAll(annotationHandlersForClass);
                else if(annotationHandlersForMethod!=null)
                    annotationHandlers.addAll(annotationHandlersForMethod);

                for(AnnotationHandler a: annotationHandlers){
                    a.before( proxy,  method,  args);
                }
                try {
                    invoke = method.invoke(proxy, args);
                }catch (InvocationTargetException e){
                    // 固定执行代理链上的error方法
                    for(AnnotationHandler a: annotationHandlers){
                        a.error( proxy,  method,  args);
                    }
                    System.out.println("发生异常！");
                    //e.getTargetException().printStackTrace();
                }
                for(AnnotationHandler a: annotationHandlers){
                    a.after( proxy,  method,  args,invoke);
                }
                return invoke;
            }
        });
    }

    /**
     * cglib动态代理
     * @param obj
     * @return
     */
    public Object getCglibProxy(Object obj) {
        return  Enhancer.create(obj.getClass(), new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                String name = o.getClass().getName();
                List<AnnotationHandler> annotationHandlersForClass = annoHandlerMap.get(name.substring(0,(name.indexOf("$")==-1?name.length():name.indexOf("$"))));
                List<AnnotationHandler> annotationHandlersForMethod = annoHandlerMap.get((name.indexOf("$")==-1?name.length():name.indexOf("$"))+"."+method.getName());
                List<AnnotationHandler> annotationHandlers= new ArrayList<>();
                if(annotationHandlersForClass!=null)
                    annotationHandlers.addAll(annotationHandlersForClass);
                else if(annotationHandlersForMethod!=null)
                    annotationHandlers.addAll(annotationHandlersForMethod);

                for(AnnotationHandler a: annotationHandlers){
                    a.before( o,  method,  objects);
                }
                Object result = null;
                try{
                    result = method.invoke(obj,objects);
                }catch (InvocationTargetException e) {
                    // 固定执行代理链上的error方法
                    for(AnnotationHandler a: annotationHandlers){
                        a.error( o,  method,  objects);
                    }
                    e.printStackTrace();
                    throw e;
                }
                for(AnnotationHandler a: annotationHandlers){
                    a.after( o,  method,  objects,result);
                }
                return result;
            }
        });
    }

}
