package club.beenest.collector;

import club.beenest.annotation.CollectInterface;
import club.beenest.model.InterfaceInfo;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class InterfaceCollector implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext =applicationContext;
    }

    /**
     * 从Spring上下文中收集带有@CollectInterface注解的接口信息
     *
     * @param applicationName 应用名称
     * @return 接口信息列表
     */
    public List<InterfaceInfo> collectInterfaces(String applicationName) {
        List<InterfaceInfo> interfaceInfos = new ArrayList<>();

        try {
            if (applicationContext == null) {
                throw new IllegalStateException("ApplicationContext not initialized");
            }

            // 获取Spring容器中所有的bean
            String[] beanNames = applicationContext.getBeanDefinitionNames();

            for (String beanName : beanNames) {
                Object bean = applicationContext.getBean(beanName);

                // 获取目标类（处理AOP代理）
                Class<?> targetClass = AopUtils.getTargetClass(bean);

                // 首先检查类上是否有@RequestMapping注解
                // 只有控制器类才需要进一步检查方法
                RequestMapping classRequestMapping = AnnotationUtils.findAnnotation(targetClass, RequestMapping.class);
                if (classRequestMapping != null) {
                    // 获取类级别@RequestMapping的路径 - 优化：将这个操作移到循环外部，每个类只执行一次
                    String classPath = getPathFromAnnotation(classRequestMapping);

                    // 获取类上所有方法
                    Method[] methods = targetClass.getDeclaredMethods();
                    for (Method method : methods) {
                        // 检查方法上是否有@CollectInterface注解
                        CollectInterface collectInterface = AnnotationUtils.findAnnotation(method, CollectInterface.class);

                        if (collectInterface != null) {
                            // 获取接口描述
                            String description = collectInterface.description();

                            // 处理方法上的请求映射注解
                            InterfaceInfo interfaceInfo = processMethodAnnotationWithReflection(method, targetClass,
                                    description, targetClass.getName(), method.getName(), classPath);

                            if (interfaceInfo != null) {
                                interfaceInfo.setApplicationName(applicationName);
                                interfaceInfos.add(interfaceInfo);
                            }
                        }
                    }
                }
            }


        } catch (Exception e) {
            throw new RuntimeException("Failed to collect interfaces", e);
        }

        return interfaceInfos;
    }


    /**
     * 处理方法上的请求映射注解，支持@RequestMapping及各种组合注解（GetMapping、PostMapping等）
     */
    private InterfaceInfo processMethodAnnotationWithReflection(Method method, Class<?> clazz, String description,
                                                                String className, String methodName, String classPath) {
        try {
            // 检查各种HTTP方法特定的注解（优先级高于RequestMapping）
            String methodPath = "";
            String httpMethod = "GET"; // 默认GET方法

            // 检查@GetMapping注解
            GetMapping getMapping = AnnotationUtils.findAnnotation(method, GetMapping.class);
            if (getMapping != null) {
                methodPath = extractPathFromMappingAnnotation(getMapping);
                httpMethod = "GET";
            }
            // 检查@PostMapping注解
            else if (AnnotationUtils.findAnnotation(method, PostMapping.class) != null) {
                PostMapping postMapping = AnnotationUtils.findAnnotation(method, PostMapping.class);
                methodPath = extractPathFromMappingAnnotation(postMapping);
                httpMethod = "POST";
            }
            // 检查@PutMapping注解
            else if (AnnotationUtils.findAnnotation(method, PutMapping.class) != null) {
                PutMapping putMapping = AnnotationUtils.findAnnotation(method, PutMapping.class);
                methodPath = extractPathFromMappingAnnotation(putMapping);
                httpMethod = "PUT";
            }
            // 检查@DeleteMapping注解
            else if (AnnotationUtils.findAnnotation(method, DeleteMapping.class) != null) {
                DeleteMapping deleteMapping = AnnotationUtils.findAnnotation(method, DeleteMapping.class);
                methodPath = extractPathFromMappingAnnotation(deleteMapping);
                httpMethod = "DELETE";
            }
            // 检查@PatchMapping注解
            else if (AnnotationUtils.findAnnotation(method, PatchMapping.class) != null) {
                PatchMapping patchMapping = AnnotationUtils.findAnnotation(method, PatchMapping.class);
                methodPath = extractPathFromMappingAnnotation(patchMapping);
                httpMethod = "PATCH";
            }
            // 最后检查@RequestMapping注解
            else if (AnnotationUtils.findAnnotation(method, RequestMapping.class) != null) {
                RequestMapping requestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
                methodPath = getPathFromAnnotation(requestMapping);
                httpMethod = getMethodFromAnnotation(requestMapping);
            }
            else {
                // 如果没有找到任何映射注解，直接返回null
                return null;
            }

            // 构建并返回接口信息
            return createInterfaceInfo(classPath, methodPath, httpMethod, description, className, methodName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从特定HTTP方法注解（GetMapping、PostMapping等）中提取路径信息
     */
    private String extractPathFromMappingAnnotation(Object annotation) {
        String finalPath = "";

        try {
            // 使用反射获取value和path属性
            Class<?> annotationClass = annotation.getClass();

            // 尝试获取value属性
            try {
                java.lang.reflect.Method valueMethod = annotationClass.getMethod("value");
                String[] values = (String[]) valueMethod.invoke(annotation);
                if (values != null && values.length > 0 && !values[0].isEmpty()) {
                    finalPath = values[0];
                }
            } catch (Exception ignored) {}

            // 如果value为空，尝试获取path属性
            if (finalPath.isEmpty()) {
                try {
                    java.lang.reflect.Method pathMethod = annotationClass.getMethod("path");
                    String[] paths = (String[]) pathMethod.invoke(annotation);
                    if (paths != null && paths.length > 0 && !paths[0].isEmpty()) {
                        finalPath = paths[0];
                    }
                } catch (Exception ignored) {}
            }

            // 确保路径格式正确
            if (!finalPath.isEmpty() && !finalPath.startsWith("/")) {
                finalPath = "/" + finalPath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return finalPath;
    }

    /**
     * 从RequestMapping注解中获取路径信息
     */
    private String getPathFromAnnotation(RequestMapping annotation) {
        // 处理路径
        String finalPath = "";
        if (annotation.value().length > 0 && !annotation.value()[0].isEmpty()) {
            finalPath = annotation.value()[0];
        } else if (annotation.path().length > 0 && !annotation.path()[0].isEmpty()) {
            finalPath = annotation.path()[0];
        }

        // 确保路径格式正确
        if (!finalPath.isEmpty() && !finalPath.startsWith("/")) {
            finalPath = "/" + finalPath;
        }

        return finalPath;
    }



    /**
     * 从@RequestMapping注解中获取HTTP方法
     * 支持处理多个HTTP方法，如果有多个方法则用逗号分隔
     */
    private String getMethodFromAnnotation(RequestMapping annotation) {
        RequestMethod[] methods = annotation.method();
        if (methods.length > 0) {
            if (methods.length == 1) {
                // 单个方法直接返回
                return methods[0].name();
            } else {
                // 多个方法用逗号分隔
                StringBuilder methodBuilder = new StringBuilder();
                for (int i = 0; i < methods.length; i++) {
                    methodBuilder.append(methods[i].name());
                    if (i < methods.length - 1) {
                        methodBuilder.append(",");
                    }
                }
                return methodBuilder.toString();
            }
        } else {
            // 默认GET
            return "GET";
        }
    }

    /**
     * 创建接口信息对象，优化URL路径处理逻辑
     */
    private InterfaceInfo createInterfaceInfo(String classPath, String methodPath, String httpMethod,
                                              String description, String className, String methodName) {
        InterfaceInfo interfaceInfo = new InterfaceInfo();

        // 构建完整路径 - 优化路径处理逻辑
        StringBuilder fullPathBuilder = new StringBuilder();

        // 处理类路径：确保不以/结尾，除非是空路径
        if (classPath != null && !classPath.isEmpty()) {
            String normalizedClassPath = classPath;
            // 确保以/开头
            if (!normalizedClassPath.startsWith("/")) {
                normalizedClassPath = "/" + normalizedClassPath;
            }
            // 移除末尾的/（如果有）
            if (normalizedClassPath.length() > 1 && normalizedClassPath.endsWith("/")) {
                normalizedClassPath = normalizedClassPath.substring(0, normalizedClassPath.length() - 1);
            }
            fullPathBuilder.append(normalizedClassPath);
        }

        // 处理方法路径：如果不为空，确保以/开头
        if (methodPath != null && !methodPath.isEmpty()) {
            String normalizedMethodPath = methodPath;
            // 确保以/开头
            if (!normalizedMethodPath.startsWith("/")) {
                normalizedMethodPath = "/" + normalizedMethodPath;
            }
            fullPathBuilder.append(normalizedMethodPath);
        }

        // 确保完整路径以/开头
        String fullPath = fullPathBuilder.toString();
        if (!fullPath.isEmpty() && !fullPath.startsWith("/")) {
            fullPath = "/" + fullPath;
        }

        // 设置基本信息
        interfaceInfo.setFullPath(fullPath);
        interfaceInfo.setMethod(httpMethod);
        interfaceInfo.setDescription(description);
        interfaceInfo.setClassName(className);
        interfaceInfo.setMethodName(methodName);

        // 设置时间戳
        long currentTime = System.currentTimeMillis();
        interfaceInfo.setCreateTime(currentTime);
        interfaceInfo.setUpdateTime(currentTime);

        // 设置启用状态
        interfaceInfo.setEnabled(true);

        return interfaceInfo;
    }
}
