package com.dcits.im.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

/**
 * 数学公式表达式解析
 */
@Slf4j
public class FormulaUtils {

    /**
     * 表达式解析
     */
    public static ExpressionEvaluator ExpressionEvaluator = new ExpressionEvaluator();

    /**
     * 表达式各个字符节点的类型枚举类
     */
    public enum ExpressionNodeType {
        Unknown, Plus, // +
        Subtract, /// -
        MultiPly, // *
        Divide, // /
        LParentheses, // (
        RParentheses, /// )
        Mod, // % (求模,取余)
        Power, // ^ (次幂)
        BitwiseAnd, /// & (按位与)
        BitwiseOr, /// | (按位或)
        And, // && (逻辑与)
        Or, /// || (逻辑或)
        Not, /// ! (逻辑非)
        Equal, /// == (相等)
        Unequal, /// != 或 <> (不等于)
        GT, /// > (大于)
        LT, /// < (小于)
        GTOrEqual, /// >= (大于等于)
        LTOrEqual, /// <= (小于等于)
        LShift, /// << (左移位)
        RShift, /// >> (右移位)
        Numeric, /// 数值,
        String, Date, Like, // 包含
        NotLike, // 不包含
        StartWith, // 已什么开始
        EndWith// 已什么结尾
    }

    /**
     * 存储表达式运算符或操作数的各个节点的类
     */
    public static class ExpressionNode {

        private String value;

        private ExpressionNodeType type;

        private int pri;

        private ExpressionNode unitaryNode;

        private Object numeric;

        /**
         *
         * @param value
         *            操作数或运算符
         */
        public ExpressionNode(String value) {
            this.value = value;
            this.type = parseNodeType(value);
            this.pri = getNodeTypePRI(this.type);
            this.numeric = null;
        }

        public Object getNumeric() {
            if (this.numeric == null) {

                if ((this.type == ExpressionNodeType.String) || (this.type == ExpressionNodeType.Date)) {
                    return this.value;
                }

                if (this.type != ExpressionNodeType.Numeric) {
                    return 0;
                }
                Double num = new Double(this.value);
                if (this.unitaryNode != null && this.unitaryNode.type == ExpressionNodeType.Subtract) {
                    num = 0 - num;
                }
                this.numeric = num;
            }
            return numeric;
        }

        public void setNumeric(Object numeric) {
            this.numeric = numeric;
            this.value = this.numeric.toString();
        }

        /**
         * 设置或返回与当前节点相关联的一元操作符节点
         * @param unitaryNode
         */
        public void setUnitaryNode(ExpressionNode unitaryNode) {
            this.unitaryNode = unitaryNode;
        }

        /**
         * 解析节点类型
         * @param value
         * @return
         */
        private ExpressionNodeType parseNodeType(String value) {
            if (StringUtils.isEmpty(value)) {
                return ExpressionNodeType.Unknown;
            }
            switch (value) {
                case "+":
                    return ExpressionNodeType.Plus;
                case "-":
                    return ExpressionNodeType.Subtract;
                case "*":
                    return ExpressionNodeType.MultiPly;
                case "/":
                    return ExpressionNodeType.Divide;
                case "%":
                    return ExpressionNodeType.Mod;
                case "^":
                    return ExpressionNodeType.Power;
                case "(":
                    return ExpressionNodeType.LParentheses;
                case ")":
                    return ExpressionNodeType.RParentheses;
                case "&":
                    return ExpressionNodeType.BitwiseAnd;
                case "|":
                    return ExpressionNodeType.BitwiseOr;
                case "&&":
                case "<并且>":
                case "并且":
                    return ExpressionNodeType.And;
                case "||":
                case "<或者>":
                case "或者":
                    return ExpressionNodeType.Or;
                case "!":
                    return ExpressionNodeType.Not;
                case "==":
                case "=":
                case "等于":
                    return ExpressionNodeType.Equal;
                case "!=":
                case "<>":
                case "≠":
                case "不等于":
                    return ExpressionNodeType.Unequal;
                case ">":
                case "大于":
                    return ExpressionNodeType.GT;
                case "<":
                case "小于":
                    return ExpressionNodeType.LT;
                case ">=":
                case "≥":
                    return ExpressionNodeType.GTOrEqual;
                case "<=":
                case "≤":
                    return ExpressionNodeType.LTOrEqual;
                case "<<":
                    return ExpressionNodeType.LShift;
                case ">>":
                    return ExpressionNodeType.RShift;
                case "@":
                case "<包含>":
                case "包含":
                    return ExpressionNodeType.Like;
                case "!@":
                case "<不包含>":
                case "不包含":
                    return ExpressionNodeType.NotLike;
                case "!!$":
                    return ExpressionNodeType.StartWith;
                case "!!@":
                    return ExpressionNodeType.EndWith;

            }
            if (isNumerics(value)) {
                return ExpressionNodeType.Numeric;
            }
            if (isDatetime(value)) {
                return ExpressionNodeType.Date;
            }
            if (value.contains("\"")) {
                return ExpressionNodeType.String;
            }
            return ExpressionNodeType.Unknown;
        }

