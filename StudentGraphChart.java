import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class StudentGraphChart extends JPanel {
    private Student student;
    private String chartTitle;
    
    public StudentGraphChart(Student student) {
        this.student = student;
        this.chartTitle = student != null ? "Performance Chart - " + student.getName() : "Performance Chart";
        setPreferredSize(new Dimension(500, 400));
        setBackground(Color.WHITE);
    }
    
    public String getCurrentStudentId() {
        return student != null ? student.getId() : null;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (student == null || student.getSubjectScores().isEmpty()) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("No data to display", getWidth()/2 - 70, getHeight()/2);
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Chart dimensions
        int margin = 50;
        int chartWidth = getWidth() - 2 * margin;
        int chartHeight = getHeight() - 2 * margin - 50; // Extra space for title
        
        // Draw title
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics titleMetrics = g2d.getFontMetrics();
        int titleX = (getWidth() - titleMetrics.stringWidth(chartTitle)) / 2;
        g2d.drawString(chartTitle, titleX, 30);
        
        // Get subject scores
        Map<String, Integer> scores = student.getSubjectScores();
        int numSubjects = scores.size();
        
        if (numSubjects == 0) return;
        
        // Calculate bar dimensions
        int barWidth = Math.max(30, chartWidth / numSubjects - 20);
        int maxScore = 100; // Assuming max score is 100
        
        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        
        // Y-axis
        g2d.drawLine(margin, margin + 20, margin, margin + chartHeight + 20);
        
        // X-axis
        g2d.drawLine(margin, margin + chartHeight + 20, margin + chartWidth, margin + chartHeight + 20);
        
        // Draw Y-axis labels (scores)
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        for (int i = 0; i <= 10; i++) {
            int score = i * 10;
            int y = margin + chartHeight + 20 - (int)((double)score / maxScore * chartHeight);
            g2d.drawString(String.valueOf(score), margin - 25, y + 5);
            
            // Draw grid lines
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawLine(margin, y, margin + chartWidth, y);
            g2d.setColor(Color.BLACK);
        }
        
        // Draw bars and X-axis labels
        int barSpacing = chartWidth / numSubjects;
        int barIndex = 0;
        
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            String subject = entry.getKey();
            int score = entry.getValue();
            
            // Calculate bar position and height
            int barX = margin + barIndex * barSpacing + (barSpacing - barWidth) / 2;
            int barHeight = (int)((double)score / maxScore * chartHeight);
            int barY = margin + chartHeight + 20 - barHeight;
            
            // Choose bar color based on score
            Color barColor;
            if (score >= 90) {
                barColor = new Color(46, 204, 113); // Green - Excellent
            } else if (score >= 75) {
                barColor = new Color(52, 152, 219); // Blue - Good
            } else if (score >= 50) {
                barColor = new Color(241, 196, 15); // Yellow - Needs Improvement
            } else {
                barColor = new Color(231, 76, 60); // Red - Poor
            }
            
            // Draw bar with gradient effect
            GradientPaint gradient = new GradientPaint(
                barX, barY, barColor,
                barX, barY + barHeight, barColor.darker()
            );
            g2d.setPaint(gradient);
            g2d.fillRect(barX, barY, barWidth, barHeight);
            
            // Draw bar border
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect(barX, barY, barWidth, barHeight);
            
            // Draw score on top of bar
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            String scoreText = String.valueOf(score);
            FontMetrics scoreMetrics = g2d.getFontMetrics();
            int scoreX = barX + (barWidth - scoreMetrics.stringWidth(scoreText)) / 2;
            int scoreY = barY - 5;
            
            // Draw white background for score text
            g2d.setColor(Color.WHITE);
            g2d.fillRect(scoreX - 2, scoreY - scoreMetrics.getHeight() + 2, 
                        scoreMetrics.stringWidth(scoreText) + 4, scoreMetrics.getHeight());
            
            g2d.setColor(Color.BLACK);
            g2d.drawString(scoreText, scoreX, scoreY);
            
            // Draw subject name on X-axis
            g2d.setFont(new Font("Arial", Font.PLAIN, 11));
            FontMetrics subjectMetrics = g2d.getFontMetrics();
            int subjectX = barX + (barWidth - subjectMetrics.stringWidth(subject)) / 2;
            int subjectY = margin + chartHeight + 40;
            g2d.drawString(subject, subjectX, subjectY);
            
            barIndex++;
        }
        
        // Draw average line
        double average = student.calculateAverage();
        int avgY = margin + chartHeight + 20 - (int)(average / maxScore * chartHeight);
        
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                                     0, new float[]{9}, 0));
        g2d.drawLine(margin, avgY, margin + chartWidth, avgY);
        
        // Draw average label
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        String avgText = String.format("Average: %.1f", average);
        g2d.drawString(avgText, margin + chartWidth - 100, avgY - 5);
        
        // Draw legend
        drawLegend(g2d, margin, getHeight() - 80);
    }
    
    private void drawLegend(Graphics2D g2d, int x, int y) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        
        String[] labels = {"Excellent (90-100)", "Good (75-89)", "Needs Improvement (50-74)", "Poor (<50)"};
        Color[] colors = {
            new Color(46, 204, 113),
            new Color(52, 152, 219),
            new Color(241, 196, 15),
            new Color(231, 76, 60)
        };
        
        for (int i = 0; i < labels.length; i++) {
            int legendX = x + i * 120;
            
            // Draw color box
            g2d.setColor(colors[i]);
            g2d.fillRect(legendX, y, 15, 15);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(legendX, y, 15, 15);
            
            // Draw label
            g2d.drawString(labels[i], legendX + 20, y + 12);
        }
    }
    
    public void updateStudent(Student student) {
        this.student = student;
        this.chartTitle = "Performance Chart - " + student.getName();
        repaint();
    }
}