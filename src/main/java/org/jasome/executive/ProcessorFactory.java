package org.jasome.executive;

import org.jasome.metrics.calculators.*;
import org.jasome.input.Processor;

class ProcessorFactory {
    static Processor getProcessor() {
        Processor processor = new Processor();

        processor.registerTypeCalculator(new RawTotalLinesOfCodeCalculator());

        processor.registerTypeCalculator(new NumberOfFieldsCalculator());

        processor.registerProjectCalculator(new TotalLinesOfCodeCalculator.ProjectCalculator());
        processor.registerPackageCalculator(new TotalLinesOfCodeCalculator.PackageCalculator());
        processor.registerTypeCalculator(new TotalLinesOfCodeCalculator.TypeCalculator());
        processor.registerMethodCalculator(new TotalLinesOfCodeCalculator.MethodCalculator());

        processor.registerMethodCalculator(new CyclomaticComplexityCalculator());
        processor.registerTypeCalculator(new WeightedMethodsCalculator());

        processor.registerMethodCalculator(new NumberOfParametersCalculator());
        processor.registerPackageCalculator(new NumberOfClassesCalculator());

        processor.registerTypeCalculator(new SpecializationIndexCalculator());

        processor.registerPackageCalculator(new RobertMartinCouplingCalculator());
        return processor;
    }
}