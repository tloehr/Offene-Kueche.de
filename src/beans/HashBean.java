package beans;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 18.02.11
 * Time: 16:34
 * To change this template use File | Settings | File Templates.
 */
public class HashBean {
    Object key, value;

    public HashBean(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = obj != null &&
                (obj instanceof String ? key.toString().equalsIgnoreCase(obj.toString()) : key.equals(((HashBean) obj).getKey()));
        if (obj instanceof String){
            Main.Main.logger.debug("key: " + key.toString());
            Main.Main.logger.debug("obj: " + obj.toString());
            Main.Main.logger.debug("equal?: " + result);
        }

        return result;
    }
}
