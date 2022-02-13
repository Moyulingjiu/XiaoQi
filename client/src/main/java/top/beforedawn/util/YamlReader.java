package top.beforedawn.util;

import java.io.*;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

/**
 * yaml文件读取器
 *
 * @author 墨羽翎玖
 */
public class YamlReader {
    private Map<String, Object> properties;

    public YamlReader(String filename) {
        Yaml yaml = new Yaml();
        File f = new File(filename);
        Object result = null;
        try {
            result = yaml.load(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            MyLogger.log(this, String.format("无法找到配置文件@文件[%s]", filename));
        }
        properties = (Map<String, Object>) result;
    }

    /**
     * 获取数据
     *
     * @param key 键
     * @return 键对应的值
     */
    public Object getValueByKey(String key) {
        final String separator = ".";
        String[] path;
        if (key.contains(separator)) {
            path = key.split("\\" + separator);
        } else {
            return CommonUtil.decorateValue(properties.get(key));
        }
        Map<String, Object> obj = properties;
        Object ret = null;
        for (int i = 0; i < path.length; i++) {
            if (obj == null) {
                break;
            } else if (i != path.length - 1) {
                obj = (Map<String, Object>) obj.get(path[i]);
            } else {
                ret = obj.get(path[i]);
            }
        }
        return CommonUtil.decorateValue(ret);
    }

    /**
     * 通过key来更改value的值
     *
     * @param key 键值
     * @param obj 新的value值
     * @return 是否成功
     */
    public boolean setValueByKey(String key, Object obj) {
        final String separator = ".";
        String[] path;
        if (key.contains(separator)) {
            path = key.split("\\" + separator);
        } else {
            if (properties.containsKey(key)) {
                properties.put(key, obj);
                return true;
            }
            return false;
        }
        Map<String, Object> p = properties;
        for (int i = 0; i < path.length; i++) {
            if (obj == null) {
                break;
            } else if (i != path.length - 1) {
                p = (Map<String, Object>) p.get(path[i]);
            } else {
                if (p.containsKey(path[i])) {
                    p.put(path[i], obj);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 保存yaml
     *
     * @param filename 文件名
     */
    public void save(String filename) {
        Yaml yaml = new Yaml();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            yaml.dump(properties, writer);
        } catch (IOException e) {
            MyLogger.log(this, String.format("读写异常@文件[%s]", filename));
            e.printStackTrace();
        }
    }
}