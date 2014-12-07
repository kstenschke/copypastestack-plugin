/*
 * Copyright 2014 Kay Stenschke
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

import com.kstenschke.copypastestack.TagManager;
import com.kstenschke.copypastestack.ToolWindow;
import com.kstenschke.copypastestack.resources.Icons;
import com.kstenschke.copypastestack.resources.StaticTexts;
import com.kstenschke.copypastestack.resources.ui.ToolWindowForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PopupTagUnset extends PopupBase {

    final private JPopupMenu popup;

    private final JMenuItem menuItemUnsetAllTags;

    /**
     * Constructor
     */
    public PopupTagUnset(final ToolWindow toolWindow) {
        this.popup = new JPopupMenu();

            // Copy selection
        menuItemUnsetAllTags = getJMenuItem(StaticTexts.POPUP_UNTAG_UNTAG_ALL, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ToolWindowForm form = toolWindow.getForm();
                TagManager.untagAll(form);
            }
        }, Icons.ICON_TAG_DELETE);
        this.popup.add(menuItemUnsetAllTags);
    }

    /**
     * @return  PopupListener
     */
    public PopupListener getPopupListener() {
        return new PopupListener();
    }

    /**
     * PopupListener
     */
    class PopupListener extends MouseAdapter {
        /**
         * @param   e
         */
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        /**
         * @param   e
         */
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        /**
         * @param   e
         */
        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger() ) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

}
