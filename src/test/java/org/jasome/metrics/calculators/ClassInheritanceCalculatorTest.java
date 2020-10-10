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
public class ClassInheritanceCalculatorTest {


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
    public void test_that_correctly_calculates_number_of_ancestors() {
        // Given
        String absolutePath = "/metrics/calculators/ClassInheritanceCalculator/";
        final String name = "Ancestors.java";
        Project project = scan(absolutePath, name);
        org.jasome.input.Package aPackage = project.getPackages().iterator().next();
        Type classX = aPackage.getTypes().stream().filter((t) -> t.getName().equals("ClassX")).findFirst().get();
        Type classY = aPackage.getTypes().stream().filter((t) -> t.getName().equals("ClassY")).findFirst().get();
        // When
        Set<Metric> resultX = new ClassInheritanceCalculator().calculate(classX);
        Set<Metric> resultY = new ClassInheritanceCalculator().calculate(classY);
        // Then
        System.out.println(resultY);
        assertThat(resultX).containsExactly(
                Metric.of("NOPa", "", 2),
                Metric.of("NOCh", "", 0),
                Metric.of("NOD", "", 0),
                Metric.of("NOA", "", 4));
        assertThat(resultY).containsExactly(
                Metric.of("NOPa", "", 1),
                Metric.of("NOCh", "", 0),
                Metric.of("NOD", "", 0),
                Metric.of("NOA", "", 1));
    }

    @Test
    public void test_that_correctly_calculates_number_of_children_and_parents() {
        // Given
        String absolutePath = "/metrics/calculators/ClassInheritanceCalculator/";
        final String name = "ChildrenParent.java";
        Project project = scan(absolutePath, name);
        org.jasome.input.Package aPackage = project.getPackages().iterator().next();
        Type classX = aPackage.getTypes().stream().filter((t) -> t.getName().equals("ClassX")).findFirst().get();
        Type classY = aPackage.getTypes().stream().filter((t) -> t.getName().equals("ClassY")).findFirst().get();
        Type classA = aPackage.getTypes().stream().filter((t) -> t.getName().equals("A")).findFirst().get();
        Type classK = aPackage.getTypes().stream().filter((t) -> t.getName().equals("K")).findFirst().get();
        // When
        Set<Metric> resultX = new ClassInheritanceCalculator().calculate(classX);
        Set<Metric> resultY = new ClassInheritanceCalculator().calculate(classY);
        Set<Metric> resultA = new ClassInheritanceCalculator().calculate(classA);
        Set<Metric> resultK = new ClassInheritanceCalculator().calculate(classK);
        // Then
        assertThat(resultX).containsExactly(
                Metric.of("NOPa", "", 2),
                Metric.of("NOCh", "", 0),
                Metric.of("NOD", "", 0),
                Metric.of("NOA", "", 4));
        assertThat(resultY).containsExactly(
                Metric.of("NOPa", "", 1),
                Metric.of("NOCh", "", 0),
                Metric.of("NOD", "", 0),
                Metric.of("NOA", "", 1));
        assertThat(resultA).containsExactly(
                Metric.of("NOPa", "", 0),
                Metric.of("NOCh", "", 2),
                Metric.of("NOD", "", 2),
                Metric.of("NOA", "", 0));
        assertThat(resultK).containsExactly(
                Metric.of("NOPa", "", 1),
                Metric.of("NOCh", "", 1),
                Metric.of("NOD", "", 1),
                Metric.of("NOA", "", 2));
    }

    @Test
    public void test_that_correctly_calculates_number_of_descendants() {
        // Given
        String absolutePath = "/metrics/calculators/ClassInheritanceCalculator/";
        final String name = "Descendants.java";
        Project project = scan(absolutePath, name);
        org.jasome.input.Package aPackage = project.getPackages().iterator().next();
        Type classA = aPackage.getTypes().stream().filter((t) -> t.getName().equals("A")).findFirst().get();
        Type classK = aPackage.getTypes().stream().filter((t) -> t.getName().equals("K")).findFirst().get();
        Type classI = aPackage.getTypes().stream().filter((t) -> t.getName().equals("I")).findFirst().get();
        // When
        Set<Metric> resultA = new ClassInheritanceCalculator().calculate(classA);
        Set<Metric> resultK = new ClassInheritanceCalculator().calculate(classK);
        Set<Metric> resultI = new ClassInheritanceCalculator().calculate(classI);
        // Then
        System.out.println(resultI);
        assertThat(resultA).containsExactly(
                Metric.of("NOPa", "", 0),
                Metric.of("NOCh", "", 2),
                Metric.of("NOD", "", 2),
                Metric.of("NOA", "", 0));
        assertThat(resultK).containsExactly(
                Metric.of("NOPa", "", 1),
                Metric.of("NOCh", "", 1),
                Metric.of("NOD", "", 1),
                Metric.of("NOA", "", 2));
        assertThat(resultI).containsExactly(
                Metric.of("NOPa", "", 0),
                Metric.of("NOCh", "", 1),
                Metric.of("NOD", "", 3),
                Metric.of("NOA", "", 0));
    }

    @Test
    public void test_that_properly_handles_inner_classes() {
        // Given
        String absolutePath = "/metrics/calculators/ClassInheritanceCalculator/";
        final String name = "InnerClasses.java";
        Project project = scan(absolutePath, name);
        org.jasome.input.Package aPackage = project.getPackages().iterator().next();
        Type classX = aPackage.getTypes().stream().filter((t) -> t.getName().equals("ClassX")).findFirst().get();
        // When
        Set<Metric> resultX = new ClassInheritanceCalculator().calculate(classX);
        // Then
        System.out.println(resultX);
        assertThat(resultX).containsExactly(
                Metric.of("NOPa", "", 1),
                Metric.of("NOCh", "", 0),
                Metric.of("NOD", "", 0),
                Metric.of("NOA", "", 1));
    }


}