        /**
         * 获取各节点类型的优先级
         * @param nodeType
         * @return
         */
        private int getNodeTypePRI(ExpressionNodeType nodeType) {
            switch (nodeType) {
                case LParentheses:
                case RParentheses:
                    return 9;
                // 逻辑非是一元操作符,所以其优先级较高
                case Not:
                    return 8;
                case Mod:
                    return 7;
                case MultiPly:
                case Divide:
                case Power:
                    return 6;
                case Plus:
                case Subtract:
                    return 5;
                case LShift:
                case RShift:
                    return 4;
                case BitwiseAnd:
                case BitwiseOr:
                    return 3;
                case Equal:
                case Unequal:
                case GT:
                case LT:
                case GTOrEqual:
                case LTOrEqual:
                case Like:
                case NotLike:
                case StartWith:
                case EndWith:
                    return 2;
                case And:
                case Or:
                    return 1;
                default:
                    return 0;
            }

        }

        /**
         * 判断是否为数值
         * @param op
         * @return
         */
        public boolean isNumerics(String op) {
            return op.matches("^[\\+\\-]?(0|[1-9]\\d*|[1-9]\\d*\\.\\d+|0\\.\\d+)");
        }

        /**
         * 判断是否为日期
         * @param op
         * @return
         */
        public static boolean isDatetime(String op) {
            op = op.replace("\"", "").trim();
            return op.matches("\\d{4}\\-\\d{2}\\-\\d{2}(\\s\\d{2}\\:\\d{2}\\:\\d{2})?");
        }

        /**
         * 判断某个字符后是否需要更多的操作符
         * @param c
         * @return
         */
        public boolean needMoreOperator(char c) {
            switch (c) {
                case '&':
                case '|':
                case '=':
                case '!':
                case '>':
                case '<':
                case '.': // 小数点
                case '并':
                case '或':
                case '包':
                case '不':
                case '大':
                case '小':
                case '等':
                    return true;
            }
            // //数字则需要更多
            return Character.isDigit(c);
        }

        /**
         * 判断两个字符是否是同一类
         * @param c1
         * @param c2
         * @return
         */
        public boolean IsCongener(char c1, char c2) {
            if ((c1 == '(') || (c2 == '(')) {
                return false;
            }
            if ((c1 == ')') || (c2 == ')')) {
                return false;
            }
            if ((c1 == '"') || (c2 == '"')) {
                return false;
            }
            if (Character.isDigit(c1) || (c1 == '.')) {
                // c1为数字,则c2也为数字
                return (Character.isDigit(c2) || (c2 == '.'));
            }
            return (!Character.isDigit(c2) && (c2 != '.'));
        }

