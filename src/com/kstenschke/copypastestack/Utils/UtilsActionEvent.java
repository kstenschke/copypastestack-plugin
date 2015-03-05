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
package com.kstenschke.copypastestack.Utils;

import org.jetbrains.annotations.Nullable;

import java.awt.event.ActionEvent;

public class UtilsActionEvent {

    /**
     * @param   e
     * @return  boolean     ALT, SHIFT or CTRL pressed?
     */
    public static boolean isAnySpecialKeyPressed(@Nullable ActionEvent e) {
        if (e == null) return false;

        int modifiers = e.getModifiers();
        return checkMod(modifiers, ActionEvent.ALT_MASK)
            || checkMod(modifiers, ActionEvent.SHIFT_MASK)
            || checkMod(modifiers, ActionEvent.CTRL_MASK);
    }

    /**
     * @param   modifiers
     * @param   mask
     * @return  boolean     Detected given mask?
     */
    private static boolean checkMod(int modifiers, int mask) {
        return ((modifiers & mask) == mask);
    }
}
