package org.jasome.calculators.impl;

import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.common.collect.ImmutableSet;
import org.jasome.calculators.Calculator;
import org.jasome.calculators.Metric;
import org.jasome.parsing.Package;
import org.jasome.parsing.Type;
import org.jscience.mathematics.number.LargeInteger;
import org.jscience.mathematics.number.Rational;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RobertMartinCouplingCalculator implements Calculator<Package> {
    @Override
    public Set<Metric> calculate(Package aPackage) {
        Map<String, Type> allClassesOutsideOfPackage = aPackage.getParentProject().getPackages()
                .stream()
                .filter(p -> p != aPackage)
                .map(Package::getTypes)
                .flatMap(Set::stream)
                .filter(type -> type.getSource().isPublic()) //only public classes count, nothing else is visible outside of the package
                .collect(Collectors.toMap(Type::getName, t -> t));

        Map<String, Type> allClassesInsideOfPackage = aPackage.getTypes()
                .stream()
                .filter(type -> type.getSource().isPublic())
                .collect(Collectors.toMap(Type::getName, t -> t));

        LargeInteger afferentCoupling = LargeInteger.ZERO; //The number of classes outside a package that depend on classes inside the package.
        LargeInteger efferentCoupling = LargeInteger.ZERO; //The number of classes inside a package that depend on classes outside the package.

        for (Type typeInsidePackage : aPackage.getTypes()) {
            List<ClassOrInterfaceType> referencedTypes = typeInsidePackage.getSource().getNodesByType(ClassOrInterfaceType.class);

            long numberOfTypesReferencedThatAreInsideAnotherPackage = referencedTypes
                    .stream()
                    .map(typ -> typ.getName().getIdentifier())
                    .filter(typeName ->
                            allClassesOutsideOfPackage.containsKey(typeName) && !allClassesInsideOfPackage.containsKey(typeName)
                    ).count();

            //TODO: this should be more restrictive, we only need these inside of certain kinds of expressions
            List<SimpleName> referencedNames = typeInsidePackage.getSource().getNodesByType(SimpleName.class);

            long numberOfSimpleNamesReferencedThatCorrespondToTypesInsideAnotherPackage = referencedNames
                    .stream()
                    .map(SimpleName::getIdentifier)
                    .filter(typeName ->
                            allClassesOutsideOfPackage.containsKey(typeName) && !allClassesInsideOfPackage.containsKey(typeName)
                    ).count();

            if (numberOfTypesReferencedThatAreInsideAnotherPackage + numberOfSimpleNamesReferencedThatCorrespondToTypesInsideAnotherPackage > 0) {
                efferentCoupling = efferentCoupling.plus(LargeInteger.ONE);
            }
        }

        for (Type typeOutsidePackage : allClassesOutsideOfPackage.values()) {
            List<ClassOrInterfaceType> referencedTypes = typeOutsidePackage.getSource().getNodesByType(ClassOrInterfaceType.class);

            long numberOfTypesReferencedThatAreInsideThisPackage = referencedTypes
                    .stream()
                    .map(typ -> typ.getName().getIdentifier())
                    .filter(typeName ->
                            allClassesInsideOfPackage.containsKey(typeName) && !allClassesOutsideOfPackage.containsKey(typeName)
                    ).count();

            List<SimpleName> referencedNames = typeOutsidePackage.getSource().getNodesByType(SimpleName.class);

            long numberOfSimpleNamesReferencedThatCorrespondToTypesInsideThisPackage = referencedNames
                    .stream()
                    .map(SimpleName::getIdentifier)
                    .filter(typeName ->
                            allClassesInsideOfPackage.containsKey(typeName) && !allClassesOutsideOfPackage.containsKey(typeName)
                    ).count();

            if (numberOfTypesReferencedThatAreInsideThisPackage + numberOfSimpleNamesReferencedThatCorrespondToTypesInsideThisPackage > 0) {
                afferentCoupling = afferentCoupling.plus(LargeInteger.ONE);
            }
        }

        ImmutableSet.Builder<Metric> metrics = ImmutableSet.<Metric>builder()
                .add(Metric.of("Ca", "Afferent Coupling", afferentCoupling))
                .add(Metric.of("Ce", "Efferent Coupling", efferentCoupling));

        Optional<Rational> instabilityOpt = Optional.ofNullable(
                afferentCoupling.plus(efferentCoupling).isGreaterThan(LargeInteger.ZERO) ?
                        Rational.valueOf(efferentCoupling, afferentCoupling.plus(efferentCoupling))
                        : null
        );

        instabilityOpt.ifPresent(i -> metrics.add(Metric.of("I", "Instability", i)));

        LargeInteger numberOfAbstractClassesAndInterfacesInPackage = LargeInteger.valueOf(
                aPackage.getTypes()
                        .stream()
                        .filter(type -> type.getSource().isInterface() || type.getSource().isAbstract())
                        .count()
        );

        metrics.add(Metric.of("NOI", "Number of Interfaces and Abstract Classes", numberOfAbstractClassesAndInterfacesInPackage));

        Optional<Rational> abstractnessOpt = Optional.ofNullable(
                aPackage.getTypes().size() > 0 ?
                        Rational.valueOf(numberOfAbstractClassesAndInterfacesInPackage, LargeInteger.valueOf(aPackage.getTypes().size()))
                        : null
        );


        abstractnessOpt.ifPresent(a -> metrics.add(Metric.of("A", "Abstractness", a)));


        if (instabilityOpt.isPresent() && abstractnessOpt.isPresent()) {
            metrics.add(Metric.of("DMS", "Normalized Distance from Main Sequence", abstractnessOpt.get().plus(instabilityOpt.get()).minus(Rational.ONE).abs()));
        }

        return metrics.build();


    }
}
