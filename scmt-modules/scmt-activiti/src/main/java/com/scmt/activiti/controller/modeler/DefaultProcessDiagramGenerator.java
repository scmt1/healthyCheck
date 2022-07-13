//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.scmt.activiti.controller.modeler;

import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramCanvas.SHAPE_TYPE;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;

public class DefaultProcessDiagramGenerator implements ProcessDiagramGenerator {

    protected Map<Class<? extends BaseElement>, ActivityDrawInstruction> activityDrawInstructions;
    protected Map<Class<? extends BaseElement>, ArtifactDrawInstruction> artifactDrawInstructions;

    public DefaultProcessDiagramGenerator() {
        this(1.0D);
    }

    public DefaultProcessDiagramGenerator(final double scaleFactor) {
        this.activityDrawInstructions = new HashMap();
        this.artifactDrawInstructions = new HashMap();
        this.activityDrawInstructions.put(StartEvent.class, new ActivityDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                StartEvent startEvent = (StartEvent) flowNode;
                if (startEvent.getEventDefinitions() != null && !startEvent.getEventDefinitions().isEmpty()) {
                    EventDefinition eventDefinition = (EventDefinition) startEvent.getEventDefinitions().get(0);
                    if (eventDefinition instanceof TimerEventDefinition) {
                        processDiagramCanvas.drawTimerStartEvent(graphicInfo, scaleFactor);
                    } else if (eventDefinition instanceof ErrorEventDefinition) {
                        processDiagramCanvas.drawErrorStartEvent(graphicInfo, scaleFactor);
                    } else if (eventDefinition instanceof SignalEventDefinition) {
                        processDiagramCanvas.drawSignalStartEvent(graphicInfo, scaleFactor);
                    } else if (eventDefinition instanceof MessageEventDefinition) {
                        processDiagramCanvas.drawMessageStartEvent(graphicInfo, scaleFactor);
                    } else {
                        processDiagramCanvas.drawNoneStartEvent(graphicInfo);
                    }
                } else {
                    processDiagramCanvas.drawNoneStartEvent(graphicInfo);
                }

            }
        });
        this.activityDrawInstructions.put(IntermediateCatchEvent.class, new ActivityDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                IntermediateCatchEvent intermediateCatchEvent = (IntermediateCatchEvent) flowNode;
                if (intermediateCatchEvent.getEventDefinitions() != null && !intermediateCatchEvent.getEventDefinitions().isEmpty()) {
                    if (intermediateCatchEvent.getEventDefinitions().get(0) instanceof SignalEventDefinition) {
                        processDiagramCanvas.drawCatchingSignalEvent(flowNode.getName(), graphicInfo, true, scaleFactor);
                    } else if (intermediateCatchEvent.getEventDefinitions().get(0) instanceof TimerEventDefinition) {
                        processDiagramCanvas.drawCatchingTimerEvent(flowNode.getName(), graphicInfo, true, scaleFactor);
                    } else if (intermediateCatchEvent.getEventDefinitions().get(0) instanceof MessageEventDefinition) {
                        processDiagramCanvas.drawCatchingMessageEvent(flowNode.getName(), graphicInfo, true, scaleFactor);
                    }
                }

            }
        });
        this.activityDrawInstructions.put(ThrowEvent.class, new ActivityDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                ThrowEvent throwEvent = (ThrowEvent) flowNode;
                if (throwEvent.getEventDefinitions() != null && !throwEvent.getEventDefinitions().isEmpty()) {
                    if (throwEvent.getEventDefinitions().get(0) instanceof SignalEventDefinition) {
                        processDiagramCanvas.drawThrowingSignalEvent(graphicInfo, scaleFactor);
                    } else if (throwEvent.getEventDefinitions().get(0) instanceof CompensateEventDefinition) {
                        processDiagramCanvas.drawThrowingCompensateEvent(graphicInfo, scaleFactor);
                    } else {
                        processDiagramCanvas.drawThrowingNoneEvent(graphicInfo, scaleFactor);
                    }
                } else {
                    processDiagramCanvas.drawThrowingNoneEvent(graphicInfo, scaleFactor);
                }

            }
        });
        this.activityDrawInstructions.put(EndEvent.class, new ActivityDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                EndEvent endEvent = (EndEvent) flowNode;
                if (endEvent.getEventDefinitions() != null && !endEvent.getEventDefinitions().isEmpty()) {
                    if (endEvent.getEventDefinitions().get(0) instanceof ErrorEventDefinition) {
                        processDiagramCanvas.drawErrorEndEvent(flowNode.getName(), graphicInfo, scaleFactor);
                    } else {
                        processDiagramCanvas.drawNoneEndEvent(graphicInfo, scaleFactor);
                    }
                } else {
                    processDiagramCanvas.drawNoneEndEvent(graphicInfo, scaleFactor);
                }

            }
        });
        this.activityDrawInstructions.put(Task.class, new ActivityDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawTask(flowNode.getName(), graphicInfo);
            }
        });
        this.activityDrawInstructions.put(UserTask.class, new ActivityDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawUserTask(flowNode.getName(), graphicInfo, scaleFactor);
            }
        });
        this.activityDrawInstructions.put(ScriptTask.class, new ActivityDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawScriptTask(flowNode.getName(), graphicInfo, scaleFactor);
            }
        });
        this.activityDrawInstructions.put(ServiceTask.class, new ActivityDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                ServiceTask serviceTask = (ServiceTask) flowNode;
                if ("camel".equalsIgnoreCase(serviceTask.getType())) {
                    processDiagramCanvas.drawCamelTask(serviceTask.getName(), graphicInfo, scaleFactor);
                } else if ("mule".equalsIgnoreCase(serviceTask.getType())) {
                    processDiagramCanvas.drawMuleTask(serviceTask.getName(), graphicInfo, scaleFactor);
                } else {
                    processDiagramCanvas.drawServiceTask(serviceTask.getName(), graphicInfo, scaleFactor);
                }

            }
        });
        this.activityDrawInstructions.put(ReceiveTask.class, new ActivityDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawReceiveTask(flowNode.getName(), graphicInfo, scaleFactor);
            }
        });
        this.activityDrawInstructions.put(SendTask.class, new ActivityDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawSendTask(flowNode.getName(), graphicInfo, scaleFactor);
            }
        });
        this.activityDrawInstructions.put(ManualTask.class, new ActivityDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawManualTask(flowNode.getName(), graphicInfo, scaleFactor);
            }
        });
        this.activityDrawInstructions.put(BusinessRuleTask.class, new ActivityDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawBusinessRuleTask(flowNode.getName(), graphicInfo, scaleFactor);
            }
        });
        this.activityDrawInstructions.put(ExclusiveGateway.class, new ActivityDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawExclusiveGateway(graphicInfo, scaleFactor);
            }
        });
        this.activityDrawInstructions.put(InclusiveGateway.class, new ActivityDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawInclusiveGateway(graphicInfo, scaleFactor);
            }
        });
        this.activityDrawInstructions.put(ParallelGateway.class, new ActivityDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawParallelGateway(graphicInfo, scaleFactor);
            }
        });
        this.activityDrawInstructions.put(EventGateway.class, new ActivityDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawEventBasedGateway(graphicInfo, scaleFactor);
            }
        });
        this.activityDrawInstructions.put(BoundaryEvent.class, new ActivityDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                BoundaryEvent boundaryEvent = (BoundaryEvent) flowNode;
                if (boundaryEvent.getEventDefinitions() != null && !boundaryEvent.getEventDefinitions().isEmpty()) {
                    if (boundaryEvent.getEventDefinitions().get(0) instanceof TimerEventDefinition) {
                        processDiagramCanvas.drawCatchingTimerEvent(flowNode.getName(), graphicInfo, boundaryEvent.isCancelActivity(), scaleFactor);
                    } else if (boundaryEvent.getEventDefinitions().get(0) instanceof ErrorEventDefinition) {
                        processDiagramCanvas.drawCatchingErrorEvent(graphicInfo, boundaryEvent.isCancelActivity(), scaleFactor);
                    } else if (boundaryEvent.getEventDefinitions().get(0) instanceof SignalEventDefinition) {
                        processDiagramCanvas.drawCatchingSignalEvent(flowNode.getName(), graphicInfo, boundaryEvent.isCancelActivity(), scaleFactor);
                    } else if (boundaryEvent.getEventDefinitions().get(0) instanceof MessageEventDefinition) {
                        processDiagramCanvas.drawCatchingMessageEvent(flowNode.getName(), graphicInfo, boundaryEvent.isCancelActivity(), scaleFactor);
                    } else if (boundaryEvent.getEventDefinitions().get(0) instanceof CompensateEventDefinition) {
                        processDiagramCanvas.drawCatchingCompensateEvent(graphicInfo, boundaryEvent.isCancelActivity(), scaleFactor);
                    }
                }

            }
        });
        this.activityDrawInstructions.put(SubProcess.class, new ActivityDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                if (graphicInfo.getExpanded() != null && !graphicInfo.getExpanded()) {
                    processDiagramCanvas.drawCollapsedSubProcess(flowNode.getName(), graphicInfo, false);
                } else {
                    processDiagramCanvas.drawExpandedSubProcess(flowNode.getName(), graphicInfo, false, scaleFactor);
                }

            }
        });
        this.activityDrawInstructions.put(EventSubProcess.class, new ActivityDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                if (graphicInfo.getExpanded() != null && !graphicInfo.getExpanded()) {
                    processDiagramCanvas.drawCollapsedSubProcess(flowNode.getName(), graphicInfo, true);
                } else {
                    processDiagramCanvas.drawExpandedSubProcess(flowNode.getName(), graphicInfo, true, scaleFactor);
                }

            }
        });
        this.activityDrawInstructions.put(CallActivity.class, new ActivityDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawCollapsedCallActivity(flowNode.getName(), graphicInfo);
            }
        });
        this.artifactDrawInstructions.put(TextAnnotation.class, new ArtifactDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, Artifact artifact) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(artifact.getId());
                TextAnnotation textAnnotation = (TextAnnotation) artifact;
                processDiagramCanvas.drawTextAnnotation(textAnnotation.getText(), graphicInfo);
            }
        });
        this.artifactDrawInstructions.put(Association.class, new ArtifactDrawInstruction() {
            @Override
            public void draw(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, Artifact artifact) {
                Association association = (Association) artifact;
                String sourceRef = association.getSourceRef();
                String targetRef = association.getTargetRef();
                BaseElement sourceElement = bpmnModel.getFlowElement(sourceRef);
                BaseElement targetElement = bpmnModel.getFlowElement(targetRef);
                if (sourceElement == null) {
                    sourceElement = bpmnModel.getArtifact(sourceRef);
                }

                if (targetElement == null) {
                    targetElement = bpmnModel.getArtifact(targetRef);
                }

                List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(artifact.getId());
                graphicInfoList = DefaultProcessDiagramGenerator.connectionPerfectionizer(processDiagramCanvas, bpmnModel, (BaseElement) sourceElement, (BaseElement) targetElement, graphicInfoList);
                int[] xPoints = new int[graphicInfoList.size()];
                int[] yPoints = new int[graphicInfoList.size()];

                for (int i = 1; i < graphicInfoList.size(); ++i) {
                    GraphicInfo graphicInfo = (GraphicInfo) graphicInfoList.get(i);
                    GraphicInfo previousGraphicInfo = (GraphicInfo) graphicInfoList.get(i - 1);
                    if (i == 1) {
                        xPoints[0] = (int) previousGraphicInfo.getX();
                        yPoints[0] = (int) previousGraphicInfo.getY();
                    }

                    xPoints[i] = (int) graphicInfo.getX();
                    yPoints[i] = (int) graphicInfo.getY();
                }

                AssociationDirection associationDirection = association.getAssociationDirection();
                processDiagramCanvas.drawAssociation(xPoints, yPoints, associationDirection, false, scaleFactor);
            }
        });
    }

    protected static List<GraphicInfo> connectionPerfectionizer(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, BaseElement sourceElement, BaseElement targetElement, List<GraphicInfo> graphicInfoList) {
        GraphicInfo sourceGraphicInfo = bpmnModel.getGraphicInfo(sourceElement.getId());
        GraphicInfo targetGraphicInfo = bpmnModel.getGraphicInfo(targetElement.getId());
        SHAPE_TYPE sourceShapeType = getShapeType(sourceElement);
        SHAPE_TYPE targetShapeType = getShapeType(targetElement);
        return processDiagramCanvas.connectionPerfectionizer(sourceShapeType, targetShapeType, sourceGraphicInfo, targetGraphicInfo, graphicInfoList);
    }

    protected static SHAPE_TYPE getShapeType(BaseElement baseElement) {
        if (!(baseElement instanceof Task) && !(baseElement instanceof Activity) && !(baseElement instanceof TextAnnotation)) {
            if (baseElement instanceof Gateway) {
                return SHAPE_TYPE.Rhombus;
            } else {
                return baseElement instanceof Event ? SHAPE_TYPE.Ellipse : null;
            }
        } else {
            return SHAPE_TYPE.Rectangle;
        }
    }

    protected static GraphicInfo getLineCenter(List<GraphicInfo> graphicInfoList) {
        GraphicInfo gi = new GraphicInfo();
        int[] xPoints = new int[graphicInfoList.size()];
        int[] yPoints = new int[graphicInfoList.size()];
        double length = 0.0D;
        double[] lengths = new double[graphicInfoList.size()];
        lengths[0] = 0.0D;

        int p1;
        GraphicInfo graphicInfo1;
        for (p1 = 1; p1 < graphicInfoList.size(); ++p1) {
            GraphicInfo graphicInfo = (GraphicInfo) graphicInfoList.get(p1);
            graphicInfo1 = (GraphicInfo) graphicInfoList.get(p1 - 1);
            if (p1 == 1) {
                xPoints[0] = (int) graphicInfo1.getX();
                yPoints[0] = (int) graphicInfo1.getY();
            }

            xPoints[p1] = (int) graphicInfo.getX();
            yPoints[p1] = (int) graphicInfo.getY();
            length += Math.sqrt(Math.pow((double) ((int) graphicInfo.getX() - (int) graphicInfo1.getX()), 2.0D) + Math.pow((double) ((int) graphicInfo.getY() - (int) graphicInfo1.getY()), 2.0D));
            lengths[p1] = length;
        }

        double m = length / 2.0D;
        p1 = 0;
        int p2 = 1;

        for (int i = 1; i < lengths.length; ++i) {
            double len = lengths[i];
            p1 = i - 1;
            p2 = i;
            if (len > m) {
                break;
            }
        }

        graphicInfo1 = (GraphicInfo) graphicInfoList.get(p1);
        GraphicInfo graphicInfo2 = (GraphicInfo) graphicInfoList.get(p2);
        double AB = (double) ((int) graphicInfo2.getX() - (int) graphicInfo1.getX());
        double OA = (double) ((int) graphicInfo2.getY() - (int) graphicInfo1.getY());
        double OB = lengths[p2] - lengths[p1];
        double ob = m - lengths[p1];
        double ab = AB * ob / OB;
        double oa = OA * ob / OB;
        double mx = graphicInfo1.getX() + ab;
        double my = graphicInfo1.getY() + oa;
        gi.setX(mx);
        gi.setY(my);
        return gi;
    }

    private static void drawHighLight(DefaultProcessDiagramCanvas processDiagramCanvas, GraphicInfo graphicInfo) {
        processDiagramCanvas.drawHighLight((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo.getWidth(), (int) graphicInfo.getHeight());
    }

    protected static DefaultProcessDiagramCanvas initProcessDiagramCanvas(BpmnModel bpmnModel, String imageType, String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader) {
        double minX = 1.7976931348623157E308D;
        double maxX = 0.0D;
        double minY = 1.7976931348623157E308D;
        double maxY = 0.0D;

        GraphicInfo graphicInfo;
        for (Iterator var14 = bpmnModel.getPools().iterator(); var14.hasNext(); maxY = graphicInfo.getY() + graphicInfo.getHeight()) {
            Pool pool = (Pool) var14.next();
            graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
            minX = graphicInfo.getX();
            maxX = graphicInfo.getX() + graphicInfo.getWidth();
            minY = graphicInfo.getY();
        }

        List<FlowNode> flowNodes = gatherAllFlowNodes(bpmnModel);
        Iterator var24 = flowNodes.iterator();

        label155:
        while (var24.hasNext()) {
            FlowNode flowNode = (FlowNode) var24.next();
            GraphicInfo flowNodeGraphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            if (flowNodeGraphicInfo.getX() + flowNodeGraphicInfo.getWidth() > maxX) {
                maxX = flowNodeGraphicInfo.getX() + flowNodeGraphicInfo.getWidth();
            }

            if (flowNodeGraphicInfo.getX() < minX) {
                minX = flowNodeGraphicInfo.getX();
            }

            if (flowNodeGraphicInfo.getY() + flowNodeGraphicInfo.getHeight() > maxY) {
                maxY = flowNodeGraphicInfo.getY() + flowNodeGraphicInfo.getHeight();
            }

            if (flowNodeGraphicInfo.getY() < minY) {
                minY = flowNodeGraphicInfo.getY();
            }

            Iterator var18 = flowNode.getOutgoingFlows().iterator();

            while (true) {
                List graphicInfoList;
                do {
                    if (!var18.hasNext()) {
                        continue label155;
                    }

                    SequenceFlow sequenceFlow = (SequenceFlow) var18.next();
                    graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(sequenceFlow.getId());
                } while (graphicInfoList == null);

                Iterator var21 = graphicInfoList.iterator();

                while (var21.hasNext()) {
                    graphicInfo = (GraphicInfo) var21.next();
                    if (graphicInfo.getX() > maxX) {
                        maxX = graphicInfo.getX();
                    }

                    if (graphicInfo.getX() < minX) {
                        minX = graphicInfo.getX();
                    }

                    if (graphicInfo.getY() > maxY) {
                        maxY = graphicInfo.getY();
                    }

                    if (graphicInfo.getY() < minY) {
                        minY = graphicInfo.getY();
                    }
                }
            }
        }

        List<Artifact> artifacts = gatherAllArtifacts(bpmnModel);
        Iterator var27 = artifacts.iterator();

        while (var27.hasNext()) {
            Artifact artifact = (Artifact) var27.next();
            GraphicInfo artifactGraphicInfo = bpmnModel.getGraphicInfo(artifact.getId());
            if (artifactGraphicInfo != null) {
                if (artifactGraphicInfo.getX() + artifactGraphicInfo.getWidth() > maxX) {
                    maxX = artifactGraphicInfo.getX() + artifactGraphicInfo.getWidth();
                }

                if (artifactGraphicInfo.getX() < minX) {
                    minX = artifactGraphicInfo.getX();
                }

                if (artifactGraphicInfo.getY() + artifactGraphicInfo.getHeight() > maxY) {
                    maxY = artifactGraphicInfo.getY() + artifactGraphicInfo.getHeight();
                }

                if (artifactGraphicInfo.getY() < minY) {
                    minY = artifactGraphicInfo.getY();
                }
            }

            List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(artifact.getId());
            if (graphicInfoList != null) {
                Iterator var35 = graphicInfoList.iterator();

                while (var35.hasNext()) {
                    graphicInfo = (GraphicInfo) var35.next();
                    if (graphicInfo.getX() > maxX) {
                        maxX = graphicInfo.getX();
                    }

                    if (graphicInfo.getX() < minX) {
                        minX = graphicInfo.getX();
                    }

                    if (graphicInfo.getY() > maxY) {
                        maxY = graphicInfo.getY();
                    }

                    if (graphicInfo.getY() < minY) {
                        minY = graphicInfo.getY();
                    }
                }
            }
        }

        int nrOfLanes = 0;
        Iterator var30 = bpmnModel.getProcesses().iterator();

        while (var30.hasNext()) {
            Process process = (Process) var30.next();
            Iterator var34 = process.getLanes().iterator();

            while (var34.hasNext()) {
                Lane l = (Lane) var34.next();
                ++nrOfLanes;
                graphicInfo = bpmnModel.getGraphicInfo(l.getId());
                if (graphicInfo.getX() + graphicInfo.getWidth() > maxX) {
                    maxX = graphicInfo.getX() + graphicInfo.getWidth();
                }

                if (graphicInfo.getX() < minX) {
                    minX = graphicInfo.getX();
                }

                if (graphicInfo.getY() + graphicInfo.getHeight() > maxY) {
                    maxY = graphicInfo.getY() + graphicInfo.getHeight();
                }

                if (graphicInfo.getY() < minY) {
                    minY = graphicInfo.getY();
                }
            }
        }

        if (flowNodes.isEmpty() && bpmnModel.getPools().isEmpty() && nrOfLanes == 0) {
            minX = 0.0D;
            minY = 0.0D;
        }

        return new DefaultProcessDiagramCanvas((int) maxX + 10, (int) maxY + 10, (int) minX, (int) minY, imageType, activityFontName, labelFontName, annotationFontName, customClassLoader);
    }

    protected static List<Artifact> gatherAllArtifacts(BpmnModel bpmnModel) {
        List<Artifact> artifacts = new ArrayList();
        Iterator var2 = bpmnModel.getProcesses().iterator();

        while (var2.hasNext()) {
            Process process = (Process) var2.next();
            artifacts.addAll(process.getArtifacts());
        }

        return artifacts;
    }

    protected static List<FlowNode> gatherAllFlowNodes(BpmnModel bpmnModel) {
        List<FlowNode> flowNodes = new ArrayList();
        Iterator var2 = bpmnModel.getProcesses().iterator();

        while (var2.hasNext()) {
            Process process = (Process) var2.next();
            flowNodes.addAll(gatherAllFlowNodes((FlowElementsContainer) process));
        }

        return flowNodes;
    }

    protected static List<FlowNode> gatherAllFlowNodes(FlowElementsContainer flowElementsContainer) {
        List<FlowNode> flowNodes = new ArrayList();
        Iterator var2 = flowElementsContainer.getFlowElements().iterator();

        while (var2.hasNext()) {
            FlowElement flowElement = (FlowElement) var2.next();
            if (flowElement instanceof FlowNode) {
                flowNodes.add((FlowNode) flowElement);
            }

            if (flowElement instanceof FlowElementsContainer) {
                flowNodes.addAll(gatherAllFlowNodes((FlowElementsContainer) flowElement));
            }
        }

        return flowNodes;
    }

    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities, List<String> highLightedFlows, String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader, double scaleFactor) {
        return this.generateProcessDiagram(bpmnModel, imageType, highLightedActivities, highLightedFlows, activityFontName, labelFontName, annotationFontName, customClassLoader, scaleFactor).generateImage(imageType);
    }

    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities, List<String> highLightedFlows) {
        return this.generateDiagram(bpmnModel, imageType, highLightedActivities, highLightedFlows, (String) null, (String) null, (String) null, (ClassLoader) null, 1.0D);
    }

    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities, List<String> highLightedFlows, double scaleFactor) {
        return this.generateDiagram(bpmnModel, imageType, highLightedActivities, highLightedFlows, (String) null, (String) null, (String) null, (ClassLoader) null, scaleFactor);
    }

    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities) {
        return this.generateDiagram(bpmnModel, imageType, highLightedActivities, Collections.emptyList());
    }

    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities, double scaleFactor) {
        return this.generateDiagram(bpmnModel, imageType, highLightedActivities, Collections.emptyList(), scaleFactor);
    }

    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel, String imageType, String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader) {
        return this.generateDiagram(bpmnModel, imageType, Collections.emptyList(), Collections.emptyList(), activityFontName, labelFontName, annotationFontName, customClassLoader, 1.0D);
    }

    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel, String imageType, String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader, double scaleFactor) {
        return this.generateDiagram(bpmnModel, imageType, Collections.emptyList(), Collections.emptyList(), activityFontName, labelFontName, annotationFontName, customClassLoader, scaleFactor);
    }

    @Override
    public InputStream generatePngDiagram(BpmnModel bpmnModel) {
        return this.generatePngDiagram(bpmnModel, 1.0D);
    }

    @Override
    public InputStream generatePngDiagram(BpmnModel bpmnModel, double scaleFactor) {
        return this.generateDiagram(bpmnModel, "png", Collections.emptyList(), Collections.emptyList(), scaleFactor);
    }

    @Override
    public InputStream generateJpgDiagram(BpmnModel bpmnModel) {
        return this.generateJpgDiagram(bpmnModel, 1.0D);
    }

    @Override
    public InputStream generateJpgDiagram(BpmnModel bpmnModel, double scaleFactor) {
        return this.generateDiagram(bpmnModel, "jpg", Collections.emptyList(), Collections.emptyList());
    }

    public BufferedImage generateImage(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities, List<String> highLightedFlows, String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader, double scaleFactor) {
        return this.generateProcessDiagram(bpmnModel, imageType, highLightedActivities, highLightedFlows, activityFontName, labelFontName, annotationFontName, customClassLoader, scaleFactor).generateBufferedImage(imageType);
    }

    public BufferedImage generateImage(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities, List<String> highLightedFlows, double scaleFactor) {
        return this.generateImage(bpmnModel, imageType, highLightedActivities, highLightedFlows, (String) null, (String) null, (String) null, (ClassLoader) null, scaleFactor);
    }

    @Override
    public BufferedImage generatePngImage(BpmnModel bpmnModel, double scaleFactor) {
        return this.generateImage(bpmnModel, "png", Collections.emptyList(), Collections.emptyList(), scaleFactor);
    }

    protected DefaultProcessDiagramCanvas generateProcessDiagram(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities, List<String> highLightedFlows, String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader, double scaleFactor) {
        this.prepareBpmnModel(bpmnModel);
        DefaultProcessDiagramCanvas processDiagramCanvas = initProcessDiagramCanvas(bpmnModel, imageType, activityFontName, labelFontName, annotationFontName, customClassLoader);
        Iterator var12 = bpmnModel.getPools().iterator();

        while (var12.hasNext()) {
            Pool pool = (Pool) var12.next();
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
            processDiagramCanvas.drawPoolOrLane(pool.getName(), graphicInfo);
        }

        var12 = bpmnModel.getProcesses().iterator();

        Process process;
        Iterator var20;
        while (var12.hasNext()) {
            process = (Process) var12.next();
            var20 = process.getLanes().iterator();

            while (var20.hasNext()) {
                Lane lane = (Lane) var20.next();
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(lane.getId());
                processDiagramCanvas.drawPoolOrLane(lane.getName(), graphicInfo);
            }
        }

        var12 = bpmnModel.getProcesses().iterator();

        while (var12.hasNext()) {
            process = (Process) var12.next();
            var20 = process.findFlowElementsOfType(FlowNode.class).iterator();

            while (var20.hasNext()) {
                FlowNode flowNode = (FlowNode) var20.next();
                this.drawActivity(processDiagramCanvas, bpmnModel, flowNode, highLightedActivities, highLightedFlows, scaleFactor);
            }
        }

        var12 = bpmnModel.getProcesses().iterator();

        while (true) {
            List subProcesses;
            do {
                if (!var12.hasNext()) {
                    return processDiagramCanvas;
                }

                process = (Process) var12.next();
                var20 = process.getArtifacts().iterator();

                while (var20.hasNext()) {
                    Artifact artifact = (Artifact) var20.next();
                    this.drawArtifact(processDiagramCanvas, bpmnModel, artifact);
                }

                subProcesses = process.findFlowElementsOfType(SubProcess.class, true);
            } while (subProcesses == null);

            Iterator var24 = subProcesses.iterator();

            while (var24.hasNext()) {
                SubProcess subProcess = (SubProcess) var24.next();
                Iterator var17 = subProcess.getArtifacts().iterator();

                while (var17.hasNext()) {
                    Artifact subProcessArtifact = (Artifact) var17.next();
                    this.drawArtifact(processDiagramCanvas, bpmnModel, subProcessArtifact);
                }
            }
        }
    }

    protected void prepareBpmnModel(BpmnModel bpmnModel) {
        List<GraphicInfo> allGraphicInfos = new ArrayList();
        if (bpmnModel.getLocationMap() != null) {
            allGraphicInfos.addAll(bpmnModel.getLocationMap().values());
        }

        if (bpmnModel.getLabelLocationMap() != null) {
            allGraphicInfos.addAll(bpmnModel.getLabelLocationMap().values());
        }

        if (bpmnModel.getFlowLocationMap() != null) {
            Iterator var3 = bpmnModel.getFlowLocationMap().values().iterator();

            while (var3.hasNext()) {
                List<GraphicInfo> flowGraphicInfos = (List) var3.next();
                allGraphicInfos.addAll(flowGraphicInfos);
            }
        }

        if (allGraphicInfos.size() > 0) {
            boolean needsTranslationX = false;
            boolean needsTranslationY = false;
            double lowestX = 0.0D;
            double lowestY = 0.0D;
            Iterator var9 = allGraphicInfos.iterator();

            double translationY;
            while (var9.hasNext()) {
                GraphicInfo graphicInfo = (GraphicInfo) var9.next();
                translationY = graphicInfo.getX();
                double y = graphicInfo.getY();
                if (translationY < lowestX) {
                    needsTranslationX = true;
                    lowestX = translationY;
                }

                if (y < lowestY) {
                    needsTranslationY = true;
                    lowestY = y;
                }
            }

            if (needsTranslationX || needsTranslationY) {
                double translationX = Math.abs(lowestX);
                translationY = Math.abs(lowestY);
                Iterator var18 = allGraphicInfos.iterator();

                while (var18.hasNext()) {
                    GraphicInfo graphicInfo = (GraphicInfo) var18.next();
                    if (needsTranslationX) {
                        graphicInfo.setX(graphicInfo.getX() + translationX);
                    }

                    if (needsTranslationY) {
                        graphicInfo.setY(graphicInfo.getY() + translationY);
                    }
                }
            }
        }

    }

    protected void drawActivity(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode, List<String> highLightedActivities, List<String> highLightedFlows, double scaleFactor) {
        ActivityDrawInstruction drawInstruction = (ActivityDrawInstruction) this.activityDrawInstructions.get(flowNode.getClass());
        boolean highLighted;
        if (drawInstruction != null) {
            drawInstruction.draw(processDiagramCanvas, bpmnModel, flowNode);
            boolean multiInstanceSequential = false;
            boolean multiInstanceParallel = false;
            highLighted = false;
            if (flowNode instanceof Activity) {
                Activity activity = (Activity) flowNode;
                MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = activity.getLoopCharacteristics();
                if (multiInstanceLoopCharacteristics != null) {
                    multiInstanceSequential = multiInstanceLoopCharacteristics.isSequential();
                    multiInstanceParallel = !multiInstanceSequential;
                }
            }

            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            if (!(flowNode instanceof SubProcess)) {
                if (flowNode instanceof CallActivity) {
                    highLighted = true;
                }
            } else {
                highLighted = graphicInfo.getExpanded() != null && !graphicInfo.getExpanded();
            }

            if (scaleFactor == 1.0D) {
                processDiagramCanvas.drawActivityMarkers((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo.getWidth(), (int) graphicInfo.getHeight(), multiInstanceSequential, multiInstanceParallel, highLighted);
            }

            if (highLightedActivities.contains(flowNode.getId())) {
                drawHighLight(processDiagramCanvas, bpmnModel.getGraphicInfo(flowNode.getId()));
            }
        }

        Iterator var25 = flowNode.getOutgoingFlows().iterator();

        while (var25.hasNext()) {
            SequenceFlow sequenceFlow = (SequenceFlow) var25.next();
            highLighted = highLightedFlows.contains(sequenceFlow.getId());
            String defaultFlow = null;
            if (flowNode instanceof Activity) {
                defaultFlow = ((Activity) flowNode).getDefaultFlow();
            } else if (flowNode instanceof Gateway) {
                defaultFlow = ((Gateway) flowNode).getDefaultFlow();
            }

            boolean isDefault = false;
            if (defaultFlow != null && defaultFlow.equalsIgnoreCase(sequenceFlow.getId())) {
                isDefault = true;
            }

            boolean drawConditionalIndicator = sequenceFlow.getConditionExpression() != null && !(flowNode instanceof Gateway);
            String sourceRef = sequenceFlow.getSourceRef();
            String targetRef = sequenceFlow.getTargetRef();
            FlowElement sourceElement = bpmnModel.getFlowElement(sourceRef);
            FlowElement targetElement = bpmnModel.getFlowElement(targetRef);
            List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(sequenceFlow.getId());
            if (graphicInfoList != null && graphicInfoList.size() > 0) {
                graphicInfoList = connectionPerfectionizer(processDiagramCanvas, bpmnModel, sourceElement, targetElement, graphicInfoList);
                int[] xPoints = new int[graphicInfoList.size()];
                int[] yPoints = new int[graphicInfoList.size()];

                for (int i = 1; i < graphicInfoList.size(); ++i) {
                    GraphicInfo graphicInfo = (GraphicInfo) graphicInfoList.get(i);
                    GraphicInfo previousGraphicInfo = (GraphicInfo) graphicInfoList.get(i - 1);
                    if (i == 1) {
                        xPoints[0] = (int) previousGraphicInfo.getX();
                        yPoints[0] = (int) previousGraphicInfo.getY();
                    }

                    xPoints[i] = (int) graphicInfo.getX();
                    yPoints[i] = (int) graphicInfo.getY();
                }

                processDiagramCanvas.drawSequenceflow(xPoints, yPoints, drawConditionalIndicator, isDefault, highLighted, scaleFactor);
                GraphicInfo labelGraphicInfo = bpmnModel.getLabelGraphicInfo(sequenceFlow.getId());
                if (labelGraphicInfo != null) {
                    processDiagramCanvas.drawLabel(sequenceFlow.getName(), labelGraphicInfo, false);
                } else {
                    GraphicInfo lineCenter = getLineCenter(graphicInfoList);
                    lineCenter.setY(lineCenter.getY() + 5d);
                    processDiagramCanvas.drawLabel(sequenceFlow.getName(), lineCenter, true);
                }
            }
        }

        if (flowNode instanceof FlowElementsContainer) {
            var25 = ((FlowElementsContainer) flowNode).getFlowElements().iterator();

            while (var25.hasNext()) {
                FlowElement nestedFlowElement = (FlowElement) var25.next();
                if (nestedFlowElement instanceof FlowNode) {
                    this.drawActivity(processDiagramCanvas, bpmnModel, (FlowNode) nestedFlowElement, highLightedActivities, highLightedFlows, scaleFactor);
                }
            }
        }

    }

    protected void drawArtifact(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, Artifact artifact) {
        ArtifactDrawInstruction drawInstruction = (ArtifactDrawInstruction) this.artifactDrawInstructions.get(artifact.getClass());
        if (drawInstruction != null) {
            drawInstruction.draw(processDiagramCanvas, bpmnModel, artifact);
        }

    }

    public Map<Class<? extends BaseElement>, ActivityDrawInstruction> getActivityDrawInstructions() {
        return this.activityDrawInstructions;
    }

    public void setActivityDrawInstructions(Map<Class<? extends BaseElement>, ActivityDrawInstruction> activityDrawInstructions) {
        this.activityDrawInstructions = activityDrawInstructions;
    }

    public Map<Class<? extends BaseElement>, ArtifactDrawInstruction> getArtifactDrawInstructions() {
        return this.artifactDrawInstructions;
    }

    public void setArtifactDrawInstructions(Map<Class<? extends BaseElement>, ArtifactDrawInstruction> artifactDrawInstructions) {
        this.artifactDrawInstructions = artifactDrawInstructions;
    }

    protected interface ArtifactDrawInstruction {
        void draw(DefaultProcessDiagramCanvas var1, BpmnModel var2, Artifact var3);
    }

    protected interface ActivityDrawInstruction {
        void draw(DefaultProcessDiagramCanvas var1, BpmnModel var2, FlowNode var3);
    }
}
