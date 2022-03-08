package top.beforedawn.util;

import java.io.*;

public class FileUtil {
    /**
     * 读取文件中的文本（会全部读取出来，避免读取大文件；文件应当为utf-8编码格式）
     *
     * @param filepath 文件路径
     * @return 文本
     */
    public static String readFile(String filepath) {
        try {
            File file = new File(filepath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
            br.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * 写文件
     *
     * @param filepath 文件路径
     * @param content 内容
     */
    public static void writeFile(String filepath, String content) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
            writer.write(content);
            writer.close();
        } catch (IOException ignored) {
        }
    }
}
