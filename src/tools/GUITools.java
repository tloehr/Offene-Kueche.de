package tools;

import Main.Main;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.popup.JidePopup;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 16.06.11
 * Time: 14:46
 * To change this template use File | Settings | File Templates.
 */
public class GUITools {

    public static void exportToPNG(JPanel pnl, File output) {
        BufferedImage bi = new BufferedImage(pnl.getSize().width, pnl.getSize().height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        pnl.paint(g);
        g.dispose();
        try {
            ImageIO.write(bi, "png", output);
        } catch (Exception e) {
        }

    }


    public static ByteArrayOutputStream getAsImage(JPanel pnl) throws Exception {
        BufferedImage bi = new BufferedImage(pnl.getPreferredSize().width, pnl.getPreferredSize().height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        pnl.paint(g);
        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", baos);
        return baos;
    }


    /**
     * Shows a JidePopup in relation to its owner. Calculates the new position that it leaves the owner
     * visible. The popup is placed according to the <code>location</code> setting. The size of the content
     * pane is taken into the calculation in order to find the necessary <code>x, y</code> coordinates on the screen.
     * <p/>
     * <ul>
     * <li>SwingConstants.CENTER <i>You can use this, but I fail to see the sense in it.</i></li>
     * <li>SwingConstants.SOUTH</li>
     * <li>SwingConstants.NORTH</li>
     * <li>SwingConstants.WEST</li>
     * <li>SwingConstants.EAST</li>
     * <li>SwingConstants.NORTH_EAST</li>
     * <li>SwingConstants.NORTH_WEST</li>
     * <li>SwingConstants.SOUTH_EAST</li>
     * <li>SwingConstants.SOUTH_WEST</li>
     * </ul>
     *
     * @param popup    the JidePopup to show
     * @param location where to show the popup in relation to the <code>reference</code>. Use the SwingConstants above.
     */
    public static void showPopup(JidePopup popup, int location, boolean keepOnScreen) {


        Point desiredPosition = getDesiredPosition(popup, location);

//        Main.debug(Boolean.toString(isFullyVisibleOnScreen(popup, desiredPosition)));


        if (keepOnScreen && !isFullyVisibleOnScreen(popup, desiredPosition)) {
            int[] positions = new int[]{SwingConstants.SOUTH_EAST, SwingConstants.SOUTH_WEST, SwingConstants.NORTH_EAST, SwingConstants.NORTH_WEST, SwingConstants.SOUTH, SwingConstants.EAST, SwingConstants.SOUTH_WEST, SwingConstants.NORTH, SwingConstants.CENTER};
            boolean found = false;

            for (int pos : positions) {
                desiredPosition = getDesiredPosition(popup, pos);
                if (isFullyVisibleOnScreen(popup, desiredPosition)) {
                    found = true;
                    Main.debug("fits on screen");
                    break;
                }
            }

            if (!found) {
                // desiredPosition = getDesiredPosition(popup, location);
                desiredPosition = centerOnScreen(popup);
                Main.debug("didnt find any position thats on the screen");
            }

        }

        popup.showPopup(desiredPosition.x, desiredPosition.y);

    }

    private static Point centerOnScreen(JidePopup popup) {
        GraphicsDevice gd = getCurrentScreen(Main.getMainframe());
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        int midx = width / 2;
        int midy = height / 2;


        int x = midx - popup.getContentPane().getPreferredSize().width / 2;
        int y = midy - popup.getContentPane().getPreferredSize().height / 2;

        return new Point(x, y);
    }

    private static Point getDesiredPosition(JidePopup popup, int location) {
        Container content = popup.getContentPane();

        final Point screenposition = new Point(popup.getOwner().getLocationOnScreen().x, popup.getOwner().getLocationOnScreen().y);
//        Point screenposition = new Point(popup.getOwner().getLocation());
//        SwingUtilities.convertPointToScreen(screenposition, Main.getMainframe());

        int x = screenposition.x;
        int y = screenposition.y;

        switch (location) {
            case SwingConstants.SOUTH_WEST: {
                x = screenposition.x - content.getPreferredSize().width;
                y = screenposition.y;
                break;
            }
            case SwingConstants.SOUTH: {
                x = screenposition.x;
                y = screenposition.y + popup.getOwner().getPreferredSize().height;
                break;
            }
            case SwingConstants.SOUTH_EAST: {
                x = screenposition.x + popup.getOwner().getPreferredSize().width;
                y = screenposition.y + popup.getOwner().getPreferredSize().height;
                break;
            }
            case SwingConstants.NORTH_EAST: {
                x = screenposition.x + popup.getOwner().getPreferredSize().width;
                y = screenposition.y - popup.getOwner().getPreferredSize().height - content.getPreferredSize().height;
                break;
            }
            case SwingConstants.NORTH_WEST: {
                x = screenposition.x - content.getPreferredSize().width - popup.getOwner().getPreferredSize().width;
                y = screenposition.y - popup.getOwner().getPreferredSize().height - content.getPreferredSize().height;
                break;
            }
            case SwingConstants.EAST: {
                x = screenposition.x + popup.getOwner().getPreferredSize().width;
                y = screenposition.y - (popup.getOwner().getPreferredSize().height / 2);
                break;
            }
            case SwingConstants.WEST: {
                x = screenposition.x - content.getPreferredSize().width;
                y = screenposition.y - (popup.getOwner().getPreferredSize().height / 2);
                break;
            }
            case SwingConstants.NORTH: {
                x = screenposition.x;
                y = screenposition.y - content.getPreferredSize().height;
                break;
            }
            case SwingConstants.CENTER: {
                x = screenposition.x + popup.getOwner().getPreferredSize().width / 2 - content.getPreferredSize().width / 2;
                y = screenposition.y + popup.getOwner().getPreferredSize().height / 2 - content.getPreferredSize().height / 2;
                break;
            }
            default: {
                // nop
            }
        }
        return new Point(x, y);
    }

    public static boolean isFullyVisibleOnScreen(JidePopup popup, Point point) {
//        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        GraphicsDevice gd = getCurrentScreen(Main.getMainframe());

        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        int spreadX = point.x + popup.getContentPane().getPreferredSize().width;
        int spreadY = point.y + popup.getContentPane().getPreferredSize().height;

        Main.debug("PointX: " + point.x);
        Main.debug("PointY: " + point.y);

        return point.x >= 0 && point.y >= 0 && width > spreadX && height > spreadY;

    }

    public static void showPopup(JidePopup popup, int location) {
        showPopup(popup, location, true);
    }


    public static void setCollapsed(Container root, boolean collapsed) throws PropertyVetoException {
        if (root instanceof CollapsiblePane) {
            if (((CollapsiblePane) root).isCollapsible()) {
                ((CollapsiblePane) root).setCollapsed(collapsed);
            }
        }
        for (Component component : root.getComponents()) {
            if (component instanceof Container) {
                setCollapsed((Container) component, collapsed);
            }
        }
    }

    public static void expand(CollapsiblePane cp) throws PropertyVetoException {
//        ArrayList<CollapsiblePane> path = new ArrayList<CollapsiblePane>();
//        path.add(cp);
        cp.setCollapsed(false);
        Container cont = cp.getParent();
        while (cont != null) {
            if (cont instanceof CollapsiblePane) {
                ((CollapsiblePane) cont).setCollapsed(false);
            }
            cont = cont.getParent();
        }
//
//
//        if (root instanceof CollapsiblePane) {
//            if (((CollapsiblePane) root).isCollapsible()) {
//                ((CollapsiblePane) root).setCollapsed(collapsed);
//            }
//        }
//        for (Component component : root.getComponents()) {
//            if (component instanceof Container) {
//                setCollapsed((Container) component, collapsed);
//            }
//        }
    }


    /**
     * @param distance a double between 0.0f and 1.0f to express the distance between the source and destination color
     *                 see http://stackoverflow.com/questions/27532/generating-gradients-programatically
     * @return
     */
    public static Color interpolateColor(Color source, Color destination, double distance) {
        int red = (int) (destination.getRed() * distance + source.getRed() * (1 - distance));
        int green = (int) (destination.getGreen() * distance + source.getGreen() * (1 - distance));
        int blue = (int) (destination.getBlue() * distance + source.getBlue() * (1 - distance));
        return new Color(red, green, blue);
    }


    public static boolean containsEmpty(ArrayList<JTextComponent> list) {
        boolean result = false;
        for (JTextComponent comp : list) {
            if (comp.getText().trim().isEmpty()) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static FocusTraversalPolicy createTraversalPolicy(final ArrayList<Component> list) {
        FocusTraversalPolicy myPolicy = new FocusTraversalPolicy() {

            @Override
            public Component getComponentAfter(Container aContainer, Component aComponent) {
                int pos = list.indexOf(aComponent) + 1;
                if (pos == list.size()) {
                    pos = 0;
                }
                return list.get(pos);
            }

            @Override
            public Component getComponentBefore(Container aContainer, Component aComponent) {
                int pos = list.indexOf(aComponent) - 1;
                if (pos < 0) {
                    pos = list.size() - 1;
                }
                return list.get(pos);
            }

            @Override
            public Component getFirstComponent(Container aContainer) {
                return list.get(0);
            }

            @Override
            public Component getLastComponent(Container aContainer) {
                return list.get(list.size() - 1);
            }

            @Override
            public Component getDefaultComponent(Container aContainer) {
                return list.get(0);
            }
        };
        return myPolicy;
    }


    public static void save(Properties content, java.util.List<Component> components) {
        for (Component comp : components) {
            if (comp instanceof JTextComponent) {
                content.setProperty(comp.getName(), ((JTextComponent) comp).getText());
            } else if (comp instanceof AbstractButton) {
                content.setProperty(comp.getName(), Boolean.toString(((AbstractButton) comp).isSelected()));
            }
        }
    }

    public static Color invert(Color color) {
        return new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
    }

    /**
     * http://stackoverflow.com/questions/8741479/automatically-determine-optimal-fontcolor-by-backgroundcolor
     *
     * @param background
     * @return
     */
    public static Color getForeground(Color background) {
        int red = 0;
        int green = 0;
        int blue = 0;

        if (background.getRed() + background.getGreen() + background.getBlue() < 383) {
            red = 255;
            green = 255;
            blue = 255;
        }
        return new Color(red, green, blue);
    }

    /**
     * http://stackoverflow.com/questions/4059133/getting-html-color-codes-with-a-jcolorchooser
     *
     * @param c
     * @return
     */
    public static String toHexString(Color c) {
        StringBuilder sb = new StringBuilder("");

        if (c.getRed() < 16) sb.append('0');
        sb.append(Integer.toHexString(c.getRed()));

        if (c.getGreen() < 16) sb.append('0');
        sb.append(Integer.toHexString(c.getGreen()));

        if (c.getBlue() < 16) sb.append('0');
        sb.append(Integer.toHexString(c.getBlue()));

        return sb.toString();
    }


    /**
     * creates a blend between two colors. The float specifies where the balance is.
     * the more towards 1.0 emphasizes the <b>first</b> color.
     * the more towards 0.0 emphasizes the <b>second</b> color.
     *
     * @param clOne
     * @param clTwo
     * @param fAmount
     * @return
     */
    public static Color blend(Color clOne, Color clTwo, float fAmount) {
        float fInverse = 1.0f - fAmount;

        // I had to look up getting colour components in java.  Google is good :)
        float afOne[] = new float[3];
        clOne.getColorComponents(afOne);
        float afTwo[] = new float[3];
        clTwo.getColorComponents(afTwo);

        float afResult[] = new float[3];
        afResult[0] = afOne[0] * fAmount + afTwo[0] * fInverse;
        afResult[1] = afOne[1] * fAmount + afTwo[1] * fInverse;
        afResult[2] = afOne[2] * fAmount + afTwo[2] * fInverse;

        return new Color(afResult[0], afResult[1], afResult[2]);
    }

//    public static Color brighter(Color originalColour, float FACTOR) {
//
//
//        float hsbVals[] = Color.RGBtoHSB(originalColour.getRed(),
//                originalColour.getGreen(),
//                originalColour.getBlue(), null);
//
//        Color highlight = Color.getHSBColor(hsbVals[0], hsbVals[1], FACTOR * (1f + hsbVals[2]));
////            Color shadow = Color.getHSBColor( hsbVals[0], hsbVals[1], 0.5f * hsbVals[2] );
//
//        return highlight;
//
//
////        return new Color(Math.min((int) (color.getRed() * (1 / FACTOR)), 255),
////                Math.min((int) (color.getGreen() * (1 / FACTOR)), 255),
////                Math.min((int) (color.getBlue() * (1 / FACTOR)), 255));
//    }
//
//    public static Color darker(Color color, float FACTOR) {
//        return new Color(Math.max((int) (color.getRed() * FACTOR), 0),
//                Math.max((int) (color.getGreen() * FACTOR), 0),
//                Math.max((int) (color.getBlue() * FACTOR), 0));
//    }
//
//
//    static Image iconToImage(Icon icon) {
//       if (icon instanceof ImageIcon) {
//          return ((ImageIcon)icon).getImage();
//       }
//       else {
//          int w = icon.getIconWidth();
//          int h = icon.getIconHeight();
//          GraphicsEnvironment ge =
//            GraphicsEnvironment.getLocalGraphicsEnvironment();
//          GraphicsDevice gd = ge.getDefaultScreenDevice();
//          GraphicsConfiguration gc = gd.getDefaultConfiguration();
//          BufferedImage image = gc.createCompatibleImage(w, h);
//          Graphics2D g = image.createGraphics();
//          icon.paintIcon(null, g, 0, 0);
//          g.dispose();
//          return image;
//       }
//     }


//    public static Icon paint(Icon in) {
//       Image myImage = iconToImage(in);
//       BufferedImage bufferedImage = new BufferedImage(myImage.getWidth(null), myImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
//
//        GraphicsEnvironment ge =
//                GraphicsEnvironment.getLocalGraphicsEnvironment();
//              GraphicsDevice gd = ge.getDefaultScreenDevice();
//              GraphicsConfiguration gc = gd.getDefaultConfiguration();
//              BufferedImage image = gc.createCompatibleImage(in.getIconWidth(), in.getIconHeight());
//              Graphics2D g = image.createGraphics();
//
//
//       Graphics gb = bufferedImage.getGraphics();
//       gb.drawImage(myImage, 0, 0, null);
//       gb.dispose();
//
//       AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
//       tx.translate(-myImage.getWidth(null), 0);
//       AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
//       bufferedImage = op.filter(bufferedImage, null);
//
//
//
//       g2d.drawImage(myImage, 10, 10, null);
//       g2d.drawImage(bufferedImage, null, 300, 10);
//     }

    /**
     * http://stackoverflow.com/questions/2234476/how-to-detect-the-current-display-with-java
     *
     * @param myWindow
     * @return
     */
    public static GraphicsDevice getCurrentScreen(JFrame myWindow) {

        GraphicsConfiguration config = myWindow.getGraphicsConfiguration();
        return config.getDevice();
//        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        // AFAIK - there are no guarantees that screen devices are in order...
//        // but they have been on every system I've used.
//        GraphicsDevice[] allScreens = env.getScreenDevices();
//        int myScreenIndex = -1;
//        for (int i = 0; i < allScreens.length; i++) {
//            if (allScreens[i].equals(myScreen))
//            {
//                myScreenIndex = i;
//                break;
//            }
//        }
////        System.out.println("window is on screen" + myScreenIndex);
//        return myScreenIndex;
    }

    public static Rectangle getScreenSize(GraphicsDevice myScreen) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

        // AFAIK - there are no guarantees that screen devices are in order...
        // but they have been on every system I've used.
        GraphicsDevice[] allScreens = env.getScreenDevices();
        int myScreenIndex = -1;
        for (int i = 0; i < allScreens.length; i++) {
            if (allScreens[i].equals(myScreen)) {
                myScreenIndex = i;
                break;
            }
        }
//        System.out.println("window is on screen" + myScreenIndex);

        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[myScreenIndex].getDefaultConfiguration().getBounds();


    }


}


