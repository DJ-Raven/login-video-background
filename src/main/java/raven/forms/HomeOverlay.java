package raven.forms;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.CubicBezierEasing;
import net.miginfocom.swing.MigLayout;
import raven.components.EventHomeOverlay;
import raven.components.HeaderButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.List;

public class HomeOverlay extends JWindow {

    public PanelOverlay getOverlay() {
        return overlay;
    }

    private PanelOverlay overlay;
    private List<ModelLocation> locations;

    public HomeOverlay(JFrame frame, List<ModelLocation> locations) {
        super(frame);
        this.locations = locations;
        init();
    }

    private void init() {
        setBackground(new Color(35, 96, 135, 80));
        setLayout(new BorderLayout());
        overlay = new PanelOverlay();
        add(overlay);
    }

    public class PanelOverlay extends JPanel {

        public void setEventHomeOverlay(EventHomeOverlay eventHomeOverlay) {
            this.eventHomeOverlay = eventHomeOverlay;
        }

        private MigLayout migLayout;
        private EventHomeOverlay eventHomeOverlay;
        private AnimationType animationType = AnimationType.NONE;
        private Animator animator;
        private Animator loginAnimator;
        private float animate;
        private int index;
        private boolean showLogin;

        public void setIndex(int index) {
            this.index = index;
            ModelLocation location = locations.get(index);
            textTitle.setText(location.getTitle());
            textDescription.setText(location.getDescription());
        }

        public PanelOverlay() {
            init();
        }

        private void init() {
            setOpaque(false);
            migLayout = new MigLayout("fill,insets 10 180 10 180", "fill", "[grow 0][]");
            setLayout(migLayout);
            createHeader();
            createPageButton();
            createLogin();
            JPanel panel = new JPanel(new MigLayout("wrap", "", "[]30[]"));
            panel.setOpaque(false);
            textTitle = new JTextPane();
            textDescription = new JTextPane();
            cmdReadMore = new JButton("Read More");
            textTitle.setOpaque(false);
            textTitle.setEditable(false);
            textTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                    "font:bold +40;" +
                    "border:0,0,0,0");

