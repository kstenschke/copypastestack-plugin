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

import com.intellij.openapi.project.Project;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

public class ToolWindowFactory implements com.intellij.openapi.wm.ToolWindowFactory {

    /**
     * Add icon and evoke creation of tool window content
     *
     * @param   project
     * @param   toolWindow
     */
    @Override
    public void createToolWindowContent(Project project, final com.intellij.openapi.wm.ToolWindow toolWindow) {
        final com.kstenschke.copypastestack.ToolWindow newToolWindow = new com.kstenschke.copypastestack.ToolWindow(project);
        Content content = ContentFactory.SERVICE.getInstance().createContent(newToolWindow, "", false);

        toolWindow.setAvailable(true, null);
        toolWindow.setToHideOnEmptyContent(true);
        toolWindow.setTitle(StaticTexts.TITLE_TOOL_WINDOW);
        loadAndSetToolWindowIcon(toolWindow);

        toolWindow.getContentManager().addContent(content);

        toolWindow.activate(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    /**
     * @param   toolWindow
     */
    private void loadAndSetToolWindowIcon(final com.intellij.openapi.wm.ToolWindow toolWindow) {
        SwingWorker worker = new SwingWorker<Image, Void>() {
            @Override
            public Image doInBackground() {
                try {
                    return ImageIO.read(getClass().getResource( "resources/images/logo-13.png" ));
                } catch(Exception exception) {
                    exception.printStackTrace();
                }

                return null;
            }

            @Override
            public void done() {
                try {
                    Image img = get();
                    toolWindow.setIcon( new ImageIcon(img) );
                } catch (InterruptedException ignore) {

                } catch (java.util.concurrent.ExecutionException e) {
                    Throwable cause = e.getCause();
                    String why = cause != null ? cause.getMessage() : e.getMessage();
                    System.err.println(StaticTexts.ERROR_RETRIEVING_FILE + ": " + why);
                }
            }
        };

        worker.execute();
    }

}
