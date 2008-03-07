/**
 * <copyright>
 * </copyright>
 *
 * $Id: DeclaredParameter.java,v 1.4 2008/03/07 14:21:08 sefftinge Exp $
 */
package org.eclipse.xpand3.staticTypesystem;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Declared Parameter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.xpand3.staticTypesystem.DeclaredParameter#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.xpand3.staticTypesystem.StaticTypesystemPackage#getDeclaredParameter()
 * @model
 * @generated
 */
public interface DeclaredParameter extends AbstractNamedElement {
	/**
	 * Returns the value of the '<em><b>Type</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' containment reference.
	 * @see #setType(AbstractTypeReference)
	 * @see org.eclipse.xpand3.staticTypesystem.StaticTypesystemPackage#getDeclaredParameter_Type()
	 * @model containment="true" required="true"
	 * @generated
	 */
	AbstractTypeReference getType();

	/**
	 * Sets the value of the '{@link org.eclipse.xpand3.staticTypesystem.DeclaredParameter#getType <em>Type</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' containment reference.
	 * @see #getType()
	 * @generated
	 */
	void setType(AbstractTypeReference value);

} // DeclaredParameter