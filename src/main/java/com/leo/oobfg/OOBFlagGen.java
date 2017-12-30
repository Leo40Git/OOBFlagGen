package com.leo.oobfg;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.MaskFormatter;

public class OOBFlagGen implements ActionListener {

	public static List<Image> appIcons;

	public static void initAppIcons() throws IOException {
		appIcons = new LinkedList<>();
		final String[] sizes = new String[] { "16", "32", "64" };
		for (String size : sizes)
			appIcons.add(Toolkit.getDefaultToolkit()
					.getImage(OOBFlagGen.class.getResource("/com/leo/oobfg/icon" + size + ".png")));
	}

	public static final int APPICON_16 = 0;
	public static final int APPICON_32 = 1;
	public static final int APPICON_64 = 2;

	public static final Version VERSION = new Version("1.1");
	public static final String UPDATE_CHECK_SITE = "https://raw.githubusercontent.com/Leo40Git/OOBFlagGen/master/.version";
	public static final String DOWNLOAD_SITE = "https://github.com/Leo40Git/OOBFlagGen/releases/";
	public static final String ISSUES_SITE = "https://github.com/Leo40Git/OOBFlagGen/issues";

	public static final String A_GENERATE = "generate";
	public static final String A_COPY = "copy";
	public static final String A_SIZE_BYTE = "size.byte";
	public static final String A_SIZE_WORD = "size.word";
	public static final String A_SIZE_DWORD = "size.dword";
	public static final String A_ABOUT = "about";
	public static final String A_UPDATE = "update";

	private JFrame frame;
	private JFormattedTextField fldAddress;
	private JFormattedTextField fldValue;
	private MaskFormatter mfValue;
	private JTextArea txtOutput;
	private JButton btnGenerate;
	private JButton btnCopy;
	private final ButtonGroup btnGSize = new ButtonGroup();
	private JRadioButton rdbtnByte;
	private JRadioButton rdbtnWord;
	private JRadioButton rdbtnDword;
	private JMenu mnHelp;
	private ImageIcon aboutIcon;

