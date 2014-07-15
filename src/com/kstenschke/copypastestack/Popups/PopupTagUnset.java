package com.kstenschke.copypastestack.Popups;


import com.kstenschke.copypastestack.TagManager;
import com.kstenschke.copypastestack.ToolWindow;
import com.kstenschke.copypastestack.Utils.UtilsEnvironment;
import com.kstenschke.copypastestack.resources.Icons;
import com.kstenschke.copypastestack.resources.StaticTexts;
import com.kstenschke.copypastestack.resources.StaticValues;
import com.kstenschke.copypastestack.resources.ui.ToolWindowForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PopupTagUnset extends PopupBase {

    private ToolWindow toolWindow;

    final private JPopupMenu popup;

    JMenuItem menuItemUnsetAllTags;

    /**
     * Constructor
     */
    public PopupTagUnset(final ToolWindow toolWindow) {
        this.toolWindow = toolWindow;

        this.popup = new JPopupMenu();

            // Copy selection
        menuItemUnsetAllTags = getJMenuItem(StaticTexts.POPUP_UNTAG_UNTAG_ALL, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ToolWindowForm form = toolWindow.getForm();
                TagManager.tagAll(form, StaticValues.ID_COLOR_NONE);
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
