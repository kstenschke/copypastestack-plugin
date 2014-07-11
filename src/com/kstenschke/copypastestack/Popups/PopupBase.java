package com.kstenschke.copypastestack.Popups;

import com.kstenschke.copypastestack.Static.StaticValues;
import com.kstenschke.copypastestack.ToolWindow;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class PopupBase {

    /**
     * Test for way w/ best performance and get class instance of ToolWindow
     *
     * @return  Class
     */
    Class getToolWindowClassInstance(@Nullable ToolWindow toolWindow) {
        Class classTW = null;

        if( toolWindow != null ) {
            classTW = toolWindow.getClass();
            if( classTW != null ) {
                return classTW;
            }
        }

        ToolWindow tw = ToolWindow.getInstance();

        if( tw != null ) {
            return tw.getClass();
        } else {
            try {
                classTW = Class.forName("com.kstenschke.copypastestack.ToolWindow");
            } catch(Exception exception) {
                exception.printStackTrace();
            }

            return classTW;
        }
    }

    /**
     * @param   label
     * @param   actionListener
     * @return  JMenuItem
     */
    JMenuItem getJMenuItem(String label, ActionListener actionListener, char mnemonic) {
        JMenuItem item = mnemonic != StaticValues.NULL_CHAR ? new JMenuItem(label, mnemonic) : new JMenuItem(label);

        item.addActionListener(actionListener);

        return item;
    }

    JMenuItem getJMenuItem(String label, ActionListener actionListener, Class classToolWindow, @Nullable String pathImage, @Nullable Color foreground, @Nullable Color background) {
        return getJMenuItem(label, actionListener, classToolWindow, pathImage, foreground, background, null);
    }

    JMenuItem getJMenuItem(String label, ActionListener actionListener, Class classToolWindow, @Nullable String pathImage, @Nullable Color foreground, @Nullable Color background, KeyStroke accelleratorKeyStroke) {
        return getJMenuItem(label, actionListener, classToolWindow, pathImage, foreground, background, accelleratorKeyStroke, StaticValues.NULL_CHAR);
    }

    /**
     * @param   label
     * @param   actionListener
     * @param   classToolWindow
     * @param   pathImage
     * @return  JMenuItem
     */
    JMenuItem getJMenuItem(String label, ActionListener actionListener, Class classToolWindow, @Nullable String pathImage, @Nullable Color foreground, @Nullable Color background, KeyStroke acceleratorKeyStroke, char mnemoric) {
        JMenuItem item = getJMenuItem(label, actionListener, mnemoric);
        setJMenuItemIcon(item, classToolWindow, pathImage);

        if( foreground != null && background != null ) {
            item.setBackground(background);
            item.setForeground(foreground);

        }

        if( acceleratorKeyStroke != null ) {
            item.setAccelerator(acceleratorKeyStroke);
        }

        return item;
    }

    /**
     * @param   label
     * @param   actionListener
     * @param   classToolWindow
     * @param   pathImage
     * @return  JMenuItem
     */
    JMenuItem getJMenuItem(String label, ActionListener actionListener, Class classToolWindow, @Nullable String pathImage) {

        return getJMenuItem(label, actionListener, classToolWindow, pathImage, null, null);
    }

    /**
     * @param   jMenuItem
     * @param   classToolWindow
     * @param   pathImage
     */
    void setJMenuItemIcon(JMenuItem jMenuItem, Class classToolWindow, @Nullable String pathImage) {
        if( pathImage == null ) {
            pathImage = "resources/images/blank16x16.png";
        }

        if( classToolWindow == null ) {
            classToolWindow = ToolWindow.getInstance().getClass();
        }

        try {
            Image image    = ImageIO.read( classToolWindow.getResource(pathImage) );
            ImageIcon icon = new ImageIcon(image);
            jMenuItem.setIcon(icon);
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }

}