	public static void resourceError(Throwable e) {
		System.err.println("Error while loading resources!");
		e.printStackTrace();
		JOptionPane.showMessageDialog(null,
				"Could not load resources:" + e + "\nPlease report this error here:\n" + ISSUES_SITE,
				"Could not load resources!", JOptionPane.ERROR_MESSAGE);
		System.exit(1);
	}

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
		Config.init();
		final String skipuc = "skipuc";
		boolean skipucF = new File(System.getProperty("user.dir") + "/" + skipuc).exists();
		boolean skipucR = Config.getBoolean(Config.KEY_SKIP_UPDATE_CHECK, false);
		if (skipucR) {
			Config.setBoolean(Config.KEY_SKIP_UPDATE_CHECK, false);
			skipucF = skipucR;
		}
		try {
			initAppIcons();
		} catch (IOException e1) {
			resourceError(e1);
		}
		LoadFrame loadFrame;
		if (skipucF) {
			System.out.println("Update check: skip file detected, skipping");
			loadFrame = new LoadFrame();
		} else {
			loadFrame = updateCheck(false, false);
		}
		EventQueue.invokeLater(() -> {
			loadFrame.setLoadString("Loading...");
			loadFrame.repaint();
		});
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OOBFlagGen window = new OOBFlagGen();
					window.frame.setVisible(true);
					loadFrame.dispose();
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
		frame.setIconImages(appIcons);
		frame.setResizable(false);
		frame.setTitle("OOB Flag Generator v" + VERSION);
		frame.setBounds(100, 100, 353, 358);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);

		JMenuBar mb = new JMenuBar();
		frame.setJMenuBar(mb);

		mnHelp = new JMenu("Help");
		mb.add(mnHelp);

		JMenuItem mnHelpAbout = new JMenuItem("About OOB Flag Generator");
		mnHelpAbout.setActionCommand(A_ABOUT);
		mnHelpAbout.addActionListener(this);
		mnHelp.add(mnHelpAbout);

		JMenuItem mnHelpUpdate = new JMenuItem("Check for Updates");
		mnHelpUpdate.setActionCommand(A_UPDATE);
		mnHelpUpdate.addActionListener(this);
		mnHelp.add(mnHelpUpdate);

		JLabel lblAddress = new JLabel("Address:");
		lblAddress.setBounds(10, 11, 83, 14);
		frame.getContentPane().add(lblAddress);

		MaskFormatter mfAddress = null;
		try {
			mfAddress = new MaskFormatter("00HHHHHH");
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(1);
		}
		mfAddress.setPlaceholderCharacter('0');
		fldAddress = new JFormattedTextField(mfAddress);
		fldAddress.setToolTipText("Address in memory to edit.");
		fldAddress.setBounds(103, 8, 58, 20);
		frame.getContentPane().add(fldAddress);

		JLabel lblValue = new JLabel("Value (in hex):");
		lblValue.setBounds(10, 39, 83, 14);
		frame.getContentPane().add(lblValue);

		try {
			mfValue = new MaskFormatter("000000HH");
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(1);
		}
		mfValue.setPlaceholderCharacter('0');
		fldValue = new JFormattedTextField(mfValue);
		fldValue.setToolTipText("Value to set address to.");
		fldValue.setBounds(103, 36, 58, 20);
		frame.getContentPane().add(fldValue);

		btnGenerate = new JButton("Generate!");
		btnGenerate.setToolTipText("Generates the TSC.");
		btnGenerate.setActionCommand(A_GENERATE);
		btnGenerate.addActionListener(this);
		btnGenerate.setBounds(10, 91, 327, 23);
		frame.getContentPane().add(btnGenerate);

		JScrollPane txtOutputSP = new JScrollPane();
		txtOutputSP.setBounds(10, 125, 327, 142);
		frame.getContentPane().add(txtOutputSP);

		txtOutput = new JTextArea();
		txtOutput.setToolTipText("Generated TSC appears here.");
		txtOutputSP.setViewportView(txtOutput);
		txtOutput.setWrapStyleWord(true);
		txtOutput.setLineWrap(true);
		txtOutput.setEditable(false);

		btnCopy = new JButton("Copy to Clipboard");
		btnCopy.setToolTipText("Copies the generated TSC to the clipboard.");
		btnCopy.setActionCommand(A_COPY);
		btnCopy.addActionListener(this);
		btnCopy.setBounds(10, 278, 327, 23);
		frame.getContentPane().add(btnCopy);

		JLabel lblValueSize = new JLabel("Value size:");
		lblValueSize.setBounds(171, 11, 64, 14);
		frame.getContentPane().add(lblValueSize);

		rdbtnByte = new JRadioButton("BYTE");
		rdbtnByte.setToolTipText("Sets a BYTE-sized value to an address.");
		btnGSize.add(rdbtnByte);
		rdbtnByte.setActionCommand(A_SIZE_BYTE);
		rdbtnByte.addActionListener(this);
		rdbtnByte.setSelected(true);
		rdbtnByte.setBounds(241, 7, 83, 23);
		frame.getContentPane().add(rdbtnByte);

		rdbtnWord = new JRadioButton("WORD");
		rdbtnWord.setToolTipText("Sets a WORD-sized value to an address.");
		btnGSize.add(rdbtnWord);
		rdbtnWord.setActionCommand(A_SIZE_WORD);
		rdbtnWord.addActionListener(this);
		rdbtnWord.setBounds(241, 35, 83, 23);
		frame.getContentPane().add(rdbtnWord);

		rdbtnDword = new JRadioButton("DWORD");
		rdbtnDword.setToolTipText("Sets a DWORD-sized value to an address.");
		btnGSize.add(rdbtnDword);
		rdbtnDword.setActionCommand(A_SIZE_DWORD);
		rdbtnDword.addActionListener(this);
		rdbtnDword.setBounds(241, 61, 109, 23);
		frame.getContentPane().add(rdbtnDword);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String valueStr;
		switch (e.getActionCommand()) {
		case A_GENERATE:
			System.out.println("Generating!");
			String strAddress = fldAddress.getText();
			long flag = Long.parseUnsignedLong(strAddress, 16);
			if (flag < 0) {
				JOptionPane.showMessageDialog(null, "Address cannot be negative!", "Could not generate!",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
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
			else if (btnGSize.isSelected(rdbtnDword.getModel()))
				size = 32;
			String[] flagStrs = new String[size];
			flag *= 8;
			for (int i = 0; i < flagStrs.length; i++) {
				System.out.println("Creating string equivalent for flag " + flag);
				long flag2 = flag;
				String flagStr = "";
				if (flag > -1 && flag <= 9999) {
					flagStr = Long.toUnsignedString(flag);
					while (flagStr.length() < 4)
						flagStr = "0" + flagStr;
				} else {
					for (int j = 0; j < 3; j++) {
						flagStr = (char) ('0' + flag2 % 10) + flagStr;
						flag2 /= 10;
					}
					flagStr = (char) ('0' + flag2) + flagStr;
				}
				System.out.println("flagStr=" + flagStr);
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
			if (!valueStr.startsWith("000000"))
				valueStr = "000000" + valueStr.substring(6);
			fldValue.setText(valueStr);
			try {
				mfValue.setMask("000000HH");
			} catch (ParseException e1) {
				e1.printStackTrace();
				System.exit(1);
			}
			break;
		case A_SIZE_WORD:
			valueStr = fldValue.getText();
			if (!valueStr.startsWith("0000"))
				valueStr = "0000" + valueStr.substring(4);
			fldValue.setText(valueStr);
			try {
				mfValue.setMask("0000HHHH");
			} catch (ParseException e1) {
				e1.printStackTrace();
				System.exit(1);
			}
			break;
		case A_SIZE_DWORD:
			try {
				mfValue.setMask("HHHHHHHH");
			} catch (ParseException e1) {
				e1.printStackTrace();
				System.exit(1);
			}
			break;
		case A_ABOUT:
			if (aboutIcon == null)
				aboutIcon = new ImageIcon(appIcons.get(APPICON_32), "About");
			JOptionPane.showMessageDialog(frame, "OOB Flag Generator (OSTBM) version " + VERSION + "\nMade by Leo",
					"About OOB Flag Generator v" + VERSION, JOptionPane.INFORMATION_MESSAGE, aboutIcon);
			break;
		case A_UPDATE:
			SwingUtilities.invokeLater(() -> {
				updateCheck(true, true);
			});
			break;
		default:
			System.err.println("Unknown action command: " + e.getActionCommand());
			break;
		}
	}

	public static void downloadFile(String url, File dest) throws IOException {
		URL site = new URL(url);
		try (InputStream siteIn = site.openStream();
				ReadableByteChannel rbc = Channels.newChannel(siteIn);
				FileOutputStream out = new FileOutputStream(dest)) {
			out.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		}
	}

	public static boolean browseTo(String url) throws URISyntaxException, IOException {
		URI dlSite = new URI(url);
		if (Desktop.isDesktopSupported())
			Desktop.getDesktop().browse(dlSite);
		else
			return false;
		return true;
	}

	public static LoadFrame updateCheck(boolean disposeOfLoadFrame, boolean showUpToDate) {
		LoadFrame loadFrame = new LoadFrame();
		File verFile = new File(System.getProperty("user.dir") + "/temp.version");
		System.out.println("Update check: starting");
		try {
			downloadFile(UPDATE_CHECK_SITE, verFile);
		} catch (IOException e1) {
			System.err.println("Update check failed: attempt to download caused exception");
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "The update check has failed!\nAre you not connected to the internet?",
					"Update check failed", JOptionPane.ERROR_MESSAGE);
		}
		if (verFile.exists()) {
			System.out.println("Update check: reading version");
			try (FileReader fr = new FileReader(verFile); BufferedReader reader = new BufferedReader(fr);) {
				Version check = new Version(reader.readLine());
				if (VERSION.compareTo(check) < 0) {
					System.out.println("Update check successful: have update");
					JPanel panel = new JPanel();
					panel.setLayout(new BorderLayout());
					panel.add(new JLabel("A new update is available: " + check), BorderLayout.PAGE_START);
					final String defaultCl = "No changelog provided.";
					String cl = defaultCl;
					while (reader.ready()) {
						if (defaultCl.equals(cl))
							cl = reader.readLine();
						else
							cl += "\n" + reader.readLine();
					}
					JTextArea chglog = new JTextArea(cl);
					chglog.setEditable(false);
					chglog.setPreferredSize(new Dimension(800, 450));
					JScrollPane scrollChglog = new JScrollPane(chglog);
					panel.add(scrollChglog, BorderLayout.CENTER);
					panel.add(
							new JLabel("Click \"Yes\" to go to the download site, click \"No\" to continue to OSTBM."),
							BorderLayout.PAGE_END);
					int result = JOptionPane.showConfirmDialog(null, panel, "New update!", JOptionPane.YES_NO_OPTION,
							JOptionPane.PLAIN_MESSAGE);
					if (result == JOptionPane.YES_OPTION) {
						if (!browseTo(DOWNLOAD_SITE))
							JOptionPane.showMessageDialog(null,
									"Sadly, we can't browse to the download site for you on this platform. :(\nHead to\n"
											+ DOWNLOAD_SITE + "\nto get the newest update!",
									"Operation not supported...", JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}
				} else {
					System.out.println("Update check successful: up to date");
					if (showUpToDate) {
						JOptionPane.showMessageDialog(null,
								"You are using the most up to date version of the OneShot Textbox Maker! Have fun!",
								"Up to date!", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			} catch (IOException e) {
				System.err.println("Update check failed: attempt to read downloaded file caused exception");
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"The update check has failed!\nAn exception occured while reading update check results:\n" + e,
						"Update check failed", JOptionPane.ERROR_MESSAGE);
			} catch (URISyntaxException e1) {
				System.err.println("Browse to download site failed: bad URI syntax");
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "Failed to browse to the download site...",
						"Well, this is awkward.", JOptionPane.ERROR_MESSAGE);
			} finally {
				verFile.delete();
			}
		} else
			System.err.println("Update check failed: downloaded file doesn't exist");
		if (disposeOfLoadFrame) {
			loadFrame.dispose();
			return null;
		}
		return loadFrame;
	}
}
