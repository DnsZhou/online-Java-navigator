/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ncl.cs.tongzhou.navigator;

import com.github.javaparser.JavaToken;
import com.github.javaparser.ast.Node;

/**
 * A generic printer that recreates the original source file content.
 */
public class Printer {

    protected final StringBuilder stringBuilder = new StringBuilder();

    public String getPrintout() {
        return stringBuilder.toString();
    }

    public void printToken(JavaToken javaToken, Node node) {
        stringBuilder.append(replaceHtmlSymbols(javaToken.getText()));
    }

    public void begin(Node node) {
    }

    public void end(Node node) {
    }

    private String replaceHtmlSymbols(String rawText) {
        return rawText.replace("&", "&#38;")
                .replace(">", "&#62;")
                .replace("<", "&#60;");
    }
}
