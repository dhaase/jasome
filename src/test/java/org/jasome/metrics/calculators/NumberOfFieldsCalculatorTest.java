package org.jasome.metrics.calculators;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;
import org.jasome.input.Method;
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
public class NumberOfFieldsCalculatorTest {


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
    public void test_that_correctly_calculates_simple_metric_with_nested_class() {
        // Given
        String absolutePath = "/metrics/calculators/NumberOfFieldsCalculator/";
        final String name = "NestedClass.java";
        Project project = scan(absolutePath, name);
        org.jasome.input.Package aPackage = project.getPackages().iterator().next();
        Type classA = aPackage.getTypes().stream().filter((t) -> t.getName().equals("Example")).findFirst().get();
        // When
        Set<Metric> resultA = new NumberOfFieldsCalculator().calculate(classA);
        // Then
        System.out.println(resultA);
        assertThat(resultA).containsExactly(
                Metric.of("NF", "", 3),
                Metric.of("NSF", "", 1),
                Metric.of("NPF", "", 2),
                Metric.of("NM", "", 3),
                Metric.of("NSM", "", 1),
                Metric.of("NPM", "", 1)
                );
    }

}
