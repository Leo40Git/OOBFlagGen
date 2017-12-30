package com.leo.oobfg;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class LoadFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JLabel loadLabel;

	public void setLoadString(String loadString) {
		loadLabel.setText(loadString);
	}

	public LoadFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);
		final Dimension size = new Dimension(320, 120);
		setPreferredSize(size);
		setMaximumSize(size);
		setMinimumSize(size);
		setResizable(false);
		loadLabel = new JLabel("Checking for updates...");
		loadLabel.setFont(loadLabel.getFont().deriveFont(Font.BOLD, 20));
		loadLabel.setHorizontalAlignment(SwingConstants.CENTER);
		loadLabel.setVerticalAlignment(SwingConstants.CENTER);
		loadLabel.setOpaque(true);
		add(loadLabel);
		pack();
		setLocationRelativeTo(null);
		setIconImage(Toolkit.getDefaultToolkit().getImage(OOBFlagGen.class.getResource("/com/leo/oobfg/icon.png")));
		setVisible(true);
		requestFocus();
	}

}