            textDescription.setOpaque(false);
            textDescription.setEditable(false);
            textDescription.putClientProperty(FlatClientProperties.STYLE, "" +
                    "font:bold +2;" +
                    "border:0,0,0,0");
            cmdReadMore.putClientProperty(FlatClientProperties.STYLE, "" +
                    "background:$Component.accentColor;" +
                    "borderWidth:0;" +
                    "margin:5,15,5,15;" +
                    "focusWidth:0;" +
                    "innerFocusWidth:0;" +
                    "arc:999");
            panel.add(textTitle);
            panel.add(textDescription);
            panel.add(cmdReadMore);
            add(panel, "width 50%!");
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    runLoginAnimation(false);
                }
            });
            animator = new Animator(500, new Animator.TimingTarget() {
                @Override
                public void timingEvent(float v) {
                    animate = v;
                    repaint();
                }

                @Override
                public void end() {
                    if (animationType == AnimationType.CLOSE_VIDEO) {
                        eventHomeOverlay.onChanged(index);
                        SwingUtilities.invokeLater(() -> {
                            sleep(500);
                            runAnimation(index, AnimationType.SHOW_VIDEO);
                        });
                    } else {
                        animationType = AnimationType.NONE;
                    }
                }
            });
            loginAnimator = new Animator(500, new Animator.TimingTarget() {
                @Override
                public void timingEvent(float v) {
                    float f = showLogin ? v : 1f - v;
                    int x = (int) ((350 + 180) * f);
                    migLayout.setComponentConstraints(panelLogin, "pos 100%-" + x + " 0.5al, w 350");
                    revalidate();
                }
            });
            animator.setInterpolator(CubicBezierEasing.EASE_IN);
            loginAnimator.setInterpolator(CubicBezierEasing.EASE);
        }

        private void sleep(long l) {
            try {
                Thread.sleep(l);
            } catch (Exception e) {
                System.err.println(e);
            }
        }

        private void createHeader() {
            header = new JPanel(new MigLayout("fill", "[]push[][]"));
            header.setOpaque(false);
            JLabel title = new JLabel("JOURNEY");
            title.putClientProperty(FlatClientProperties.STYLE, "" +
                    "font:bold +10");
            HeaderButton home = new HeaderButton("Home");
            HeaderButton about = new HeaderButton("About");
            HeaderButton explore = new HeaderButton("Explore");
            HeaderButton login = new HeaderButton("Login");

            login.addActionListener(e -> {
                runLoginAnimation(true);
            });

            header.add(title);
            header.add(home);
            header.add(about);
            header.add(explore);
            header.add(login);
            add(header, "wrap");
        }

        private void createLogin() {
            panelLogin = new Login();
            add(panelLogin, "pos 100% 0.5al,w 350");
        }


        private void createPageButton() {
            JPanel panel = new JPanel(new MigLayout("gapx 20"));
            panel.setOpaque(false);
            for (int i = 0; i < locations.size(); i++) {
                JButton cmd = new JButton("");
                cmd.putClientProperty(FlatClientProperties.STYLE, "" +
                        "margin:5,5,5,5;" +
                        "arc:999;" +
                        "borderWidth:0;" +
                        "focusWidth:0;" +
                        "innerFocusWidth:0;" +
                        "selectedBackground:$Component.accentColor");
                cmd.setCursor(new Cursor(Cursor.HAND_CURSOR));
                final int index = i;
                cmd.addActionListener(e -> {
                    if (this.index != index) {
                        boolean act = runAnimation(index, AnimationType.CLOSE_VIDEO);
                        if (act) {
                            setSelectedButton(panel, index);
                        }
                    }
                });
                panel.add(cmd);
            }
            add(panel, "pos 0.5al 80%");
            setSelectedButton(panel, index);
        }

        private void setSelectedButton(JPanel panel, int index) {
            int count = panel.getComponentCount();
            for (int i = 0; i < count; i++) {
                JButton cmd = (JButton) panel.getComponent(i);
                if (i == index) {
                    cmd.setSelected(true);
                } else {
                    cmd.setSelected(false);
                }
            }
        }

        private boolean runAnimation(int index, AnimationType animationType) {
            if (!animator.isRunning()) {
                this.animate = 0;
                this.animationType = animationType;
                this.index = index;
                animator.start();
                return true;
            } else {
                return false;
            }
        }

        private void runLoginAnimation(boolean show) {
            if (showLogin != show) {
                if (!loginAnimator.isRunning()) {
                    showLogin = show;
                    loginAnimator.start();
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (animationType != AnimationType.NONE) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = getWidth();
                int height = getHeight();
                g2.setColor(UIManager.getColor("Component.accentColor"));
                Rectangle rec = new Rectangle(0, 0, width, height);
                if (animationType == AnimationType.CLOSE_VIDEO) {
                    g2.setComposite(AlphaComposite.SrcOver.derive(animate));
                    g2.fill(rec);
                } else {
                    Area area = new Area(rec);
                    area.subtract(new Area(createRec(rec)));
                    g2.fill(area);
                }
                g2.dispose();
            }
            super.paintComponent(g);
        }

        private Shape createRec(Rectangle rec) {
            int maxSize = Math.max(rec.width, rec.height);
            float size = maxSize * animate;
            float x = (rec.width - size) / 2;
            float y = (rec.height - size) / 2;
            Ellipse2D ell = new Ellipse2D.Double(x, y, size, size);
            return ell;
        }


        private JPanel header;
        private JTextPane textTitle;
        private JTextPane textDescription;
        private JButton cmdReadMore;
        private Login panelLogin;
    }

    public enum AnimationType {
        CLOSE_VIDEO, SHOW_VIDEO, NONE
    }
}
