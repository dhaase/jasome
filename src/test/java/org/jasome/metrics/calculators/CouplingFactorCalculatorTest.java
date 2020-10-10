package org.jasome.metrics.calculators;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;
import org.jasome.input.Project;
import org.jasome.input.Scanner;
import org.jasome.input.StringScanner;
import org.jasome.input.Type;
import org.jasome.metrics.Metric;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class CouplingFactorCalculatorTest {

    private String readFile(String absolutePath, String name) {
        return new BufferedReader(
                new InputStreamReader(ClassInheritanceCalculatorTest.class.getResourceAsStream(absolutePath + name), StandardCharsets.UTF_8)).lines()
                .collect(Collectors.joining("\n"));
    }

    private Project scan(String absolutePath, String name) {
        String fileContents = readFile(absolutePath, name);
        Map<String, String> attributes = ImmutableMap.of("sourceFile", "");
        Collection<Pair<String, Map<String, String>>> sourceCodeWithAttributes = Collections.singleton(Pair.of(fileContents, attributes));
        Scanner ss = new StringScanner(sourceCodeWithAttributes, absolutePath);
        return ss.scan();
    }

    @Test
    public void test_that_properly_calculates_coupling_factor() {
        // Given
        String absolutePath = "/metrics/calculators/CouplingFactorCalculator/";
        final String name = "CouplingFactor.java";
        Project project = scan(absolutePath, name);
        org.jasome.input.Package aPackage = project.getPackages().iterator().next();
        Type classA = aPackage.getTypes().stream().filter((t) -> t.getName().equals("ClassA")).findFirst().get();
        Type classB = aPackage.getTypes().stream().filter((t) -> t.getName().equals("ClassB")).findFirst().get();
        Type classC = aPackage.getTypes().stream().filter((t) -> t.getName().equals("ClassC")).findFirst().get();
        Type classM = aPackage.getTypes().stream().filter((t) -> t.getName().equals("MainClass")).findFirst().get();
        // When
        Set<Metric> resultA = new CouplingFactorCalculator().calculate(classA);
        Set<Metric> resultB = new CouplingFactorCalculator().calculate(classB);
        Set<Metric> resultC = new CouplingFactorCalculator().calculate(classC);
        Set<Metric> resultM = new CouplingFactorCalculator().calculate(classM);
        // Then
        System.out.println(resultM);
        assertThat(resultA).containsExactly(
                Metric.of("NODa", "", 1),
                Metric.of("NODe", "", 1),
                Metric.of("CF", "", 0.333333333));
        assertThat(resultB).containsExactly(
                Metric.of("NODa", "", 2),
                Metric.of("NODe", "", 1),
                Metric.of("CF", "", 0.5));
        assertThat(resultC).containsExactly(
                Metric.of("NODa", "", 2),
                Metric.of("NODe", "", 0),
                Metric.of("CF", "", 0.333333333));
        assertThat(resultM).containsExactly(
                Metric.of("NODa", "", 0),
                Metric.of("NODe", "", 3),
                Metric.of("CF", "", 0.5));
    }

}
