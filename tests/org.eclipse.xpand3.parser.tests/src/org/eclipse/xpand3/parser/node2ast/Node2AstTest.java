package org.eclipse.xpand3.parser.node2ast;

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.tmf.common.node.Node;
import org.eclipse.tmf.common.node.NodeUtil;
import org.eclipse.tmf.common.treetest.TreetestPackage;
import org.eclipse.xpand3.SyntaxElement;
import org.eclipse.xpand3.parser.Xpand3NodeLexer;
import org.eclipse.xpand3.parser.Xpand3NodeParser;

public class Node2AstTest extends TestCase {

	private SyntaxElement parseAndTransform(String s) throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
				"ecore", new XMIResourceFactoryImpl());

		EPackage treePackage = TreetestPackage.eINSTANCE;
		EPackage.Registry.INSTANCE.put(treePackage.getNsURI(), treePackage);

		System.out.println(s);
		ANTLRStringStream stream = new ANTLRStringStream(s);
		Xpand3NodeLexer lexer = new Xpand3NodeLexer(stream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		Xpand3NodeParser xpand3NodeParser = new Xpand3NodeParser(tokenStream);

		Node rootNode = xpand3NodeParser.r_file();
		if (rootNode == null) {
			System.out.println("Nothing parsed.");
		} else {
			System.out.println(NodeUtil.toString(rootNode));
		}
		Node2AstTransformer node2AstTransformer = new Node2AstTransformer();
		return node2AstTransformer.doSwitch(rootNode);
	}

	public void testExpression() throws Exception {
		String expr = "foo(Object this, Object that):\n\tthis.toString()==that.toString();";
		SyntaxElement ast = parseAndTransform(expr);

	}
}
