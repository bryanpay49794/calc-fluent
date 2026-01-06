package io.calcfluent.demo;

import io.calcfluent.CalculatorV2;
import java.math.BigDecimal;

/**
 * Quick side-by-side comparison showing how V2 syntax improves readability
 *
 * @author azataiot
 * @since 2025.12
 */
public class QuickComparison {

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("SYNTAX COMPARISON: Readability Improvements");
        System.out.println("=".repeat(70));
        System.out.println();

        compareSimpleGrouping();
        compareComplexFinancialFormula();
        showNestedGroupingPower();
    }

    static void compareSimpleGrouping() {
        System.out.println("EXAMPLE 1: Simple Grouping");
        System.out.println("-".repeat(70));
        System.out.println("Formula: 10 + (2 * 3) = 16\n");

        System.out.println("OLD Syntax (V1):");
        System.out.println("Calculator.startWith(10)");
        System.out.println("    .addParenthesisThen(2)");
        System.out.println("    .multiply(3)");
        System.out.println("    .rightParenthesis()  // ⚠️ Easy to forget!");
        System.out.println("    .result();\n");

        System.out.println("NEW Syntax (V2):");
        System.out.println("CalculatorV2.startWith(10)");
        System.out.println("    .add(grp -> grp");
        System.out.println("        .with(2)");
        System.out.println("        .multiply(3)");
        System.out.println("    )  // Automatic scope closure");
        System.out.println("    .result();\n");

        BigDecimal result = CalculatorV2.startWith(10)
            .add(grp -> grp.with(2).multiply(3))
            .result();

        System.out.println("Result: " + result);
        System.out.println("\n");
    }

    static void compareComplexFinancialFormula() {
        System.out.println("EXAMPLE 2: Real-World Financial Calculation");
        System.out.println("-".repeat(70));
        System.out.println("Formula: (amount - (threshold * rate)) * discount + (base * rate) * tax\n");

        BigDecimal amount = new BigDecimal("1000");
        BigDecimal threshold = new BigDecimal("100");
        BigDecimal rate = new BigDecimal("0.1");
        BigDecimal discount = new BigDecimal("0.9");
        BigDecimal base = new BigDecimal("50");
        BigDecimal inviteRate = new BigDecimal("0.05");
        BigDecimal tax = new BigDecimal("1.1");

        System.out.println("OLD Syntax (V1):");
        System.out.println("Calculator.startWith(amount)");
        System.out.println("    .subtractParenthesisThen(threshold)");
        System.out.println("    .multiply(rate)");
        System.out.println("    .rightParenthesis()");
        System.out.println("    .multiply(discount)");
        System.out.println("    .addParenthesisThen(base)");
        System.out.println("    .multiply(inviteRate)");
        System.out.println("    .rightParenthesis()");
        System.out.println("    .multiply(tax)");
        System.out.println("    .round(2)");
        System.out.println("    .result();\n");

        System.out.println("NEW Syntax (V2):");
        System.out.println("CalculatorV2.startWith(amount)");
        System.out.println("    .subtract(grp -> grp");
        System.out.println("        .with(threshold)");
        System.out.println("        .multiply(rate)");
        System.out.println("    )");
        System.out.println("    .multiply(discount)");
        System.out.println("    .add(grp -> grp");
        System.out.println("        .with(base)");
        System.out.println("        .multiply(inviteRate)");
        System.out.println("    )");
        System.out.println("    .multiply(tax)");
        System.out.println("    .round(2)");
        System.out.println("    .result();\n");

        BigDecimal result = CalculatorV2.startWith(amount)
            .subtract(grp -> grp.with(threshold).multiply(rate))
            .multiply(discount)
            .add(grp -> grp.with(base).multiply(inviteRate))
            .multiply(tax)
            .round(2)
            .result();

        System.out.println("Result: $" + result);
        System.out.println("\n");
    }

    static void showNestedGroupingPower() {
        System.out.println("EXAMPLE 3: Deeply Nested Operations");
        System.out.println("-".repeat(70));
        System.out.println("Formula: 100 + (20 * (15 / (3 + 2)))\n");

        System.out.println("NEW Syntax (V2) with natural nesting:");
        System.out.println("CalculatorV2.startWith(100)");
        System.out.println("    .add(grp1 -> grp1");
        System.out.println("        .with(20)");
        System.out.println("        .multiply(grp2 -> grp2");
        System.out.println("            .with(15)");
        System.out.println("            .divide(grp3 -> grp3");
        System.out.println("                .with(3)");
        System.out.println("                .add(2)");
        System.out.println("            , 2)");
        System.out.println("        )");
        System.out.println("    )");
        System.out.println("    .result();\n");

        BigDecimal result = CalculatorV2.startWith(100)
            .add(grp1 -> grp1
                .with(20)
                .multiply(grp2 -> grp2
                    .with(15)
                    .divide(grp3 -> grp3
                        .with(3)
                        .add(2)
                    , 2)
                )
            )
            .result();

        System.out.println("Result: " + result);
        System.out.println("\n");

        System.out.println("=".repeat(70));
        System.out.println("KEY ADVANTAGES:");
        System.out.println("=".repeat(70));
        System.out.println("✅ No manual parenthesis matching");
        System.out.println("✅ Visual structure matches mathematical precedence");
        System.out.println("✅ IDE autocomplete works inside lambda scopes");
        System.out.println("✅ Impossible to forget .rightParenthesis()");
        System.out.println("✅ Easier code review - clearer intent");
        System.out.println("✅ Aligns with modern Java functional style");
        System.out.println("=".repeat(70));
    }
}
