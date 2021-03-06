package printer;

import java.awt.*;
import java.awt.print.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 29.07.11
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
public class MyPrintableObject implements Printable {
    public int iResMul = 4;  // 1 = 72 dpi; 4 = 288 dpi

    @Override
    public int print(Graphics g, PageFormat pf, int iPage)
            throws PrinterException {
        final int FONTSIZE = 12;
        final double PNT_MM = 25.4 / 72.;
        if (0 != iPage)
            return NO_SUCH_PAGE;
        try {
            int iPosX = 1;
            int iPosY = 1;
            int iAddY = FONTSIZE * 3 / 2 * iResMul;
            int iWdth = (int) Math.round(pf.getImageableWidth() * iResMul) - 3;
            int iHght = (int) Math.round(pf.getImageableHeight() * iResMul) - 3;
            int iCrcl = Math.min(iWdth, iHght) - 4 * iResMul;
            Graphics2D g2 = (Graphics2D) g;
            PrinterJob prjob = ((PrinterGraphics) g2).getPrinterJob();
            g2.translate(pf.getImageableX(), pf.getImageableY());
            g2.scale(1.0 / iResMul, 1.0 / iResMul);
            g2.setFont(new Font("arial", Font.PLAIN, FONTSIZE * iResMul));
            g2.setColor(Color.black);
            g2.drawRect(iPosX, iPosY, iWdth, iHght);
            g2.drawLine(iPosX, iHght / 2 + iWdth / 50, iPosX + iWdth, iHght / 2 - iWdth / 50);
            g2.drawLine(iPosX, iHght / 2 - iWdth / 50, iPosX + iWdth, iHght / 2 + iWdth / 50);
            g2.drawOval(iPosX + 2 * iResMul, iHght - iCrcl - 2 * iResMul, iCrcl, iCrcl);
            iPosX += iAddY;
            iPosY += iAddY / 2;
            g2.drawString("PrinterJob-UserName: " + prjob.getUserName(),
                    iPosX, iPosY += iAddY);
//      g2.drawString( "Betriebssystem: "
//                     + System.getProperty( "os.name" ) + " "
//                     + System.getProperty( "os.version" ), iPosX, iPosY+=iAddY );
//      g2.drawString( "Java-Version: JDK "
//                     + System.getProperty( "java.version" ), iPosX, iPosY+=iAddY );
//      g2.drawString( "Width/Height: "
//                     + dbldgt( pf.getWidth() )  + " / "
//                     + dbldgt( pf.getHeight() ) + " points = "
//                     + dbldgt( pf.getWidth()  * PNT_MM ) + " / "
//                     + dbldgt( pf.getHeight() * PNT_MM ) + " mm",
//                     iPosX, iPosY+=iAddY );
//      g2.drawString( "Imageable Width/Height: "
//                     + dbldgt( pf.getImageableWidth() )  + " / "
//                     + dbldgt( pf.getImageableHeight() ) + " points = "
//                     + dbldgt( pf.getImageableWidth()  * PNT_MM ) + " / "
//                     + dbldgt( pf.getImageableHeight() * PNT_MM ) + " mm",
//                     iPosX, iPosY+=iAddY );
//      g2.drawString( "Imageable X/Y: "
//                     + dbldgt( pf.getImageableX() ) + " / "
//                     + dbldgt( pf.getImageableY() ) + " points = "
//                     + dbldgt( pf.getImageableX() * PNT_MM ) + " / "
//                     + dbldgt( pf.getImageableY() * PNT_MM ) + " mm",
//                     iPosX, iPosY+=iAddY );
//      g2.drawString( "versuchte Druckauflösung: "
//                     + 72 * iResMul + " dpi", iPosX, iPosY+=iAddY );
        } catch (Exception ex) {
            throw new PrinterException(ex.getMessage());
        }
        return PAGE_EXISTS;
    }

    private static double dbldgt(double d) {
        return Math.round(d * 10.) / 10.;  // show one digit after point
    }
}
