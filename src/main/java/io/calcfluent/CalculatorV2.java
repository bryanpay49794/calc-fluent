package io.calcfluent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.function.Function;

/**
 * Alternative fluent BigDecimal calculator using lambda-based grouping syntax.
 * Provides cleaner, more readable syntax compared to explicit parenthesis methods.
 * <p>
 * 基于 lambda 分组语法的替代性流式 BigDecimal 计算器。
 * 相比显式括号方法，提供更清晰、更易读的语法。
 *
 * Example / 示例:
 * <pre>
 * BigDecimal result = CalculatorV2.startWith(orderAmount)
 *     .subtract(grp -> grp
 *         .with(fullReduceThreshold)
 *         .multiply(fullReduceRate)
 *     )
 *     .multiply(vipDiscount)
 *     .add(grp -> grp
 *         .with(inviteBase)
 *         .multiply(inviteRate)
 *     )
 *     .multiply(taxRate)
 *     .round(2)
 *     .result();
 * </pre>
 *
 * @author azataiot
 * @since 2025.12
 */
public class CalculatorV2 {

    /**
     * Start a calculation with an initial value
     * 使用初始值开始计算
     *
     * @param value initial value / 初始值
     * @return CalcChain instance for method chaining / 用于方法链式调用的 CalcChain 实例
     */
    public static CalcChain startWith(Number value) {
        return new CalcChain(toBigDecimal(value));
    }

    /**
     * Start a calculation with zero
     * 从零开始计算
     *
     * @return CalcChain instance for method chaining / 用于方法链式调用的 CalcChain 实例
     */
    public static CalcChain startWith() {
        return new CalcChain(BigDecimal.ZERO);
    }

    /**
     * Main calculation chain - supports fluent method chaining for BigDecimal operations
     * 主计算链 - 支持 BigDecimal 运算的流式方法链
     */
    public static class CalcChain {
        private final BigDecimal value;

        private CalcChain(BigDecimal value) {
            this.value = value;
        }

        // Simple operations / 简单运算

        /**
         * Addition / 加法
         * @param other number to add / 要加的数
         * @return new CalcChain with result / 包含结果的新 CalcChain
         */
        public CalcChain add(Number other) {
            return new CalcChain(value.add(toBigDecimal(other)));
        }

        /**
         * Subtraction / 减法
         * @param other number to subtract / 要减的数
         * @return new CalcChain with result / 包含结果的新 CalcChain
         */
        public CalcChain subtract(Number other) {
            return new CalcChain(value.subtract(toBigDecimal(other)));
        }

        /**
         * Multiplication / 乘法
         * @param other number to multiply / 要乘的数
         * @return new CalcChain with result / 包含结果的新 CalcChain
         */
        public CalcChain multiply(Number other) {
            return new CalcChain(value.multiply(toBigDecimal(other)));
        }

        /**
         * Division with rounding / 带四舍五入的除法
         * @param other divisor / 除数
         * @param scale decimal places / 小数位数
         * @return new CalcChain with result / 包含结果的新 CalcChain
         */
        public CalcChain divide(Number other, int scale) {
            return new CalcChain(value.divide(toBigDecimal(other), scale, RoundingMode.HALF_UP));
        }

        /**
         * Negation / 取反
         * @return new CalcChain with negated value / 包含取反值的新 CalcChain
         */
        public CalcChain negate() {
            return new CalcChain(value.negate());
        }

        /**
         * Absolute value / 绝对值
         * @return new CalcChain with absolute value / 包含绝对值的新 CalcChain
         */
        public CalcChain abs() {
            return new CalcChain(value.abs());
        }

        /**
         * Round to specified decimal places / 四舍五入到指定小数位
         * @param scale decimal places / 小数位数
         * @return new CalcChain with rounded value / 包含四舍五入值的新 CalcChain
         */
        public CalcChain round(int scale) {
            return new CalcChain(value.setScale(scale, RoundingMode.HALF_UP));
        }

        // Grouped operations with lambda syntax / 使用 lambda 语法的分组运算

        /**
         * Add a grouped sub-expression: a + (group)
         * 加上一个分组子表达式：a + (分组)
         * <pre>
         * .add(grp -> grp.with(5).multiply(2)) // equivalent to + (5 * 2) / 等价于 + (5 * 2)
         * </pre>
         * @param groupFn lambda function defining the group / 定义分组的 lambda 函数
         * @return new CalcChain with result / 包含结果的新 CalcChain
         */
        public CalcChain add(Function<Group, Group> groupFn) {
            BigDecimal groupResult = groupFn.apply(new Group()).result();
            return new CalcChain(value.add(groupResult));
        }

