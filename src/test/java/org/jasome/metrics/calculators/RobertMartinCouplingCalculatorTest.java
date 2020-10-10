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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class RobertMartinCouplingCalculatorTest {

    private String readFile(String absolutePath, String name) {
        return new BufferedReader(
                new InputStreamReader(ClassInheritanceCalculatorTest.class.getResourceAsStream(absolutePath + name), StandardCharsets.UTF_8)).lines()
                .collect(Collectors.joining("\n"));
    }

    private Project scan(String absolutePath, String[] names) {
        Collection<Pair<String, Map<String, String>>> sourceCodeWithAttributes = new ArrayList<>();
        for(String name : names) {
            String fileContents = readFile(absolutePath, name);
            Map<String, String> attributes = ImmutableMap.of("sourceFile", "");
            sourceCodeWithAttributes.add(Pair.of(fileContents, attributes));
        }
        Scanner ss = new StringScanner(sourceCodeWithAttributes, absolutePath);
        return ss.scan();
    }

    @Test
    public void test_that_properly_calculates_coupling_metrics() {
        // Given
        String absolutePath = "/metrics/calculators/RobertMartinCouplingCalculator/";
        String[] names = {"A.java", "B.java", "C.java", "D.java", "E.java", "I.java", "CustomException.java"};
        Project project = scan(absolutePath, names);
        org.jasome.input.Package package1 = project.getPackages().stream().filter((t) -> t.getName().equals("org.whatever.stuff")).findFirst().get();
        org.jasome.input.Package package2 = project.getPackages().stream().filter((t) -> t.getName().equals("org.whatever.stuff2")).findFirst().get();
         // When
        Set<Metric> resultA = new RobertMartinCouplingCalculator().calculate(package1);
        Set<Metric> resultB = new RobertMartinCouplingCalculator().calculate(package2);
        // Then
        System.out.println(resultB);
        assertThat(resultA).containsExactly(
                Metric.of("Ca", "", 2),
                Metric.of("Ce", "", 2),
                Metric.of("I", "", 0.5),
                Metric.of("NOI", "", 1),
                Metric.of("A", "", 0.25),
                Metric.of("DMS", "", 0.25));
        assertThat(resultB).containsExactly(
                Metric.of("Ca", "", 2),
                Metric.of("Ce", "", 2),
                Metric.of("I", "", 0.5),
                Metric.of("NOI", "", 1),
                Metric.of("A", "", 0.333333333),
                Metric.of("DMS", "", 0.166666667));
     }

}