        /**
         * 判断某个字符是否是空白字符
         * @param c
         * @return
         */
        public boolean IsWhileSpace(char c) {
            return c == ' ' || c == '\t';
        }

        /**
         * 判断是否是一元操作符节点
         * @param nodeType
         * @return
         */
        public static boolean IsUnitaryNode(ExpressionNodeType nodeType) {
            return (nodeType == ExpressionNodeType.Plus || nodeType == ExpressionNodeType.Subtract);
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public ExpressionNodeType getType() {
            return type;
        }

        public void setType(ExpressionNodeType type) {
            this.type = type;
        }

        public int getPri() {
            return pri;
        }

        public void setPri(int pri) {
            this.pri = pri;
        }

        public ExpressionNode getUnitaryNode() {
            return unitaryNode;
        }
    }

    /**
     * 表达式异常类
     */
    public static class ExpressionException extends RuntimeException {
        private static final long serialVersionUID = 3136681292988750961L;

        public ExpressionException(String message) {
            super(message);
        }

        public ExpressionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * 负责读取表达式生成ExpressionNode对象的类
     */
    public static class ExpressionParser {

        // 当前分析的表达式
        private String expression;

        // 当前读取的位置
        private int position;

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public ExpressionParser(String expression) {
            this.expression = expression;
            this.position = 0;
        }

        /**
         * 读取下一个表达式节点,如果读取失败则返回null
         *
         * @return
         */
        public ExpressionNode readNode() {
            ExpressionNode s = new ExpressionNode(null);
            // 空格的位置
            int whileSpacePos = -1;
            boolean stringStartEnd = false;//true-开始 false-结束
            StringBuffer buffer = new StringBuffer(10);
            while (this.position < this.expression.length()) {
                char c = this.expression.charAt(this.position);
                if (c == '"') {
                    stringStartEnd = !stringStartEnd;
                    if (!stringStartEnd) {
                        this.position++;
                        buffer.append(c);
                        break;
                    }
                    if (buffer.length() != 0) {
                        break;
                    }
                }
                if (stringStartEnd) {
                    this.position++;
                    buffer.append(c);
                } else {
                    if (s.IsWhileSpace(c)) {
                        if ((whileSpacePos >= 0) && ((this.position - whileSpacePos) > 1)) {
                            throw new ExpressionException(
                                    String.format("表达式\"%s\"在位置(%s)上的字符非法!", this.getExpression(), this.getPosition()));
                        }
                        if (buffer.length() == 0) {
                            whileSpacePos = -1;
                        } else {
                            whileSpacePos = this.position;
                        }
                        this.position++;
                        continue;
                    }
                    if ((buffer.length() == 0) || s.IsCongener(c, buffer.charAt(buffer.length() - 1))) {
                        this.position++;
                        buffer.append(c);
                    } else {
                        break;
                    }
                    if (!s.needMoreOperator(c)) {
                        break;
                    }
                }
            }
            if (buffer.length() == 0) {
                return null;
            }
            ExpressionNode node = new ExpressionNode(buffer.toString());
            if (node.getType() == ExpressionNodeType.Unknown) {
                throw new ExpressionException(String.format("表达式\"%s\"在位置%s上的字符\"%s\"非法!", this.getExpression(),
                        this.getPosition() - node.getValue().length(), node.getValue()));
            }
            return node;
        }

    }

