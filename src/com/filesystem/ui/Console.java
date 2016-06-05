package com.filesystem.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 *
 * @author Alistair
 */
public class Console extends JPanel implements KeyListener {

    private static final long serialVersionUID = -4538532229007904362L;
    private JLabel keyLabel;
    private String prompt = "";
    public boolean ReadOnly = false;
    private ConsoleVector vec = new ConsoleVector();
    private ConsoleListener con = null;
    private String oldTxt = "";
    private Vector history = new Vector();
    private int history_index = -1;
    private boolean history_mode = false;

    public Console() {
        super();
        setSize(300, 200);
        setLayout(new FlowLayout(FlowLayout.CENTER));
        keyLabel = new JLabel("");
        setFocusable(true);
        keyLabel.setFocusable(true);
        keyLabel.addKeyListener(this);
        addKeyListener(this);
        add(keyLabel);
        setVisible(true);
    }

    public void registerConsoleListener(ConsoleListener c) {
        this.con = c;
    }

    public String getPrompt() {
        return this.prompt;
    }

    public void setPrompt(String s) {
        this.prompt = s;
    }

    private void backspace() {
        if (!this.vec.isEmpty()) {
            this.vec.remove(this.vec.size() - 1);
            this.print();
        }
    }

    @SuppressWarnings("unchecked")
    private void enter() {
        String com = this.vec.toString();
        String return$ = "";
        if (this.con != null) {
            return$ = this.con.receiveCommand(com);
        }

        this.history.add(com);
        this.vec.clear();
        if (!return$.equals("")) {
            return$ = return$ + "<br>";
        }
        // <HTML> </HTML>
        String h = this.keyLabel.getText().substring(6, this.keyLabel.getText().length() - 7);
        this.oldTxt = h.substring(0, h.length() - 1) + "<BR>" + return$;
        this.keyLabel.setText("<HTML>" + this.oldTxt + this.prompt + "_</HTML>");
    }

    private void print() {
        this.keyLabel.setText("<HTML>" + this.oldTxt + this.prompt + this.vec.toString() + "_</HTML>");
        this.repaint();
    }

    @SuppressWarnings("unchecked")
    private void print(String s) {
        this.vec.add(s);
        this.print();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.handleKey(e);
    }

    private void history(int dir) {
        if (this.history.isEmpty()) {
            return;
        }
        if (dir == 1) {
            this.history_mode = true;
            this.history_index++;
            if (this.history_index > this.history.size() - 1) {
                this.history_index = 0;
            }
            // System.out.println(this.history_index);
            this.vec.clear();
            String p = (String) this.history.get(this.history_index);
            this.vec.fromString(p.split(""));

        } else if (dir == 2) {
            this.history_index--;
            if (this.history_index < 0) {
                this.history_index = this.history.size() - 1;
            }
            // System.out.println(this.history_index);
            this.vec.clear();
            String p = (String) this.history.get(this.history_index);
            this.vec.fromString(p.split(""));
        }

        print();
    }

    private void handleKey(KeyEvent e) {

        if (!this.ReadOnly) {
            if (e.getKeyCode() == 38 | e.getKeyCode() == 40) {
                if (e.getKeyCode() == 38) {
                    history(1);
                } else if (e.getKeyCode() == 40 & this.history_mode != false) {
                    history(2);
                }
            } else {
                this.history_index = -1;
                this.history_mode = false;
                if (e.getKeyCode() == 13 | e.getKeyCode() == 10) {
                    enter();
                } else if (e.getKeyCode() == 8) {
                    this.backspace();
                } else {
                    if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
                        this.print(String.valueOf(e.getKeyChar()));
                    }
                }
            }
        }
    }
}


class ConsoleVector extends Vector {

    private static final long serialVersionUID = -5527403654365278223L;

    @SuppressWarnings("unchecked")
    public void fromString(String[] p) {
        for (int i = 0; i < p.length; i++) {
            this.add(p[i]);
        }
    }

    public ConsoleVector() {
        super();
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < this.size(); i++) {
            s.append(this.get(i));
        }
        return s.toString();
    }
}