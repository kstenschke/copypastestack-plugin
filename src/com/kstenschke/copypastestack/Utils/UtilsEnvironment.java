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
package com.kstenschke.copypastestack.Utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiUtilBase;
import com.kstenschke.copypastestack.StaticTexts;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class UtilsEnvironment {

    /**
     * @return  The currently opened project
     */
    public static Project getOpenProject() {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();

        return (projects.length > 0) ? projects[0] : null;
    }

    /**
     * @param   info    Text to be shown inside the status bar
     */
    public static void setStatusBarInfo(String info) {
        Project project = getOpenProject();

        if( project != null ) {
            StatusBar statusbar = WindowManager.getInstance().getStatusBar(project);
            if( statusbar != null ) {
                statusbar.setInfo(info);
            }
        }
    }

    /**
     * @param   curProject
     * @return  Editor
     */
    public static Editor getEditor(Project curProject) {
        if( curProject == null ) {
            curProject = getOpenProject();
        }
        if( curProject != null ) {
            FileEditorManager manager = FileEditorManager.getInstance(curProject);

            return manager.getSelectedTextEditor();
        }

        return null;
    }

    /**
     * @param   editor
     * @param   text
     */
    public static void insertInEditor(final Project project, final Editor editor, final String text, Boolean focusEditor) {
        if( editor != null && text != null && ! text.isEmpty() ) {
            CaretModel caretModel       = editor.getCaretModel();
            final Integer currentOffset = caretModel.getOffset();

            CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                public void run() {
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        public void run() {
                            Integer textLen   = text.length();
                            Document document = editor.getDocument();

                            document.insertString(currentOffset, text );
                            editor.getCaretModel().moveToOffset(currentOffset + textLen);

                            VirtualFile file = FileDocumentManager.getInstance().getFile( document );
                            if( file != null) {
                                PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
                                if( psiFile != null ) {
                                    CodeStyleManager.getInstance(project).reformatText( psiFile, currentOffset, currentOffset + textLen  );
                                }
                            }
                        }
                    });
                }
            }, StaticTexts.LABEL_CMD_PROC_INSERT, UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);

                // Activate editor
            if( focusEditor ) {
                VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(editor.getDocument());
                if (virtualFile != null) {
                    FileEditorManager.getInstance(project).openFile(virtualFile, true);
                }
            }
        }
    }

    /**
     * @param   text
     * @param   focusEditor
     */
    public static void insertInEditor(String text, Boolean focusEditor) {
        Project project= getOpenProject();
        Editor editor  = getEditor(project);

        insertInEditor(project, editor, text, focusEditor);
    }

    /**
     * @return  String
     */
    public static String getOS() {
        return System.getProperty("os.name").toLowerCase();
    }

    /**
     * @return  Boolean
     */
    public static boolean isMac() {
        return getOS().contains("mac");

    }

    /**
     * @param   str
     */
    public static void copyToClipboard(String str) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(str), null);
    }

}
