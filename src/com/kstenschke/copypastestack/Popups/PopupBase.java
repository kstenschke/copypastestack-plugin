/*
 * Copyright 2014-2015 Kay Stenschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kstenschke.copypastestack.Popups;

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
