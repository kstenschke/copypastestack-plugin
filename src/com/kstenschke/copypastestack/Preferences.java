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

import com.intellij.ide.util.PropertiesComponent;
import com.kstenschke.copypastestack.resources.StaticTexts;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;

public class Preferences {

    @NonNls
    private static final String PROPERTY_ITEMS = "PluginCopyPasteStack.Items";
    @NonNls
    private static final String PROPERTY_IS_ACTIVE_WRAP = "PluginCopyPasteStack.IsActiveWrap";
    @NonNls
    private static final String PROPERTY_IS_ACTIVE_FOCUS_ON_PASTE = "PluginCopyPasteStack.IsActiveFocusEditor";
    @NonNls
    private static final String PROPERTY_IS_ACTIVE_IMMEDIATE_PASTE = "PluginCopyPasteStack.IsActiveImmediatePaste";
    @NonNls
    private static final String PROPERTY_IS_ACTIVE_WRAP_EXTENDED = "PluginCopyPasteStack.IsActiveWrapExtended";
    @NonNls
    private static final String PROPERTY_IS_ACTIVE_KEEP_SORTING = "PluginCopyPasteStack.IsActiveKeepSorting";
    @NonNls
    private static final String PROPERTY_WRAP_BEFORE = "PluginCopyPasteStack.WrapBefore";
    @NonNls
    private static final String PROPERTY_WRAP_AFTER = "PluginCopyPasteStack.WrapAfter";
    @NonNls
    private static final String PROPERTY_WRAP_DELIMITER = "PluginCopyPasteStack.WrapDelimiter";
    @NonNls
    private static final String PROPERTY_SPLITPANE_DIVIDER_LOCATION = "PluginCopyPasteStack.SplitPaneDividerLocation";
    @NonNls
    private static final String PROPERTY_TAG = "PluginCopyPasteStack.HashTag";

    /**
     * Store items
     *
     * @param items
     */
    public static void saveCopyItems(String[] items) {
        PropertiesComponent.getInstance().setValue( PROPERTY_ITEMS, StringUtils.join(items, StaticTexts.SEPARATOR_ITEMS_SPLIT) );
    }

    /**
     * @param   isActive
     */
    public static void saveIsActiveWrap(boolean isActive) {
        PropertiesComponent.getInstance().setValue( PROPERTY_IS_ACTIVE_WRAP, isActive ? "1":"0" );
    }

    /**
     * @param   isActive
     */
    public static void saveIsActiveWrapExtended(boolean isActive) {
        PropertiesComponent.getInstance().setValue( PROPERTY_IS_ACTIVE_WRAP_EXTENDED, isActive ? "1":"0" );
    }

    /**
     * @param   isActive
     */
    public static void saveIsActiveFocusOnPaste(boolean isActive) {
        PropertiesComponent.getInstance().setValue(PROPERTY_IS_ACTIVE_FOCUS_ON_PASTE, isActive ? "1":"0" );
    }

    /**
     * @param   isActive
     */
    public static void saveIsActiveKeepSorting(boolean isActive) {
        PropertiesComponent.getInstance().setValue( PROPERTY_IS_ACTIVE_KEEP_SORTING, isActive ? "1":"0" );
    }

    /**
     * @param   isActive
     */
    public static void saveIsActiveImmediatePaste(boolean isActive) {
        PropertiesComponent.getInstance().setValue( PROPERTY_IS_ACTIVE_IMMEDIATE_PASTE, isActive ? "1":"0" );
    }

    /**
     * @param   value
     */
    public static void saveWrapBefore(String value) {
        PropertiesComponent.getInstance().setValue( PROPERTY_WRAP_BEFORE, value );
    }

    /**
     * @param   value
     */
    public static void saveWrapAfter(String value) {
        PropertiesComponent.getInstance().setValue( PROPERTY_WRAP_AFTER, value );
    }

    /**
     * @param   value
     */
    public static void saveWrapDelimiter(String value) {
        PropertiesComponent.getInstance().setValue( PROPERTY_WRAP_DELIMITER, value );
    }

    /**
     * Load items
     *
     * @return String
     */
    public static String[] getItems() {
        String items = PropertiesComponent.getInstance().getValue(PROPERTY_ITEMS);

        if( items == null ) {
            items = "";
        }

        return items.split( StaticTexts.SEPARATOR_ITEMS_SPLIT );
    }

    /**
     * @return boolean
     */
    public static boolean getIsActiveWrap() {
        String value = PropertiesComponent.getInstance().getValue(PROPERTY_IS_ACTIVE_WRAP);

        return value != null && value.equals("1");
    }

    /**
     * @return boolean
     */
    public static boolean getIsActiveKeepSorting() {
        String value = PropertiesComponent.getInstance().getValue(PROPERTY_IS_ACTIVE_KEEP_SORTING);

        return value != null && value.equals("1");
    }

    /**
     * @return boolean
     */
    public static boolean getIsActiveWrapExtended() {
        String value = PropertiesComponent.getInstance().getValue(PROPERTY_IS_ACTIVE_WRAP_EXTENDED);

        return value != null && value.equals("1");
    }

    /**
     * @return boolean
     */
    public static boolean getIsActiveFocusOnPaste() {
        String value = PropertiesComponent.getInstance().getValue(PROPERTY_IS_ACTIVE_FOCUS_ON_PASTE);

        return value != null && value.equals("1");
    }

    /**
     * @return boolean
     */
    public static boolean getIsActiveImmediatePaste() {
        String value = PropertiesComponent.getInstance().getValue(PROPERTY_IS_ACTIVE_IMMEDIATE_PASTE);

        return value != null && value.equals("1");
    }

    /**
     * @return String
     */
    public static String getWrapBefore() {
        String value = PropertiesComponent.getInstance().getValue(PROPERTY_WRAP_BEFORE);

        return value == null ? "" : value;
    }

    /**
     * @return String
     */
    public static String getWrapAfter() {
        String value = PropertiesComponent.getInstance().getValue(PROPERTY_WRAP_AFTER);

        return value == null ? "" : value;
    }

    /**
     * @return String
     */
    public static String getWrapDelimiter() {
        String value = PropertiesComponent.getInstance().getValue(PROPERTY_WRAP_DELIMITER);

        return value == null ? "" : value;
    }


    public static Integer getSplitPaneDividerLocation() {
        String value = PropertiesComponent.getInstance().getValue(PROPERTY_SPLITPANE_DIVIDER_LOCATION);

        return value == null ? null : Integer.valueOf( value );
    }

    public static void saveSplitPaneDividerLocation(int location) {
        PropertiesComponent.getInstance().setValue( PROPERTY_SPLITPANE_DIVIDER_LOCATION, String.valueOf(location));
    }

    public static void saveHashTag(String hashCode, int idColor) {
        PropertiesComponent.getInstance().setValue( PROPERTY_TAG + hashCode, String.valueOf(idColor));
    }

    public static void deleteHashTag(String hashCode) {
        PropertiesComponent.getInstance().unsetValue(PROPERTY_TAG + hashCode);
    }

    /**
     * @return String
     */
    public static Integer getIdColorByHashTag(String hashCode) {
        String value = PropertiesComponent.getInstance().getValue(PROPERTY_TAG + hashCode);

        return value == null ? 0 : Integer.valueOf( value );
    }

    public static Integer getIdColorByHashTag(int hashCode) {
        return getIdColorByHashTag( String.valueOf(hashCode) );
    }

}
