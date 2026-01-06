package io.calcfluent;

import org.junit.Test;
import java.math.BigDecimal;
import static org.junit.Assert.*;

/**
 * Unit tests for CalculatorV2 lambda-based syntax
 *
 * @author azataiot
 * @since 2025.12
 */
public class CalculatorV2Test {

    @Test
    public void testBasicAddition() {
        BigDecimal result = CalculatorV2.startWith(10).add(5).result();
        assertEquals(new BigDecimal("15"), result);
    }

    @Test
    public void testBasicSubtraction() {
        BigDecimal result = CalculatorV2.startWith(20).subtract(3).result();
        assertEquals(new BigDecimal("17"), result);
    }

    @Test
    public void testBasicMultiplication() {
        BigDecimal result = CalculatorV2.startWith(6).multiply(7).result();
        assertEquals(new BigDecimal("42"), result);
    }

    @Test
    public void testBasicDivision() {
        BigDecimal result = CalculatorV2.startWith(100).divide(3, 2).result();
        assertEquals(new BigDecimal("33.33"), result);
    }

    @Test
    public void testGroupedAddition() {
        // 10 + (2 * 3) = 16
        BigDecimal result = CalculatorV2.startWith(10)
            .add(grp -> grp.with(2).multiply(3))
            .result();
        assertEquals(new BigDecimal("16"), result);
    }

    @Test
    public void testGroupedSubtraction() {
        // 100 - (50 / 2) = 75
        BigDecimal result = CalculatorV2.startWith(100)
            .subtract(grp -> grp.with(50).divide(2, 0))
            .result();
        assertEquals(new BigDecimal("75"), result);
    }

    @Test
    public void testGroupedMultiplication() {
        // 5 * (10 + 2) = 60
        BigDecimal result = CalculatorV2.startWith(5)
            .multiply(grp -> grp.with(10).add(2))
            .result();
        assertEquals(new BigDecimal("60"), result);
    }

    @Test
    public void testGroupedDivision() {
        // 100 / (10 + 5) = 6.67
        BigDecimal result = CalculatorV2.startWith(100)
            .divide(grp -> grp.with(10).add(5), 2)
            .result();
        assertEquals(new BigDecimal("6.67"), result);
    }

    @Test
    public void testNestedGroups() {
        // 10 + ((2 + 3) * 4) = 30
        BigDecimal result = CalculatorV2.startWith(10)
            .add(outer -> outer
                .multiply(inner -> inner.with(2).add(3))
                .multiply(4)
            )
            .result();
        assertEquals(new BigDecimal("30"), result);
    }

    @Test
    public void testDeeplyNestedGroups() {
        // 100 + (20 * (15 / (3 + 2))) = 160
        BigDecimal result = CalculatorV2.startWith(100)
            .add(grp1 -> grp1
                .with(20)
                .multiply(grp2 -> grp2
                    .with(15)
                    .divide(grp3 -> grp3.with(3).add(2), 2)
                )
            )
            .result();
        assertEquals(new BigDecimal("160.00"), result);
    }

    @Test
    public void testComplexExpression() {
        // (1000 - (100 * 0.1)) * 0.9 + (50 * 0.05) * 1.1 rounded to 2 decimals
        BigDecimal result = CalculatorV2.startWith(1000)
            .subtract(grp -> grp.with(100).multiply(new BigDecimal("0.1")))
            .multiply(new BigDecimal("0.9"))
            .add(grp -> grp.with(50).multiply(new BigDecimal("0.05")))
            .multiply(new BigDecimal("1.1"))
            .round(2)
            .result();
        assertEquals(new BigDecimal("982.85"), result);
    }

    @Test
    public void testNegation() {
        BigDecimal result = CalculatorV2.startWith(5).subtract(10).negate().result();
        assertEquals(new BigDecimal("5"), result);
    }

    @Test
    public void testAbsoluteValue() {
        BigDecimal result = CalculatorV2.startWith(5).subtract(10).abs().result();
        assertEquals(new BigDecimal("5"), result);
    }

    @Test
    public void testRounding() {
        BigDecimal result = CalculatorV2.startWith(new BigDecimal("10.12345"))
            .multiply(2)
            .round(2)
            .result();
        assertEquals(new BigDecimal("20.25"), result);
    }

    @Test
    public void testChainedOperations() {
        // (10 + 5) * 2 - 3 = 27
        BigDecimal result = CalculatorV2.startWith(10)
            .add(5)
            .multiply(2)
            .subtract(3)
            .result();
        assertEquals(new BigDecimal("27"), result);
    }

    @Test
    public void testMultipleGroups() {
        // (10 + (2 * 3)) - (5 + 1) = 10
        BigDecimal result = CalculatorV2.startWith(10)
            .add(grp -> grp.with(2).multiply(3))
            .subtract(grp -> grp.with(5).add(1))
            .result();
        assertEquals(new BigDecimal("10"), result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectsFloatInput() {
        CalculatorV2.startWith(10.5); // Should throw exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectsDoubleInput() {
        CalculatorV2.startWith(10).add(5.5); // Should throw exception
    }

    @Test(expected = IllegalStateException.class)
    public void testGroupWithoutInitialization() {
        // Should fail because we try to add without setting initial value
        CalculatorV2.startWith(10)
            .add(grp -> grp.add(5)) // Missing .with() initialization
            .result();
    }

    @Test
    public void testStartWithZero() {
        BigDecimal result = CalculatorV2.startWith()
            .add(10)
            .multiply(2)
            .result();
        assertEquals(new BigDecimal("20"), result);
    }

    // Edge cases

    @Test
    public void testDivisionPrecision() {
        // 1 / 3 with different precisions
        BigDecimal result2 = CalculatorV2.startWith(1).divide(3, 2).result();
        BigDecimal result5 = CalculatorV2.startWith(1).divide(3, 5).result();

        assertEquals(new BigDecimal("0.33"), result2);
        assertEquals(new BigDecimal("0.33333"), result5);
    }

    @Test
    public void testZeroOperations() {
        BigDecimal result = CalculatorV2.startWith(0)
            .add(10)
            .subtract(5)
            .result();
        assertEquals(new BigDecimal("5"), result);
    }

    @Test
    public void testNegativeNumbers() {
        BigDecimal result = CalculatorV2.startWith(-10)
            .add(-5)
            .result();
        assertEquals(new BigDecimal("-15"), result);
    }
}
