/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package exceptions;

import java.math.BigDecimal;

/**
 *
 * @author tloehr
 */
public class OutOfRangeException extends Exception{
    BigDecimal validMin, validMax, value;

    public OutOfRangeException(BigDecimal validMin, BigDecimal validMax, BigDecimal value) {
        this.validMin = validMin;
        this.validMax = validMax;
        this.value = value;
    }

    public OutOfRangeException(int validMin, int validMax, int value) {
        this.validMin = new BigDecimal(validMin);
        this.validMax = new BigDecimal(value);
        this.value = new BigDecimal(value);
    }

        public OutOfRangeException(double validMin, double validMax, double value) {
        this.validMin = new BigDecimal(validMin);
        this.validMax = new BigDecimal(value);
        this.value = new BigDecimal(value);
    }

    public BigDecimal getValidMax() {
        return validMax;
    }

    public BigDecimal getValidMin() {
        return validMin;
    }

    public BigDecimal getValue() {
        return value;
    }
    
    
}