    /**
     * 解析公式并返回结果的类
     */
    public static class ExpressionEvaluator {
        /**
         * 将算术表达式转换为逆波兰表达式
         *
         * @param expression
         *            要计算的表达式,如"1+2+3+4"
         * @return
         */
        private static  List<ExpressionNode> parseExpression(String expression) {
            if (StringUtils.isEmpty(expression)) {
                return new ArrayList<>();
            }

            List<ExpressionNode> listOperator = new ArrayList<>(10);
            Stack<ExpressionNode> stackOperator = new Stack<>();

            ExpressionParser expParser = new ExpressionParser(expression);
            ExpressionNode beforeExpNode = null; // 前一个节点
            ExpressionNode unitaryNode = null; // 一元操作符
            ExpressionNode expNode;
            // 是否需要操作数
            boolean requireOperand = false;

            while ((expNode = expParser.readNode()) != null) {
                if ((expNode.getType() == ExpressionNodeType.Numeric)
                        || (expNode.getType() == ExpressionNodeType.String)
                        || (expNode.getType() == ExpressionNodeType.Date)) {
                    // 操作数， 直接加入后缀表达式中
                    if (unitaryNode != null) {
                        // 设置一元操作符节点
                        expNode.setUnitaryNode(unitaryNode);
                        unitaryNode = null;
                    }

                    listOperator.add(expNode);
                    requireOperand = false;
                    continue;
                } else if (expNode.getType() == ExpressionNodeType.LParentheses) {
                    // 左括号， 直接加入操作符栈
                    stackOperator.push(expNode);
                    continue;
                } else if (expNode.getType() == ExpressionNodeType.RParentheses) {
                    // 右括号则在操作符栈中反向搜索，直到遇到匹配的左括号为止，将中间的操作符依次加到后缀表达式中。
                    ExpressionNode lpNode = null;
                    while (stackOperator.size() > 0) {
                        lpNode = stackOperator.pop();
                        if (lpNode.getType() == ExpressionNodeType.LParentheses)
                            break;
                        listOperator.add(lpNode);
                    }
                    if (lpNode == null || lpNode.getType() != ExpressionNodeType.LParentheses) {
                        throw new ExpressionException(String.format("在表达式\"%s\"中没有与在位置(%s)上\")\"匹配的\"(%s)\"字符!",expParser.getExpression(), expParser.getPosition()));
                    }
                } else {
                    if (stackOperator.size() == 0) {
                        // 第一个节点则判断此节点是否是一元操作符"+,-,!,("中的一个,否则其它都非法
                        if (listOperator.size() == 0 && !(expNode.getType() == ExpressionNodeType.LParentheses
                                || expNode.getType() == ExpressionNodeType.Not)) {
                            // 后缀表达式没有任何数据则判断是否是一元操作数
                            if (ExpressionNode.IsUnitaryNode(expNode.getType())) {
                                unitaryNode = expNode;
                            } else {
                                // 丢失操作数
                                throw new ExpressionException(String.format("表达式\"%s\"在位置(%s)上缺少操作数!",
                                        expParser.getExpression(), expParser.getPosition()));
                            }
                        } else {
                            // 直接压入操作符栈
                            stackOperator.push(expNode);
                        }
                        requireOperand = true; // 下一个节点需要操作数
                        continue;
                    } else {
                        if (requireOperand) {
                            // 如果需要操作数则判断当前的是否是"+","-"号(一元操作符),如果是则继续
                            if (ExpressionNode.IsUnitaryNode(expNode.getType()) && unitaryNode == null) {
                                unitaryNode = expNode;
                            } else {
                                // 丢失操作数
                                throw new ExpressionException(String.format("表达式\"%s\"在位置({1})上缺少操作数!",
                                        expParser.getExpression(), expParser.getPosition()));
                            }
                        } else {
                            // 对前面的所有操作符进行优先级比较
                            do {
                                // 取得上一次的操作符
                                beforeExpNode = stackOperator.peek();

                                // 如果前一个操作符优先级较高，则将前一个操作符加入后缀表达式中
                                if (beforeExpNode.getType() != ExpressionNodeType.LParentheses
                                        && (beforeExpNode.getPri() - expNode.getPri()) >= 0) {
                                    listOperator.add(stackOperator.pop());
                                } else {
                                    break;
                                }

                            } while (stackOperator.size() > 0);

                            // 将操作符压入操作符栈
                            stackOperator.push(expNode);
                            requireOperand = true;
                        }
                    }
                }
            }

            if (requireOperand) {
                // 丢失操作数
                throw new ExpressionException(
                        String.format("表达式\"%s\"在位置({1})上缺少操作数!", expParser.getExpression(), expParser.getPosition()));
            }
            // 清空堆栈
            while (stackOperator.size() > 0) {
                // 取得操作符
                beforeExpNode = stackOperator.pop();
                if (beforeExpNode.getType() == ExpressionNodeType.LParentheses) {
                    throw new ExpressionException(String.format("表达式\"%s\"中括号不匹配,丢失右括号!", expParser.getExpression(),
                            expParser.getPosition()));
                }
                listOperator.add(beforeExpNode);
            }

            return listOperator;
        }