        /**
         * Subtract a grouped sub-expression: a - (group)
         * 减去一个分组子表达式：a - (分组)
         * <pre>
         * .subtract(grp -> grp.with(5).multiply(2)) // equivalent to - (5 * 2) / 等价于 - (5 * 2)
         * </pre>
         * @param groupFn lambda function defining the group / 定义分组的 lambda 函数
         * @return new CalcChain with result / 包含结果的新 CalcChain
         */
        public CalcChain subtract(Function<Group, Group> groupFn) {
            BigDecimal groupResult = groupFn.apply(new Group()).result();
            return new CalcChain(value.subtract(groupResult));
        }

        /**
         * Multiply by a grouped sub-expression: a * (group)
         * 乘以一个分组子表达式：a * (分组)
         * <pre>
         * .multiply(grp -> grp.with(5).add(2)) // equivalent to * (5 + 2) / 等价于 * (5 + 2)
         * </pre>
         * @param groupFn lambda function defining the group / 定义分组的 lambda 函数
         * @return new CalcChain with result / 包含结果的新 CalcChain
         */
        public CalcChain multiply(Function<Group, Group> groupFn) {
            BigDecimal groupResult = groupFn.apply(new Group()).result();
            return new CalcChain(value.multiply(groupResult));
        }

        /**
         * Divide by a grouped sub-expression: a / (group)
         * 除以一个分组子表达式：a / (分组)
         * <pre>
         * .divide(grp -> grp.with(10).add(5), 2) // equivalent to / (10 + 5) with 2 decimal places
         *                                        // 等价于 / (10 + 5) 保留2位小数
         * </pre>
         * @param groupFn lambda function defining the group / 定义分组的 lambda 函数
         * @param scale decimal places / 小数位数
         * @return new CalcChain with result / 包含结果的新 CalcChain
         */
        public CalcChain divide(Function<Group, Group> groupFn, int scale) {
            BigDecimal groupResult = groupFn.apply(new Group()).result();
            return new CalcChain(value.divide(groupResult, scale, RoundingMode.HALF_UP));
        }

        /**
         * Get the final result
         * 获取最终结果
         * @return final BigDecimal value / 最终的 BigDecimal 值
         */
        public BigDecimal result() {
            return value;
        }
    }

    /**
     * Group represents a sub-expression within parentheses.
     * It uses the same fluent API as CalcChain but accumulates operations.
     * <p>
     * Group 表示括号内的子表达式。
     * 它使用与 CalcChain 相同的流式 API，但会累积运算。
     */
    public static class Group {
        private BigDecimal value;

        private Group() {
            this.value = null;
        }

        /**
         * Set the initial value for this group
         * 设置此分组的初始值
         * @param initial initial value / 初始值
         * @return this Group for method chaining / 用于方法链式调用的 Group
         */
        public Group with(Number initial) {
            this.value = toBigDecimal(initial);
            return this;
        }

        /**
         * Addition within group / 分组内的加法
         * @param other number to add / 要加的数
         * @return this Group for method chaining / 用于方法链式调用的 Group
         */
        public Group add(Number other) {
            ensureInitialized();
            this.value = value.add(toBigDecimal(other));
            return this;
        }

        /**
         * Subtraction within group / 分组内的减法
         * @param other number to subtract / 要减的数
         * @return this Group for method chaining / 用于方法链式调用的 Group
         */
        public Group subtract(Number other) {
            ensureInitialized();
            this.value = value.subtract(toBigDecimal(other));
            return this;
        }

        /**
         * Multiplication within group / 分组内的乘法
         * @param other number to multiply / 要乘的数
         * @return this Group for method chaining / 用于方法链式调用的 Group
         */
        public Group multiply(Number other) {
            ensureInitialized();
            this.value = value.multiply(toBigDecimal(other));
            return this;
        }

