package org.opengis.feature;

import java.util.Collection;

import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;

public interface ComplexAttributeBuilder<C extends Collection<Attribute>, T extends ComplexType<C>, A extends ComplexAttribute<C,T>> {

	//
	// Injection
	//
	// Used to inject dependencies we need during construction time.
	//
	/**
	 * Returns the underlying attribute factory.
	 */
	AttributeFactory getAttributeFactory();
	
	/**
	 * Sets the underlying attribute factory.
	 */
	void setAttributeFactory(AttributeFactory attributeFactory);
	
	// State
	/**
	 * Initializes the builder to its initial state, the same state it is in 
	 * directly after being instantiated.
	 */
	void init();
	
	/**
	 * Initializes the state of the builder based on a previously built attribute.
	 * <p>
	 *	This method is useful when copying another attribute.
	 * </p>
	 */
	void init(A attribute);
	
	/**
	 * This namespace will be used when constructing AttributeName
	 */
	void setNamespaceURI(String uri );
	
	/**
	 * This namespace will be used when constructing AttributeName
	 * 
	 * @return namespace will be used when constructing AttributeName
	 */
	String getNamespaceURI();
	
	/**
	 * Sets the type with which to build attributes contained within the 
	 * complex attribute being built.
	 * <p>
	 * This method should be called before any calls to {@link #attribute(String, B)}.
	 * </p>
	 * 
	 * @param type The type that the built attribute will implement.
	 */
	void setType(T type);
	
	/**
	 * Returns the type
	 * @return
	 */
	T getType();
	
	/**
	 * Adds an attribute to the complex attribute to be created.
	 * <br>
	 * <p>
	 * This method uses the result of {@link #getNamespaceURI()} to build a 
	 * qualified attribute name.
	 * </p>
	 * <p>
	 * This method uses the type supplied in {@link #setType(T)} in order to 
	 * determine the attribute type.
	 * </p>
	 * 
	 * @param name
	 * 	The name of the attribute.
	 * @param value
	 * 	The value of the attribute.
	 * 
	 */
	ComplexAttributeBuilder<C,T,A> add(Object value, String name);
	
	/**
	 * Adds an attribute to the complex attribute to be created.
	 * <br>
	 * <p>
	 * This method uses the type supplied in {@link #setType(T)} in order to 
	 * determine the attribute type.
	 * </p>
	 * 
	 * @param name
	 * 	The name of the attribute.
	 * @param namespace
	 * 	The namespace of the attribute.
	 * @param value
	 * 	The value of the attribute.
	 * 
	 */
	ComplexAttributeBuilder<C,T,A> add(Object value, String name, String namespaceURI);
	
	/**
	 * Adds an attribute to the complex attribute to be created.
	 * <br>
	 * <p>
	 * This method uses the type supplied in {@link #setType(T)} in order to 
	 * determine the attribute type.
	 * </p>
	 * 
	 * @param name
	 *	The name of the attribute.
	 * @param value
	 * 	The value of the attribute.
	 * 
	 */
	ComplexAttributeBuilder<C,T,A> add(Object value, AttributeName name);
	
	/**
	 * Adds an attribute to the complex attribute to be created.
	 * <br>
	 * <p>
	 * The result of {@link #getNamespaceURI()} to build a qualified attribute 
	 * name.
	 * </p>
	 * <p>
	 * This method uses the type supplied in {@link #setType(T)} in order to 
	 * determine the attribute type.
	 * </p>
	 * 
	 * @param name
	 * 	The name of the attribute.
	 * @param value
	 * 	The value of the attribute.
	 * @param id
	 * 	The id of the attribute.
	 */
	ComplexAttributeBuilder<C,T,A> add(String id, Object value, String name);
	
	/**
	 * Adds an attribute to the complex attribute to be created.
	 * <br>
	 * <p>
	 * This method uses the type supplied in {@link #setType(T)} in order to 
	 * determine the attribute type.
	 * </p>
	 * 
	 * @param name
	 * 	The name of the attribute.
	 * @param namespaceURI
	 * 	The namespace of the attribute.
	 * @param value
	 * 	The value of the attribute.
	 * @param id
	 * 	The id of the attribute.
	 */
	ComplexAttributeBuilder<C,T,A> add(String id, Object value, String name, String namespaceURI);
	
	/**
	 * Adds an attribute to the complex attribute to be created.
	 * <br>
	 * <p>
	 * This method uses the type supplied in {@link #setType(T)} in order to 
	 * determine the attribute type.
	 * </p>
	 * 
	 * @param name
	 * 	The name of the attribute.
	 * @param value
	 * 	The value of the attribute.
	 * @param id
	 * 	The id of the attribute.
	 */
	ComplexAttributeBuilder<C,T,A> add(String id, Object value, AttributeName name);
	
	/**
	 * Builds the complex attribute.
	 * <p>
	 * Note: This method clears all added attributes.
	 * </p>
	 */
	A build();
	
	/**
	 * Builds the complex attribute with a specific id.
	 * <p>
	 * Note: This method clears all added attributes.
	 * </p>
	 */
	A build(String id);

}
