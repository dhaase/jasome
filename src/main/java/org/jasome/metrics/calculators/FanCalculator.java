package org.jasome.metrics.calculators;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.type.VoidType;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.Network;
import org.jasome.input.Method;
import org.jasome.metrics.Calculator;
import org.jasome.metrics.Metric;
import org.jasome.metrics.value.NumericValue;
import org.jasome.util.Distinct;

import java.util.Set;

public class FanCalculator implements Calculator<Method> {

    @Override
    public synchronized Set<Metric> calculate(Method method) {

        Network<Method, Distinct<Expression>> methodCalls = method.getParentType().getParentPackage().getParentProject().getMetadata().getCallNetwork();

        Set<Method> methodsCalled = methodCalls.successors(method);

        int fanOut = 0;

        for (Method methodCalled : methodsCalled) {
            Set<Distinct<Expression>> calls = methodCalls.edgesConnecting(method, methodCalled);
            fanOut += calls.size();
        }

        Set<Method> methodsCalling = methodCalls.predecessors(method);

        int fanIn = 0;

        for (Method methodCalling : methodsCalling) {
            Set<Distinct<Expression>> calls = methodCalls.edgesConnecting(methodCalling, method);
            fanIn += calls.size();
        }

        int returns = method.getSource().getType() instanceof VoidType ? 0 : 1;
        int parameters = method.getSource().getParameters().size();
        int iovars = parameters + returns;

        NumericValue dataComplexity = NumericValue.of(iovars).divide(NumericValue.ONE.plus(NumericValue.of(fanOut)));
        NumericValue structuralComplexity = NumericValue.of(fanOut).pow(2);
        NumericValue systemComplexity = dataComplexity.plus(structuralComplexity.divide(NumericValue.ONE));

        return ImmutableSet.of(
                Metric.of("Fout", "Fan-out (Fout) - The number of methods immediately subordinate to a method (method)", fanOut),
                Metric.of("Fin", " Fan-in (Fin) - The number of methods that invoke a method (method)", fanIn),
                Metric.of("Si", "Structural Complexity (Si) - Fout^2 (method)", structuralComplexity),
                Metric.of("IOVars", "Input/Output Variables (IOVars) - NOP + 1 (0 if void return type) (method)", iovars),
                Metric.of("Di", "Data Complexity (Di) - (IOVars)/(Fout+1) (method)", dataComplexity),
                Metric.of("Ci", "System Complexity (Ci) - Si + Di (method)", systemComplexity)
        );


    }

}
