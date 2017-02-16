package org.jasome.calculators.impl;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jasome.calculators.Calculator;
import org.jasome.calculators.Metric;
import org.jasome.parsing.Method;
import org.jasome.parsing.Package;
import org.jasome.parsing.Type;
import org.jscience.mathematics.number.LargeInteger;
import org.jscience.mathematics.number.Rational;

import java.util.*;
import java.util.stream.Collectors;

/**
 * http://support.objecteering.com/objecteering6.1/help/us/metrics/metrics_in_detail/specialization_index.htm
 */
public class SpecializationIndexCalculator implements Calculator<Type> {


    @Override
    public Set<Metric> calculate(Type type) {

        Multimap<String, Type> allTypesByName = HashMultimap.create();
        for (Package aPackage : type.getParentPackage().getParentProject().getPackages()) {
            for (Type aType : aPackage.getTypes()) {
                allTypesByName.put(aType.getName(), aType);
            }
        }

        LargeInteger depth = LargeInteger.valueOf(calculateInheritanceDepth(type, allTypesByName));

        Pair<Integer, Integer> overloadedAndInheritedOperations = calculateOverloadedAndInheritedOperations(type, allTypesByName);

        LargeInteger overriddenMethods = LargeInteger.valueOf(overloadedAndInheritedOperations.getLeft());
        LargeInteger inheritedMethods = LargeInteger.valueOf(overloadedAndInheritedOperations.getRight());

        LargeInteger numberOfMethods = LargeInteger.valueOf(type.getMethods().size());

        //more good and related ones here http://www.cs.kent.edu/~jmaletic/cs63901/lectures/SoftwareMetrics.pdf

        ImmutableSet.Builder<Metric> metricBuilder = ImmutableSet.<Metric>builder()
                .add(Metric.of("DIT", "Depth of Inheritance Tree", depth))
                .add(Metric.of("NORM", "Number of Overridden Methods", overriddenMethods))
                .add(Metric.of("NM", "Number of Methods", numberOfMethods))
                .add(Metric.of("NMI", "Number of Inherited Methods", inheritedMethods))
                .add(Metric.of("NMA", "Number of Methods Added to Inheritance", numberOfMethods.minus(overriddenMethods)));

        if (numberOfMethods.isGreaterThan(LargeInteger.ZERO)) {
            LargeInteger numerator = overriddenMethods.times(depth);
            Rational specializationIndex = Rational.valueOf(numerator, numberOfMethods);
            metricBuilder = metricBuilder.add(Metric.of("SIX", "Specialization Index", specializationIndex));
        }

        return metricBuilder.build();
    }

    private Pair<Integer, Integer> calculateOverloadedAndInheritedOperations(Type type, Multimap<String, Type> allTypesByName) {

        Set<Triple<com.github.javaparser.ast.type.Type, String, List<com.github.javaparser.ast.type.Type>>> parentMethods = new HashSet<>();

        Stack<Type> typesToCheck = new Stack<>();

        typesToCheck.addAll(getParentTypes(type, allTypesByName, true));

        while (!typesToCheck.empty()) {
            Type typeToCheck = typesToCheck.pop();

            for (Method method : typeToCheck.getMethods()) {
                Triple<com.github.javaparser.ast.type.Type, String, List<com.github.javaparser.ast.type.Type>> methodSignature = getMethodSignatureData(method);
                parentMethods.add(methodSignature);
            }

            typesToCheck.addAll(getParentTypes(typeToCheck, allTypesByName, true));
        }

        int numberOverridden = 0;

        for (Method m : type.getMethods()) {

            Triple<com.github.javaparser.ast.type.Type, String, List<com.github.javaparser.ast.type.Type>> methodSignature = getMethodSignatureData(m);

            if (parentMethods.contains(methodSignature)) {
                numberOverridden++;
            }
        }

        return Pair.of(numberOverridden, parentMethods.size() - numberOverridden);
    }

    private Triple<com.github.javaparser.ast.type.Type, String, List<com.github.javaparser.ast.type.Type>> getMethodSignatureData(Method method) {
        com.github.javaparser.ast.type.Type returnType = method.getSource().getType();
        String name = method.getSource().getName().getIdentifier();
        List<com.github.javaparser.ast.type.Type> parameterTypes = method.getSource().getParameters().stream().map(parameter -> parameter.getType()).collect(Collectors.toList());

        return Triple.of(returnType, name, parameterTypes);
    }

    private Set<Type> getParentTypes(Type type, Multimap<String, Type> allTypesByName, boolean includeInterfaceTypes) {
        Set<ClassOrInterfaceType> parentClasses = new HashSet<>();

        parentClasses.addAll(type.getSource().getExtendedTypes());
        if (includeInterfaceTypes) {
            parentClasses.addAll(type.getSource().getImplementedTypes());
        }

        Set<Type> parentTypes = new HashSet<Type>();

        for (ClassOrInterfaceType parentClass : parentClasses) {
            Collection<Type> nextLevelTypes = allTypesByName.get(parentClass.getName().toString());
            parentTypes.addAll(nextLevelTypes);
        }
        return parentTypes;
    }

    private int calculateInheritanceDepth(Type type, Multimap<String, Type> allTypesByName) {
        ClassOrInterfaceDeclaration decl = type.getSource();

        Collection<Type> nextLevelTypes = getParentTypes(type, allTypesByName, true);


        List<Integer> maximums = new ArrayList<>();

        for (Type t : nextLevelTypes) {
            maximums.add(1 + calculateInheritanceDepth(t, allTypesByName));
        }


        return maximums.stream().mapToInt(i -> i.intValue()).max().orElse(1);
    }


}
