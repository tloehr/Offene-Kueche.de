package tools;

import javax.swing.*;

/**
 * http://java.dzone.com/articles/unselect-all-toggle-buttons?utm_source=feedburner&utm_medium=feed&utm_campaign=Feed:+javalobby/frontpage+(Javalobby+/+Java+Zone
 */
public class NoneSelectedButtonGroup extends ButtonGroup {

  @Override
  public void setSelected(ButtonModel model, boolean selected) {
    if (selected) {
      super.setSelected(model, selected);
    } else {
      clearSelection();
    }
  }
}