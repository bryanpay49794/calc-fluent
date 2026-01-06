# CalcFluent V2 - Lambda-Based Syntax for BigDecimal Calculations

**[简体中文](README-ZH-V2.md) | English**

## Why V2?

The original CalcFluent syntax requires explicit `rightParenthesis()` calls which can be:
- Easy to forget
- Hard to match visually in complex expressions
- Verbose and cluttered

**V2 solves this** using Java lambda expressions for automatic scope management.

## Quick Comparison

### Original Syntax (V1)
```java
Calculator.startWith(orderAmount)
    .subtractParenthesisThen(threshold)
    .multiply(rate)
    .rightParenthesis()  // Easy to forget!
    .multiply(vipDiscount)
    .addParenthesisThen(inviteBase)
    .multiply(inviteRate)
    .rightParenthesis()  // Which opening does this match?
    .multiply(taxRate)
    .round(2)
    .result();
```

### Lambda Syntax (V2)
```java
CalculatorV2.startWith(orderAmount)
    .subtract(grp -> grp
        .with(threshold)
        .multiply(rate)
    )
    .multiply(vipDiscount)
    .add(grp -> grp
        .with(inviteBase)
        .multiply(inviteRate)
    )
    .multiply(taxRate)
    .round(2)
    .result();
```

## Key Advantages

* **No manual parenthesis matching** - Lambda scope handles it automatically
* **Visual clarity** - Indentation shows mathematical precedence naturally
* **Impossible to mismatch** - Java compiler enforces correct nesting
* **Better IDE support** - Autocomplete works inside lambda scopes
* **Modern Java style** - Aligns with functional programming patterns (Stream API, etc.)
* **Easier code review** - Intent is immediately clear

## Basic Usage

### Simple Operations
```java
import io.calcfluent.CalculatorV2;
import java.math.BigDecimal;

// Basic arithmetic
BigDecimal result = CalculatorV2.startWith(10)
    .add(5)
    .multiply(2)
    .subtract(3)
    .result();  // (10 + 5) * 2 - 3 = 27
```

### Grouped Operations
```java
// With grouping: 10 + (2 * 3) = 16
BigDecimal result = CalculatorV2.startWith(10)
    .add(grp -> grp
        .with(2)
        .multiply(3)
    )
    .result();
```

### Nested Groups
```java
// Deeply nested: 100 + (20 * (15 / (3 + 2))) = 160
BigDecimal result = CalculatorV2.startWith(100)
    .add(grp1 -> grp1
        .with(20)
        .multiply(grp2 -> grp2
            .with(15)
            .divide(grp3 -> grp3
                .with(3)
                .add(2)
            , 2)  // 2 decimal places
        )
    )
    .result();
```

## API Reference

### Main Operations (CalcChain)

All operations return `CalcChain` for method chaining:

```java
.add(Number)              // Addition
.subtract(Number)         // Subtraction
.multiply(Number)         // Multiplication
.divide(Number, scale)    // Division with precision
.negate()                 // Negation
.abs()                    // Absolute value
.round(scale)             // Round to specified decimal places
.result()                 // Get final BigDecimal
```

### Grouped Operations

Operations that accept lambdas for sub-expressions:

```java
.add(grp -> ...)          // Add a grouped sub-expression
.subtract(grp -> ...)     // Subtract a grouped sub-expression
.multiply(grp -> ...)     // Multiply by a grouped sub-expression
.divide(grp -> ..., scale) // Divide by a grouped sub-expression
```

### Inside Groups

Within a lambda scope (Group):

```java
grp -> grp
    .with(Number)         // Initialize the group (required first call)
    .add(Number)          // All same operations as main chain
    .multiply(...)        // Supports nested lambdas
```

## Real-World Examples

### Financial Calculation
```java
// Calculate: (orderAmount - bulkDiscount) * vipRate + referralBonus * bonusRate * tax
BigDecimal orderAmount = new BigDecimal("1000");
BigDecimal bulkDiscount = new BigDecimal("100");
BigDecimal vipRate = new BigDecimal("0.9");
BigDecimal referralBonus = new BigDecimal("50");
BigDecimal bonusRate = new BigDecimal("0.05");
BigDecimal taxRate = new BigDecimal("1.1");

BigDecimal finalPrice = CalculatorV2.startWith(orderAmount)
    .subtract(bulkDiscount)
    .multiply(vipRate)
    .add(grp -> grp
        .with(referralBonus)
        .multiply(bonusRate)
    )
    .multiply(taxRate)
    .round(2)
    .result();
```

