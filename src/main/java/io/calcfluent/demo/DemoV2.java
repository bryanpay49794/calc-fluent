package io.calcfluent.demo;

import io.calcfluent.CalculatorV2;
import java.math.BigDecimal;

/**
 * Demo showing the improved lambda-based syntax for CalculatorV2
 * 演示 CalculatorV2 (基于 lambda) 的语法
 *
 * @author azataiot
 * @since 2025.12
 */
public class DemoV2 {

    public static void main(String[] args) {
        System.out.println("CalculatorV2 Lambda Syntax Demo\n");

        example1_BasicGrouping();
        example2_NestedGrouping();
        example3_ComplexFinancialFormula();
        example4_ComparisonWithV1();

        System.out.println("\nAll examples completed!");
    }

    /**
     * Example 1: Basic grouping
     * 示例 1：基础分组运算
     * Formula: 10 + (2 * 3) = 16
     * 公式：10 + (2 * 3) = 16
     */
    static void example1_BasicGrouping() {
        System.out.println("Example 1: 10 + (2 * 3) = 16");

        BigDecimal result = CalculatorV2.startWith(10)
            .add(grp -> grp
                .with(2)
                .multiply(3)
            )
            .result();

        System.out.println("Result: " + result);
        assert result.equals(new BigDecimal("16"));
        System.out.println("Passed\n");
    }

    /**
     * Example 2: Nested grouping
     * 示例 2：嵌套分组运算
     * Formula: 10 + ((2 + 3) * 4) = 30
     * 公式：10 + ((2 + 3) * 4) = 30
     */
    static void example2_NestedGrouping() {
        System.out.println("Example 2: 10 + ((2 + 3) * 4) = 30");

        // Method 1: Flat grouping (left-to-right within group)
        // 方法 1：平铺分组（组内从左到右）
        BigDecimal result = CalculatorV2.startWith(10)
            .add(grp -> grp
                .with(2)
                .add(3)
                .multiply(4)
            )
            .result();

        // Method 2: Explicit nesting - truly nested groups
        // 方法 2：显式嵌套 - 真正的嵌套分组
        BigDecimal result2 = CalculatorV2.startWith(10)
            .add(outer -> outer
                .multiply(inner -> inner
                    .with(2)
                    .add(3)
                )
                .multiply(4)
            )
            .result();

        System.out.println("Result (flat): " + result);
        System.out.println("Result (nested): " + result2);
        assert result.equals(new BigDecimal("30"));
        assert result2.equals(new BigDecimal("30"));
        System.out.println("Passed\n");
    }

    /**
     * Example 3: Complex financial formula
     * 示例 3：复杂金融计算公式
     * Formula: (orderAmount - (threshold * rate)) * vipDiscount + (inviteBase * inviteRate) * taxRate
     * 公式：(订单金额 - (阈值 * 费率)) * VIP折扣 + (邀请基数 * 邀请费率) * 税率
     */
    static void example3_ComplexFinancialFormula() {
        System.out.println("Example 3: Complex financial calculation");
        System.out.println("Formula: (orderAmount - (threshold * rate)) * vipDiscount + (inviteBase * inviteRate) * taxRate");

        // Variable definitions / 变量定义
        BigDecimal orderAmount = new BigDecimal("1000");   // Order amount / 订单金额
        BigDecimal threshold = new BigDecimal("100");      // Threshold / 阈值
        BigDecimal rate = new BigDecimal("0.1");           // Rate / 费率
        BigDecimal vipDiscount = new BigDecimal("0.9");    // VIP discount / VIP折扣
        BigDecimal inviteBase = new BigDecimal("50");      // Invite base / 邀请基数
        BigDecimal inviteRate = new BigDecimal("0.05");    // Invite rate / 邀请费率
        BigDecimal taxRate = new BigDecimal("1.1");        // Tax rate / 税率

        BigDecimal result = CalculatorV2.startWith(orderAmount)
            .subtract(grp -> grp                           // Subtract grouped discount / 减去分组折扣
                .with(threshold)
                .multiply(rate)
            )
            .multiply(vipDiscount)                         // Apply VIP discount / 应用VIP折扣
            .add(grp -> grp                                // Add grouped invite bonus / 加上分组邀请奖金
                .with(inviteBase)
                .multiply(inviteRate)
            )
            .multiply(taxRate)                             // Apply tax / 应用税率
            .round(2)                                      // Round to 2 decimals / 四舍五入到2位小数
            .result();

        System.out.println("Result: " + result);
        System.out.println("Calculation completed\n");
    }

    /**
     * Example 4: Side-by-side comparison with V1 syntax
     * 示例 4：与 V1 语法的并排对比
     */
    static void example4_ComparisonWithV1() {
        System.out.println("Example 4: Syntax comparison");
        System.out.println("Formula: 100 + (50 / 3) rounded to 2 decimals\n");

        // V1 syntax (old) / V1 语法（旧）:
        System.out.println("V1 Syntax:");
        System.out.println("Calculator.startWith(100)");
        System.out.println("    .addParenthesisThen(50)");
        System.out.println("    .divide(3, 2)");
        System.out.println("    .rightParenthesis()");
        System.out.println("    .result();\n");

        // V2 syntax (new) / V2 语法（新）:
        System.out.println("V2 Syntax:");
        System.out.println("CalculatorV2.startWith(100)");
        System.out.println("    .add(grp -> grp");
        System.out.println("        .with(50)");
        System.out.println("        .divide(3, 2)");
        System.out.println("    )");
        System.out.println("    .result();\n");

        BigDecimal result = CalculatorV2.startWith(100)
            .add(grp -> grp
                .with(50)
                .divide(3, 2)
            )
            .result();

        System.out.println("Result: " + result + " (expected: 116.67)");
        assert result.equals(new BigDecimal("116.67"));
        System.out.println("Passed\n");
    }
}
