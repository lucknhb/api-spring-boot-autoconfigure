package com.nhb.api.utils;

import com.nhb.api.annotation.ApiLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author luck_nhb
 * @version 1.0
 * @description 用于指定controller扫描路径 此后可更换成SpringBoot默认包扫描路径
 * @date 2021/4/28 17:15
 */

public class ApiScanUtil {
    private static Logger logger = LoggerFactory.getLogger(ApiScanUtil.class);

    /**
     * 根据控制层(controller)类所在包路径扫描ApiLabel注解
     *
     * @param controllerPackage
     * @return
     */
    public static Set<Class> getApiLabelAnnotation(String controllerPackage) {
        Set<Class> classSet = new HashSet<>();
        // 获取包的名字 并进行替换
        String packageDirName = controllerPackage.replace('.', '/');
        try {
            Enumeration<URL> classPackages = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (classPackages.hasMoreElements()) {
                URL url = classPackages.nextElement();
                String protocol = url.getProtocol();
                switch (protocol) {
                    case "file": {
                        String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                        //开始扫描
                        File dir = new File(filePath);
                        List<File> files = new ArrayList<>();
                        findFilesByDirectory(dir, files);
                        for (File file : files) {
                            String fileName = file.getAbsolutePath();
                            if (fileName.endsWith(".class")) {
                                String noSuffixFileName = fileName.substring(8 + fileName.lastIndexOf("classes"), fileName.indexOf(".class"));
                                String regex = "\\\\";
                                if (!"\\".equals(File.separator)) {
                                    regex = File.separator;
                                }
                                String filePackage = noSuffixFileName.replaceAll(regex, ".");
                                Class aClass = Class.forName(filePackage);
                                if (judgeControlLevel(aClass)) {
                                    classSet.add(aClass);
                                }
                            }
                        }
                        break;
                    }
                    case "jar":
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        JarFile jarFile = jarURLConnection.getJarFile();
                        //获取jar包下所有的实体class
                        Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                        while (jarEntryEnumeration.hasMoreElements()) {
                            JarEntry jarEntry = jarEntryEnumeration.nextElement();
                            String jarEntryName = jarEntry.getName();
                            if (jarEntryName.charAt(0) == '/') {
                                jarEntryName = jarEntryName.substring(1);
                            }
                            if (jarEntryName.startsWith(packageDirName)) {
                                int indexOf = jarEntryName.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (indexOf != -1) {
                                    // 获取包名 把"/"替换成"."
                                    controllerPackage = jarEntryName.substring(0, indexOf).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                // 如果是一个.class文件 而且不是目录
                                if (jarEntryName.endsWith(".class") && !jarEntry.isDirectory()) {
                                    String className = jarEntryName.substring(controllerPackage.length() + 1, jarEntryName.length() - 6);
                                    Class<?> aClass = Class.forName(controllerPackage + "." + className);
                                    if (judgeControlLevel(aClass)) {
                                        logger.debug("获取到API接口:{}", aClass);
                                        classSet.add(aClass);
                                    }
                                }
                            }
                        }
                        break;
                }
            }
        } catch (Exception e) {
            logger.error("扫描包路径发生异常:{}", e.getMessage());
        }
        return classSet;
    }


    /**
     * 判断该类是不是控制层
     * @param aClass
     * @return
     */
    private static boolean judgeControlLevel(Class<?> aClass){
        if (null != aClass.getDeclaredAnnotation(ApiLabel.class)
                && (null != aClass.getDeclaredAnnotation(Controller.class)  ||
                    null != aClass.getDeclaredAnnotation(RestController.class))){
            return true;
        }
        return false;
    }


    /**
     * 根据文件夹获取其下面的文件列表
     *
     * @param dir
     * @return
     */
    private static void findFilesByDirectory(File dir, List<File> files) {
        if (dir.isDirectory()) {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                findFilesByDirectory(file, files);
            }
        } else {
            files.add(dir);
        }
    }
}
