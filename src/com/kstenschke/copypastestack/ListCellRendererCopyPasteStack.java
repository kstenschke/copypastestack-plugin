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

import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.kstenschke.copypastestack.Utils.UtilsClipboard;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * ListCellRenderer for copy items
 *
 * @param   <String>
 */
public class ListCellRendererCopyPasteStack<String> extends JPanel implements ListCellRenderer<String> {

    private final Border selectedBorder;
    private final Border borderWhite;
    private final Border borderYellow;
    private final Border borderGreen;
    private final Border borderRed;
    private final Border nonFocusBorder;

    private final JBLabel columnText;

    private final Color colorSelectionBackground;
    private final Color colorSelectionBackgroundNoFocus;
    private final Color colorSelectionForeground;
    private final Color colorBackground;
    private final Color colorForeground;

    private ImageIcon iconDefault;
    private ImageIcon iconHistoric;
    private final Boolean isMac;

    public final static int idColorNone   = 0;
    public final static int idColorYellow = 1;
    public final static int idColorGreen  = 2;
    public final static int idColorRed    = 3;

    private Color colorYellow   = new Color(255, 238, 169);
    private Color colorGreen    = new Color(202, 233, 190);
    private Color colorRed      = new Color(238, 207, 207);

    /**
     * Constructor
     *
     * @param   list
     * @param   isDark
     */
    public ListCellRendererCopyPasteStack(JList<String> list, Boolean isDark, Boolean isMac) {
        this.isMac  = isMac;

        this.setFont(list.getFont());
        this.setLayout(new BorderLayout());

        this.columnText = new JBLabel();
        this.columnText.setHorizontalAlignment(SwingConstants.LEFT);

        this.add(columnText, BorderLayout.WEST);

        this.colorSelectionBackground = list.getSelectionBackground();

        Color colorDarkBgNoFocus = new JBColor(new Color(13, 41, 62), new Color(13, 41, 62));
        this.colorSelectionBackgroundNoFocus = isDark ? colorDarkBgNoFocus : new JBColor(Gray._212, Gray._212);
        this.colorSelectionForeground = list.getSelectionForeground();
        this.colorBackground = list.getBackground();
        this.colorForeground = list.getForeground();

        this.borderWhite    = BorderFactory.createLineBorder(this.colorBackground);
        this.borderYellow   = BorderFactory.createLineBorder(this.colorYellow);
        this.borderGreen    = BorderFactory.createLineBorder(this.colorGreen);
        this.borderRed      = BorderFactory.createLineBorder(this.colorRed);
        this.nonFocusBorder = BorderFactory.createLineBorder(isDark ? colorDarkBgNoFocus : this.colorSelectionBackground);
        this.selectedBorder = BorderFactory.createLineBorder(this.colorSelectionBackground.darker());

        initIcons();
    }

    public void initIcons() {
        try {
            this.iconDefault = new ImageIcon( ImageIO.read(getClass().getResource("resources/images/item.png")) );
            this.iconHistoric= new ImageIcon( ImageIO.read( getClass().getResource("resources/images/item-historic.png")) );
        } catch(Exception ide ) {
            ide.printStackTrace();
        }
    }

    /**
     * @param   list
     * @param   value
     * @param   index
     * @param   isSelected
     * @param   cellHasFocus
     * @return  Component
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        java.lang.String itemText = value != null ? value.toString() : "";

        columnText.setIcon( UtilsClipboard.isInClipboard(itemText) ? this.iconDefault : this.iconHistoric);
        columnText.setText(itemText);

        Boolean hasFocus    = list.hasFocus();

        int hashCode = itemText.hashCode();

        if (isSelected) {
                // Item is selected
            setBackground( hasFocus ? this.colorSelectionBackground : colorSelectionBackgroundNoFocus );
            setForeground( this.colorSelectionForeground );
            setBorder(cellHasFocus ? selectedBorder : nonFocusBorder);

            if( this.isMac ) {
                this.columnText.setForeground( hasFocus ? JBColor.WHITE : JBColor.BLACK );
            }
        } else {
                // Item is not selected

            int idColorTagged       = Preferences.getIdColorByHashTag(hashCode);
            Color colorBackground   = getColorByIndex(idColorTagged);
            Border border           = getBorderByIndex(idColorTagged);

            setBorder( border );
            setBackground( colorBackground );
            setForeground(this.colorForeground);

            if( this.isMac ) {
                this.columnText.setForeground( this.colorForeground );
            }
        }

        setOpaque(true);

        return this;
    }

    /**
     * @param   index
     * @return  Color
     */
    private Color getColorByIndex(int index) {
        switch(index) {
            case idColorYellow:
                return this.colorYellow;

            case idColorGreen:
                return this.colorGreen;

            case idColorRed:
                return this.colorRed;
        }

        return this.colorBackground;
    }

    /**
     * @param   index
     * @return  Color
     */
    private Border getBorderByIndex(int index) {
        switch(index) {
            case idColorYellow:
                return this.borderYellow;

            case idColorGreen:
                return this.borderGreen;

            case idColorRed:
                return this.borderRed;
        }

        return this.borderWhite;
    }
}