        /**
         * Division within group / 分组内的除法
         * @param other divisor / 除数
         * @param scale decimal places / 小数位数
         * @return this Group for method chaining / 用于方法链式调用的 Group
         */
        public Group divide(Number other, int scale) {
            ensureInitialized();
            this.value = value.divide(toBigDecimal(other), scale, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * Negation within group / 分组内的取反
         * @return this Group for method chaining / 用于方法链式调用的 Group
         */
        public Group negate() {
            ensureInitialized();
            this.value = value.negate();
            return this;
        }

        /**
         * Absolute value within group / 分组内的绝对值
         * @return this Group for method chaining / 用于方法链式调用的 Group
         */
        public Group abs() {
            ensureInitialized();
            this.value = value.abs();
            return this;
        }

        /**
         * Round within group / 分组内的四舍五入
         * @param scale decimal places / 小数位数
         * @return this Group for method chaining / 用于方法链式调用的 Group
         */
        public Group round(int scale) {
            ensureInitialized();
            this.value = value.setScale(scale, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * Nested group support - these operations can be called even without initialization
         * to allow for complex nested expressions like: grp.multiply(inner -> inner.with(2).add(3))
         * <p>
         * 嵌套分组支持 - 这些操作即使未初始化也可调用，
         * 以支持复杂嵌套表达式，如：grp.multiply(inner -> inner.with(2).add(3))
         */
        public Group add(Function<Group, Group> groupFn) {
            BigDecimal groupResult = groupFn.apply(new Group()).result();
            if (this.value == null) {
                this.value = groupResult;
            } else {
                this.value = value.add(groupResult);
            }
            return this;
        }

        /**
         * Nested subtraction / 嵌套减法
         * @param groupFn lambda function defining the nested group / 定义嵌套分组的 lambda 函数
         * @return this Group for method chaining / 用于方法链式调用的 Group
         */
        public Group subtract(Function<Group, Group> groupFn) {
            BigDecimal groupResult = groupFn.apply(new Group()).result();
            if (this.value == null) {
                this.value = groupResult.negate();
            } else {
                this.value = value.subtract(groupResult);
            }
            return this;
        }

        /**
         * Nested multiplication / 嵌套乘法
         * @param groupFn lambda function defining the nested group / 定义嵌套分组的 lambda 函数
         * @return this Group for method chaining / 用于方法链式调用的 Group
         */
        public Group multiply(Function<Group, Group> groupFn) {
            BigDecimal groupResult = groupFn.apply(new Group()).result();
            if (this.value == null) {
                this.value = groupResult;
            } else {
                this.value = value.multiply(groupResult);
            }
            return this;
        }

        /**
         * Nested division / 嵌套除法
         * @param groupFn lambda function defining the nested group / 定义嵌套分组的 lambda 函数
         * @param scale decimal places / 小数位数
         * @return this Group for method chaining / 用于方法链式调用的 Group
         */
        public Group divide(Function<Group, Group> groupFn, int scale) {
            BigDecimal groupResult = groupFn.apply(new Group()).result();
            if (this.value == null) {
                this.value = BigDecimal.ONE.divide(groupResult, scale, RoundingMode.HALF_UP);
            } else {
                this.value = value.divide(groupResult, scale, RoundingMode.HALF_UP);
            }
            return this;
        }

        /**
         * Ensure the group has been initialized with .with()
         * 确保分组已用 .with() 初始化
         * @throws IllegalStateException if not initialized / 如果未初始化
         */
        private void ensureInitialized() {
            if (value == null) {
                throw new IllegalStateException("Group must be initialized with .with(value) first");
            }
        }

        /**
         * Get the result of this group
         * 获取此分组的结果
         * @return group result / 分组结果
         * @throws IllegalStateException if group has no value / 如果分组没有值
         */
        BigDecimal result() {
            if (value == null) {
                throw new IllegalStateException("Group has no value");
            }
            return value;
        }
    }

    /**
     * Convert Number to BigDecimal, rejecting floating point types
     * 将 Number 转换为 BigDecimal，拒绝浮点类型
     *
     * @param number number to convert / 要转换的数字
     * @return BigDecimal representation / BigDecimal 表示
     * @throws IllegalArgumentException if number is Float or Double / 如果数字是 Float 或 Double
     */
    private static BigDecimal toBigDecimal(Number number) {
        Objects.requireNonNull(number, "number must not be null");
        if (number instanceof BigDecimal bd) {
            return bd;
        } else if (number instanceof Double || number instanceof Float) {
            throw new IllegalArgumentException("Float and Double are not supported - use BigDecimal, Integer, or Long");
        } else {
            return BigDecimal.valueOf(number.longValue());
        }
    }
}
