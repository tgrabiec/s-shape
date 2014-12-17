package tgrabiec;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.floor;

class Settings {
    private int blockSize = 100;
    private int blockCount = 1000;
    private double evictionRate = 0.5;

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(int blockCount) {
        this.blockCount = blockCount;
    }

    public double getEvictionRate() {
        return evictionRate;
    }

    public void setEvictionRate(double evictionRate) {
        this.evictionRate = evictionRate;
    }
}

public class SShape {
    public static void main(String[] args) {
        final Settings settings = new Settings();

        JFrame frame = new JFrame();

        final SigmoidPanel displayPanel = new SigmoidPanel(settings);
        frame.add(displayPanel, BorderLayout.CENTER);

        final JLabel blockSizeLabel = new JLabel();
        JScrollBar blockSizeScrollBar = new JScrollBar(Adjustable.HORIZONTAL, 1, 1, 1, 2000);
        blockSizeScrollBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
                blockSizeLabel.setText("Block size (" + adjustmentEvent.getValue() + ")");
                settings.setBlockSize(adjustmentEvent.getValue());
                displayPanel.onSettingsChanged();
            }
        });
        blockSizeScrollBar.setValue(settings.getBlockSize());

        final JLabel blockCountLabel = new JLabel();
        JScrollBar blockCountScrollBar = new JScrollBar(Adjustable.HORIZONTAL, 1, 1, 1, 2000);
        blockCountScrollBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
                blockCountLabel.setText("Block count (" + adjustmentEvent.getValue() + ")");
                settings.setBlockCount(adjustmentEvent.getValue());
                displayPanel.onSettingsChanged();
            }
        });
        blockCountScrollBar.setValue(settings.getBlockCount());

        final JLabel evictionRateLabel = new JLabel();
        JScrollBar evictionRateScrollBar = new JScrollBar(Adjustable.HORIZONTAL, 0, 1, 0, 100);
        evictionRateScrollBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
                evictionRateLabel.setText("Eviction (" + adjustmentEvent.getValue() + "%)");
                settings.setEvictionRate((double) adjustmentEvent.getValue() / 100);
                displayPanel.onSettingsChanged();
            }
        });
        evictionRateScrollBar.setValue((int) (settings.getEvictionRate() * 100));

        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(blockSizeLabel)
                        .addComponent(blockCountLabel)
                        .addComponent(evictionRateLabel)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(blockSizeScrollBar)
                        .addComponent(blockCountScrollBar)
                        .addComponent(evictionRateScrollBar)
                )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(blockSizeLabel)
                        .addComponent(blockSizeScrollBar)
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(blockCountLabel)
                        .addComponent(blockCountScrollBar)
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(evictionRateLabel)
                        .addComponent(evictionRateScrollBar)
                )
        );

        frame.add(panel, BorderLayout.NORTH);

        frame.setTitle("S-shape");
        frame.setPreferredSize(new Dimension(800, 600));
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static class SigmoidPanel extends JPanel {
        private final Settings settings;
        private final Random random = new Random();
        private int[] blocks;

        public SigmoidPanel(Settings settings) {
            this.settings = settings;
            blocks = new int[settings.getBlockCount()];
            recalculate();
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.GRAY);
            g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
            g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
            g.setColor(Color.RED);
            double scaleX = (double) getWidth() / blocks.length;
            int px = 0;
            int py = getHeight();
            for (int i = 0; i < blocks.length; i++) {
                int y = getHeight() - (int) ((double) blocks[i] / settings.getBlockSize() * getHeight());
                int x = (int) ((i + 1) * scaleX);
                g.drawLine(px, py, px, y);
                g.drawLine(px, y, x, y);
                px = x;
                py = y;
            }
        }

        private void recalculate() {
            int itemCount = settings.getBlockSize() * settings.getBlockCount();
            int[] items = new int[itemCount];

            int pos = 0;
            for (int i = 0; i < settings.getBlockCount(); i++) {
                for (int j = 0; j < settings.getBlockSize(); j++) {
                    items[pos++] = i;
                }
            }

            for (int i = items.length; i > 1; i--) {
                int i2 = random.nextInt(i);
                int t = items[i - 1];
                items[i - 1] = items[i2];
                items[i2] = t;
            }

            if (blocks.length != settings.getBlockCount()) {
                blocks = new int[settings.getBlockCount()];
            }
            Arrays.fill(blocks, 0);
            for (int i = 0; i < floor(itemCount * (1-settings.getEvictionRate())); i++) {
                blocks[items[i]]++;
            }

            Arrays.sort(blocks);
        }

        public void onSettingsChanged() {
            recalculate();
            repaint();
        }
    }
}
