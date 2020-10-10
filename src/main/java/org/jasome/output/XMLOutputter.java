package org.jasome.output;

import org.jasome.input.Code;
import org.jasome.input.Method;
import org.jasome.input.Package;
import org.jasome.input.Project;
import org.jasome.input.Type;
import org.jasome.metrics.Metric;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class XMLOutputter implements Outputter<Document> {

    private Map<String, String> overallMetricsDescription = new TreeMap<>();

    private void addMetricsDescription(Document doc, Node parentElement) {
        Element metricsContainer = doc.createElement("MetricsDescription");
        for (Map.Entry<String, String> entry : overallMetricsDescription.entrySet()) {
            Element metricsElement = doc.createElement("Metric");
            metricsElement.setAttribute("name", entry.getKey());
            metricsElement.setAttribute("description", entry.getValue());
            metricsContainer.appendChild(metricsElement);
        }
        parentElement.appendChild(metricsContainer);
    }

    private <T extends Code> List<T> sortChildren(Collection<T> children) {
        return children.stream().sorted(new Comparator<Code>() {
            @Override
            public int compare(Code o1, Code o2) {
                return o1.getName().compareTo(o2.getName());
            }

        }).collect(Collectors.toList());
    }

    private void addAttributes(Code classNode, Element classElement) {
        for (Map.Entry<String, String> attribute : classNode.getAttributes().entrySet()) {
            classElement.setAttribute(attribute.getKey(), attribute.getValue());
        }
    }

    private void addMetricsForNode(Document doc, Node parentElement, Code node) {
        Element metricsContainer = doc.createElement("Metrics");

        Set<Metric> metrics = node.getMetrics();
        List<Metric> sortedMetrics = metrics.stream().sorted(Comparator.comparing(Metric::getName)).collect(Collectors.toList());
        for (Metric metric : sortedMetrics) {
            overallMetricsDescription.put(metric.getName(), metric.getDescription());
            Element metricsElement = doc.createElement("Metric");

            metricsElement.setAttribute("name", metric.getName());
            metricsElement.setAttribute("value", metric.getFormattedValue());

            metricsContainer.appendChild(metricsElement);
        }
        parentElement.appendChild(metricsContainer);
    }

    @Override
    public Document output(Project project) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element projectElement = doc.createElement("Project");
            doc.appendChild(projectElement);

            addAttributes(project, projectElement);
            addMetricsForNode(doc, projectElement, project);

            Element packagesElement = doc.createElement("Packages");
            projectElement.appendChild(packagesElement);

            for (Package packageNode : sortChildren(project.getPackages())) {
                Element packageElement = doc.createElement("Package");
                packageElement.setAttribute("name", packageNode.getName());
                packagesElement.appendChild(packageElement);

                addAttributes(packageNode, packageElement);
                addMetricsForNode(doc, packageElement, packageNode);

                Element classesElement = doc.createElement("Classes");
                packageElement.appendChild(classesElement);

                for (Type classNode : sortChildren(packageNode.getTypes())) {
                    Element classElement = doc.createElement("Class");
                    classElement.setAttribute("name", classNode.getName());
                    classesElement.appendChild(classElement);

                    addAttributes(classNode, classElement);
                    addMetricsForNode(doc, classElement, classNode);

                    Element methodsElement = doc.createElement("Methods");
                    classElement.appendChild(methodsElement);

                    for (Method methodNode : sortChildren(classNode.getMethods())) {
                        Element methodElement = doc.createElement("Method");
                        methodElement.setAttribute("name", methodNode.getName());
                        methodsElement.appendChild(methodElement);

                        addAttributes(methodNode, methodElement);
                        addMetricsForNode(doc, methodElement, methodNode);
                    }
                }
            }
            addMetricsDescription(doc, projectElement);
            return doc;

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

    }
    
}
