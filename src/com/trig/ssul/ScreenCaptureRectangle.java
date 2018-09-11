package com.trig.ssul;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

public class ScreenCaptureRectangle {

	private Rectangle captureRect;
	private JLabel screenLabel;
	private GUI gui;
	

	public ScreenCaptureRectangle(final BufferedImage screen, final GUI gui) {
		this.gui = gui;
		BufferedImage screenCopy = new BufferedImage(screen.getWidth(), screen.getHeight(), screen.getType());
		JLabel screenLabel = new JLabel(new ImageIcon(screenCopy));
		JScrollPane screenScroll = new JScrollPane(screenLabel);

		screenScroll.setPreferredSize(get80Screen());

		repaint(screen, screenCopy);
		screenLabel.repaint();

		screenLabel.addMouseMotionListener(new MouseMotionAdapter() {
			Point start = new Point();

			@Override
			public void mouseMoved(MouseEvent me) {
				start = me.getPoint();
				repaint(screen, screenCopy);
				screenLabel.repaint();
			}

			@Override
			public void mouseDragged(MouseEvent me) {
				Point end = me.getPoint();
				captureRect = new Rectangle(start, new Dimension(end.x - start.x, end.y - start.y));
				repaint(screen, screenCopy);
				screenLabel.repaint();
			}
		});
		JOptionPane.showMessageDialog(null, screenScroll);
		gui.processSelection(screen, captureRect); //We needed to pass in the screenshot we received when we created this GUI.
	}

	public void repaint(BufferedImage orig, BufferedImage copy) {
		Graphics2D g = copy.createGraphics();
		g.drawImage(orig, 0, 0, null);
		g.setColor(Color.RED);
		if (captureRect == null) {
			return;
		}
		g.draw(captureRect);
		g.setColor(new Color(25, 25, 23, 10));
		g.fill(captureRect);
		g.dispose();
	}

	private Dimension get80Screen() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) (screenSize.width * 0.80);
		int height = (int) (screenSize.height * 0.80);
		return new Dimension(width, height);
	}
}
