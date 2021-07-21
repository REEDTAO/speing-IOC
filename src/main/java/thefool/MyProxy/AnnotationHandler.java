package thefool.MyProxy;

import java.lang.reflect.Method;

public interface AnnotationHandler {
    void before(Object proxy, Method method, Object[] args);
    void after(Object proxy, Method method, Object[] args,Object result);
    void error(Object proxy, Method method, Object[] args);
}
