package com.kstenschke.copypastestack.Popups;


import com.kstenschke.copypastestack.Static.StaticTexts;
import com.kstenschke.copypastestack.ToolWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PopupItems extends PopupBase {

    final private JPopupMenu popup;
    final private JList jListItems;

    /**
     * Constructor
     */
    public PopupItems(final ToolWindow toolWindow) {
        final Class classToolWindow   = getToolWindowClassInstance(toolWindow);

        this.jListItems = toolWindow.getJlistItems();

        this.popup = new JPopupMenu();

//            // Toggle favorite
//        JMenuItem menuItemFavorite = getJMenuItem(StaticTexts.POPUP_ITEMS_TOGGLE_FAVORITE, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//            }
//        }, classToolWindow, "resources/images/favorite.png", null, null, null, 'R');
//        this.popup.add(menuItemFavorite);
//
//
//        this.popup.addSeparator();

            // Paste
        JMenuItem menuItemPaste = getJMenuItem(StaticTexts.POPUP_ITEMS_PASTE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toolWindow.pasteItems();
            }
        }, classToolWindow, "resources/images/paste.png");
        this.popup.add(menuItemPaste);

            // Re-copy
        JMenuItem menuItemRecopy = getJMenuItem(StaticTexts.POPUP_ITEMS_RECOPY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toolWindow.copySelectedItems();
            }
        }, classToolWindow, "resources/images/copy.png");
        this.popup.add(menuItemRecopy);

        this.popup.addSeparator();

            // Delete
        JMenuItem menuItemDelete = getJMenuItem(StaticTexts.POPUP_ITEMS_DELETE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toolWindow.removeSelectedItems();
            }
        }, classToolWindow, "resources/images/delete.png");
        this.popup.add(menuItemDelete);

        this.popup.addSeparator();

            // Color tags
        JMenuItem menuItemTagYellow = getJMenuItem(StaticTexts.INFO_TAG_YELLOW, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //...
            }
        }, classToolWindow, "resources/images/tag-label-yellow.png");
        this.popup.add(menuItemTagYellow);

        JMenuItem menuItemTagGreen = getJMenuItem(StaticTexts.INFO_TAG_GREEN, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //...
            }
        }, classToolWindow, "resources/images/tag-label-green.png");
        this.popup.add(menuItemTagGreen);

        JMenuItem menuItemTagRed = getJMenuItem(StaticTexts.INFO_TAG_RED, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //...
            }
        }, classToolWindow, "resources/images/tag-label-red.png");
        this.popup.add(menuItemTagRed);

        JMenuItem menuItemTagRemove = getJMenuItem(StaticTexts.INFO_TAG_REMOVE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //...
            }
        }, classToolWindow, "resources/images/tag.png");
        this.popup.add(menuItemTagRemove);


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
                Boolean isAnySelected  = getIndexSelected() > 0;
                if( isAnySelected ) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }
    }

    /**
     * @return  Integer
     */
    Integer getIndexSelected() {
        return jListItems.getSelectedIndex();
    }

}
