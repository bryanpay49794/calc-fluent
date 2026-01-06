# CalcFluent V2 - 基于 Lambda 的 BigDecimal 计算语法

**简体中文 | [English](README-V2.md)**

## 为什么需要 V2？

原始的 CalcFluent 语法需要显式调用 `rightParenthesis()`，这会导致：
- 容易忘记调用
- 在复杂表达式中难以视觉匹配
- 代码冗长、杂乱

**V2 通过 Java lambda 表达式解决了这些问题**，实现自动作用域管理。

## 快速对比

### 原始语法 (V1)
```java
Calculator.startWith(orderAmount)
    .subtractParenthesisThen(threshold)
    .multiply(rate)
    .rightParenthesis()  // 容易忘记！
    .multiply(vipDiscount)
    .addParenthesisThen(inviteBase)
    .multiply(inviteRate)
    .rightParenthesis()  // 这个对应哪个左括号？
    .multiply(taxRate)
    .round(2)
    .result();
```

### Lambda 语法 (V2)
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

## 核心优势

* **无需手动匹配括号** - Lambda 作用域自动处理
* **视觉清晰** - 缩进自然展示数学优先级
* **不可能括号不匹配** - Java 编译器强制检查嵌套正确性
* **更好的 IDE 支持** - Lambda 作用域内自动补全完美工作
* **现代 Java 风格** - 与函数式编程模式对齐（Stream API 等）
* **更易代码审查** - 意图立即清晰

## 基础用法

### 简单运算
```java
import io.calcfluent.CalculatorV2;
import java.math.BigDecimal;

// 基础算术运算
BigDecimal result = CalculatorV2.startWith(10)
    .add(5)
    .multiply(2)
    .subtract(3)
    .result();  // (10 + 5) * 2 - 3 = 27
```

### 分组运算
```java
// 带分组：10 + (2 * 3) = 16
BigDecimal result = CalculatorV2.startWith(10)
    .add(grp -> grp
        .with(2)
        .multiply(3)
    )
    .result();
```

### 嵌套分组
```java
// 深度嵌套：100 + (20 * (15 / (3 + 2))) = 160
BigDecimal result = CalculatorV2.startWith(100)
    .add(grp1 -> grp1
        .with(20)
        .multiply(grp2 -> grp2
            .with(15)
            .divide(grp3 -> grp3
                .with(3)
                .add(2)
            , 2)  // 保留2位小数
        )
    )
    .result();
```

## API 参考

### 主要运算 (CalcChain)

所有运算返回 `CalcChain` 以支持链式调用：

```java
.add(Number)              // 加法
.subtract(Number)         // 减法
.multiply(Number)         // 乘法
.divide(Number, scale)    // 除法（指定精度）
.negate()                 // 取反
.abs()                    // 绝对值
.round(scale)             // 四舍五入到指定小数位
.result()                 // 获取最终 BigDecimal 结果
```

### 分组运算

接受 lambda 的子表达式运算：

```java
.add(grp -> ...)          // 加上一个分组子表达式
.subtract(grp -> ...)     // 减去一个分组子表达式
.multiply(grp -> ...)     // 乘以一个分组子表达式
.divide(grp -> ..., scale) // 除以一个分组子表达式
```

### 分组内部

在 lambda 作用域内 (Group)：

```java
grp -> grp
    .with(Number)         // 初始化分组（必须首次调用）
    .add(Number)          // 所有与主链相同的运算
    .multiply(...)        // 支持嵌套 lambda
```

## 实际应用示例

### 金融计算
```java
// 计算：(订单金额 - 批量折扣) * VIP费率 + 推荐奖金 * 奖金费率 * 税率
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

### 阶梯定价
```java
// 包含多重折扣的复杂阶梯计算
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

### 带精度的除法
```java
// 100 + (50 / 3) 保留2位小数
BigDecimal result = CalculatorV2.startWith(100)
    .add(grp -> grp
        .with(50)
        .divide(3, 2)
    )
    .result();  // 116.67
```

