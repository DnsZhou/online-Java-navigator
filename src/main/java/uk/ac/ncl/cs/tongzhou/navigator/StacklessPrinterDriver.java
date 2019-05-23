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
import com.github.javaparser.Range;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.DataKey;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

import java.util.*;

/**
 * Alternative, more efficient, walker for generating AST output.
 */
public class StacklessPrinterDriver {

    private static final DataKey<List<Node>> PREPARED_CHILDREN = new DataKey<>() {
    };
    private static final DataKey<Boolean> STACKLESS_IS_PHANTOM = new DataKey<>() {
    };

    private final Printer printer;

    public StacklessPrinterDriver(Printer printer) {
        this.printer = printer;
    }

    public static String print(Node node, Printer printer) {
        StacklessPrinterDriver printerDriver = new StacklessPrinterDriver(printer);

        // kludge for https://github.com/javaparser/javaparser/issues/1601
        node.accept(new VoidVisitorWithDefaults<Void>() {
            @Override
            public void defaultAction(Node n, Void arg) {

                Range range = n.getRange().get();

                for(int i = 0; i < n.getChildNodes().size(); i++)
                {
                    n.getChildNodes().get(i).accept(this, null);
                }

                if(n.getParentNode().isPresent() && range.begin.isBefore(n.getParentNode().get().getRange().get().begin)) {
                    n.setParentNode(n.getParentNode().get().getParentNode().get());
                }
            }
        }, null);

        printerDriver.print(node);
        String out = printer.getPrintout();
        return out;
    }

    private List<Node> prepareChildren(Node node) {

        List<Node> childNodes = new LinkedList<>();
        for (Node c : node.getChildNodes()) {
            if (!isPhantomNode(c)) {
                childNodes.add(c);
            }
        }

        childNodes.sort(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.getRange().get().begin.compareTo(o2.getRange().get().begin);
            }
        });

        return childNodes;
    }


    public void print(Node node) {

        TokenRange tokenRange = node.getTokenRange().get();
        Iterator<JavaToken> tokenRangeIterator = tokenRange.iterator();

        Stack<Node> openNodes = new Stack<>();
        Node current = node;

        while (tokenRangeIterator.hasNext()) {
            JavaToken token = tokenRangeIterator.next();

            while (true) {
                if (current.getRange().get().begin == token.getRange().get().begin) {
                    printer.begin(current);
                    openNodes.push(current);
                    List<Node> children = prepareChildren(current);
                    current.setData(PREPARED_CHILDREN, children);
                }

                List<Node> children = current.getData(PREPARED_CHILDREN);
                if (!children.isEmpty() && children.get(0).getRange().get().begin == token.getRange().get().begin) {
                    current = children.get(0);
                    continue;
                } else {
                    break;
                }
            }

            printer.printToken(token, current);

            while (true) {

                if (current.getRange().get().end == token.getRange().get().end) {
                    printer.end(current);
                    openNodes.pop();
                    if (!openNodes.isEmpty()) {
                        current = openNodes.peek();
                        if (!current.getData(PREPARED_CHILDREN).isEmpty()) {
                            current.getData(PREPARED_CHILDREN).remove(0);
                        }
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }

        }
    }

    private static Boolean isCertainlyPhantomNode(Node node) {

        if (node.containsData(STACKLESS_IS_PHANTOM)) {
            return node.getData(STACKLESS_IS_PHANTOM);
        }

        if (node instanceof UnknownType) {
            node.setData(STACKLESS_IS_PHANTOM, Boolean.TRUE);
            return Boolean.TRUE;
        }

        if (node.getParentNode().isPresent() &&
                !node.getParentNode().get().getRange().get().contains(node.getRange().get())) {
            node.setData(STACKLESS_IS_PHANTOM, Boolean.TRUE);
            return Boolean.TRUE;
        }

        if (node.getParentNode().isEmpty()) {
            return false;
        }

        return null;
    }

    private static boolean isPhantomNode(Node node) {

        Boolean isCertainly = isCertainlyPhantomNode(node);
        if (isCertainly != null) {
            return isCertainly;
        }

        // uncertain, so depends on the parent

        Stack<Node> stack = new Stack<>();
        stack.push(node);
        while (isCertainly == null) {
            isCertainly = isCertainlyPhantomNode(node.getParentNode().get());
            if (isCertainly == null) {
                stack.push(node.getParentNode().get());
                node = node.getParentNode().get();
            }
        }

        while (!stack.isEmpty()) {
            node = stack.pop();
            node.setData(STACKLESS_IS_PHANTOM, isCertainly);
        }

        return isCertainly;
    }
}