        /**
         * 对逆波兰表达式进行计算
         * @param nodes
         * @return
         */
        private static  Object CalcExpression(List<ExpressionNode> nodes) {
            if (nodes == null || nodes.size() == 0)
                return null;

            if (nodes.size() > 1) {
                int index = 0;
                // 储存数据
                ArrayList values = new ArrayList();
                while (index < nodes.size()) {
                    ExpressionNode node = nodes.get(index);

                    switch (node.getType()) {
                        // 如果是数字，则将值存入 values 中
                        case Numeric:
                        case String:
                        case Date:
                            values.add(node.getNumeric());
                            index++;
                            break;
                        default:
                            // 二元表达式，需要二个参数， 如果是Not的话，则只要一个参数
                            int paramCount = 2;
                            if (node.getType() == ExpressionNodeType.Not)
                                paramCount = 1;
                            // 计算操作数的值
                            if (values.size() < paramCount) {
                                throw new ExpressionException("缺少操作数");
                            }
                            // 传入参数
                            Object[] data = new Object[paramCount];
                            for (int i = 0; i < paramCount; i++) {
                                data[i] = values.get(index - paramCount + i);
                            }
                            // 将计算结果再存入当前节点
                            node.setNumeric(calculate(node.getType(), data));
                            node.setType(ExpressionNodeType.Numeric);
                            // 将操作数节点删除
                            for (int i = 0; i < paramCount; i++) {
                                nodes.remove(index - i - 1);
                                values.remove(index - i - 1);
                            }
                            index -= paramCount;
                            break;
                    }

                }
            }

            if (nodes.size() != 1) {
                throw new ExpressionException("缺少操作符或操作数");
            }
            switch (nodes.get(0).getType()) {
                case Numeric:
                    return nodes.get(0).getNumeric();

                case String:
                case Date:
                    return nodes.get(0).getNumeric().toString().replace("\"", "");
            }
            throw new ExpressionException("缺少操作数");
        }

