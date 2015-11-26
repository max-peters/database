package database.main.GraphicalUserInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import database.main.Terminal;
import database.main.date.Date;
import database.main.date.Time;

public class GraphicalUserInterface extends JFrame {
	private Font			font;
	private int				frameWidth;
	private Image			icon;
	private JTextField		input						= new JTextField();
	private String			inputText;
	private JPanel			panel						= new JPanel();
	private int				currentLineNumber			= 0;
	private JScrollPane		scrollPane					= new JScrollPane(panel);
	private Object			synchronizerInputConfirm	= new Object();
	private Object			synchronizerKeyInput		= new Object();
	private JTextField		time						= new JTextField();
	private Timer			timer						= new Timer();
	private JTextPane		output						= new JTextPane();
	private StyledDocument	styledDocument				= output.getStyledDocument();
	private int				pressedKey					= 0;

	public GraphicalUserInterface() throws IOException, FontFormatException {
		super("Database");
		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("DejaVuSansMono.ttf");
		font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
		font = font.deriveFont(Font.PLAIN, 15);
		inputStream.close();
		icon = new ImageIcon(classLoader.getResource("icon.png")).getImage();
		ActionListener inputListener = new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				inputText = input.getText();
				input.setText(null);
				synchronized (synchronizerInputConfirm) {
					synchronizerInputConfirm.notify();
				}
			}
		};
		KeyListener keyListener = new KeyListener() {
			@Override public void keyPressed(KeyEvent arg0) {
				synchronized (synchronizerKeyInput) {
					pressedKey = arg0.getKeyCode();
					synchronizerKeyInput.notify();
				}
			}

			@Override public void keyReleased(KeyEvent e) {}

			@Override public void keyTyped(KeyEvent e) {}
		};
		panel.setLayout(new BorderLayout(0, 0));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(icon);
		setResizable(false);
		time.setEditable(false);
		time.setEnabled(false);
		time.setFont(font);
		time.setBorder(null);
		time.setBackground(Color.BLACK);
		time.setDisabledTextColor(Color.WHITE);
		panel.add(time, BorderLayout.NORTH);
		output.setEditable(false);
		output.setEnabled(false);
		output.setFont(font);
		output.setBorder(null);
		output.setBackground(Color.BLACK);
		output.setDisabledTextColor(Color.WHITE);
		panel.add(output, BorderLayout.CENTER);
		input.setBorder(null);
		input.setFont(font.deriveFont(Font.ITALIC));
		input.setCaretColor(Color.WHITE);
		input.setBackground(Color.BLACK);
		input.setForeground(Color.WHITE);
		input.addActionListener(inputListener);
		input.addKeyListener(keyListener);
		output.add(input, BorderLayout.AFTER_LAST_LINE);
		timer.scheduleAtFixedRate(new UpdateTime(time), 0, 500);
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		scrollPane.getViewport().setBorder(null);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		setContentPane(scrollPane);
		panel.add(output);
		for (StringFormat format : StringFormat.values()) {
			format.initialise(styledDocument);
		}
	}

	public String readLine() throws InterruptedException {
		synchronized (synchronizerInputConfirm) {
			synchronizerInputConfirm.wait();
		}
		return inputText;
	}

	public void setBounds(String longestString) {
		if (longestString == null || longestString.isEmpty()) {
			frameWidth = 800;
		}
		else if (output.getFontMetrics(font).stringWidth(longestString) + 25 > frameWidth) {
			frameWidth = output.getFontMetrics(font).stringWidth(longestString) + 25;
		}
		setSize(frameWidth, frameWidth * 2 / 3);
		input.setSize(frameWidth - 10, input.getFontMetrics(font).getHeight());
	}

	public void setLocation() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);
	}

	public void showMessageDialog(Throwable e) {
		String stackTrace = "";
		for (StackTraceElement element : e.getStackTrace()) {
			stackTrace = stackTrace + "\r\n" + element;
		}
		JOptionPane.showMessageDialog(this, stackTrace, e.getClass().getName(), JOptionPane.INFORMATION_MESSAGE);
	}

	public int getFrameWidth() {
		return frameWidth;
	}

	private String formatCheckLine(ArrayList<String> strings, int currentLine) {
		String output = "";
		int counter = 1;
		for (String string : strings) {
			if (counter == currentLine) {
				output = output + "\u2611 ";
			}
			else {
				output = output + "\u2610 ";
			}
			output = output + string + "\r\n";
			counter++;
		}
		return output;
	}

	private void moveTextField(int steps) {
		input.setLocation(0, steps * input.getFontMetrics(font).getHeight());
	}

	public void blockInput() {
		input.setEditable(false);
		input.setFocusable(false);
		input.setCaretColor(Color.BLACK);
	}

	public int checkRequest(ArrayList<String> strings) throws InterruptedException, BadLocationException {
		int position = -1;
		int current = 0;
		pressedKey = 0;
		Terminal.printLine("check:", StringType.MAIN, StringFormat.ITALIC);
		if (strings.isEmpty()) {
			Terminal.printLine("no entries", StringType.SOLUTION, StringFormat.STANDARD);
			waitForInput();
			return position;
		}
		input.setEditable(false);
		input.setCaretColor(Color.BLACK);
		while (pressedKey != 10) {
			Terminal.printLine(formatCheckLine(strings, current), StringType.REQUEST, StringFormat.STANDARD);
			synchronized (synchronizerKeyInput) {
				synchronizerKeyInput.wait();
			}
			if (pressedKey == 40) {
				current++;
			}
			if (pressedKey == 38) {
				current--;
			}
			if (current < 0) {
				current = 0;
			}
			if (current > strings.size()) {
				current = strings.size();
			}
		}
		if (current != 0) {
			position = current - 1;
		}
		Terminal.update();
		pressedKey = 0;
		releaseInput();
		return position;
	}

	public void clear() {
		output.setText("");
		currentLineNumber = 0;
	}

	public void printLine(String line, StringType stringType, StringFormat stringFormat) throws BadLocationException {
		if (!stringType.equals(StringType.SOLUTION)) {
			styledDocument.remove(currentLineNumber, styledDocument.getLength() - currentLineNumber);
		}
		styledDocument.insertString(styledDocument.getLength(), line, styledDocument.getStyle(stringFormat.toString()));
		if (stringType.equals(StringType.MAIN)) {
			currentLineNumber = (currentLineNumber + line.length());
		}
		moveTextField(output.getText().contains((System.getProperty("line.separator"))) ? (output.getText().split(System.getProperty("line.separator")).length) : 0);
	}

	public void releaseInput() {
		input.setEditable(true);
		input.setFocusable(true);
		input.grabFocus();
		input.setCaretColor(Color.WHITE);
	}

	public void waitForInput() throws InterruptedException {
		releaseInput();
		input.setCaretColor(Color.BLACK);
		synchronized (synchronizerKeyInput) {
			synchronizerKeyInput.wait();
		}
		input.setCaretColor(Color.WHITE);
	}
}

class UpdateTime extends TimerTask {
	private JTextField timeTextfield;

	public UpdateTime(JTextField timeTextfield) {
		this.timeTextfield = timeTextfield;
	}

	@Override public void run() {
		timeTextfield.setText(Date.getDateAsString() + " " + Time.getTime());
	}
}
