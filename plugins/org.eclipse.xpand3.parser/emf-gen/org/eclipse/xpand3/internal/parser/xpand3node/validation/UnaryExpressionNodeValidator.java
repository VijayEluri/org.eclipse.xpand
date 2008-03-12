/**
 * <copyright>
 * </copyright>
 *
 * $Id: UnaryExpressionNodeValidator.java,v 1.3 2008/03/12 09:54:13 jkohnlein Exp $
 */
package org.eclipse.xpand3.internal.parser.xpand3node.validation;

import org.eclipse.tmf.common.node.CompositeNode;


/**
 * A sample validator interface for {@link org.eclipse.xpand3.internal.parser.xpand3node.UnaryExpressionNode}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface UnaryExpressionNodeValidator {
	boolean validate();

	boolean validateOperand(CompositeNode value);

}
