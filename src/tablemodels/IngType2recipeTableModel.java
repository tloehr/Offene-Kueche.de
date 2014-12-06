package tablemodels;

import entity.*;

import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tloehr on 19.09.14.
 */
public class IngType2recipeTableModel extends DefaultTableModel {

    public static final int COL_BEZEICHNUNG = 0;
    public static final int COL_UNIT = 1;
    public static final int COL_PLANNED_AMOUNT = 2;
    public static final int COL_ACTUAL_AMOUNT = 3;
    private Object[] colID = new Object[]{"Bezeichnung", "Einheit", "Nötige Menge", "Zugeordnete Menge"};

    //    private final Recipes recipe;
    private List<Ingtypes2Recipes> data;

    public List<Ingtypes2Recipes> getData() {
        return data;
    }

    public HashMap<IngTypes, BigDecimal> amountMap;

    public IngType2recipeTableModel(List<Ingtypes2Recipes> its, List<Stock> stocks) {

        this.data = new ArrayList<Ingtypes2Recipes>(its);
        amountMap = new HashMap<IngTypes, BigDecimal>();
        setColumnIdentifiers(colID);
        update(stocks);
    }

    @Override
    public void removeRow(int row) {
        data.remove(row);
        fireTableRowsDeleted(row, row);
    }


    public void remove(Ingtypes2Recipes ingtypes2Recipe) {
        if (!data.contains(ingtypes2Recipe)) return;
        removeRow(data.indexOf(ingtypes2Recipe));
    }

    public void add(IngTypes ingType, Recipes recipe) {
        if (!contains(ingType)) {
            Ingtypes2Recipes newIngType2Recipe = new Ingtypes2Recipes(recipe, ingType);
            amountMap.put(ingType, BigDecimal.ZERO);
            data.add(newIngType2Recipe);
            fireTableRowsInserted(data.size() - 1, data.size() - 1);
        }
    }

//    public void update(Produkte produkt) {
//
//        ArrayList<Stock> myStocks = new ArrayList<Stock>();
//
//        for (Stock stock : data) {
//            if (stock.getProdukt().equals(produkt)) {
//                stock.setProdukt(produkt);
//                myStocks.add(stock);
//            }
//        }
//
//        for (Stock stock : myStocks) {
//            update(stock);
//        }
//
//        myStocks.clear();
//    }


//    public void update(Stock stock) {
//        if (!data.contains(stock)) {
//            add(stock);
//            return;
//        }
//        int row = data.indexOf(stock);
//        data.set(row, stock);
//        fireTableRowsUpdated(row, row);
//    }


    public boolean contains(IngTypes ingType) {
        for (Ingtypes2Recipes ingtypes2Recipes : data) {
            if (ingtypes2Recipes.getIngType().equals(ingType)) {
                return true;
            }
        }
        return false;
    }

    public void update(List<Stock> stocks) {
        amountMap.clear();
        for (Stock stock : stocks) {
            if (amountMap.containsKey(stock.getProdukt().getIngTypes())) {
//                if (!contains(stock.getProdukt().getIngTypes())) {
//                    add(new Ingtypes2Recipes(recipe, stock.getProdukt().getIngTypes()));
//                }
//                amountMap.put(stock.getProdukt().getIngTypes(), BigDecimal.ZERO);

                BigDecimal amount = amountMap.get(stock.getProdukt().getIngTypes()).add(StockTools.getSumme(stock));
                amountMap.put(stock.getProdukt().getIngTypes(), amount);
            }
        }
        fireTableDataChanged();
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {

        switch (column) {

            case COL_BEZEICHNUNG: {
                data.get(row).setIngType((IngTypes) aValue);
                break;
            }
            case COL_PLANNED_AMOUNT: {
                data.get(row).setAmount((BigDecimal) aValue);
                break;
            }

            default: {
                // nothing
            }
        }

        fireTableCellUpdated(row, column);

//        EntityManager em = Main.getEMF().createEntityManager();
//        try {
//            em.getTransaction().begin();
//
//            final Ingtypes2Recipes myIngType2recipe = em.merge(data.get(row));
//
//            switch (column) {
//
//                case COL_BEZEICHNUNG: {
//                    if (!aValue.toString().isEmpty()) {
//                        myIngType2recipe.setIngType((IngTypes) aValue);
//                    }
//                    break;
//                }
//                case COL_PLANNED_AMOUNT: {
//                    data.get(row).setAmount((BigDecimal) aValue);
//                    break;
//                }
//
//                default: {
//                    // nothing
//                }
//            }
//
//            em.getTransaction().commit();
//            data.set(row, myIngType2recipe);
//            fireTableCellUpdated(row, column);
//
//        } catch (OptimisticLockException ole) {
//            Main.warn(ole);
//            if (em.getTransaction().isActive()) {
//                em.getTransaction().rollback();
//            }
//
//            data.clear();
////            ((FrmDesktop) Main.getMainframe()).getTypesFrame().reload();
//        } catch (Exception e) {
//            if (em.getTransaction().isActive()) {
//                em.getTransaction().rollback();
//            }
//            Main.fatal(e);
//        } finally {
//            em.close();
//        }
    }

    @Override
    public int getRowCount() {
        int rowcount = 0;

        if (data != null) {
            rowcount = data.size();
        }
        return rowcount;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Class c;
        switch (columnIndex) {

            case COL_BEZEICHNUNG: {
                c = IngTypes.class;
                break;
            }
            case COL_PLANNED_AMOUNT: {
                c = BigDecimal.class;
                break;
            }
            case COL_ACTUAL_AMOUNT: {
                c = BigDecimal.class;
                break;
            }
            default: {
                c = String.class;
            }

        }
        return c;
    }


    @Override
    public Object getValueAt(int row, int column) {
        Object value;
        Ingtypes2Recipes ingType2recipe = data.get(row);
        switch (column) {
            case COL_BEZEICHNUNG: {
                value = ingType2recipe.getIngType();
                break;
            }
            case COL_PLANNED_AMOUNT: {
                value = ingType2recipe.getAmount();
                break;
            }
            case COL_ACTUAL_AMOUNT: {
                value = amountMap.get(ingType2recipe.getIngType());
                break;
            }
            case COL_UNIT: {
                value = IngTypesTools.EINHEIT[ingType2recipe.getIngType().getEinheit()];
                break;
            }
            default: {
                value = null;
            }
        }
        return value;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == COL_PLANNED_AMOUNT || column == COL_BEZEICHNUNG;
    }

}
