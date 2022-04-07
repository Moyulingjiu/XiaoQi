package top.beforedawn.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 用于系统初始化检查文件的完整性
 *
 * @author moyulingjiu
 */
public class InitFileChecker {
    /**
     * 检查文件完整性，并且修复
     *
     * @param path 路径
     * @throws IOException 无法修复文件
     */
    public static void checkAndRepair(String path) throws IOException {
        createFile(path);
        String cache = path + "/cache";
        String group = path + "/group";
        String user = path + "/user";
        String image = path + "/image";
        createFile(cache);
        createFile(group);
        createFile(user);
        createFile(image);
    }

    private static void createFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            if (!file.mkdirs()) {
                throw new IOException("创建文件失败");
            }
        }
    }
}
