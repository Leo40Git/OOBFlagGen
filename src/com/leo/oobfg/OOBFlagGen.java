package com.leo.oobfg;

import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.ParseException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.MaskFormatter;

public class OOBFlagGen implements ActionListener {

	public static final String A_GENERATE = "generate";
	public static final String A_COPY = "copy";
	public static final String A_SIZE_BYTE = "size.byte";
	public static final String A_SIZE_WORD = "size.word";

	private JFrame frame;
	private JFormattedTextField fldAddress;
	private JFormattedTextField fldValue;
	private MaskFormatter mfValue;
	private JTextArea txtOutput;
	private JButton btnGenerate;
	private JButton btnCopyToClipboard;
	private final ButtonGroup btnGSize = new ButtonGroup();
	private JRadioButton rdbtnByte;
	private JRadioButton rdbtnWord;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		if (GraphicsEnvironment.isHeadless()) {
			System.out.println("Headless mode is enabled!\nThis app cannot run in headless mode!");
			System.exit(0);
		}
		final String nolaf = "nolaf";
		if (new File(System.getProperty("user.dir") + "/" + nolaf).exists())
			System.out.println("No L&F file detected, skipping setting Look & Feel");
		else
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Could not set Look & Feel!\nPlease add a file named \"" + nolaf
						+ "\" (all lowercase, no extension) to the application folder, and then restart the application.",
						"Could not set Look & Feel", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OOBFlagGen window = new OOBFlagGen();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public OOBFlagGen() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setTitle("OOB Flag Generator");
		frame.setBounds(100, 100, 353, 313);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);

		JLabel lblAddress = new JLabel("Address:");
		lblAddress.setBounds(10, 11, 64, 14);
		frame.getContentPane().add(lblAddress);

		MaskFormatter mfAddress = null;
		try {
			mfAddress = new MaskFormatter("004HHHHH");
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(1);
		}
		mfAddress.setPlaceholderCharacter('0');
		fldAddress = new JFormattedTextField(mfAddress);
		fldAddress.setBounds(78, 8, 83, 20);
		frame.getContentPane().add(fldAddress);

		JLabel lblValue = new JLabel("Value (in hex):");
		lblValue.setBounds(10, 39, 83, 14);
		frame.getContentPane().add(lblValue);

		try {
			mfValue = new MaskFormatter("00HH");
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(1);
		}
		mfValue.setPlaceholderCharacter('0');
		fldValue = new JFormattedTextField(mfValue);
		fldValue.setBounds(103, 36, 40, 20);
		frame.getContentPane().add(fldValue);

		btnGenerate = new JButton("Generate!");
		btnGenerate.setActionCommand(A_GENERATE);
		btnGenerate.addActionListener(this);
		btnGenerate.setBounds(10, 64, 327, 23);
		frame.getContentPane().add(btnGenerate);

		txtOutput = new JTextArea();
		txtOutput.setWrapStyleWord(true);
		txtOutput.setLineWrap(true);
		txtOutput.setEditable(false);
		txtOutput.setBounds(10, 98, 327, 142);
		frame.getContentPane().add(txtOutput);

		btnCopyToClipboard = new JButton("Copy to Clipboard");
		btnCopyToClipboard.setActionCommand(A_COPY);
		btnCopyToClipboard.addActionListener(this);
		btnCopyToClipboard.setBounds(10, 251, 327, 23);
		frame.getContentPane().add(btnCopyToClipboard);

		JLabel lblValueSize = new JLabel("Value size:");
		lblValueSize.setBounds(171, 11, 64, 14);
		frame.getContentPane().add(lblValueSize);

		rdbtnByte = new JRadioButton("Byte");
		btnGSize.add(rdbtnByte);
		rdbtnByte.setActionCommand(A_SIZE_BYTE);
		rdbtnByte.addActionListener(this);
		rdbtnByte.setSelected(true);
		rdbtnByte.setBounds(241, 7, 83, 23);
		frame.getContentPane().add(rdbtnByte);

		rdbtnWord = new JRadioButton("Word");
		btnGSize.add(rdbtnWord);
		rdbtnWord.setActionCommand(A_SIZE_WORD);
		rdbtnWord.addActionListener(this);
		rdbtnWord.setBounds(241, 35, 83, 23);
		frame.getContentPane().add(rdbtnWord);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String valueStr;
		switch (e.getActionCommand()) {
		case A_GENERATE:
			System.out.println("Generating!");
			String strAddress = fldAddress.getText();
			long flag = Long.parseUnsignedLong(strAddress, 16);
			flag -= 0x49DDA0;
			String distStr = "0x";
			if (flag < 0)
				distStr = "-" + distStr + Long.toUnsignedString(-flag, 16).toUpperCase();
			else
				distStr += Long.toUnsignedString(flag, 16).toUpperCase();
			System.out.println("Address is " + distStr + " bytes away");
			if (flag > 0x288E) {
				JOptionPane.showMessageDialog(null,
						"Address is too far away!\nAddress is 0x" + Long.toUnsignedString(flag, 16).toUpperCase()
								+ " bytes after start of flag array (0x49DDA0), but maximum distance is 0x288E bytes!",
						"Could not generate!", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (flag < -0x8AE) {
				JOptionPane.showMessageDialog(null,
						"Address is too far away!\nAddress is 0x" + Long.toUnsignedString(-flag, 16).toUpperCase()
								+ " bytes before start of flag array (0x49DDA0), but maximum distance is 0x8AE bytes!",
						"Could not generate!", JOptionPane.ERROR_MESSAGE);
				return;
			}
			int size = 8;
			if (btnGSize.isSelected(rdbtnWord.getModel()))
				size = 16;
			String[] flagStrs = new String[size];
			flag *= 8;
			for (int i = 0; i < flagStrs.length; i++) {
				System.out.println("Creating string equivalent for flag " + flag);
				long flag2 = flag;
				String flagStr = "";
				if (flag > -1 && flag <= 9999)
					flagStr = Long.toUnsignedString(flag);
				else {
					for (int j = 0; j < 3; j++) {
						flagStr = (char) ('0' + flag2 % 10) + flagStr;
						flag2 /= 10;
					}
					flagStr = (char) ('0' + flag2) + flagStr;
				}
				System.out.println("flagStr=" + flagStr);
				System.out.println("ascii2Num_CS(flagStr)=" + ascii2Num_CS(flagStr));
				flagStrs[i] = flagStr;
				flag++;
			}
			valueStr = fldValue.getText();
			long value = Long.parseUnsignedLong(valueStr, 16);
			StringBuilder tscB = new StringBuilder();
			for (int i = 0; i < flagStrs.length; i++) {
				String cmd = "<FL";
				if ((value & (1 << i)) == 0)
					cmd += "-";
				else
					cmd += "+";
				tscB.append(cmd + flagStrs[i]);
			}
			txtOutput.setText(tscB.toString());
			break;
		case A_COPY:
			Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			cb.setContents(new StringSelection(txtOutput.getText()), null);
			break;
		case A_SIZE_BYTE:
			valueStr = fldValue.getText();
			if (!valueStr.startsWith("00"))
				valueStr = "00" + valueStr.substring(2);
			fldValue.setText(valueStr);
			try {
				mfValue.setMask("00HH");
			} catch (ParseException e1) {
				e1.printStackTrace();
				System.exit(1);
			}
			break;
		case A_SIZE_WORD:
			try {
				mfValue.setMask("HHHH");
			} catch (ParseException e1) {
				e1.printStackTrace();
				System.exit(1);
			}
			break;
		default:
			System.err.println("Unknown action command: " + e.getActionCommand());
			break;
		}
	}

	public static int ascii2Num_CS(String str) {
		int result = 0;
		int radix = 1;
		for (int i = 0; i < str.length(); i++) {
			if (i > 7)
				break;
			if (i > 0)
				radix *= 10;
			result += (str.charAt(str.length() - i - 1) - '0') * radix;
		}
		return result;
	}
}
