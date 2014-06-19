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
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;

class Preferences {

    @NonNls
    private static final String PROPERTY_ITEMS = "PluginCopyPasteStack.Items";
    @NonNls
    private static final String PROPERTY_IS_ACTIVE_WRAP = "PluginCopyPasteStack.IsActiveWrap";
    @NonNls
    private static final String PROPERTY_IS_ACTIVE_PREVIEW = "PluginCopyPasteStack.IsActivePreview";
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
    public static void saveIsActiveWrap(Boolean isActive) {
        PropertiesComponent.getInstance().setValue( PROPERTY_IS_ACTIVE_WRAP, isActive ? "1":"0" );
    }

    /**
     * @param   isActive
     */
    public static void saveIsActivePreview(Boolean isActive) {
        PropertiesComponent.getInstance().setValue( PROPERTY_IS_ACTIVE_PREVIEW, isActive ? "1":"0" );
    }

    /**
     * @param   isActive
     */
    public static void saveIsActiveWrapExtended(Boolean isActive) {
        PropertiesComponent.getInstance().setValue( PROPERTY_IS_ACTIVE_WRAP_EXTENDED, isActive ? "1":"0" );
    }

    /**
     * @param   isActive
     */
    public static void saveIsActiveFocusOnPaste(Boolean isActive) {
        PropertiesComponent.getInstance().setValue(PROPERTY_IS_ACTIVE_FOCUS_ON_PASTE, isActive ? "1":"0" );
    }

    /**
     * @param   isActive
     */
    public static void saveIsActiveKeepSorting(Boolean isActive) {
        PropertiesComponent.getInstance().setValue( PROPERTY_IS_ACTIVE_KEEP_SORTING, isActive ? "1":"0" );
    }

    /**
     * @param   isActive
     */
    public static void saveIsActiveImmediatePaste(Boolean isActive) {
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
     * @return Boolean
     */
    public static Boolean getIsActiveWrap() {
        String value = PropertiesComponent.getInstance().getValue(PROPERTY_IS_ACTIVE_WRAP);

        return value != null && value.equals("1");
    }

    /**
     * @return Boolean
     */
    public static Boolean getIsActivePreview() {
        String value = PropertiesComponent.getInstance().getValue(PROPERTY_IS_ACTIVE_PREVIEW);

        return value != null && value.equals("1");
    }

    /**
     * @return Boolean
     */
    public static Boolean getIsActiveKeepSorting() {
        String value = PropertiesComponent.getInstance().getValue(PROPERTY_IS_ACTIVE_KEEP_SORTING);

        return value != null && value.equals("1");
    }

    /**
     * @return Boolean
     */
    public static Boolean getIsActiveWrapExtended() {
        String value = PropertiesComponent.getInstance().getValue(PROPERTY_IS_ACTIVE_WRAP_EXTENDED);

        return value != null && value.equals("1");
    }

    /**
     * @return Boolean
     */
    public static Boolean getIsActiveFocusOnPaste() {
        String value = PropertiesComponent.getInstance().getValue(PROPERTY_IS_ACTIVE_FOCUS_ON_PASTE);

        return value != null && value.equals("1");
    }

    /**
     * @return Boolean
     */
    public static Boolean getIsActiveImmediatePaste() {
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

}
