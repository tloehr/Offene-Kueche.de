package tools;

import javax.swing.*;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 18.02.11
 * Time: 16:24
 * To change this template use File | Settings | File Templates.
 */
public class HashMapComboBoxModel  extends AbstractListModel implements MutableComboBoxModel, Serializable {

    protected HashMap model;
    protected Object selectedKey;

    public HashMapComboBoxModel(HashMap model) {
        this.model = model;
    }

    @Override
    public void addElement(Object obj) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeElement(Object key) {
        model.remove(key);
    }

    @Override
    public void insertElementAt(Object obj, int index) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeElementAt(int index) {
        model.remove(model.keySet().toArray()[index]);
    }

    @Override
    public void setSelectedItem(Object key) {
        this.selectedKey = key;
    }

    @Override
    public Object getSelectedItem() {
        return model.get(selectedKey);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getSize() {
        return model.size();
    }

    @Override
    public Object getElementAt(int index) {
        return model.values().toArray()[index];
    }
}
