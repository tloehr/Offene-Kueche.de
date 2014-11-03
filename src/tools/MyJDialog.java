package tools;

import Main.Main;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Created by tloehr on 24.10.14.
 */
public class MyJDialog extends JDialog {
    private JPanel content;

    public MyJDialog(boolean decorated) {
        this(Main.getMainframe(), decorated);
    }

    public MyJDialog() {
        this(true);
    }

    public MyJDialog(JFrame owner) {
        this(owner, true);
    }

    public MyJDialog(Frame owner) {
        super(owner);
    }

    public MyJDialog(Dialog owner, boolean decorated) {
        super(owner, true);
        setLocationRelativeTo(getParent());
        initContent();
        setResizable(false);
        setUndecorated(!decorated);
    }

    public MyJDialog(JFrame owner, boolean decorated) {
        super(owner, true);
        setLocationRelativeTo(getParent());
        initContent();
        setResizable(false);
        setUndecorated(!decorated);
    }

    @Override
    public Dimension getPreferredSize() {
        // makes sure, that the dialog is never larger than the screen in use.
        Rectangle maximumsize = GUITools.getScreenSize(GUITools.getCurrentScreen(Main.getMainframe()));
        Dimension dim = super.getPreferredSize();
        if (dim.getWidth() > maximumsize.getWidth() * 0.9) dim.setSize(maximumsize.getWidth() * 0.9, dim.getHeight());
        if (dim.getHeight() > maximumsize.getHeight() * 0.9) dim.setSize(dim.getWidth(), maximumsize.getHeight() * 0.9);
        return dim;
    }

    public MyJDialog(Dialog owner) {
        this(owner, true);
    }

    private void initContent() {
        content = new JPanel();
        content.setBorder(new LineBorder(Color.BLACK, 1));
        setContentPane(content);
    }

    @Override
    public void dispose() {
        setVisible(false);
    }

    @Override
    public void setVisible(boolean b) {
        Tools.centerOnParent(this);
        super.setVisible(b);
    }

    public void setMaximized(){
        setSize(getParent().getSize());
        Tools.center(this);
    }
}