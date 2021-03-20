
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WeekOneTest1 {

    public static void main(String[] args) {
        String filePath = "Hello.xlass";
        try {
            Class object = new MyClassLoader(filePath).findClass("Hello");
            Object hello = object.newInstance();
            Method helloMethod = hello.getClass().getMethod("hello");
            helloMethod.invoke(hello, null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException | InstantiationException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    static class MyClassLoader extends ClassLoader {

        private String filePath;

        MyClassLoader(String filePath) {
            this.filePath = filePath;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            Class<?> result = null ;
            try {
                Path p = Paths.get(filePath);
                byte[] bytes = Files.readAllBytes(p);
                byte[] newDatas = new byte[bytes.length];
                for (int i = 0; i < bytes.length; i++) {
                    newDatas[i] = (byte) ((255 - bytes[i]));
                }
                result =  defineClass(name, newDatas, 0, bytes.length);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (result == null) {
                throw new ClassNotFoundException(name);
            }
            return result;
        }


    }


}
