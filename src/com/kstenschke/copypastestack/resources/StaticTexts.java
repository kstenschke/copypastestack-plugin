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
package com.kstenschke.copypastestack.resources;

import org.jetbrains.annotations.NonNls;

public class StaticTexts {

    public static final String SEPARATOR_ITEMS_SPLIT = "Xx<<-!!!->>xX";

    // =================================================================================================== Plugin labels
    @NonNls
    public static final String TITLE_TOOL_WINDOW = "Copy/Paste";

    // ======================================================================================== Infos shown in statusbar
    @NonNls
    public static final String INFO_LIST = null;
    @NonNls
    public static final String INFO_PLUGIN_ABOUT = "Plugin Informatiom / Rating";
    @NonNls
    public static final String INFO_REFRESH = "Refresh items, sort by color tags";
    @NonNls
    public static final String INFO_REFRESH_PREVIEW = "Refresh preview";
    @NonNls
    public static final String INFO_SORT = "Sort items by color and alphabetical";
    @NonNls
    public static final String INFO_DELETE = "Remove selected Items from list / None selected: remove cached items";
    @NonNls
    public static final String INFO_PASTE = "Paste selected / all items";
    @NonNls
    public static final String INFO_TAG_YELLOW = "Tag selected / all items yellow";
    @NonNls
    public static final String INFO_TAG_RED = "Tag selected / all items red";
    @NonNls
    public static final String INFO_TAG_GREEN = "Tag selected / all items green";
    @NonNls
    public static final String INFO_TAG_REMOVE = "Un-tag selected / all items";
    @NonNls
    public static final String INFO_RECOPY = "Re-copy selected items to clipboard";
    @NonNls
    public static final String INFO_KEEP_SORTED_ALPHABETICAL = "Keep items in alphabetical order";
    @NonNls
    public static final String INFO_IMMEDIATE_PASTE = "Insert item into editor upon click";
    @NonNls
    public static final String INFO_FOCUS_ON_PASTE = "Focus editor upon paste";
    @NonNls
    public static final String INFO_WRAP_EXTENDED = "Convert escaped whitespace sequences of wrap parts during paste";

    // ========================================================================================================== Errors
    @NonNls
    public static final String POPUP_ITEMS_RECOPY = "Re-copy selected items to clipboard";
    @NonNls
    public static final String POPUP_ITEMS_PASTE = "Paste selected items";
    @NonNls
    public static final String POPUP_ITEMS_DELETE = "Remove selected Items";
    @NonNls
    public static final String POPUP_PREVIEW_COPY_SELECTION = "Copy Selection";
    @NonNls
    public static final String POPUP_PASTE = "Paste";
    @NonNls
    public static final String POPUP_SELECT_ALL = "Select all";
    @NonNls
    public static final String POPUP_UNTAG_UNTAG_ALL = "Remove all Color Tags";

    // ========================================================================================================== Errors
    @NonNls
    public static final String ERROR_RETRIEVING_FILE = "Error retrieving File";

    // ============================================================================================= Undo history labels
    @NonNls
    public static final String LABEL_ITEMS = "Items";
    @NonNls
    public static final String LABEL_CMD_PROC_INSERT = "Paste Clipboard Items";

    @NonNls
    public static final String ITEM_TEXT_VIEW_CURRENT = "View Current Clipboard";
}
