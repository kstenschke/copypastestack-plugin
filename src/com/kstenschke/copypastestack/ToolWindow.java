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
package com.kstenschke.copypastestack;

import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.kstenschke.copypastestack.Listeners.*;
import com.kstenschke.copypastestack.Popups.PopupItems;
import com.kstenschke.copypastestack.Popups.PopupPreview;
import com.kstenschke.copypastestack.Popups.PopupTagUnset;
import com.kstenschke.copypastestack.resources.Icons;
import com.kstenschke.copypastestack.resources.StaticTexts;
import com.kstenschke.copypastestack.resources.StaticValues;
import com.kstenschke.copypastestack.Utils.UtilsArray;
import com.kstenschke.copypastestack.Utils.UtilsClipboard;
import com.kstenschke.copypastestack.Utils.UtilsEnvironment;
import com.kstenschke.copypastestack.Utils.UtilsString;
import com.kstenschke.copypastestack.resources.ui.ToolWindowForm;
import org.apache.commons.lang.ArrayUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

public class ToolWindow extends SimpleToolWindowPanel {

    public final ToolWindowForm form;
    private final boolean isMac;

    /**
     * Constructor - initialize the tool window content
     */
    public ToolWindow() {
        super(false);

        this.isMac  = UtilsEnvironment.isMac();
        this.form    = new ToolWindowForm();

        initIcons();
        initSplitPane();

        initCurrentClipboardViewer();

        int amountItems = initItemsList();
        initStatusLabel(amountItems);

        initMainToolbar();
        initWrapOptions();
        initItemsPreview();

            // Add form into toolWindow
        add(form.getMainPanel(), BorderLayout.CENTER);
    }

    public ToolWindowForm getForm() {
        return form;
    }

    private void initIcons() {
        this.form.labelTag.setIcon( Icons.ICON_TAG );
        this.form.labelSettings.setIcon( Icons.ICON_SETTINGS );
        this.form.buttonRefresh.setIcon( Icons.ICON_REFRESH );
        this.form.buttonTagUnset.setIcon(Icons.ICON_TAG_DELETE);
        this.form.buttonCopy.setIcon( Icons.ICON_COPY );
        this.form.buttonPaste.setIcon( Icons.ICON_PASTE );
        this.form.buttonDelete.setIcon( Icons.ICON_DELETE );
        this.form.buttonInfo.setIcon( Icons.ICON_QUESTIONMARK );
    }

