package com.kstenschke.copypastestack.Popups;

import com.intellij.openapi.util.IconLoader;
import com.kstenschke.copypastestack.resources.Icons;
import com.kstenschke.copypastestack.resources.StaticValues;
import com.kstenschke.copypastestack.ToolWindow;
import org.jetbrains.annotations.Nullable;

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

    /**
     * @param   label
     * @param   actionListener
     * @return  JMenuItem
     */
    JMenuItem getJMenuItem(String label, ActionListener actionListener, Icon icon, @Nullable Color foreground, @Nullable Color background, @Nullable KeyStroke acceleratorKeyStroke, char mnemoric) {
        JMenuItem item = getJMenuItem(label, actionListener, mnemoric);
        setJMenuItemIcon(item, icon);

        if( foreground != null && background != null ) {
            item.setBackground(background);
            item.setForeground(foreground);

        }

        if( acceleratorKeyStroke != null ) {
            item.setAccelerator(acceleratorKeyStroke);
        }

        return item;
    }

    JMenuItem getJMenuItem(String label, ActionListener actionListener, Icon icon, @Nullable Color foreground, @Nullable Color background) {
        return getJMenuItem(label, actionListener, icon, foreground, background, null, ' ');
    }

    JMenuItem getJMenuItem(String label, ActionListener actionListener, Icon icon) {
        return getJMenuItem(label, actionListener, icon, null, null);
    }

    /**
     * @param   jMenuItem
     */
    void setJMenuItemIcon(JMenuItem jMenuItem, Icon icon) {
        jMenuItem.setIcon(icon);
    }

}
