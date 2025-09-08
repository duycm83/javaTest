package org.example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

class Result {
    String code;
    BigDecimal valuePerHeir = BigDecimal.ZERO;
    BigDecimal nonTaxablePerHeir = BigDecimal.ZERO;

    public Result(String code, BigDecimal valuePerHeir, BigDecimal nonTaxablePerHeir) {
        this.code = code;
        this.valuePerHeir = valuePerHeir;
        this.nonTaxablePerHeir = nonTaxablePerHeir;
    }

    @Override
    public String toString() {
        return "Result{" +
                "Code:" + code.toString() +
                ",valuePerHeir:" + valuePerHeir.toString() +
                ", nonTaxablePerHeir:" + nonTaxablePerHeir.toString() +
                '}';
    }
}
public class Main {
    public static void main(String[] args) {
        //INPUT
        int level = 2;
//        String familyCodes = "C001, C002, C003, S001";
        String familyCodes = "O001, O002, S001";
        int min = 20000000;
        int max = 100000000;
//        for (int value = min; value < max; value++) {
//            try {
//                getNonTaxable(familyCodes, level, BigDecimal.valueOf(value));
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
            try {
                getNonTaxable(familyCodes, level, BigDecimal.valueOf(min));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    private static void getNonTaxable(String familyCodes, int level, BigDecimal value) throws Exception {
        List<String> codes = new ArrayList<>(
                Arrays.asList(familyCodes.split("\\s*,\\s*"))
        );
        Map<String,BigDecimal> rates = new HashMap<>();
        for(String code: codes) {
            BigDecimal rate = BigDecimal.ZERO;
            if (level == 1) {
                if ("S001".equals(code)) {
                    rate = BigDecimal.ONE.divide(BigDecimal.valueOf(2), 10, RoundingMode.DOWN);
                } else if(codes.contains("C001") && code.startsWith("C")) {
                    rate = new BigDecimal("0.5")
                            .divide(BigDecimal.valueOf(codes.size()-1), 10, RoundingMode.DOWN);
                } else if(!codes.contains("C001") && code.startsWith("C")) {
                    rate = BigDecimal.ONE.divide(BigDecimal.valueOf(codes.size()-1), 10, RoundingMode.DOWN);
                }
            } else if (level == 2) {
                if ("S001".equals(code)) {
                    rate = new BigDecimal(2).divide(BigDecimal.valueOf(3), 10, RoundingMode.DOWN);
                } else if(codes.contains("S001") && code.startsWith("O")) {
                    rate = new BigDecimal(1)
                            .divide(BigDecimal.valueOf(3), 10, RoundingMode.DOWN)
                            .divide(BigDecimal.valueOf(codes.size()-1), 10, RoundingMode.DOWN);
                } else if(!codes.contains("S001") && code.startsWith("O")) {
                    rate = BigDecimal.ONE.divide(BigDecimal.valueOf(codes.size()-1), 10, RoundingMode.DOWN);
                }
            } else if (level == 3) {
                if ("S001".equals(code)) {
                    rate = new BigDecimal(3).divide(BigDecimal.valueOf(4), 10, RoundingMode.DOWN);
                } else if(codes.contains("S001") && code.startsWith("O")) {
                    rate = new BigDecimal(1).divide(BigDecimal.valueOf(4), 10, RoundingMode.DOWN)
                            .divide(BigDecimal.valueOf(codes.size()-1), 10, RoundingMode.DOWN);
                } else if(!codes.contains("S001") && code.startsWith("O")) {
                    rate = BigDecimal.ONE.divide(BigDecimal.valueOf(codes.size()-1), 10, RoundingMode.DOWN);
                }
            }

            rates.put(code, rate);
        }
        System.out.println(rates);

        final BigDecimal nonTaxableAmount = new BigDecimal(5000000 * codes.size());
        System.out.println("Value:"+value);
        System.out.println("NonTaxable:"+nonTaxableAmount);
        Result sum = new Result("Total:",BigDecimal.ZERO,BigDecimal.ZERO);
        for (String code : rates.keySet()) {
            BigDecimal valuePerHeir = value.multiply(rates.get(code));
            BigDecimal nonTaxablePerHeir = nonTaxableAmount.multiply(valuePerHeir.divide(value, 10, RoundingMode.DOWN));
            Result result = new Result(code, valuePerHeir, nonTaxablePerHeir);
            sum.valuePerHeir = sum.valuePerHeir.add(valuePerHeir);
            sum.nonTaxablePerHeir = sum.nonTaxablePerHeir.add(nonTaxablePerHeir);
            System.out.println(result);
        }
        System.out.println(sum);
        BigDecimal diff = nonTaxableAmount.subtract(sum.nonTaxablePerHeir.setScale(0, RoundingMode.DOWN));
        System.out.println("@@diff:"+diff);
        if(diff.compareTo(BigDecimal.ONE) > 0) {
            throw new Exception("ERR");
        }
    }


}
