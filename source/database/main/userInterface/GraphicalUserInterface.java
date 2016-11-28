package database.main.userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class GraphicalUserInterface {
	private int					currentLineNumber;
	private Font				font;
	private JFrame				frame;
	private Image				icon;
	private JTextField			input;
	private JTextPane			output;
	private JPanel				panel;
	private int					pressedKey;
	private JScrollPane			scrollPane;
	private StyledDocument		styledDocument;
	private Object				synchronizerKeyInput;
	private Object				synchronizerKeyPressed;
	private JTextField			time;
	private Timer				timer;
	private DocumentListener	documentListener;
	private KeyListener			keyListener;
	private TimerTask			timerTask;

	public GraphicalUserInterface() {
		frame = new JFrame("Database");
		input = new JTextField();
		output = new JTextPane();
		panel = new JPanel();
		scrollPane = new JScrollPane(panel);
		styledDocument = output.getStyledDocument();
		synchronizerKeyInput = new Object();
		synchronizerKeyPressed = new Object();
		time = new JTextField();
		timer = new Timer();
		documentListener = new DocumentListener() {
			@Override public void changedUpdate(DocumentEvent e) {}

			@Override public void insertUpdate(DocumentEvent e) {
				synchronized (synchronizerKeyInput) {
					synchronizerKeyInput.notify();
				}
			}

			@Override public void removeUpdate(DocumentEvent e) {}
		};
		keyListener = new KeyListener() {
			@Override public void keyPressed(KeyEvent e) {
				pressedKey = e.getExtendedKeyCode();
				if (e.getExtendedKeyCode() == 8) {
					input.replaceSelection("");
				}
				else if (e.getExtendedKeyCode() == 10) {
					synchronized (synchronizerKeyInput) {
						synchronizerKeyInput.notify();
					}
				}
				synchronized (synchronizerKeyPressed) {
					synchronizerKeyPressed.notify();
				}
			}

			@Override public void keyReleased(KeyEvent e) {}

			@Override public void keyTyped(KeyEvent e) {}
		};
		timerTask = new TimerTask() {
			@Override public void run() {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
				SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
				time.setText(dateFormat.format(Calendar.getInstance().getTime()) + " " + timeFormat.format(Calendar.getInstance().getTime()));
			}
		};
	}

	public void initialise() throws FontFormatException, IOException {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("DejaVuSansMono.ttf");
		font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
		font = font.deriveFont(Font.PLAIN, 15);
		inputStream.close();
		icon = new ImageIcon(classLoader.getResource("icon.png")).getImage();
		panel.setLayout(new BorderLayout(0, 0));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(icon);
		frame.setResizable(false);
		time.setEditable(false);
		time.setEnabled(false);
		time.setFont(font);
		time.setBorder(BorderFactory.createEmptyBorder());
		time.setBackground(Color.BLACK);
		time.setDisabledTextColor(Color.WHITE);
		panel.add(time, BorderLayout.NORTH);
		output.setEditable(false);
		output.setEnabled(false);
		output.setFont(font);
		output.setBorder(BorderFactory.createEmptyBorder());
		output.setBackground(Color.BLACK);
		output.setDisabledTextColor(Color.WHITE);
		panel.add(output, BorderLayout.CENTER);
		input.setBorder(BorderFactory.createEmptyBorder());
		input.setFont(font.deriveFont(Font.ITALIC));
		input.setCaretColor(Color.WHITE);
		input.setBackground(Color.BLACK);
		input.setForeground(Color.WHITE);
		input.setSelectedTextColor(Color.BLACK);
		input.setSelectionColor(Color.WHITE);
		input.addKeyListener(keyListener);
		input.getDocument().addDocumentListener(documentListener);
		input.setFocusable(false);
		output.add(input, BorderLayout.AFTER_LAST_LINE);
		timer.scheduleAtFixedRate(timerTask, 0, 500);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
		verticalScrollBar.setPreferredSize(new Dimension(0, 0));
		verticalScrollBar.setUnitIncrement(10);
		JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
		horizontalScrollBar.setPreferredSize(new Dimension(0, 0));
		horizontalScrollBar.setUnitIncrement(10);
		frame.setContentPane(scrollPane);
		panel.add(output);
		for (StringFormat format : StringFormat.values()) {
			format.initialise(styledDocument);
		}
		frame.setSize(800, 530);
		input.setSize(770, input.getFontMetrics(font).getHeight());
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
	}

	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}

	protected String autocomplete(Completeable completeable) throws InterruptedException {
		String inputString = null;
		pressedKey = 0;
		while (pressedKey != 10) {
			synchronized (synchronizerKeyInput) {
				synchronizerKeyInput.wait();
			}
			inputString = input.getText();
			setInputText(completeable.getNewInput(inputString));
		}
		return inputString;
	}

	protected void blockInput() {
		input.setEditable(false);
		input.setCaretColor(Color.BLACK);
	}

	protected void clearInput() {
		input.setText("");
	}

	protected void clearOutput() throws BadLocationException {
		output.setText("");
		currentLineNumber = 0;
	}

	protected int getNumberOfCharsPerLine(char character) {
		return frame.getWidth() / output.getFontMetrics(font).charWidth(character);
	}

	protected void printLine(Object object, StringType stringType, StringFormat stringFormat) throws BadLocationException {
		String outputString;
		if (object == null) {
			return;
		}
		outputString = object.toString();
		if (!outputString.endsWith(System.getProperty("line.separator"))) {
			outputString += System.getProperty("line.separator");
		}
		if (!stringType.equals(StringType.SOLUTION)) {
			styledDocument.remove(currentLineNumber, styledDocument.getLength() - currentLineNumber);
		}
		styledDocument.insertString(styledDocument.getLength(), outputString, styledDocument.getStyle(stringFormat.toString()));
		if (stringType.equals(StringType.MAIN)) {
			currentLineNumber = currentLineNumber + outputString.length();
		}
		moveTextField(output.getText().contains(System.getProperty("line.separator")) ? output.getText().split(System.getProperty("line.separator")).length : 0);
	}

	protected String readLine() throws InterruptedException {
		int lastKey = 0;
		while (lastKey != 10) {
			lastKey = waitAndReturnKeyInput();
		}
		return input.getText();
	}

	protected void releaseInput() {
		input.setEditable(true);
		input.setFocusable(true);
		input.grabFocus();
		input.setCaretColor(Color.WHITE);
	}

	protected void setInputText(String string) {
		String inputText = input.getText();
		input.setText(inputText + string);
		input.select(inputText.length(), (inputText + string).length());
	}

	protected int waitAndReturnKeyInput() throws InterruptedException {
		synchronized (synchronizerKeyPressed) {
			synchronizerKeyPressed.wait();
		}
		return pressedKey;
	}

	protected void waitForInput() throws InterruptedException {
		int lastKey = 0;
		releaseInput();
		input.setCaretColor(Color.BLACK);
		do {
			lastKey = waitAndReturnKeyInput();
		}
		while ((lastKey == 38 || lastKey == 40) && input.getY() > 520);
		input.setCaretColor(Color.WHITE);
	}

	private void moveTextField(int steps) {
		input.setLocation(0, steps * input.getFontMetrics(font).getHeight());
	}
}
