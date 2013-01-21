/*
 *  MyPrintableObject.java: Printable-Hilfsklasse für J2SE-Programmierbeispiele
 *  http://www.torsten-horn.de/techdocs/java-print.htm
 */
package printer;

import Main.Main;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;
import tools.Tools;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.print.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class TKLabel implements Printable {

    private int iResMul = 8;  // 1 = 72 dpi; 4 = 288 dpi
    private HashMap data;

    public TKLabel(HashMap data) {
        this.data = data;
    }

//            hm.put("produkt.bezeichnung", "Cologran");
//        hm.put("in-store-prefix", "20");
//        hm.put("vorrat.id", "6743");
//        hm.put("vorrat.eingang", new Date());
//        hm.put("user", "tloehr");
    @Override
    public int print(Graphics g, PageFormat pf, int iPage)
            throws PrinterException {

        if (data == null){
            throw new PrinterException("keine Daten");
        }

        EAN13Bean bean = new EAN13Bean();

        final int NORMAL_SIZE = 12;
        final int LARGE_SIZE = 18;
        final int HUGE_SIZE = 36;
        final double PNT_MM = 25.4 / 72.;
        if (0 != iPage) {
            return NO_SUCH_PAGE;
        }
        try {
            int iPosX = 1;
            int iPosY = 1;
            int iAddYN = NORMAL_SIZE * 3 / 2 * iResMul;
            int iAddYL = LARGE_SIZE * 3 / 2 * iResMul;
            int iAddYH = HUGE_SIZE * 3 / 2 * iResMul;
            int iWdth = (int) Math.round(pf.getImageableWidth() * iResMul) - 3;
            int iHght = (int) Math.round(pf.getImageableHeight() * iResMul) - 3;
            //int iCrcl = Math.min(iWdth, iHght) - 4 * iResMul;
            Graphics2D g2 = (Graphics2D) g;
            PrinterJob prjob = ((PrinterGraphics) g2).getPrinterJob();
            g2.translate(pf.getImageableX(), pf.getImageableY());
            g2.scale(1.0 / iResMul, 1.0 / iResMul);
            g2.setColor(Color.black);
            g2.drawRect(iPosX, iPosY, iWdth, iHght);

            //Configure the barcode generator
            bean.setModuleWidth(UnitConv.in2mm(8.0f / 200)); //makes the narrow bar
            bean.setBarHeight(500f);
            bean.setFontSize(NORMAL_SIZE);

            bean.doQuietZone(true);

            boolean antiAlias = false;
            BitmapCanvasProvider canvasP = new BitmapCanvasProvider(200, BufferedImage.TYPE_BYTE_BINARY, antiAlias, 0);
            BitmapCanvasProvider canvasH = new BitmapCanvasProvider(200, BufferedImage.TYPE_BYTE_BINARY, antiAlias, 90);

            String eancode = data.get("system.in-store-prefix").toString() + Tools.padL(data.get("vorrat.id").toString(), 10, "0");
            bean.generateBarcode(canvasP, eancode);
            bean.generateBarcode(canvasH, eancode);
            canvasP.finish();
            canvasH.finish();


            iPosX += iAddYN;
            iPosY += iAddYN / 2;
            g2.setFont(new Font("SansSerif", Font.PLAIN, LARGE_SIZE * iResMul));
            g2.drawString(data.get("produkt.bezeichnung").toString(), iPosX, iPosY += iAddYL);
            g2.setFont(new Font("SansSerif", Font.PLAIN, NORMAL_SIZE * iResMul));

            g2.drawString("Lieferant: " + data.get("vorrat.lieferant").toString(), iPosX, iPosY += iAddYN);
            if (data.containsKey("produkt.gtin")){
                 g2.drawString("GTIN: " + data.get("produkt.gtin").toString(), iPosX, iPosY += iAddYN);
            }

            g2.drawString("Eingebucht am: " + DateFormat.getDateInstance().format((Date) data.get("vorrat.eingang")), iPosX, iPosY += iAddYN);
            g2.drawString("Eingebucht von: " + data.get("vorrat.userlang").toString(), iPosX, iPosY += iAddYN);
            g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, HUGE_SIZE * iResMul));
            g2.drawString(data.get("vorrat.id").toString(), iPosX, iPosY += iAddYH);

            g2.drawImage(canvasP.getBufferedImage(), iWdth / 2 - canvasP.getBufferedImage().getWidth() / 2, iPosY += iAddYL, new ImageObserver() {

                public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            });

            g2.drawImage(canvasH.getBufferedImage(), iPosX, iHght / 2 - canvasH.getBufferedImage().getHeight() / 2, new ImageObserver() {

                public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            });

           
        } catch (Exception ex) {
            Main.logger.fatal(ex);
            throw new PrinterException(ex.getMessage());
        }
        return PAGE_EXISTS;
    }

}
