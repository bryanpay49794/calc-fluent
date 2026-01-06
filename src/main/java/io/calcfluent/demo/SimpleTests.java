package io.calcfluent.demo;

import io.calcfluent.CalculatorV2;
import java.math.BigDecimal;

/**
 * Simple assertion-based tests for CalculatorV2
 *
 * @author azataiot
 * @since 2025.12
 */
public class SimpleTests {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("Running CalculatorV2 Tests...\n");

        testBasicOperations();
        testGroupedOperations();
        testNestedGroups();
        testComplexExpressions();
        testEdgeCases();

        System.out.println("\n" + "=".repeat(70));
        System.out.println("Test Results: " + passed + " passed, " + failed + " failed");
        System.out.println("=".repeat(70));

        if (failed > 0) {
            System.exit(1);
        }
    }

    static void testBasicOperations() {
        System.out.println("Testing basic operations...");

        assertEqual("10 + 5",
            CalculatorV2.startWith(10).add(5).result(),
            new BigDecimal("15"));

        assertEqual("20 - 3",
            CalculatorV2.startWith(20).subtract(3).result(),
            new BigDecimal("17"));

        assertEqual("6 * 7",
            CalculatorV2.startWith(6).multiply(7).result(),
            new BigDecimal("42"));

        assertEqual("100 / 3 (2 decimals)",
            CalculatorV2.startWith(100).divide(3, 2).result(),
            new BigDecimal("33.33"));

        assertEqual("abs(-5)",
            CalculatorV2.startWith(-5).abs().result(),
            new BigDecimal("5"));

        assertEqual("negate(5)",
            CalculatorV2.startWith(5).negate().result(),
            new BigDecimal("-5"));

        System.out.println();
    }

    static void testGroupedOperations() {
        System.out.println("Testing grouped operations...");

        assertEqual("10 + (2 * 3)",
            CalculatorV2.startWith(10)
                .add(grp -> grp.with(2).multiply(3))
                .result(),
            new BigDecimal("16"));

        assertEqual("100 - (50 / 2)",
            CalculatorV2.startWith(100)
                .subtract(grp -> grp.with(50).divide(2, 0))
                .result(),
            new BigDecimal("75"));

        assertEqual("5 * (10 + 2)",
            CalculatorV2.startWith(5)
                .multiply(grp -> grp.with(10).add(2))
                .result(),
            new BigDecimal("60"));

        assertEqual("100 / (10 + 5)",
            CalculatorV2.startWith(100)
                .divide(grp -> grp.with(10).add(5), 2)
                .result(),
            new BigDecimal("6.67"));

        System.out.println();
    }

    static void testNestedGroups() {
        System.out.println("Testing nested groups...");

        assertEqual("10 + ((2 + 3) * 4)",
            CalculatorV2.startWith(10)
                .add(outer -> outer
                    .multiply(inner -> inner.with(2).add(3))
                    .multiply(4)
                )
                .result(),
            new BigDecimal("30"));

        assertEqual("100 + (20 * (15 / (3 + 2)))",
            CalculatorV2.startWith(100)
                .add(grp1 -> grp1
                    .with(20)
                    .multiply(grp2 -> grp2
                        .with(15)
                        .divide(grp3 -> grp3.with(3).add(2), 2)
                    )
                )
                .result(),
            new BigDecimal("160.00"));

        System.out.println();
    }

    static void testComplexExpressions() {
        System.out.println("Testing complex expressions...");

        assertEqual("Financial calculation",
            CalculatorV2.startWith(1000)
                .subtract(grp -> grp.with(100).multiply(new BigDecimal("0.1")))
                .multiply(new BigDecimal("0.9"))
                .add(grp -> grp.with(50).multiply(new BigDecimal("0.05")))
                .multiply(new BigDecimal("1.1"))
                .round(2)
                .result(),
            new BigDecimal("982.85"));

        assertEqual("Multiple groups: (10 + (2 * 3)) - (5 + 1)",
            CalculatorV2.startWith(10)
                .add(grp -> grp.with(2).multiply(3))
                .subtract(grp -> grp.with(5).add(1))
                .result(),
            new BigDecimal("10"));

        System.out.println();
    }

    static void testEdgeCases() {
        System.out.println("Testing edge cases...");

        assertEqual("Start with zero",
            CalculatorV2.startWith().add(10).multiply(2).result(),
            new BigDecimal("20"));

        assertEqual("Negative numbers",
            CalculatorV2.startWith(-10).add(-5).result(),
            new BigDecimal("-15"));

        assertEqual("Division precision (1/3 to 5 decimals)",
            CalculatorV2.startWith(1).divide(3, 5).result(),
            new BigDecimal("0.33333"));

        // Test float/double rejection
        try {
            CalculatorV2.startWith(10.5);
            System.out.println("  ❌ Should reject Double input");
            failed++;
        } catch (IllegalArgumentException e) {
            System.out.println("  ✓ Correctly rejects Double input");
            passed++;
        }

        // Test uninitialized group
        try {
            CalculatorV2.startWith(10)
                .add(grp -> grp.add(5)) // Missing .with()
                .result();
            System.out.println("  ❌ Should reject uninitialized group");
            failed++;
        } catch (IllegalStateException e) {
            System.out.println("  ✓ Correctly rejects uninitialized group");
            passed++;
        }

        System.out.println();
    }

    static void assertEqual(String testName, BigDecimal actual, BigDecimal expected) {
        if (actual.stripTrailingZeros().equals(expected.stripTrailingZeros())) {
            System.out.println("  ✓ " + testName);
            passed++;
        } else {
            System.out.println("  ❌ " + testName);
            System.out.println("     Expected: " + expected);
            System.out.println("     Actual:   " + actual);
            failed++;
        }
    }
}
