package org.jasome.metrics.calculators;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.google.common.collect.ImmutableSet;
import org.jasome.input.Type;
import org.jasome.metrics.Calculator;
import org.jasome.metrics.Metric;

import java.util.Set;

/**
 * Counts the number of fields and methods in a class.
 *
 * <ul>
 *     <li>NF - Number of Attributes (fields)</li>
 *     <li>NSF - Number of Static Attributes (fields)</li>
 *     <li>NPF - Number of Public Attributes (fields)</li>
 *     <li>NM - Number of Methods</li>
 *     <li>NSM - Number of Static Methods</li>
 *     <li>NPM - Number of Public Methods</li>
 * </ul>
 *
 * @author Rod Hilton
 * @since 0.2
 */
public class NumberOfFieldsCalculator implements Calculator<Type> {

    @Override
    public Set<Metric> calculate(Type type) {
        ClassOrInterfaceDeclaration declaration = type.getSource();


        long numAttributes = declaration.getFields().size();
        long numStaticAttributes = declaration.getFields().stream().filter(f -> f.getModifiers().contains(Modifier.STATIC)).count();
        long numPublicAttributes = declaration.getFields().stream().filter(f -> f.getModifiers().contains(Modifier.PUBLIC)).count();

        long numMethods = declaration.getMethods().size();
        long numStaticMethods = declaration.getMethods().stream().filter(f -> f.getModifiers().contains(Modifier.STATIC)).count();
        long numPublicMethods = declaration.getMethods().stream().filter(f -> f.getModifiers().contains(Modifier.PUBLIC)).count();

        return ImmutableSet.<Metric>builder()
                .add(Metric.of("NF", "Number of Fields (NF) - The number of fields/attributes (class)", numAttributes))
                .add(Metric.of("NSF", "Number of Static Fields (NSF) - The number of static attributes (class)", numStaticAttributes))
                .add(Metric.of("NPF", "Number of Public Fields (NPF) - The number of public attributes (class)", numPublicAttributes))
                .add(Metric.of("NM", "Number of Methods (NM) - The number of methods (class)", numMethods))
                .add(Metric.of("NSM", "Number of Static Methods (NSM) - The number of static methods (class)", numStaticMethods))
                .add(Metric.of("NPM", "Number of Public Methods (NPM) - The number of public methods (class)", numPublicMethods))
                .build();
    }
}