    private void initSplitPane() {
        Integer dividerLocation = Preferences.getSplitPaneDividerLocation();
        if( dividerLocation != null ) {
            this.form.splitpaneToolContent.setDividerLocation( dividerLocation );
        }

        this.form.splitpaneToolContent.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Integer location = form.splitpaneToolContent.getDividerLocation();
                Preferences.saveSplitPaneDividerLocation(location);
            }
        });
    }

    /**
     * @return  JList
     */
    public JList getItemsList() {
        return this.form.clipItemsList;
    }

    private void initMainToolbar() {
        this.form.buttonRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshClipboardList();
            }
        });
        this.form.buttonRefresh.addMouseListener( new MouseListenerBase(StaticTexts.INFO_REFRESH));

        this.form.buttonSortAlphabetical.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortClipboardListAlphabetical();
            }
        });
        this.form.buttonSortAlphabetical.addMouseListener(new MouseListenerBase(StaticTexts.INFO_SORT));

        this.form.checkboxKeepSorted.setSelected( Preferences.getIsActiveKeepSorting() );
        this.form.checkboxKeepSorted.addMouseListener( new MouseListenerBase(StaticTexts.INFO_KEEP_SORTED_ALPHABETICAL));
        this.form.checkboxKeepSorted.addChangeListener( new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                boolean isActive = form.checkboxKeepSorted.isSelected();
                if( isActive ) {
                    sortClipboardListAlphabetical();
                } else {
                    refreshClipboardList();
                }

                Preferences.saveIsActiveKeepSorting(isActive);
            }
        });

        this.form.buttonDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedItems();
            }
        });
        this.form.buttonDelete.addMouseListener(new MouseListenerBase(StaticTexts.INFO_DELETE));

        this.form.buttonPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pasteItems();
            }
        });
        this.form.buttonPaste.addMouseListener(new MouseListenerBase(StaticTexts.INFO_PASTE));

        this.form.buttonCopy.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copySelectedItems();
            }
        } );
        this.form.buttonCopy.addMouseListener(new MouseListenerBase(StaticTexts.INFO_RECOPY));

        this.initTagOptions();
        this.initAdditionalOptions();
    }

    /**
     * @return  String
     */
    public String getSelectedItemText() {
        Object selectedValue = this.form.clipItemsList.getSelectedValue();

        return selectedValue != null ? selectedValue.toString() : "";
    }

    public void removeSelectedItems() {
        boolean hasSelection = ! this.form.clipItemsList.isSelectionEmpty();
        String[] items       = hasSelection ? getUnselectedItems() : new String[0];

        this.updateItemsList(items);
        Preferences.saveCopyItems(items);

        if( !hasSelection ) {
            this.refreshClipboardList();
        }
    }

    /**
     * @return  String[]
     */
    private String[] getUnselectedItems() {
        ListModel<String> listModel                     = this.form.clipItemsList.getModel();
        javax.swing.ListSelectionModel selectionModel   = this.form.clipItemsList.getSelectionModel();
        int[] selectedIndices                           = this.form.clipItemsList.getSelectedIndices();

        int amountItems             = listModel.getSize();
        String[] unselectedItems    = new String[ amountItems - selectedIndices.length ];

        int index = 0;
        for(int i=0; i< amountItems; i++ ) {
            if( !selectionModel.isSelectedIndex(i) ) {
                String item = listModel.getElementAt(i);
                if( !item.trim().isEmpty() ) {
                    unselectedItems[index] = item;
                    index++;
                }
            }
        }

        return unselectedItems;
    }

    /**
     * Paste all / selected items into editor
     */
    public void pasteItems() {
        boolean hasSelection = ! this.form.clipItemsList.isSelectionEmpty();
        int amountSelected   = ! hasSelection ? 0 : this.form.clipItemsList.getSelectedValuesList().size();
        boolean focusEditor = this.form.checkboxFocusOnPaste.isSelected();

        String wrapBefore   = "";
        String wrapAfter    = "";
        String wrapDelimiter= "";

        if( this.form.checkBoxWrap.isSelected() ) {
            wrapBefore      = this.form.textFieldWrapBefore.getText();
            wrapAfter       = this.form.textFieldWrapAfter.getText();
            wrapDelimiter   = this.form.textFieldWrapDelimiter.getText();

            if( this.form.checkboxWrapExtended.isSelected() ) {
                wrapBefore      = UtilsString.convertWhitespace(wrapBefore);
                wrapAfter       = UtilsString.convertWhitespace(wrapAfter);
                wrapDelimiter   = UtilsString.convertWhitespace(wrapDelimiter);
            }
        }

        if( !hasSelection || amountSelected > 1 ) {
                // Insert multiple items
            ListModel<String> listModel                     = this.form.clipItemsList.getModel();
            javax.swing.ListSelectionModel selectionModel   = this.form.clipItemsList.getSelectionModel();

            int amountItems     = listModel.getSize();
            int amountInserted  = 0;
            for(int i=0; i< amountItems; i++ ) {
                if( selectionModel.isSelectedIndex(i) ) {
                    String currentItemText  = listModel.getElementAt(i);
                    UtilsEnvironment.insertInEditor(
                        wrapBefore + currentItemText + wrapAfter + (amountInserted + 1 < amountSelected ? wrapDelimiter : ""), focusEditor
                    );
                    amountInserted++;
                }
            }
        } else {
                // Insert selected item
            Object itemValue= this.form.clipItemsList.getSelectedValue();
            if( itemValue != null ) {
                String itemText = itemValue.toString();
                if( this.form.checkBoxWrap.isSelected() ) {
                    itemText    = wrapBefore + itemText + wrapAfter;
                }
                UtilsEnvironment.insertInEditor(itemText, focusEditor);
            }
        }
    }

    /**
     * Copy selected items back into clipboard
     */
    public void copySelectedItems() {
        boolean hasSelection = ! this.form.clipItemsList.isSelectionEmpty();
        if( hasSelection ) {
            ListModel<String> listModel                     = this.form.clipItemsList.getModel();
            javax.swing.ListSelectionModel selectionModel   = this.form.clipItemsList.getSelectionModel();

            int amountItems     = listModel.getSize();

            for(int i=0; i< amountItems; i++ ) {
                if( selectionModel.isSelectedIndex(i) ) {
                    String currentItemText  = listModel.getElementAt(i);
                    UtilsEnvironment.copyToClipboard(currentItemText);
                }
            }
        }
    }

    private void sortClipboardListAlphabetical() {
        ListModel<String> listModel = this.form.clipItemsList.getModel();
        int amountItems = listModel.getSize();

        if( amountItems > 0 ) {
            String[] items   = new String[amountItems];

                // Refill items from listModel into sortable array
            int index = 0;
            for (int i = 1; i < amountItems; i++) {
                String item = listModel.getElementAt(i);
                if( item != null && ! item.trim().isEmpty() ) {
                    items[index] = item;
                    index++;
                }
            }
                // Sort string items alphabetically
            if( items.length > 1 ) {
                    // // Skip item 0 (viewer of current contents) to keep it topmost
                Arrays.sort(items, 1, items.length - 1, String.CASE_INSENSITIVE_ORDER);
            }

            this.updateItemsList(items);
        }
    }

    /**
     * Refresh listed items from distinct merged sum of:
     * 0. option to preview the current clipboard contents
     * 1. previous clipboard items still in prefs
     * 2. current clipboard items
     */
    private void refreshClipboardList() {
        Transferable[] copiedItems = CopyPasteManager.getInstance().getAllContents();
        int amountItems            = UtilsClipboard.getAmountStringItemsInTransferables(copiedItems);
        String[] itemsUnique       = null;
        boolean hasClipboardContent= UtilsClipboard.hasContent();

        String[] copyItemsList   = new String[amountItems + 1];

            // Add item for previewing current text or image content of clipboard
        copyItemsList[0] = StaticTexts.ITEM_TEXT_VIEW_CURRENT;

        if( amountItems > 0 || hasClipboardContent ) {
                // Add copied string items, historic and current
            int index           = 1;
            for( Transferable currentItem : copiedItems) {
                if( currentItem.isDataFlavorSupported( DataFlavor.stringFlavor ) )  {
                    try {
                        String itemStr  = currentItem.getTransferData( DataFlavor.stringFlavor ).toString();
                        if( !itemStr.trim().isEmpty() ) {
                            copyItemsList[index] = itemStr;
                            index++;
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }

                // Tidy items: distinct items, none empty
            String[] copyItemsPref = Preferences.getItems();
            Object[] allItems      = (copyItemsPref.length > 0) ? ArrayUtils.addAll(copyItemsList, copyItemsPref) : copyItemsList;
            itemsUnique            = UtilsArray.tidy(allItems, true, true);
            if( itemsUnique.length > 0 ) {
                this.updateItemsList(itemsUnique);

                if( this.form.checkboxKeepSorted.isSelected() ) {
                    this.sortClipboardListAlphabetical();
                }

                Preferences.saveCopyItems(itemsUnique);
            }
        }

        initStatusLabel(itemsUnique == null ? 0 : itemsUnique.length);
    }

    /**
     * @param   items
     */
    private void updateItemsList(Object[] items) {
        items = UtilsArray.tidy(items, true, true);

        this.form.clipItemsList.setListData( items );
    }

    /**
     * @param   amountItems
     */
    public void initStatusLabel(int amountItems) {
        this.form.labelStatus.setText(String.valueOf(amountItems - 1) + " " + StaticTexts.LABEL_ITEMS);
    }

    /**
     * Init current clipboard content viewer
     */
    private void initCurrentClipboardViewer(){
        this.form.jListPreview.setCellRenderer(
                new ListCellRendererCopyPasteStack(this.form.jListPreview, false, this.isMac, true)
        );

        String[] items    = { StaticTexts.ITEM_TEXT_VIEW_CURRENT };
        this.form.jListPreview.setListData( items );
    }

    /**
     * @return  Amount of items
     */
    private int initItemsList() {
        this.form.clipItemsList.setCellRenderer(
                new ListCellRendererCopyPasteStack(this.form.clipItemsList, false, this.isMac, false)
        );
        String[] items = Preferences.getItems();
        this.updateItemsList(items);
        if( Preferences.getIsActiveKeepSorting() ) {
            this.sortClipboardListAlphabetical();
        }

            // Add keyListener
        this.form.clipItemsList.addKeyListener( new KeyListenerItemsList(this));
        this.form.clipItemsList.addMouseListener(new MouseListenerItemsList(StaticTexts.INFO_LIST, this));

            // add popup listener
        this.form.clipItemsList.addMouseListener(new PopupItems(this).getPopupListener() );

        return items.length;
    }

    public boolean isPreviewImage50Percent() {
        return this.form.a50CheckBox.isSelected();
    }

    private void initItemsPreview() {
        this.form.labelPreviewImage.setText("");
        this.form.panelPreviewImage.setVisible(false);
        this.form.scrollPanePreview.setVisible(true);

            // Install listener to update shown preview from current clipboard
        FocusListenerViewClipboard focusListenerViewClipboard = new FocusListenerViewClipboard(this);
        this.form.jListPreview.addFocusListener( focusListenerViewClipboard );
        this.form.a50CheckBox.addChangeListener(new ChangeListenerResizePreview(focusListenerViewClipboard));

            // Install items listener to update shown preview from stacked items
        this.form.clipItemsList.addListSelectionListener(new ListSelectionListenerItemsList(this));

            // Add popup listener
        this.form.textPanePreview.addMouseListener(new PopupPreview(this).getPopupListener() );
    }

    /**
     * @param itemText
     */
    public void setPreviewText(String itemText) {
        this.form.textPanePreview.setText(itemText);
    }

    private void initWrapOptions() {
        boolean isActiveWrap = Preferences.getIsActiveWrap();
        this.form.checkBoxWrap.setSelected( isActiveWrap );
        this.form.checkBoxWrap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isActive = form.checkBoxWrap.isSelected();
                form.panelWrap.setVisible( isActive );
                if( isActive ) {
                    form.textFieldWrapBefore.requestFocusInWindow();
                }
                Preferences.saveIsActiveWrap(isActive);
            }
        });

        this.form.panelWrap.setVisible( isActiveWrap );

        this.form.textFieldWrapBefore.setText(Preferences.getWrapBefore());

        FocusListener focusListenerWrapComponents = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                Preferences.saveWrapBefore( form.textFieldWrapBefore.getText() );
                Preferences.saveWrapAfter(form.textFieldWrapAfter.getText());
                Preferences.saveWrapDelimiter(form.textFieldWrapDelimiter.getText());
            }
        };

        this.form.textFieldWrapBefore.addFocusListener( focusListenerWrapComponents );

        this.form.textFieldWrapAfter.setText(Preferences.getWrapAfter());
        this.form.textFieldWrapAfter.addFocusListener(focusListenerWrapComponents);

        this.form.textFieldWrapDelimiter.setText(Preferences.getWrapDelimiter());
        this.form.textFieldWrapDelimiter.addFocusListener(focusListenerWrapComponents);
    }

    private void initAdditionalOptions() {
        this.form.checkboxImmediatePaste.addMouseListener(new MouseListenerBase(StaticTexts.INFO_IMMEDIATE_PASTE));
        this.form.labelOptionImmediateInsert.addMouseListener( new MouseListenerCheckboxLabel(StaticTexts.INFO_IMMEDIATE_PASTE, this.form.checkboxImmediatePaste));

        boolean isActiveImmediatePaste = Preferences.getIsActiveImmediatePaste();
        form.checkboxImmediatePaste.setSelected(isActiveImmediatePaste);
        this.form.checkboxImmediatePaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isActive = form.checkboxImmediatePaste.isSelected();
                Preferences.saveIsActiveImmediatePaste(isActive);
            }
        });

        boolean isActiveFocusOnPaste = Preferences.getIsActiveFocusOnPaste();
        this.form.checkboxFocusOnPaste.setSelected(isActiveFocusOnPaste);

        this.form.checkboxFocusOnPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isActive = form.checkboxFocusOnPaste.isSelected();
                Preferences.saveIsActiveFocusOnPaste( isActive );
            }
        });

        this.form.checkboxFocusOnPaste.addMouseListener(new MouseListenerBase(StaticTexts.INFO_FOCUS_ON_PASTE));
        this.form.labelOptionFocusOnPaste.addMouseListener( new MouseListenerCheckboxLabel(StaticTexts.INFO_FOCUS_ON_PASTE, this.form.checkboxFocusOnPaste));

        this.form.checkboxWrapExtended.setSelected(Preferences.getIsActiveWrapExtended());
        this.form.checkboxWrapExtended.addMouseListener( new MouseListenerBase(StaticTexts.INFO_WRAP_EXTENDED));
        this.form.checkboxWrapExtended.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Preferences.saveIsActiveWrapExtended(form.checkboxWrapExtended.isSelected());
            }
        });

        this.form.buttonInfo.addMouseListener(new MouseListenerBase(StaticTexts.INFO_PLUGIN_ABOUT));
        this.form.buttonInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UtilsEnvironment.openUrl("http://plugins.jetbrains.com/plugin/7478");
                } catch(Exception ignored) {

                }
            }
        });
    }

    private void initTagOptions() {
        this.form.buttonTagYellow.addMouseListener(new MouseListenerBase(StaticTexts.INFO_TAG_YELLOW));
        this.form.buttonTagGreen.addMouseListener(new MouseListenerBase(StaticTexts.INFO_TAG_GREEN));
        this.form.buttonTagRed.addMouseListener(new MouseListenerBase(StaticTexts.INFO_TAG_RED));

        this.form.buttonTagYellow.addActionListener(new ActionListenerTag(this, StaticValues.ID_COLOR_YELLOW));
        this.form.buttonTagRed.addActionListener(new ActionListenerTag(this, StaticValues.ID_COLOR_RED));
        this.form.buttonTagGreen.addActionListener(new ActionListenerTag(this, StaticValues.ID_COLOR_GREEN));

        this.form.buttonTagUnset.addMouseListener(new MouseListenerBase(StaticTexts.INFO_TAG_REMOVE));
        this.form.buttonTagUnset.addMouseListener(new PopupTagUnset(this).getPopupListener());
        this.form.buttonTagUnset.addActionListener(new ActionListenerTag(this, StaticValues.ID_COLOR_NONE));

    }

    /**
     * @return  boolean
     */
    public boolean isSelectedImmediatePaste() {
        return this.form.checkboxImmediatePaste.isSelected();
    }

    /**
     * @param   project     Idea Project
     * @return  Instance of AhnToolWindow
     */
    public static ToolWindow getInstance(Project project) {
        com.intellij.openapi.wm.ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Copy/Paste Stack");

        if( toolWindow != null ) {
            try {
                Content content = toolWindow.getContentManager().getContent(0);
                if( content != null ) {
                    JComponent toolWindowComponent = content.getComponent();
                    String canonicalName = toolWindowComponent.getClass().getCanonicalName();
                    if(canonicalName.endsWith("com.kstenschke.copypastestack.ToolWindow")) {
                        return (ToolWindow) toolWindowComponent;
                    }
                }
            } catch(Exception exception) {
                exception.printStackTrace();
            }
        }

        return null;
    }

    /**
     * @return  ToolWindow
     */
    public static ToolWindow getInstance() {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if( openProjects.length == 0 ) {
            return null;
        }

        Project project= openProjects[0];

        return getInstance(project);
    }

}