        /**
         * 计算节点的值
         * @param nodeType
         *            节点的类型
         * @param data
         *            要计算的值,有可能是两位或一位数
         * @return
         */
        private static  Object calculate(ExpressionNodeType nodeType, Object[] data) {
            BigDecimal d1, d2;
            boolean b1, b2;
            Date time1, time2;
            Object obj1 = data[0];
            Object obj2 = data[1];
            String str1 = obj1.toString();
            String str2 = obj2.toString();

            boolean dateFlag = ExpressionNode.isDatetime(str1) || ExpressionNode.isDatetime(str2);
            boolean strFlag = str1.contains("\"") || str2.contains("\"");
            str1 = str1.replace("\"", "");
            str2 = str2.replace("\"", "");

            switch (nodeType) {
                case Plus:
                    if (!strFlag) {
                        d1 = ConvertToDecimal(obj1);
                        d2 = ConvertToDecimal(obj2);
                        return (d1.add(d2));
                    }
                    return new StringBuffer(str1 + str2).toString();
                case Subtract:
                    d1 = ConvertToDecimal(obj1);
                    d2 = ConvertToDecimal(obj2);
                    return d1.subtract(d2);
                case MultiPly:
                    d1 = ConvertToDecimal(obj1);
                    d2 = ConvertToDecimal(obj2);
                    return d1.multiply(d2);
                case Divide:
                    d1 = ConvertToDecimal(obj1);
                    d2 = ConvertToDecimal(obj2);
                    if (BigDecimal.ZERO.compareTo(d2)==0)
                        throw new ExpressionException("/ By Zero");
                    return d1.divide(d2);
                case Power:
                    d1 = ConvertToDecimal(obj1);
                    d2 = ConvertToDecimal(obj2);
                    return Math.pow(d1.doubleValue(), d2.doubleValue());
                case Mod:
                    d1 = ConvertToDecimal(obj1);
                    d2 = ConvertToDecimal(obj2);
                    if (BigDecimal.ZERO.compareTo(d2)==0)
                        throw new RuntimeException();
                    return d1.doubleValue() % d2.doubleValue();
                case BitwiseAnd:
                    d1 = ConvertToDecimal(obj1);
                    d2 = ConvertToDecimal(obj2);
                    return d1.intValue() & d2.intValue();
                case BitwiseOr:
                    d1 = ConvertToDecimal(obj1);
                    d2 = ConvertToDecimal(obj2);
                    return d1.intValue() | d2.intValue();
                case And:
                    b1 = ConvertToBool(obj1);
                    b2 = ConvertToBool(obj2);
                    return b1 && b2;
                case Or:
                    b1 = ConvertToBool(obj1);
                    b2 = ConvertToBool(obj2);
                    return b1 || b2;
                case Not:
                    b1 = ConvertToBool(obj1);
                    return !b1;
                case Equal:
                    if (!dateFlag) {
                        if (strFlag) {
                            return str1.equals(str2);
                        }
                        d1 = ConvertToDecimal(obj1);
                        d2 = ConvertToDecimal(obj2);
                        return (d1.compareTo(d2)==0);
                    }
                    try {
                        time1 = DateUtils.parseDate(str1);
                        time2 = DateUtils.parseDate(str2);
                    } catch (ParseException e) {
                        log.error("Date parse Exception !");
                        throw new ExpressionException("Date parse Exception !");
                    }

                    return (time1.getTime() == time2.getTime());
                case Unequal:
                    if (!dateFlag) {
                        if (strFlag) {
                            return (!str1.equals(str2));
                        }
                        d1 = ConvertToDecimal(obj1);
                        d2 = ConvertToDecimal(obj2);
                        return (d1.compareTo(d2)!=0);
                    }
                    try {
                        time1 = DateUtils.parseDate(str1);
                        time2 = DateUtils.parseDate(str2);
                    } catch (ParseException e) {
                        log.error("Date parse Exception !");
                        throw new ExpressionException("Date parse Exception !");
                    }

                    return (time1.getTime() != time2.getTime());
                case GT:
                    if (!dateFlag) {
                        d1 = ConvertToDecimal(obj1);
                        d2 = ConvertToDecimal(obj2);
                        return (d1.compareTo(d2)>0);
                    }
                    try {
                        time1 = DateUtils.parseDate(str1);
                        time2 = DateUtils.parseDate(str2);
                    } catch (ParseException e) {
                        log.error("Date parse Exception !");
                        throw new ExpressionException("Date parse Exception !");
                    }
                    return (time1.getTime() > time2.getTime());

                case LT:

                    if (!dateFlag) {
                        d1 = ConvertToDecimal(obj1);
                        d2 = ConvertToDecimal(obj2);
                        return (d1.compareTo(d2)<0);
                    }
                    try {
                        time1 = DateUtils.parseDate(str1);
                        time2 = DateUtils.parseDate(str2);
                    } catch (ParseException e) {
                        log.error("Date parse Exception !");
                        throw new ExpressionException("Date parse Exception !");
                    }
                    return (time1.getTime() < time2.getTime());

                case GTOrEqual:

                    if (!dateFlag) {
                        d1 = ConvertToDecimal(obj1);
                        d2 = ConvertToDecimal(obj2);
                        return (d1.compareTo(d2)>=0);
                    }
                    try {
                        time1 = DateUtils.parseDate(str1);
                        time2 = DateUtils.parseDate(str2);
                    } catch (ParseException e) {
                        log.error("Date parse Exception !");
                        throw new ExpressionException("Date parse Exception !");
                    }
                    return (time1.getTime() >= time2.getTime());

                case LTOrEqual:
                    if (!dateFlag) {
                        d1 = ConvertToDecimal(obj1);
                        d2 = ConvertToDecimal(obj2);
                        return (d1.compareTo(d2)<=0);
                    }
                    try {
                        time1 = DateUtils.parseDate(str1);
                        time2 = DateUtils.parseDate(str2);
                    } catch (ParseException e) {
                        log.error("Date parse Exception !");
                        throw new ExpressionException("Date parse Exception !");
                    }
                    return (time1.getTime() <= time2.getTime());
                case LShift:
                    d1 = ConvertToDecimal(obj1);
                    d2 = ConvertToDecimal(obj2);
                    return d1.longValue() << d2.intValue();

                case RShift:
                    d1 = ConvertToDecimal(obj1);
                    d2 = ConvertToDecimal(obj2);
                    return d1.longValue() >> d2.intValue();
                case Like:
                    if (!strFlag) {
                        return false;
                    }
                    return str1.contains(str2);
                case NotLike:
                    if (!strFlag) {
                        return false;
                    }
                    return !str1.contains(str2);
                case StartWith:
                    if (!strFlag) {
                        return false;
                    }
                    return str1.startsWith(str2);
                case EndWith:
                    if (!strFlag) {
                        return false;
                    }
                    return str1.endsWith(str2);
            }

            return 0;
        }