### Tiered Pricing
```java
// Complex tiered calculation with multiple discounts
BigDecimal total = CalculatorV2.startWith(basePrice)
    .subtract(grp -> grp
        .with(tierDiscount)
        .multiply(tierRate)
    )
    .multiply(grp -> grp
        .with(BigDecimal.ONE)
        .subtract(seasonalDiscount)
    )
    .add(grp -> grp
        .with(membershipBonus)
        .multiply(membershipMultiplier)
    )
    .round(2)
    .result();
```

### Division with Precision
```java
// 100 + (50 / 3) with 2 decimal places
BigDecimal result = CalculatorV2.startWith(100)
    .add(grp -> grp
        .with(50)
        .divide(3, 2)
    )
    .result();  // 116.67
```

## Running the Examples

### Compile and Run
```bash
# Compile
javac -d target/classes -sourcepath src/main/java \
    src/main/java/io/calcfluent/CalculatorV2.java \
    src/main/java/io/calcfluent/demo/DemoV2.java

# Run basic demo
java -cp target/classes io.calcfluent.demo.DemoV2

# Run comparison demo
javac -d target/classes -sourcepath src/main/java \
    src/main/java/io/calcfluent/demo/QuickComparison.java
java -cp target/classes io.calcfluent.demo.QuickComparison

# Run tests
javac -d target/classes -sourcepath src/main/java \
    src/main/java/io/calcfluent/demo/SimpleTests.java
java -cp target/classes io.calcfluent.demo.SimpleTests
```

## Type Safety

V2 maintains the same strict type safety as V1:

```java
// Float and Double are rejected at compile time
CalculatorV2.startWith(10.5);        // IllegalArgumentException
CalculatorV2.startWith(10).add(5.5); // IllegalArgumentException

// Use BigDecimal, Integer, or Long
CalculatorV2.startWith(new BigDecimal("10.5"));  // OK
CalculatorV2.startWith(10);                      // OK (Integer)
CalculatorV2.startWith(10L);                     // OK (Long)
```

## Error Handling

```java
// Groups must be initialized with .with()
CalculatorV2.startWith(10)
    .add(grp -> grp.add(5))  // IllegalStateException: missing .with()
    .result();

// Correct usage
CalculatorV2.startWith(10)
    .add(grp -> grp.with(5).add(3))  // OK
    .result();
```

## Migration from V1

V2 is a standalone implementation - V1 code continues to work unchanged.

### Migration Strategy 1: Gradual
Keep both versions available:
- Use V1 for existing code
- Use V2 for new code
- Migrate incrementally during refactoring

### Migration Strategy 2: Side-by-Side Comparison
When refactoring, create V2 version alongside V1:
```java
// V1 (original)
BigDecimal oldResult = Calculator.startWith(a)
    .addParenthesisThen(b).multiply(c).rightParenthesis()
    .result();

// V2 (new)
BigDecimal newResult = CalculatorV2.startWith(a)
    .add(grp -> grp.with(b).multiply(c))
    .result();

assert oldResult.equals(newResult);  // Verify equivalence
```

## Requirements

- Java 8 or higher (for lambda support)
- Zero dependencies (completely standalone)

## Performance

Lambda overhead is negligible:
- Modern JVM optimizes lambda allocations
- BigDecimal operations dominate execution time
- No measurable performance difference vs V1

## Examples in Code

See the demo files:
- `src/main/java/io/calcfluent/demo/DemoV2.java` - Basic examples
- `src/main/java/io/calcfluent/demo/QuickComparison.java` - V1 vs V2 comparison
- `src/main/java/io/calcfluent/demo/SimpleTests.java` - Comprehensive test suite

## License

MIT License - Same as CalcFluent V1

## Contributing

Contributions welcome! Please ensure:
- Code follows existing style
- All tests pass
- New features include tests
- Complex logic includes examples

---

**Comparison with V1:**

| Aspect | V1 | V2 |
|--------|----|----|
| Readability (simple) | Good | Good |
| Readability (complex) | Fair | Excellent |
| Error Prevention | Good | Excellent |
| IDE Autocomplete | Good | Excellent |
| Learning Curve | Moderate | Easy (familiar lambda syntax) |
| Parenthesis Management | Manual | Automatic |
| Code Review Clarity | Fair | Excellent |
| Java Version | 17+ | 8+ (more compatible) |

Choose V2 for new projects and complex calculations. V1 remains available for backwards compatibility.
