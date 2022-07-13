package com.scmt.activiti.controller.modeler;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import org.activiti.bpmn.model.AssociationDirection;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.image.exception.ActivitiImageException;
import org.activiti.image.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.*;
import java.awt.geom.Ellipse2D.Double;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultProcessDiagramCanvas {
    protected static final Logger LOGGER = LoggerFactory.getLogger(org.activiti.image.impl.DefaultProcessDiagramCanvas.class);
    protected static final int ARROW_WIDTH = 5;
    protected static final int CONDITIONAL_INDICATOR_WIDTH = 16;
    protected static final int DEFAULT_INDICATOR_WIDTH = 10;
    protected static final int MARKER_WIDTH = 12;
    protected static final int FONT_SIZE = 11;
    protected static final int FONT_SPACING = 2;
    protected static final int TEXT_PADDING = 3;
    protected static final int ANNOTATION_TEXT_PADDING = 7;
    protected static final int LINE_HEIGHT = 13;
    protected static Color TASK_BOX_COLOR = new Color(249, 249, 249);
    protected static Color SUBPROCESS_BOX_COLOR = new Color(255, 255, 255);
    protected static Color EVENT_COLOR = new Color(255, 255, 255);
    protected static Color CONNECTION_COLOR = new Color(88, 88, 88);
    protected static Color CONDITIONAL_INDICATOR_COLOR = new Color(255, 255, 255);
    protected static Color HIGHLIGHT_COLOR;
    protected static Color LABEL_COLOR;
    protected static Color TASK_BORDER_COLOR;
    protected static Color EVENT_BORDER_COLOR;
    protected static Color SUBPROCESS_BORDER_COLOR;
    protected static Font LABEL_FONT;
    protected static Font ANNOTATION_FONT;
    protected static Stroke THICK_TASK_BORDER_STROKE;
    protected static Stroke GATEWAY_TYPE_STROKE;
    protected static Stroke END_EVENT_STROKE;
    protected static Stroke MULTI_INSTANCE_STROKE;
    protected static Stroke EVENT_SUBPROCESS_STROKE;
    protected static Stroke NON_INTERRUPTING_EVENT_STROKE;
    protected static Stroke HIGHLIGHT_FLOW_STROKE;
    protected static Stroke ANNOTATION_STROKE;
    protected static Stroke ASSOCIATION_STROKE;
    protected static int ICON_PADDING;
    protected static BufferedImage USERTASK_IMAGE;
    protected static BufferedImage SCRIPTTASK_IMAGE;
    protected static BufferedImage SERVICETASK_IMAGE;
    protected static BufferedImage RECEIVETASK_IMAGE;
    protected static BufferedImage SENDTASK_IMAGE;
    protected static BufferedImage MANUALTASK_IMAGE;
    protected static BufferedImage BUSINESS_RULE_TASK_IMAGE;
    protected static BufferedImage SHELL_TASK_IMAGE;
    protected static BufferedImage MULE_TASK_IMAGE;
    protected static BufferedImage CAMEL_TASK_IMAGE;
    protected static BufferedImage TIMER_IMAGE;
    protected static BufferedImage COMPENSATE_THROW_IMAGE;
    protected static BufferedImage COMPENSATE_CATCH_IMAGE;
    protected static BufferedImage ERROR_THROW_IMAGE;
    protected static BufferedImage ERROR_CATCH_IMAGE;
    protected static BufferedImage MESSAGE_THROW_IMAGE;
    protected static BufferedImage MESSAGE_CATCH_IMAGE;
    protected static BufferedImage SIGNAL_CATCH_IMAGE;
    protected static BufferedImage SIGNAL_THROW_IMAGE;

    static {
        HIGHLIGHT_COLOR = Color.RED;
        LABEL_COLOR = new Color(112, 146, 190);
        TASK_BORDER_COLOR = new Color(187, 187, 187);
        EVENT_BORDER_COLOR = new Color(88, 88, 88);
        SUBPROCESS_BORDER_COLOR = new Color(0, 0, 0);
        LABEL_FONT = null;
        ANNOTATION_FONT = null;
        THICK_TASK_BORDER_STROKE = new BasicStroke(3.0F);
        GATEWAY_TYPE_STROKE = new BasicStroke(3.0F);
        END_EVENT_STROKE = new BasicStroke(3.0F);
        MULTI_INSTANCE_STROKE = new BasicStroke(1.3F);
        EVENT_SUBPROCESS_STROKE = new BasicStroke(1.0F, 0, 0, 1.0F, new float[]{1.0F}, 0.0F);
        NON_INTERRUPTING_EVENT_STROKE = new BasicStroke(1.0F, 0, 0, 1.0F, new float[]{4.0F, 3.0F}, 0.0F);
        HIGHLIGHT_FLOW_STROKE = new BasicStroke(1.3F);
        ANNOTATION_STROKE = new BasicStroke(2.0F);
        ASSOCIATION_STROKE = new BasicStroke(2.0F, 0, 0, 1.0F, new float[]{2.0F, 2.0F}, 0.0F);
        ICON_PADDING = 5;
    }

    protected int canvasWidth = -1;
    protected int canvasHeight = -1;
    protected int minX = -1;
    protected int minY = -1;
    protected BufferedImage processDiagram;
    protected Graphics2D g;
    protected FontMetrics fontMetrics;
    protected boolean closed;
    protected ClassLoader customClassLoader;
    protected String activityFontName = "Arial";
    protected String labelFontName = "Arial";
    protected String annotationFontName = "Arial";

    public DefaultProcessDiagramCanvas(int width, int height, int minX, int minY, String imageType, String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader) {
        this.canvasWidth = width;
        this.canvasHeight = height;
        this.minX = minX;
        this.minY = minY;
        if (activityFontName != null) {
            this.activityFontName = activityFontName;
        }

        if (labelFontName != null) {
            this.labelFontName = labelFontName;
        }

        if (annotationFontName != null) {
            this.annotationFontName = annotationFontName;
        }

        this.customClassLoader = customClassLoader;
        this.initialize(imageType);
    }

    public DefaultProcessDiagramCanvas(int width, int height, int minX, int minY, String imageType) {
        this.canvasWidth = width;
        this.canvasHeight = height;
        this.minX = minX;
        this.minY = minY;
        this.initialize(imageType);
    }

    private static Shape createShape(org.activiti.image.impl.DefaultProcessDiagramCanvas.SHAPE_TYPE shapeType, GraphicInfo graphicInfo) {
        if (org.activiti.image.impl.DefaultProcessDiagramCanvas.SHAPE_TYPE.Rectangle.equals(shapeType)) {
            return new Rectangle2D.Double(graphicInfo.getX(), graphicInfo.getY(), graphicInfo.getWidth(), graphicInfo.getHeight());
        } else if (org.activiti.image.impl.DefaultProcessDiagramCanvas.SHAPE_TYPE.Rhombus.equals(shapeType)) {
            Path2D.Double rhombus = new Path2D.Double();
            rhombus.moveTo(graphicInfo.getX(), graphicInfo.getY() + graphicInfo.getHeight() / 2.0D);
            rhombus.lineTo(graphicInfo.getX() + graphicInfo.getWidth() / 2.0D, graphicInfo.getY() + graphicInfo.getHeight());
            rhombus.lineTo(graphicInfo.getX() + graphicInfo.getWidth(), graphicInfo.getY() + graphicInfo.getHeight() / 2.0D);
            rhombus.lineTo(graphicInfo.getX() + graphicInfo.getWidth() / 2.0D, graphicInfo.getY());
            rhombus.lineTo(graphicInfo.getX(), graphicInfo.getY() + graphicInfo.getHeight() / 2.0D);
            rhombus.closePath();
            return rhombus;
        } else {
            return org.activiti.image.impl.DefaultProcessDiagramCanvas.SHAPE_TYPE.Ellipse.equals(shapeType) ? new Double(graphicInfo.getX(), graphicInfo.getY(), graphicInfo.getWidth(), graphicInfo.getHeight()) : null;
        }
    }

    private static Point getIntersection(Shape shape, Line2D.Double line) {
        if (shape instanceof Ellipse2D) {
            return getEllipseIntersection(shape, line);
        } else {
            return !(shape instanceof Rectangle2D) && !(shape instanceof Path2D) ? null : getShapeIntersection(shape, line);
        }
    }

    private static Point getEllipseIntersection(Shape shape, Line2D.Double line) {
        double angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
        double x = shape.getBounds2D().getWidth() / 2.0D * Math.cos(angle) + shape.getBounds2D().getCenterX();
        double y = shape.getBounds2D().getHeight() / 2.0D * Math.sin(angle) + shape.getBounds2D().getCenterY();
        Point p = new Point();
        p.setLocation(x, y);
        return p;
    }

    private static Point getShapeIntersection(Shape shape, Line2D.Double line) {
        PathIterator it = shape.getPathIterator((AffineTransform) null);
        double[] coords = new double[6];
        double[] pos = new double[2];
        new Line2D.Double();

        for (; !it.isDone(); it.next()) {
            int type = it.currentSegment(coords);
            switch (type) {
                case 0:
                    pos[0] = coords[0];
                    pos[1] = coords[1];
                    break;
                case 1:
                    Line2D.Double l = new Line2D.Double(pos[0], pos[1], coords[0], coords[1]);
                    if (line.intersectsLine(l)) {
                        return getLinesIntersection(line, l);
                    }

                    pos[0] = coords[0];
                    pos[1] = coords[1];
                case 2:
                case 3:
                case 4:
            }
        }

        return null;
    }

    private static Point getLinesIntersection(Line2D a, Line2D b) {
        double d = (a.getX1() - a.getX2()) * (b.getY2() - b.getY1()) - (a.getY1() - a.getY2()) * (b.getX2() - b.getX1());
        double da = (a.getX1() - b.getX1()) * (b.getY2() - b.getY1()) - (a.getY1() - b.getY1()) * (b.getX2() - b.getX1());
        double ta = da / d;
        Point p = new Point();
        p.setLocation(a.getX1() + ta * (a.getX2() - a.getX1()), a.getY1() + ta * (a.getY2() - a.getY1()));
        return p;
    }

    public void initialize(String imageType) {
        if ("png".equalsIgnoreCase(imageType)) {
            this.processDiagram = new BufferedImage(this.canvasWidth, this.canvasHeight, 2);
        } else {
            this.processDiagram = new BufferedImage(this.canvasWidth, this.canvasHeight, 1);
        }

        this.g = this.processDiagram.createGraphics();
        if (!"png".equalsIgnoreCase(imageType)) {
            this.g.setBackground(new Color(255, 255, 255, 0));
            this.g.clearRect(0, 0, this.canvasWidth, this.canvasHeight);
        }

        this.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.g.setPaint(Color.black);
        Font font = new Font(this.activityFontName, 1, 11);
        this.g.setFont(font);
        this.fontMetrics = this.g.getFontMetrics();
        LABEL_FONT = new Font(this.labelFontName, Font.PLAIN, 13);
        ANNOTATION_FONT = new Font(this.annotationFontName, 0, 11);

        try {
            USERTASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/userTask.png", this.customClassLoader));
            SCRIPTTASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/scriptTask.png", this.customClassLoader));
            SERVICETASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/serviceTask.png", this.customClassLoader));
            RECEIVETASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/receiveTask.png", this.customClassLoader));
            SENDTASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/sendTask.png", this.customClassLoader));
            MANUALTASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/manualTask.png", this.customClassLoader));
            BUSINESS_RULE_TASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/businessRuleTask.png", this.customClassLoader));
            SHELL_TASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/shellTask.png", this.customClassLoader));
            CAMEL_TASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/camelTask.png", this.customClassLoader));
            MULE_TASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/muleTask.png", this.customClassLoader));
            TIMER_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/timer.png", this.customClassLoader));
            COMPENSATE_THROW_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/compensate-throw.png", this.customClassLoader));
            COMPENSATE_CATCH_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/compensate.png", this.customClassLoader));
            ERROR_THROW_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/error-throw.png", this.customClassLoader));
            ERROR_CATCH_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/error.png", this.customClassLoader));
            MESSAGE_THROW_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/message-throw.png", this.customClassLoader));
            MESSAGE_CATCH_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/message.png", this.customClassLoader));
            SIGNAL_THROW_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/signal-throw.png", this.customClassLoader));
            SIGNAL_CATCH_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/signal.png", this.customClassLoader));
        } catch (IOException var4) {
            LOGGER.warn("Could not load image for process diagram creation: {}", var4.getMessage());
        }

    }

    public InputStream generateImage(String imageType) {
        if (this.closed) {
            throw new ActivitiImageException("ProcessDiagramGenerator already closed");
        } else {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            try {
                ImageIO.write(this.processDiagram, imageType, out);
            } catch (IOException var11) {
                throw new ActivitiImageException("Error while generating process image", var11);
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException var10) {
                }

            }

            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public BufferedImage generateBufferedImage(String imageType) {
        if (this.closed) {
            throw new ActivitiImageException("ProcessDiagramGenerator already closed");
        } else {
            this.minX = this.minX <= 5 ? 5 : this.minX;
            this.minY = this.minY <= 5 ? 5 : this.minY;
            BufferedImage imageToSerialize = this.processDiagram;
            if (this.minX >= 0 && this.minY >= 0) {
                imageToSerialize = this.processDiagram.getSubimage(this.minX - 5, this.minY - 5, this.canvasWidth - this.minX + 5, this.canvasHeight - this.minY + 5);
            }

            return imageToSerialize;
        }
    }

    public void close() {
        this.g.dispose();
        this.closed = true;
    }

    public void drawNoneStartEvent(GraphicInfo graphicInfo) {
        this.drawStartEvent(graphicInfo, (BufferedImage) null, 1.0D);
    }

    public void drawTimerStartEvent(GraphicInfo graphicInfo, double scaleFactor) {
        this.drawStartEvent(graphicInfo, TIMER_IMAGE, scaleFactor);
    }

    public void drawSignalStartEvent(GraphicInfo graphicInfo, double scaleFactor) {
        this.drawStartEvent(graphicInfo, SIGNAL_CATCH_IMAGE, scaleFactor);
    }

    public void drawMessageStartEvent(GraphicInfo graphicInfo, double scaleFactor) {
        this.drawStartEvent(graphicInfo, MESSAGE_CATCH_IMAGE, scaleFactor);
    }

    public void drawStartEvent(GraphicInfo graphicInfo, BufferedImage image, double scaleFactor) {
        Paint originalPaint = this.g.getPaint();
        this.g.setPaint(EVENT_COLOR);
        Ellipse2D circle = new Double(graphicInfo.getX(), graphicInfo.getY(), graphicInfo.getWidth(), graphicInfo.getHeight());
        this.g.fill(circle);
        this.g.setPaint(EVENT_BORDER_COLOR);
        this.g.draw(circle);
        this.g.setPaint(originalPaint);
        if (image != null) {
            int imageX = (int) Math.round(graphicInfo.getX() + graphicInfo.getWidth() / 2.0D - (double) (image.getWidth() / 2) * scaleFactor);
            int imageY = (int) Math.round(graphicInfo.getY() + graphicInfo.getHeight() / 2.0D - (double) (image.getHeight() / 2) * scaleFactor);
            this.g.drawImage(image, imageX, imageY, (int) ((double) image.getWidth() / scaleFactor), (int) ((double) image.getHeight() / scaleFactor), (ImageObserver) null);
        }

    }

    public void drawNoneEndEvent(GraphicInfo graphicInfo, double scaleFactor) {
        Paint originalPaint = this.g.getPaint();
        Stroke originalStroke = this.g.getStroke();
        this.g.setPaint(EVENT_COLOR);
        Ellipse2D circle = new Double(graphicInfo.getX(), graphicInfo.getY(), graphicInfo.getWidth(), graphicInfo.getHeight());
        this.g.fill(circle);
        this.g.setPaint(EVENT_BORDER_COLOR);
        if (scaleFactor == 1.0D) {
            this.g.setStroke(END_EVENT_STROKE);
        } else {
            this.g.setStroke(new BasicStroke(2.0F));
        }

        this.g.draw(circle);
        this.g.setStroke(originalStroke);
        this.g.setPaint(originalPaint);
    }

    public void drawErrorEndEvent(String name, GraphicInfo graphicInfo, double scaleFactor) {
        this.drawErrorEndEvent(graphicInfo, scaleFactor);
        if (scaleFactor == 1.0D) {
            this.drawLabel(name, graphicInfo);
        }

    }

    public void drawErrorEndEvent(GraphicInfo graphicInfo, double scaleFactor) {
        this.drawNoneEndEvent(graphicInfo, scaleFactor);
        this.g.drawImage(ERROR_THROW_IMAGE, (int) (graphicInfo.getX() + graphicInfo.getWidth() / 4.0D), (int) (graphicInfo.getY() + graphicInfo.getHeight() / 4.0D), (int) ((double) ERROR_THROW_IMAGE.getWidth() / scaleFactor), (int) ((double) ERROR_THROW_IMAGE.getHeight() / scaleFactor), (ImageObserver) null);
    }

    public void drawErrorStartEvent(GraphicInfo graphicInfo, double scaleFactor) {
        this.drawNoneStartEvent(graphicInfo);
        this.g.drawImage(ERROR_CATCH_IMAGE, (int) (graphicInfo.getX() + graphicInfo.getWidth() / 4.0D), (int) (graphicInfo.getY() + graphicInfo.getHeight() / 4.0D), (int) ((double) ERROR_CATCH_IMAGE.getWidth() / scaleFactor), (int) ((double) ERROR_CATCH_IMAGE.getHeight() / scaleFactor), (ImageObserver) null);
    }

    public void drawCatchingEvent(GraphicInfo graphicInfo, boolean isInterrupting, BufferedImage image, String eventType, double scaleFactor) {
        Ellipse2D outerCircle = new Double(graphicInfo.getX(), graphicInfo.getY(), graphicInfo.getWidth(), graphicInfo.getHeight());
        int innerCircleSize = (int) (4.0D / scaleFactor);
        if (innerCircleSize == 0) {
            innerCircleSize = 1;
        }

        int innerCircleX = (int) graphicInfo.getX() + innerCircleSize;
        int innerCircleY = (int) graphicInfo.getY() + innerCircleSize;
        int innerCircleWidth = (int) graphicInfo.getWidth() - 2 * innerCircleSize;
        int innerCircleHeight = (int) graphicInfo.getHeight() - 2 * innerCircleSize;
        Ellipse2D innerCircle = new Double((double) innerCircleX, (double) innerCircleY, (double) innerCircleWidth, (double) innerCircleHeight);
        Paint originalPaint = this.g.getPaint();
        Stroke originalStroke = this.g.getStroke();
        this.g.setPaint(EVENT_COLOR);
        this.g.fill(outerCircle);
        this.g.setPaint(EVENT_BORDER_COLOR);
        if (!isInterrupting) {
            this.g.setStroke(NON_INTERRUPTING_EVENT_STROKE);
        }

        this.g.draw(outerCircle);
        this.g.setStroke(originalStroke);
        this.g.setPaint(originalPaint);
        this.g.draw(innerCircle);
        if (image != null) {
            int imageX = (int) (graphicInfo.getX() + graphicInfo.getWidth() / 2.0D - (double) (image.getWidth() / 2) * scaleFactor);
            int imageY = (int) (graphicInfo.getY() + graphicInfo.getHeight() / 2.0D - (double) (image.getHeight() / 2) * scaleFactor);
            if (scaleFactor == 1.0D && "timer".equals(eventType)) {
                ++imageX;
                ++imageY;
            }

            this.g.drawImage(image, imageX, imageY, (int) ((double) image.getWidth() / scaleFactor), (int) ((double) image.getHeight() / scaleFactor), (ImageObserver) null);
        }

    }

    public void drawCatchingCompensateEvent(String name, GraphicInfo graphicInfo, boolean isInterrupting, double scaleFactor) {
        this.drawCatchingCompensateEvent(graphicInfo, isInterrupting, scaleFactor);
        this.drawLabel(name, graphicInfo);
    }

    public void drawCatchingCompensateEvent(GraphicInfo graphicInfo, boolean isInterrupting, double scaleFactor) {
        this.drawCatchingEvent(graphicInfo, isInterrupting, COMPENSATE_CATCH_IMAGE, "compensate", scaleFactor);
    }

    public void drawCatchingTimerEvent(String name, GraphicInfo graphicInfo, boolean isInterrupting, double scaleFactor) {
        this.drawCatchingTimerEvent(graphicInfo, isInterrupting, scaleFactor);
        this.drawLabel(name, graphicInfo);
    }

    public void drawCatchingTimerEvent(GraphicInfo graphicInfo, boolean isInterrupting, double scaleFactor) {
        this.drawCatchingEvent(graphicInfo, isInterrupting, TIMER_IMAGE, "timer", scaleFactor);
    }

    public void drawCatchingErrorEvent(String name, GraphicInfo graphicInfo, boolean isInterrupting, double scaleFactor) {
        this.drawCatchingErrorEvent(graphicInfo, isInterrupting, scaleFactor);
        this.drawLabel(name, graphicInfo);
    }

    public void drawCatchingErrorEvent(GraphicInfo graphicInfo, boolean isInterrupting, double scaleFactor) {
        this.drawCatchingEvent(graphicInfo, isInterrupting, ERROR_CATCH_IMAGE, "error", scaleFactor);
    }

    public void drawCatchingSignalEvent(String name, GraphicInfo graphicInfo, boolean isInterrupting, double scaleFactor) {
        this.drawCatchingSignalEvent(graphicInfo, isInterrupting, scaleFactor);
        this.drawLabel(name, graphicInfo);
    }

    public void drawCatchingSignalEvent(GraphicInfo graphicInfo, boolean isInterrupting, double scaleFactor) {
        this.drawCatchingEvent(graphicInfo, isInterrupting, SIGNAL_CATCH_IMAGE, "signal", scaleFactor);
    }

    public void drawCatchingMessageEvent(GraphicInfo graphicInfo, boolean isInterrupting, double scaleFactor) {
        this.drawCatchingEvent(graphicInfo, isInterrupting, MESSAGE_CATCH_IMAGE, "message", scaleFactor);
    }

    public void drawCatchingMessageEvent(String name, GraphicInfo graphicInfo, boolean isInterrupting, double scaleFactor) {
        this.drawCatchingEvent(graphicInfo, isInterrupting, MESSAGE_CATCH_IMAGE, "message", scaleFactor);
        this.drawLabel(name, graphicInfo);
    }

    public void drawThrowingCompensateEvent(GraphicInfo graphicInfo, double scaleFactor) {
        this.drawCatchingEvent(graphicInfo, true, COMPENSATE_THROW_IMAGE, "compensate", scaleFactor);
    }

    public void drawThrowingSignalEvent(GraphicInfo graphicInfo, double scaleFactor) {
        this.drawCatchingEvent(graphicInfo, true, SIGNAL_THROW_IMAGE, "signal", scaleFactor);
    }

    public void drawThrowingNoneEvent(GraphicInfo graphicInfo, double scaleFactor) {
        this.drawCatchingEvent(graphicInfo, true, (BufferedImage) null, "none", scaleFactor);
    }

    public void drawSequenceflow(int srcX, int srcY, int targetX, int targetY, boolean conditional, double scaleFactor) {
        this.drawSequenceflow(srcX, srcY, targetX, targetY, conditional, false, scaleFactor);
    }

    public void drawSequenceflow(int srcX, int srcY, int targetX, int targetY, boolean conditional, boolean highLighted, double scaleFactor) {
        Paint originalPaint = this.g.getPaint();
        if (highLighted) {
            this.g.setPaint(HIGHLIGHT_COLOR);
        }

        Line2D.Double line = new Line2D.Double((double) srcX, (double) srcY, (double) targetX, (double) targetY);
        this.g.draw(line);
        this.drawArrowHead(line, scaleFactor);
        if (conditional) {
            this.drawConditionalSequenceFlowIndicator(line, scaleFactor);
        }

        if (highLighted) {
            this.g.setPaint(originalPaint);
        }

    }

    public void drawAssociation(int[] xPoints, int[] yPoints, AssociationDirection associationDirection, boolean highLighted, double scaleFactor) {
        boolean conditional = false;
        boolean isDefault = false;
        this.drawConnection(xPoints, yPoints, conditional, isDefault, "association", associationDirection, highLighted, scaleFactor);
    }

    public void drawSequenceflow(int[] xPoints, int[] yPoints, boolean conditional, boolean isDefault, boolean highLighted, double scaleFactor) {
        this.drawConnection(xPoints, yPoints, conditional, isDefault, "sequenceFlow", AssociationDirection.ONE, highLighted, scaleFactor);
    }

    public void drawConnection(int[] xPoints, int[] yPoints, boolean conditional, boolean isDefault, String connectionType, AssociationDirection associationDirection, boolean highLighted, double scaleFactor) {
        Paint originalPaint = this.g.getPaint();
        Stroke originalStroke = this.g.getStroke();
        this.g.setPaint(CONNECTION_COLOR);
        if (connectionType.equals("association")) {
            this.g.setStroke(ASSOCIATION_STROKE);
        } else if (highLighted) {
            this.g.setPaint(HIGHLIGHT_COLOR);
            this.g.setStroke(HIGHLIGHT_FLOW_STROKE);
        }

        for (int i = 1; i < xPoints.length; ++i) {
            Integer sourceX = xPoints[i - 1];
            Integer sourceY = yPoints[i - 1];
            Integer targetX = xPoints[i];
            Integer targetY = yPoints[i];
            Line2D.Double line = new Line2D.Double((double) sourceX, (double) sourceY, (double) targetX, (double) targetY);
            this.g.draw(line);
        }

        Line2D.Double line;
        if (isDefault) {
            line = new Line2D.Double((double) xPoints[0], (double) yPoints[0], (double) xPoints[1], (double) yPoints[1]);
            this.drawDefaultSequenceFlowIndicator(line, scaleFactor);
        }

        if (conditional) {
            line = new Line2D.Double((double) xPoints[0], (double) yPoints[0], (double) xPoints[1], (double) yPoints[1]);
            this.drawConditionalSequenceFlowIndicator(line, scaleFactor);
        }

        if (associationDirection.equals(AssociationDirection.ONE) || associationDirection.equals(AssociationDirection.BOTH)) {
            line = new Line2D.Double((double) xPoints[xPoints.length - 2], (double) yPoints[xPoints.length - 2], (double) xPoints[xPoints.length - 1], (double) yPoints[xPoints.length - 1]);
            this.drawArrowHead(line, scaleFactor);
        }

        if (associationDirection.equals(AssociationDirection.BOTH)) {
            line = new Line2D.Double((double) xPoints[1], (double) yPoints[1], (double) xPoints[0], (double) yPoints[0]);
            this.drawArrowHead(line, scaleFactor);
        }

        this.g.setPaint(originalPaint);
        this.g.setStroke(originalStroke);
    }

    public void drawSequenceflowWithoutArrow(int srcX, int srcY, int targetX, int targetY, boolean conditional, double scaleFactor) {
        this.drawSequenceflowWithoutArrow(srcX, srcY, targetX, targetY, conditional, false, scaleFactor);
    }

    public void drawSequenceflowWithoutArrow(int srcX, int srcY, int targetX, int targetY, boolean conditional, boolean highLighted, double scaleFactor) {
        Paint originalPaint = this.g.getPaint();
        if (highLighted) {
            this.g.setPaint(HIGHLIGHT_COLOR);
        }

        Line2D.Double line = new Line2D.Double((double) srcX, (double) srcY, (double) targetX, (double) targetY);
        this.g.draw(line);
        if (conditional) {
            this.drawConditionalSequenceFlowIndicator(line, scaleFactor);
        }

        if (highLighted) {
            this.g.setPaint(originalPaint);
        }

    }

    public void drawArrowHead(Line2D.Double line, double scaleFactor) {
        int doubleArrowWidth = (int) (10.0D / scaleFactor);
        if (doubleArrowWidth == 0) {
            doubleArrowWidth = 2;
        }

        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(0, 0);
        int arrowHeadPoint = (int) (-5.0D / scaleFactor);
        if (arrowHeadPoint == 0) {
            arrowHeadPoint = -1;
        }

        arrowHead.addPoint(arrowHeadPoint, -doubleArrowWidth);
        arrowHeadPoint = (int) (5.0D / scaleFactor);
        if (arrowHeadPoint == 0) {
            arrowHeadPoint = 1;
        }

        arrowHead.addPoint(arrowHeadPoint, -doubleArrowWidth);
        AffineTransform transformation = new AffineTransform();
        transformation.setToIdentity();
        double angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
        transformation.translate(line.x2, line.y2);
        transformation.rotate(angle - 1.5707963267948966D);
        AffineTransform originalTransformation = this.g.getTransform();
        this.g.setTransform(transformation);
        this.g.fill(arrowHead);
        this.g.setTransform(originalTransformation);
    }

    public void drawDefaultSequenceFlowIndicator(Line2D.Double line, double scaleFactor) {
        double length = 10.0D / scaleFactor;
        double halfOfLength = length / 2.0D;
        double f = 8.0D;
        Line2D.Double defaultIndicator = new Line2D.Double(-halfOfLength, 0.0D, halfOfLength, 0.0D);
        double angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
        double dx = f * Math.cos(angle);
        double dy = f * Math.sin(angle);
        double x1 = line.x1 + dx;
        double y1 = line.y1 + dy;
        AffineTransform transformation = new AffineTransform();
        transformation.setToIdentity();
        transformation.translate(x1, y1);
        transformation.rotate(angle - 2.356194490192345D);
        AffineTransform originalTransformation = this.g.getTransform();
        this.g.setTransform(transformation);
        this.g.draw(defaultIndicator);
        this.g.setTransform(originalTransformation);
    }

    public void drawConditionalSequenceFlowIndicator(Line2D.Double line, double scaleFactor) {
        if (scaleFactor <= 1.0D) {
            int horizontal = 11;
            int halfOfHorizontal = horizontal / 2;
            int halfOfVertical = 8;
            Polygon conditionalIndicator = new Polygon();
            conditionalIndicator.addPoint(0, 0);
            conditionalIndicator.addPoint(-halfOfHorizontal, halfOfVertical);
            conditionalIndicator.addPoint(0, 16);
            conditionalIndicator.addPoint(halfOfHorizontal, halfOfVertical);
            AffineTransform transformation = new AffineTransform();
            transformation.setToIdentity();
            double angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
            transformation.translate(line.x1, line.y1);
            transformation.rotate(angle - 1.5707963267948966D);
            AffineTransform originalTransformation = this.g.getTransform();
            this.g.setTransform(transformation);
            this.g.draw(conditionalIndicator);
            Paint originalPaint = this.g.getPaint();
            this.g.setPaint(CONDITIONAL_INDICATOR_COLOR);
            this.g.fill(conditionalIndicator);
            this.g.setPaint(originalPaint);
            this.g.setTransform(originalTransformation);
        }
    }

    public void drawTask(BufferedImage icon, String name, GraphicInfo graphicInfo, double scaleFactor) {
        this.drawTask(name, graphicInfo);
        this.g.drawImage(icon, (int) (graphicInfo.getX() + (double) ICON_PADDING / scaleFactor), (int) (graphicInfo.getY() + (double) ICON_PADDING / scaleFactor), (int) ((double) icon.getWidth() / scaleFactor), (int) ((double) icon.getHeight() / scaleFactor), (ImageObserver) null);
    }

    public void drawTask(String name, GraphicInfo graphicInfo) {
        this.drawTask(name, graphicInfo, false);
    }

    public void drawPoolOrLane(String name, GraphicInfo graphicInfo) {
        int x = (int) graphicInfo.getX();
        int y = (int) graphicInfo.getY();
        int width = (int) graphicInfo.getWidth();
        int height = (int) graphicInfo.getHeight();
        this.g.drawRect(x, y, width, height);
        if (name != null && name.length() > 0) {
            int availableTextSpace = height - 6;
            AffineTransform transformation = new AffineTransform();
            transformation.setToIdentity();
            transformation.rotate(4.71238898038469D);
            Font currentFont = this.g.getFont();
            Font theDerivedFont = currentFont.deriveFont(transformation);
            this.g.setFont(theDerivedFont);
            String truncated = this.fitTextToWidth(name, availableTextSpace);
            int realWidth = this.fontMetrics.stringWidth(truncated);
            this.g.drawString(truncated, x + 2 + this.fontMetrics.getHeight(), 3 + y + availableTextSpace - (availableTextSpace - realWidth) / 2);
            this.g.setFont(currentFont);
        }

    }

    protected void drawTask(String name, GraphicInfo graphicInfo, boolean thickBorder) {
        Paint originalPaint = this.g.getPaint();
        int x = (int) graphicInfo.getX();
        int y = (int) graphicInfo.getY();
        int width = (int) graphicInfo.getWidth();
        int height = (int) graphicInfo.getHeight();
        this.g.setPaint(TASK_BOX_COLOR);
        int arcR = 6;
        if (thickBorder) {
            arcR = 3;
        }

        RoundRectangle2D rect = new RoundRectangle2D.Double((double) x, (double) y, (double) width, (double) height, (double) arcR, (double) arcR);
        this.g.fill(rect);
        this.g.setPaint(TASK_BORDER_COLOR);
        if (thickBorder) {
            Stroke originalStroke = this.g.getStroke();
            this.g.setStroke(THICK_TASK_BORDER_STROKE);
            this.g.draw(rect);
            this.g.setStroke(originalStroke);
        } else {
            this.g.draw(rect);
        }

        this.g.setPaint(originalPaint);
        if (name != null && name.length() > 0) {
            int boxWidth = width - 6;
            int boxHeight = height - 16 - ICON_PADDING - ICON_PADDING - 12 - 2 - 2;
            int boxX = x + width / 2 - boxWidth / 2;
            int boxY = y + height / 2 - boxHeight / 2 + ICON_PADDING + ICON_PADDING - 2 - 2;
            this.drawMultilineCentredText(name, boxX, boxY, boxWidth, boxHeight);
        }

    }

    protected void drawMultilineCentredText(String text, int x, int y, int boxWidth, int boxHeight) {
        this.drawMultilineText(text, x, y, boxWidth, boxHeight, true);
    }

    protected void drawMultilineAnnotationText(String text, int x, int y, int boxWidth, int boxHeight) {
        this.drawMultilineText(text, x, y, boxWidth, boxHeight, false);
    }

    protected void drawMultilineText(String text, int x, int y, int boxWidth, int boxHeight, boolean centered) {
        AttributedString attributedString = new AttributedString(text);
        attributedString.addAttribute(TextAttribute.FONT, this.g.getFont());
        attributedString.addAttribute(TextAttribute.FOREGROUND, Color.black);
        AttributedCharacterIterator characterIterator = attributedString.getIterator();
        int currentHeight = 0;
        List<TextLayout> layouts = new ArrayList();
        String lastLine = null;
        LineBreakMeasurer measurer = new LineBreakMeasurer(characterIterator, this.g.getFontRenderContext());

        int currentY;
        int currentX;
        for (TextLayout layout = null; measurer.getPosition() < characterIterator.getEndIndex() && currentHeight <= boxHeight; currentHeight += currentX) {
            currentY = measurer.getPosition();
            layout = measurer.nextLayout((float) boxWidth);
            currentX = Float.valueOf(layout.getDescent() + layout.getAscent() + layout.getLeading()).intValue();
            if (currentHeight + currentX > boxHeight) {
                if (!layouts.isEmpty()) {
                    layouts.remove(layouts.size() - 1);
                    if (lastLine.length() >= 4) {
                        lastLine = lastLine.substring(0, lastLine.length() - 4) + "...";
                    }

                    layouts.add(new TextLayout(lastLine, this.g.getFont(), this.g.getFontRenderContext()));
                }
                break;
            }

            layouts.add(layout);
            lastLine = text.substring(currentY, measurer.getPosition());
        }

        currentY = y + (centered ? (boxHeight - currentHeight) / 2 : 0);
        currentX = 0;

        TextLayout textLayout;
        for (Iterator var16 = layouts.iterator(); var16.hasNext(); currentY = (int) ((float) currentY + textLayout.getDescent() + textLayout.getLeading())) {
            textLayout = (TextLayout) var16.next();
            currentY = (int) ((float) currentY + textLayout.getAscent());
            currentX = x + (centered ? (boxWidth - java.lang.Double.valueOf(textLayout.getBounds().getWidth()).intValue()) / 2 : 0);
            textLayout.draw(this.g, (float) currentX, (float) currentY);
        }

    }

    protected String fitTextToWidth(String original, int width) {
        String text = original;

        for (int maxWidth = width - 10; this.fontMetrics.stringWidth(text + "...") > maxWidth && text.length() > 0; text = text.substring(0, text.length() - 1)) {
        }

        if (!text.equals(original)) {
            text = text + "...";
        }

        return text;
    }

    public void drawUserTask(String name, GraphicInfo graphicInfo, double scaleFactor) {
        this.drawTask(USERTASK_IMAGE, name, graphicInfo, scaleFactor);
    }

    public void drawScriptTask(String name, GraphicInfo graphicInfo, double scaleFactor) {
        this.drawTask(SCRIPTTASK_IMAGE, name, graphicInfo, scaleFactor);
    }

    public void drawServiceTask(String name, GraphicInfo graphicInfo, double scaleFactor) {
        this.drawTask(SERVICETASK_IMAGE, name, graphicInfo, scaleFactor);
    }

    public void drawReceiveTask(String name, GraphicInfo graphicInfo, double scaleFactor) {
        this.drawTask(RECEIVETASK_IMAGE, name, graphicInfo, scaleFactor);
    }

    public void drawSendTask(String name, GraphicInfo graphicInfo, double scaleFactor) {
        this.drawTask(SENDTASK_IMAGE, name, graphicInfo, scaleFactor);
    }

    public void drawManualTask(String name, GraphicInfo graphicInfo, double scaleFactor) {
        this.drawTask(MANUALTASK_IMAGE, name, graphicInfo, scaleFactor);
    }

    public void drawBusinessRuleTask(String name, GraphicInfo graphicInfo, double scaleFactor) {
        this.drawTask(BUSINESS_RULE_TASK_IMAGE, name, graphicInfo, scaleFactor);
    }

    public void drawCamelTask(String name, GraphicInfo graphicInfo, double scaleFactor) {
        this.drawTask(CAMEL_TASK_IMAGE, name, graphicInfo, scaleFactor);
    }

    public void drawMuleTask(String name, GraphicInfo graphicInfo, double scaleFactor) {
        this.drawTask(MULE_TASK_IMAGE, name, graphicInfo, scaleFactor);
    }

    public void drawExpandedSubProcess(String name, GraphicInfo graphicInfo, Boolean isTriggeredByEvent, double scaleFactor) {
        RoundRectangle2D rect = new RoundRectangle2D.Double(graphicInfo.getX(), graphicInfo.getY(), graphicInfo.getWidth(), graphicInfo.getHeight(), 8.0D, 8.0D);
        if (isTriggeredByEvent) {
            Stroke originalStroke = this.g.getStroke();
            this.g.setStroke(EVENT_SUBPROCESS_STROKE);
            this.g.draw(rect);
            this.g.setStroke(originalStroke);
        } else {
            Paint originalPaint = this.g.getPaint();
            this.g.setPaint(SUBPROCESS_BOX_COLOR);
            this.g.fill(rect);
            this.g.setPaint(SUBPROCESS_BORDER_COLOR);
            this.g.draw(rect);
            this.g.setPaint(originalPaint);
        }

        if (scaleFactor == 1.0D && name != null && !name.isEmpty()) {
            String text = this.fitTextToWidth(name, (int) graphicInfo.getWidth());
            this.g.drawString(text, (int) graphicInfo.getX() + 10, (int) graphicInfo.getY() + 15);
        }

    }

    public void drawCollapsedSubProcess(String name, GraphicInfo graphicInfo, Boolean isTriggeredByEvent) {
        this.drawCollapsedTask(name, graphicInfo, false);
    }

    public void drawCollapsedCallActivity(String name, GraphicInfo graphicInfo) {
        this.drawCollapsedTask(name, graphicInfo, true);
    }

    protected void drawCollapsedTask(String name, GraphicInfo graphicInfo, boolean thickBorder) {
        this.drawTask(name, graphicInfo, thickBorder);
    }

    public void drawCollapsedMarker(int x, int y, int width, int height) {
        int rectangleWidth = 12;
        int rectangleHeight = 12;
        Rectangle rect = new Rectangle(x + (width - rectangleWidth) / 2, y + height - rectangleHeight - 3, rectangleWidth, rectangleHeight);
        this.g.draw(rect);
        Line2D.Double line = new Line2D.Double(rect.getCenterX(), rect.getY() + 2.0D, rect.getCenterX(), rect.getMaxY() - 2.0D);
        this.g.draw(line);
        line = new Line2D.Double(rect.getMinX() + 2.0D, rect.getCenterY(), rect.getMaxX() - 2.0D, rect.getCenterY());
        this.g.draw(line);
    }

    public void drawActivityMarkers(int x, int y, int width, int height, boolean multiInstanceSequential, boolean multiInstanceParallel, boolean collapsed) {
        if (collapsed) {
            if (!multiInstanceSequential && !multiInstanceParallel) {
                this.drawCollapsedMarker(x, y, width, height);
            } else {
                this.drawCollapsedMarker(x - 6 - 2, y, width, height);
                if (multiInstanceSequential) {
                    this.drawMultiInstanceMarker(true, x + 6 + 2, y, width, height);
                } else {
                    this.drawMultiInstanceMarker(false, x + 6 + 2, y, width, height);
                }
            }
        } else if (multiInstanceSequential) {
            this.drawMultiInstanceMarker(true, x, y, width, height);
        } else if (multiInstanceParallel) {
            this.drawMultiInstanceMarker(false, x, y, width, height);
        }

    }

    public void drawGateway(GraphicInfo graphicInfo) {
        Polygon rhombus = new Polygon();
        int x = (int) graphicInfo.getX();
        int y = (int) graphicInfo.getY();
        int width = (int) graphicInfo.getWidth();
        int height = (int) graphicInfo.getHeight();
        rhombus.addPoint(x, y + height / 2);
        rhombus.addPoint(x + width / 2, y + height);
        rhombus.addPoint(x + width, y + height / 2);
        rhombus.addPoint(x + width / 2, y);
        this.g.draw(rhombus);
    }

    public void drawParallelGateway(GraphicInfo graphicInfo, double scaleFactor) {
        this.drawGateway(graphicInfo);
        int x = (int) graphicInfo.getX();
        int y = (int) graphicInfo.getY();
        int width = (int) graphicInfo.getWidth();
        int height = (int) graphicInfo.getHeight();
        if (scaleFactor == 1.0D) {
            Stroke orginalStroke = this.g.getStroke();
            this.g.setStroke(GATEWAY_TYPE_STROKE);
            Line2D.Double line = new Line2D.Double((double) (x + 10), (double) (y + height / 2), (double) (x + width - 10), (double) (y + height / 2));
            this.g.draw(line);
            line = new Line2D.Double((double) (x + width / 2), (double) (y + height - 10), (double) (x + width / 2), (double) (y + 10));
            this.g.draw(line);
            this.g.setStroke(orginalStroke);
        }

    }

    public void drawExclusiveGateway(GraphicInfo graphicInfo, double scaleFactor) {
        this.drawGateway(graphicInfo);
        int x = (int) graphicInfo.getX();
        int y = (int) graphicInfo.getY();
        int width = (int) graphicInfo.getWidth();
        int height = (int) graphicInfo.getHeight();
        int quarterWidth = width / 4;
        int quarterHeight = height / 4;
        if (scaleFactor == 1.0D) {
            Stroke orginalStroke = this.g.getStroke();
            this.g.setStroke(GATEWAY_TYPE_STROKE);
            Line2D.Double line = new Line2D.Double((double) (x + quarterWidth + 3), (double) (y + quarterHeight + 3), (double) (x + 3 * quarterWidth - 3), (double) (y + 3 * quarterHeight - 3));
            this.g.draw(line);
            line = new Line2D.Double((double) (x + quarterWidth + 3), (double) (y + 3 * quarterHeight - 3), (double) (x + 3 * quarterWidth - 3), (double) (y + quarterHeight + 3));
            this.g.draw(line);
            this.g.setStroke(orginalStroke);
        }

    }

    public void drawInclusiveGateway(GraphicInfo graphicInfo, double scaleFactor) {
        this.drawGateway(graphicInfo);
        int x = (int) graphicInfo.getX();
        int y = (int) graphicInfo.getY();
        int width = (int) graphicInfo.getWidth();
        int height = (int) graphicInfo.getHeight();
        int diameter = width / 2;
        if (scaleFactor == 1.0D) {
            Stroke orginalStroke = this.g.getStroke();
            this.g.setStroke(GATEWAY_TYPE_STROKE);
            Double circle = new Double((double) ((width - diameter) / 2 + x), (double) ((height - diameter) / 2 + y), (double) diameter, (double) diameter);
            this.g.draw(circle);
            this.g.setStroke(orginalStroke);
        }

    }

    public void drawEventBasedGateway(GraphicInfo graphicInfo, double scaleFactor) {
        this.drawGateway(graphicInfo);
        if (scaleFactor == 1.0D) {
            int x = (int) graphicInfo.getX();
            int y = (int) graphicInfo.getY();
            int width = (int) graphicInfo.getWidth();
            int height = (int) graphicInfo.getHeight();
            double scale = 0.6D;
            GraphicInfo eventInfo = new GraphicInfo();
            eventInfo.setX((double) x + (double) width * (1.0D - scale) / 2.0D);
            eventInfo.setY((double) y + (double) height * (1.0D - scale) / 2.0D);
            eventInfo.setWidth((double) width * scale);
            eventInfo.setHeight((double) height * scale);
            this.drawCatchingEvent(eventInfo, true, (BufferedImage) null, "eventGateway", scaleFactor);
            double r = (double) width / 6.0D;
            int topX = (int) (0.95D * r);
            int topY = (int) (-0.31D * r);
            int bottomX = (int) (0.59D * r);
            int bottomY = (int) (0.81D * r);
            int[] xPoints = new int[]{0, topX, bottomX, -bottomX, -topX};
            int[] yPoints = new int[]{-((int) r), topY, bottomY, bottomY, topY};
            Polygon pentagon = new Polygon(xPoints, yPoints, 5);
            pentagon.translate(x + width / 2, y + width / 2);
            this.g.drawPolygon(pentagon);
        }

    }

    public void drawMultiInstanceMarker(boolean sequential, int x, int y, int width, int height) {
        int rectangleWidth = 12;
        int rectangleHeight = 12;
        int lineX = x + (width - rectangleWidth) / 2;
        int lineY = y + height - rectangleHeight - 3;
        Stroke orginalStroke = this.g.getStroke();
        this.g.setStroke(MULTI_INSTANCE_STROKE);
        if (sequential) {
            this.g.draw(new Line2D.Double((double) lineX, (double) lineY, (double) (lineX + rectangleWidth), (double) lineY));
            this.g.draw(new Line2D.Double((double) lineX, (double) (lineY + rectangleHeight / 2), (double) (lineX + rectangleWidth), (double) (lineY + rectangleHeight / 2)));
            this.g.draw(new Line2D.Double((double) lineX, (double) (lineY + rectangleHeight), (double) (lineX + rectangleWidth), (double) (lineY + rectangleHeight)));
        } else {
            this.g.draw(new Line2D.Double((double) lineX, (double) lineY, (double) lineX, (double) (lineY + rectangleHeight)));
            this.g.draw(new Line2D.Double((double) (lineX + rectangleWidth / 2), (double) lineY, (double) (lineX + rectangleWidth / 2), (double) (lineY + rectangleHeight)));
            this.g.draw(new Line2D.Double((double) (lineX + rectangleWidth), (double) lineY, (double) (lineX + rectangleWidth), (double) (lineY + rectangleHeight)));
        }

        this.g.setStroke(orginalStroke);
    }

    public void drawHighLight(int x, int y, int width, int height) {
        Paint originalPaint = this.g.getPaint();
        Stroke originalStroke = this.g.getStroke();
        this.g.setPaint(HIGHLIGHT_COLOR);
        this.g.setStroke(THICK_TASK_BORDER_STROKE);
        RoundRectangle2D rect = new RoundRectangle2D.Double((double) x, (double) y, (double) width, (double) height, 20.0D, 20.0D);
        this.g.draw(rect);
        this.g.setPaint(originalPaint);
        this.g.setStroke(originalStroke);
    }

    public void drawTextAnnotation(String text, GraphicInfo graphicInfo) {
        int x = (int) graphicInfo.getX();
        int y = (int) graphicInfo.getY();
        int width = (int) graphicInfo.getWidth();
        int height = (int) graphicInfo.getHeight();
        Font originalFont = this.g.getFont();
        Stroke originalStroke = this.g.getStroke();
        this.g.setFont(ANNOTATION_FONT);
        Path2D path = new Path2D.Double();
        x = (int) ((double) x + 0.5D);
        int lineLength = 18;
        path.moveTo((double) (x + lineLength), (double) y);
        path.lineTo((double) x, (double) y);
        path.lineTo((double) x, (double) (y + height));
        path.lineTo((double) (x + lineLength), (double) (y + height));
        path.lineTo((double) (x + lineLength), (double) (y + height - 1));
        path.lineTo((double) (x + 1), (double) (y + height - 1));
        path.lineTo((double) (x + 1), (double) (y + 1));
        path.lineTo((double) (x + lineLength), (double) (y + 1));
        path.closePath();
        this.g.draw(path);
        int boxWidth = width - 14;
        int boxHeight = height - 14;
        int boxX = x + width / 2 - boxWidth / 2;
        int boxY = y + height / 2 - boxHeight / 2;
        if (text != null && !text.isEmpty()) {
            this.drawMultilineAnnotationText(text, boxX, boxY, boxWidth, boxHeight);
        }

        this.g.setFont(originalFont);
        this.g.setStroke(originalStroke);
    }

    public void drawLabel(String text, GraphicInfo graphicInfo) {
        this.drawLabel(text, graphicInfo, true);
    }

    public void drawLabel(String text, GraphicInfo graphicInfo, boolean centered) {
        float interline = 1.0F;
        if (text != null && text.length() > 0) {
            Paint originalPaint = this.g.getPaint();
            Font originalFont = this.g.getFont();
            this.g.setPaint(LABEL_COLOR);
            this.g.setFont(LABEL_FONT);
            int wrapWidth = 100;
            int textY = (int) graphicInfo.getY();
            AttributedString as = new AttributedString(text);
            as.addAttribute(TextAttribute.FOREGROUND, this.g.getPaint());
            as.addAttribute(TextAttribute.FONT, this.g.getFont());
            AttributedCharacterIterator aci = as.getIterator();
            FontRenderContext frc = new FontRenderContext((AffineTransform) null, true, false);

            TextLayout tl;
            for (LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc); lbm.getPosition() < text.length(); textY = (int) ((float) textY + tl.getDescent() + tl.getLeading() + (interline - 1.0F) * tl.getAscent())) {
                tl = lbm.nextLayout((float) wrapWidth);
                textY = (int) ((float) textY + tl.getAscent());
                Rectangle2D bb = tl.getBounds();
                double tX = graphicInfo.getX();
                if (centered) {
                    tX += (double) ((int) (graphicInfo.getWidth() / 2.0D - bb.getWidth() / 2.0D));
                }

                tl.draw(this.g, (float) tX, (float) textY);
            }

            this.g.setFont(originalFont);
            this.g.setPaint(originalPaint);
        }

    }

    public List<GraphicInfo> connectionPerfectionizer(org.activiti.image.impl.DefaultProcessDiagramCanvas.SHAPE_TYPE sourceShapeType, org.activiti.image.impl.DefaultProcessDiagramCanvas.SHAPE_TYPE targetShapeType, GraphicInfo sourceGraphicInfo, GraphicInfo targetGraphicInfo, List<GraphicInfo> graphicInfoList) {
        Shape shapeFirst = createShape(sourceShapeType, sourceGraphicInfo);
        Shape shapeLast = createShape(targetShapeType, targetGraphicInfo);
        if (graphicInfoList != null && graphicInfoList.size() > 0) {
            GraphicInfo graphicInfoFirst = (GraphicInfo) graphicInfoList.get(0);
            GraphicInfo graphicInfoLast = (GraphicInfo) graphicInfoList.get(graphicInfoList.size() - 1);
            if (shapeFirst != null) {
                graphicInfoFirst.setX(shapeFirst.getBounds2D().getCenterX());
                graphicInfoFirst.setY(shapeFirst.getBounds2D().getCenterY());
            }

            if (shapeLast != null) {
                graphicInfoLast.setX(shapeLast.getBounds2D().getCenterX());
                graphicInfoLast.setY(shapeLast.getBounds2D().getCenterY());
            }

            Point p = null;
            Line2D.Double lineLast;
            if (shapeFirst != null) {
                lineLast = new Line2D.Double(graphicInfoFirst.getX(), graphicInfoFirst.getY(), ((GraphicInfo) graphicInfoList.get(1)).getX(), ((GraphicInfo) graphicInfoList.get(1)).getY());
                p = getIntersection(shapeFirst, lineLast);
                if (p != null) {
                    graphicInfoFirst.setX(p.getX());
                    graphicInfoFirst.setY(p.getY());
                }
            }

            if (shapeLast != null) {
                lineLast = new Line2D.Double(graphicInfoLast.getX(), graphicInfoLast.getY(), ((GraphicInfo) graphicInfoList.get(graphicInfoList.size() - 2)).getX(), ((GraphicInfo) graphicInfoList.get(graphicInfoList.size() - 2)).getY());
                p = getIntersection(shapeLast, lineLast);
                if (p != null) {
                    graphicInfoLast.setX(p.getX());
                    graphicInfoLast.setY(p.getY());
                }
            }
        }

        return graphicInfoList;
    }

    public static enum SHAPE_TYPE {
        Rectangle,
        Rhombus,
        Ellipse;

        private SHAPE_TYPE() {
        }
    }
}