        /**
         * 某个值转换为bool值
         * @param value
         * @return
         */
        private static  Boolean ConvertToBool(Object value) {
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else {
                return value != null;
            }
        }

        /**
         * 将某个值转换为decimal值
         * @param value
         * @return
         */
        private static BigDecimal ConvertToDecimal(Object value) {
            if (value instanceof Boolean) {
                return ((Boolean) value ? new BigDecimal("1") : new BigDecimal("0"));
            } else {
                return new BigDecimal(value.toString());
            }
        }

        /**
         * 表达式求解
         * @param expression
         *            要计算的表达式,如"1+2+3+4"
         * @return 返回计算结果,如果带有逻辑运算符则返回true/false,否则返回数值
         */
        public Object eval(String expression) {
            return CalcExpression(parseExpression(expression));
        }

        /**
         * 三木运算符
         * @param expression
         * @return
         */
        public  Object evalThreeOperand(String expression) throws ParseException {
            int index = expression.indexOf("?");
            if (index > -1) {
                String str = expression.substring(0, index);
                String str2 = expression.substring(index + 1);
                index = str2.indexOf(":");

                if (Boolean.parseBoolean((CalcExpression(parseExpression(str))).toString())) {
                    return eval(str2.substring(0, index));
                }
                return eval(str2.substring(index + 1));
            }
            return CalcExpression(parseExpression(expression));
        }

    }

    /**
     * 测试
     *
     * @方法名:main
     * @参数 @param args
     * @返回类型 void
     */
    public static void main(String[] args) {
//        String s1 = "1+2+3+4";
//        System.out.println(ExpressionEvaluator.eval(s1));
//
//        String s2 = "(20 - 6) < 3";
//        System.out.println(ExpressionEvaluator.eval(s2));
//
//        String s3 = "(3 + 1) == 4 && 5 > (2 + 3)";
//        System.out.println(ExpressionEvaluator.eval(s3));
//
//        String s4 = "\"hello\" == \"hello\" && 3 != 4";
//        System.out.println(ExpressionEvaluator.eval(s4));

        String ss = "\"HelloWorld\" 不等于 \"hello\"";
        System.out.println(ExpressionEvaluator.eval(ss));
    }
}