## 运行示例

### 编译和运行
```bash
# 编译
javac -d target/classes -sourcepath src/main/java \
    src/main/java/io/calcfluent/CalculatorV2.java \
    src/main/java/io/calcfluent/demo/DemoV2.java

# 运行基础演示
java -cp target/classes io.calcfluent.demo.DemoV2

# 运行对比演示
javac -d target/classes -sourcepath src/main/java \
    src/main/java/io/calcfluent/demo/QuickComparison.java
java -cp target/classes io.calcfluent.demo.QuickComparison

# 运行测试
javac -d target/classes -sourcepath src/main/java \
    src/main/java/io/calcfluent/demo/SimpleTests.java
java -cp target/classes io.calcfluent.demo.SimpleTests
```

## 类型安全

V2 保持与 V1 相同的严格类型安全：

```java
// Float 和 Double 在编译时被拒绝
CalculatorV2.startWith(10.5);        // IllegalArgumentException
CalculatorV2.startWith(10).add(5.5); // IllegalArgumentException

// 使用 BigDecimal、Integer 或 Long
CalculatorV2.startWith(new BigDecimal("10.5"));  // 正确
CalculatorV2.startWith(10);                      // 正确 (Integer)
CalculatorV2.startWith(10L);                     // 正确 (Long)
```

## 错误处理

```java
// 分组必须用 .with() 初始化
CalculatorV2.startWith(10)
    .add(grp -> grp.add(5))  // IllegalStateException: 缺少 .with()
    .result();

// 正确用法
CalculatorV2.startWith(10)
    .add(grp -> grp.with(5).add(3))  // 正确
    .result();
```

## 从 V1 迁移

V2 是独立实现 - V1 代码继续正常工作。

### 迁移策略 1：渐进式
同时保留两个版本：
- 现有代码使用 V1
- 新代码使用 V2
- 在重构时逐步迁移

### 迁移策略 2：并排对比
重构时，在 V1 旁边创建 V2 版本：
```java
// V1 (原始)
BigDecimal oldResult = Calculator.startWith(a)
    .addParenthesisThen(b).multiply(c).rightParenthesis()
    .result();

// V2 (新版)
BigDecimal newResult = CalculatorV2.startWith(a)
    .add(grp -> grp.with(b).multiply(c))
    .result();

assert oldResult.equals(newResult);  // 验证等价性
```

## 运行要求

- Java 8 或更高版本（支持 lambda）
- 零依赖（完全独立）

## 性能

Lambda 开销可忽略不计：
- 现代 JVM 优化 lambda 分配
- BigDecimal 运算占主导执行时间
- 与 V1 相比没有可测量的性能差异

## 代码示例

查看演示文件：
- `src/main/java/io/calcfluent/demo/DemoV2.java` - 基础示例
- `src/main/java/io/calcfluent/demo/QuickComparison.java` - V1 vs V2 对比
- `src/main/java/io/calcfluent/demo/SimpleTests.java` - 综合测试套件

## 许可证

MIT 许可证 - 与 CalcFluent V1 相同

## 贡献

欢迎贡献！请确保：
- 代码遵循现有风格
- 所有测试通过
- 新功能包含测试
- 复杂逻辑包含示例

---

**与 V1 对比：**

| 方面 | V1 | V2 |
|------|----|----|
| 可读性（简单） | 好 | 好 |
| 可读性（复杂） | 一般 | 优秀 |
| 错误预防 | 好 | 优秀 |
| IDE 自动补全 | 好 | 优秀 |
| 学习曲线 | 中等 | 简单（熟悉的 lambda 语法） |
| 括号管理 | 手动 | 自动 |
| 代码审查清晰度 | 一般 | 优秀 |
| Java 版本 | 17+ | 8+（更好的兼容性） |

新项目和复杂计算选择 V2。V1 仍可用于向后兼容。
