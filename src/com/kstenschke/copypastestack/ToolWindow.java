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
import com.kstenschke.copypastestack.Utils.*;
import com.kstenschke.copypastestack.resources.Icons;
import com.kstenschke.copypastestack.resources.StaticTexts;
import com.kstenschke.copypastestack.resources.StaticValues;
import com.kstenschke.copypastestack.resources.ui.ToolWindowForm;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;
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

    private UndoManager undoManager;

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
        initInlineEditor();

            // Add form into toolWindow
        add(form.getPanelMain(), BorderLayout.CENTER);
    }

    public ToolWindowForm getForm() {
        return form;
    }

    private void initIcons() {
        this.form.labelTag.setIcon( Icons.ICON_TAG );
        this.form.labelWrapOptions.setIcon( Icons.ICON_WRAP );
        this.form.labelImage.setIcon( Icons.ICON_IMAGE );
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
            this.form.splitPaneToolContent.setDividerLocation( dividerLocation );
        }

        this.form.splitPaneToolContent.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Integer location = form.splitPaneToolContent.getDividerLocation();
                Preferences.saveSplitPaneDividerLocation(location);
            }
        });
    }

    /**
     * @return  JList
     */
    public JList getItemsList() {
        return this.form.listClipItems;
    }

    private void initMainToolbar() {
        this.form.buttonRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshClipboardList();
            }
        });
        this.form.buttonRefresh.addMouseListener(new MouseListenerBase(StaticTexts.INFO_REFRESH));

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
                removeItemsFromStack(e);
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
        Object selectedValue = this.form.listClipItems.getSelectedValue();

        return selectedValue != null ? selectedValue.toString() : "";
    }

    /**
     * @param e Event
     */
    public void removeItemsFromStack(@Nullable ActionEvent e) {
        boolean isAnySpecialKeyPressed;

        boolean hasSelection = !this.form.listClipItems.isSelectionEmpty();
        String[] items = hasSelection ? getUnselectedItems() : new String[0];

        this.setClipboardListData(items,UtilsActionEvent.isAnySpecialKeyPressed(e));
        Preferences.saveCopyItems(items);

        if( !hasSelection ) {
            this.refreshClipboardList();
        }
    }

    /**
     * @return  String[]
     */
    private String[] getUnselectedItems() {
        ListModel<String> listModel         = this.form.listClipItems.getModel();
        ListSelectionModel selectionModel   = this.form.listClipItems.getSelectionModel();
        int[] selectedIndices               = this.form.listClipItems.getSelectedIndices();

        int amountItems         = listModel.getSize();
        String[] unselectedItems= new String[ amountItems - selectedIndices.length ];

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
        boolean hasSelection = ! this.form.listClipItems.isSelectionEmpty();
        int amountSelected   = ! hasSelection ? 0 : this.form.listClipItems.getSelectedIndices().length;
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
            ListModel<String> listModel         = this.form.listClipItems.getModel();
            ListSelectionModel selectionModel   = this.form.listClipItems.getSelectionModel();

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
            Object itemValue= this.form.listClipItems.getSelectedValue();
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
        boolean hasSelection = ! this.form.listClipItems.isSelectionEmpty();
        if( hasSelection ) {
            ListModel<String> listModel         = this.form.listClipItems.getModel();
            ListSelectionModel selectionModel   = this.form.listClipItems.getSelectionModel();

            int amountItems     = listModel.getSize();

            for(int i=0; i< amountItems; i++ ) {
                if( selectionModel.isSelectedIndex(i) ) {
                    String currentItemText  = listModel.getElementAt(i);
                    UtilsEnvironment.copyToClipboard(currentItemText);
                }
            }
        }
    }

    /**
     * Bubble all color-tagged items to the top of the list (groups: yellow, green, reed, untagged)
     */
    private void sortClipboardListByTags(boolean sortAlphabetically) {
        ListModel<String> listModel = this.form.listClipItems.getModel();
        int amountItems = listModel.getSize();

        if( amountItems > 0 ) {
            String[] itemsYellow    = new String[amountItems+1];
            int indexYellow         = 0;

            String[] itemsGreen     = new String[amountItems+1];
            int indexGreen          = 0;

            String[] itemsRed       = new String[amountItems+1];
            int indexRed            = 0;

            String[] itemsUntagged  = new String[amountItems+1];
            int indexUntagged       = 0;

                // Refill items from listModel into sortable arrays
            for (int i = 0; i < amountItems; i++) {
                String item = listModel.getElementAt(i);
                if( item != null && ! item.trim().isEmpty() ) {
                    switch( TagManager.getIdColorByValue(item) ) {
                        case StaticValues.ID_COLOR_YELLOW:
                            itemsYellow[indexYellow] = item;
                            indexYellow++;
                            break;

                        case StaticValues.ID_COLOR_GREEN:
                            itemsGreen[indexGreen] = item;
                            indexGreen++;
                            break;

                        case StaticValues.ID_COLOR_RED:
                            itemsRed[indexRed] = item;
                            indexRed++;
                            break;

                        case StaticValues.ID_COLOR_NONE:
                            itemsUntagged[indexUntagged] = item;
                            indexUntagged++;
                            break;
                    }
                }
            }

                // Reduce arrays to size of actual content
            itemsYellow     = Arrays.copyOfRange(itemsYellow, 0, indexYellow);
            itemsRed        = Arrays.copyOfRange(itemsRed, 0, indexRed);
            itemsGreen      = Arrays.copyOfRange(itemsGreen, 0, indexGreen);
            itemsUntagged   = Arrays.copyOfRange(itemsUntagged, 0, indexUntagged);

                // Sort string items alphabetically
            if( sortAlphabetically ) {
                if (itemsYellow.length > 1)   Arrays.sort(itemsYellow, String.CASE_INSENSITIVE_ORDER);
                if (itemsGreen.length > 1)    Arrays.sort(itemsGreen, String.CASE_INSENSITIVE_ORDER);
                if (itemsRed.length > 1)      Arrays.sort(itemsRed, String.CASE_INSENSITIVE_ORDER);
                if (itemsUntagged.length > 1) Arrays.sort(itemsUntagged, String.CASE_INSENSITIVE_ORDER);
            }

                // Update list from sorted items
            this.setClipboardListData(
                ArrayUtils.addAll(
                    ArrayUtils.addAll(
                        ArrayUtils.addAll(itemsYellow, itemsGreen), itemsRed
                    ),  itemsUntagged
                ), false
            );
        }
    }

    private void sortClipboardListAlphabetical() {
        this.sortClipboardListByTags(true);
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

        String[] copyItemsList   = new String[amountItems];
        if( amountItems > 0 || hasClipboardContent ) {
                // Add copied string items, historic and current
            int index           = 0;
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
            itemsUnique            = UtilsArray.tidy(allItems);
            if( itemsUnique.length > 0 ) {
                this.setClipboardListData(itemsUnique, false);
                this.sortClipboardListByTags(this.form.checkboxKeepSorted.isSelected());

                Preferences.saveCopyItems(itemsUnique);
            }
        }

        initStatusLabel(itemsUnique == null ? 0 : itemsUnique.length);
    }

    /**
     * @param   items
     * @param   overrideIDEclipItems    false (Default): remove items from shown list and restore them on refresh / true: (if SHIFT or CTRL or ALT pressed) remove them permanently from IDE
     */
    private void setClipboardListData(Object[] items, boolean overrideIDEclipItems) {
        items = UtilsArray.tidy(items);

        this.form.listClipItems.setListData(items);
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
        this.form.listPreview.setCellRenderer(
                new ListCellRendererCopyPasteStack(this.form.listPreview, false, this.isMac, true)
        );

        String[] items    = { StaticTexts.ITEM_TEXT_VIEW_CURRENT };
        this.form.listPreview.setListData( items );
    }

    /**
     * @return  Amount of items
     */
    private int initItemsList() {
        this.form.listClipItems.setCellRenderer(
                new ListCellRendererCopyPasteStack(this.form.listClipItems, false, this.isMac, false)
        );
        String[] items = Preferences.getItems();
        this.setClipboardListData(items, false);
        if( Preferences.getIsActiveKeepSorting() ) {
            this.sortClipboardListAlphabetical();
        }

            // Add keyListener
        this.form.listClipItems.addKeyListener( new KeyListenerItemsList(this));
        this.form.listClipItems.addMouseListener(new MouseListenerItemsList(StaticTexts.INFO_LIST, this));

            // add popup listener
        this.form.listClipItems.addMouseListener(new PopupItems(this).getPopupListener() );

        return items.length;
    }

    public boolean isPreviewImage50Percent() {
        return this.form.checkBoxScale50Percent.isSelected();
    }

    private void initItemsPreview() {
        this.form.labelPreviewImage.setText("");
        this.form.panelPreviewImage.setVisible(false);
        this.form.scrollPanePreview.setVisible(true);

            // Install listener to update shown preview from current clipboard
        final FocusListenerViewClipboard focusListenerViewClipboard = new FocusListenerViewClipboard(this);
        this.form.listPreview.addFocusListener( focusListenerViewClipboard );
        this.form.checkBoxScale50Percent.addChangeListener(new ChangeListenerResizePreview(focusListenerViewClipboard));

        this.form.buttonRefreshPreview.addMouseListener( new MouseListenerBase(StaticTexts.INFO_REFRESH_PREVIEW));
        final JList listPreview = this.form.listPreview;
        this.form.buttonRefreshPreview.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                focusListenerViewClipboard.updateClipboardPreview();
                listPreview.requestFocusInWindow();
                listPreview.setSelectedIndex(0);
            }
        });

            // Install items listener to update shown preview from stacked items
        this.form.listClipItems.addListSelectionListener(new ListSelectionListenerItemsList(this));

            // Add popup listener
        this.form.textPanePreview.addMouseListener(new PopupPreview(this).getPopupListener() );
    }

    /**
     * Add undoManager to clip pane
     */
    private void initInlineEditor() {
        Document document = this.form.textPanePreview.getDocument();
        if( this.undoManager != null) {
            document.removeUndoableEditListener(this.undoManager);
        }

        this.undoManager = new UndoManager();
        document.addUndoableEditListener( this.undoManager );

        final ToolWindow toolWindowFin = this;

        InputMap inputMap = this.form.textPanePreview.getInputMap();

        // CTRL + Z = undo (⌘ + Z on Mac OS)
        inputMap.put(
                KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if( toolWindowFin.undoManager.canUndo() ) {
                            toolWindowFin.undoManager.undo();
                        }
                    }
                }
        );

        // CTRL + SHIFT + Z = redo (⌘ + SHIFT + Z on Mac OS)
        inputMap.put(
                KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.SHIFT_DOWN_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if( toolWindowFin.undoManager.canRedo() ) {
                            toolWindowFin.undoManager.redo();
                        }
                    }
                }
        );
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
    private static ToolWindow getInstance(Project project) {
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
