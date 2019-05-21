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
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

/**
 * A generic printer that recreates the original source file content.
 */
public class HtmlPrinter extends Printer {


    public void begin(Node node) {

        if (node instanceof CompilationUnit) {
            stringBuilder.append("<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"UTF-8\">\n" +
                    "<title>" + node.getClass().getName() + "</title>\n" +
                    "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://dnszhou.github.io/assets/css/prettify.css\">\n" +
                    "</head>\n<body>\n<pre class=\"prettyprint linenums\">");
        }
        if (node instanceof com.github.javaparser.ast.type.ClassOrInterfaceType)
            stringBuilder.append("<a href=\"#\" class=\"" + node.getClass().getName() + "\">");
    }

    public void end(Node node) {

        if (node instanceof CompilationUnit) {
            stringBuilder.append("\n</pre>\n<script src=\"https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js\"></script>\n</body>\n</html>");
        }

        if (node instanceof com.github.javaparser.ast.type.ClassOrInterfaceType)
            stringBuilder.append("</a>");
    }

    @Override
    public void printToken(JavaToken javaToken, Node node) {
        stringBuilder.append(replaceHtmlSymbols(javaToken.getText()));
    }


    private String replaceHtmlSymbols(String rawText) {
        return rawText.replace("&", "&#38;")
                .replace(">", "&#62;")
                .replace("<", "&#60;");
    }
}
